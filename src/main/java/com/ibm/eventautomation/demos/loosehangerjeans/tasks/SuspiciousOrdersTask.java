/**
 * Copyright 2023 IBM Corp. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.ibm.eventautomation.demos.loosehangerjeans.tasks;

import java.util.List;
import java.util.Queue;
import java.util.Timer;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.connect.source.SourceRecord;

import com.ibm.eventautomation.demos.loosehangerjeans.DatagenSourceConfig;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Cancellation;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Customer;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Order;
import com.ibm.eventautomation.demos.loosehangerjeans.generators.CancellationGenerator;
import com.ibm.eventautomation.demos.loosehangerjeans.generators.OrderGenerator;
import com.ibm.eventautomation.demos.loosehangerjeans.utils.Generators;

/**
 * Timer task intended for repeated execution.
 *
 * Generates a suspicious series of {@link Order} and {@link Cancellation}
 *  events, intended to simulate an attempt to manipulate a dynamic
 *  pricing algorithm.
 *
 * It will generate:
 *  - multiple large orders
 *  - small order for the same product
 *  - cancellations for all the large orders
 */
public class SuspiciousOrdersTask extends DatagenTimerTask {

    /** Identifies the task that generated the orders and cancellations */
    private static final String ORIGIN = SuspiciousOrdersTask.class.getName();

    /**
     * minimum time to wait after creating an {@link Order} before
     *  generating the corresponding {@link Cancellation}
     */
    private int cancellationMinDelay;
    /**
     * maximum time to wait after creating an {@link Order} before
     *  generating the corresponding {@link Cancellation}
     */
    private int cancellationMaxDelay;

    /** maximum number of large orders to make and then cancel */
    private int maxNumCancelledOrders;

    /** minimum quantity of items to order in the small order */
    private int smallOrderMinItems;
    /** maximum quantity of items to order in the small order */
    private int smallOrderMaxItems;
    /** minimum quantity of items to order in the large order */
    private int largeOrderMinItems;
    /** maximum quantity of items to order in the large order */
    private int largeOrderMaxItems;

    /**
     * maximum amount that the unit price for the small order
     *  should be reduced by, compared with the large order
     *
     * intended to simulate a dynamic pricing algorithm that
     *  has reduced the price following the large order
     */
    private double maxPriceVariation;

    /**
     * Suspicious orders are made by a pre-determined list of
     *  suspicious customers, allowing them to be easily recognised
     *  in demos.
     *
     * Small orders will be placed by a customer selected from this
     *  list.
     */
    private List<Customer> customers;


    public SuspiciousOrdersTask(AbstractConfig config,
                                OrderGenerator orderGenerator,
                                CancellationGenerator cancellationGenerator,
                                Queue<SourceRecord> queue,
                                Timer timer)
    {
        super(orderGenerator, cancellationGenerator, queue, timer, config);

        cancellationMinDelay = config.getInt(DatagenSourceConfig.CONFIG_SUSPICIOUSCANCELLATIONS_MIN_DELAY);
        cancellationMaxDelay = config.getInt(DatagenSourceConfig.CONFIG_SUSPICIOUSCANCELLATIONS_MAX_DELAY);
        maxNumCancelledOrders = config.getInt(DatagenSourceConfig.CONFIG_SUSPICIOUSCANCELLATIONS_NUM);
        smallOrderMinItems = config.getInt(DatagenSourceConfig.CONFIG_ORDERS_SMALL_MIN);
        smallOrderMaxItems = config.getInt(DatagenSourceConfig.CONFIG_ORDERS_SMALL_MAX);
        largeOrderMinItems = config.getInt(DatagenSourceConfig.CONFIG_ORDERS_LARGE_MIN);
        largeOrderMaxItems = config.getInt(DatagenSourceConfig.CONFIG_ORDERS_LARGE_MAX);
        maxPriceVariation = config.getDouble(DatagenSourceConfig.CONFIG_DYNAMICPRICING_PRICE_CHANGE_MAX);

        customers = config.getList(DatagenSourceConfig.CONFIG_SUSPICIOUSCANCELLATIONS_CUSTOMER)
                .stream()
                    .map((customername) -> new Customer(customername))
                .toList();
    }



    @Override
    public void run() {
        // generate a product that this run will be based on
        Order initialOrder = orderGenerator.generate(largeOrderMinItems, largeOrderMaxItems);

        // make multiple large orders that will reduce the price
        //  (and schedule a cancellation after a short delay)
        int suspiciousOrderDelay = 1_000;
        for (int i = 0; i < Generators.randomInt(1, maxNumCancelledOrders); i++) {
            int delay = Generators.randomInt(1000, cancellationMinDelay);

            scheduleOrder(ORIGIN, delay,
                          null,
                          largeOrderMinItems, largeOrderMaxItems,
                          initialOrder.getUnitPrice(),
                          initialOrder.getRegion(),
                          initialOrder.getCountryCode(),
                          initialOrder.getDescription(),
                          cancellationMinDelay, cancellationMaxDelay,
                          initialOrder.getPriority(),
                          initialOrder.getStoreId());

            suspiciousOrderDelay += delay;
        }

        // make a small order at the reduced price
        scheduleOrder(ORIGIN, suspiciousOrderDelay,
            Generators.randomItem(customers),
            smallOrderMinItems, smallOrderMaxItems,
            Generators.randomPrice(initialOrder.getUnitPrice() - maxPriceVariation,
                                   initialOrder.getUnitPrice() - 0.01),
            initialOrder.getRegion(),
            initialOrder.getCountryCode(),
            initialOrder.getDescription(),
            initialOrder.getPriority(),
            initialOrder.getStoreId());
    }
}
