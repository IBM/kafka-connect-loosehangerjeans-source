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

import java.util.Queue;
import java.util.Timer;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.connect.source.SourceRecord;

import com.ibm.eventautomation.demos.loosehangerjeans.DatagenSourceConfig;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Cancellation;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Order;
import com.ibm.eventautomation.demos.loosehangerjeans.generators.CancellationGenerator;
import com.ibm.eventautomation.demos.loosehangerjeans.generators.OrderGenerator;
import com.ibm.eventautomation.demos.loosehangerjeans.utils.Generators;

/**
 * Timer task intended for repeated execution.
 *
 * Generates an innocent series of {@link Order} and {@link Cancellation}
 *  events that looks similar enough to the orders created by
 *  {@link SuspiciousOrdersTask} that they will be found by a naive
 *  analysis for suspicious orders.
 *
 * It will generate:
 *  - large order
 *  - small order for the same product
 *  - cancellation for the large order
 */
public class FalsePositivesTask extends DatagenTimerTask {

    /** Identifies the task that generated the orders and cancellations */
    private static final String ORIGIN = FalsePositivesTask.class.getName();

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

    /** Name of the topic to produce order events to. */
    private String topicname;


    public FalsePositivesTask(AbstractConfig config,
                              OrderGenerator orderGenerator,
                              CancellationGenerator cancellationGenerator,
                              Queue<SourceRecord> queue,
                              Timer timer)
    {
        super(orderGenerator, cancellationGenerator, queue, timer, config);

        cancellationMinDelay = config.getInt(DatagenSourceConfig.CONFIG_SUSPICIOUSCANCELLATIONS_MIN_DELAY);
        cancellationMaxDelay = config.getInt(DatagenSourceConfig.CONFIG_SUSPICIOUSCANCELLATIONS_MAX_DELAY);

        smallOrderMinItems = config.getInt(DatagenSourceConfig.CONFIG_ORDERS_SMALL_MIN);
        smallOrderMaxItems = config.getInt(DatagenSourceConfig.CONFIG_ORDERS_SMALL_MAX);
        largeOrderMinItems = config.getInt(DatagenSourceConfig.CONFIG_ORDERS_LARGE_MIN);
        largeOrderMaxItems = config.getInt(DatagenSourceConfig.CONFIG_ORDERS_LARGE_MAX);

        maxPriceVariation = config.getDouble(DatagenSourceConfig.CONFIG_DYNAMICPRICING_PRICE_CHANGE_MAX);
        
        this.topicname = config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_ORDERS);
    }



    @Override
    public void run() {
        // make the initial large order
        Order initialOrder = orderGenerator.generate(largeOrderMinItems, largeOrderMaxItems);
        queue.add(initialOrder.createSourceRecord(topicname, ORIGIN));

        // schedule the order cancellation
        cancelOrder(initialOrder, ORIGIN,
                    cancellationMinDelay,
                    cancellationMaxDelay);

        // schedule a new small order for before the cancellation is complete
        scheduleOrder(ORIGIN,
            Generators.randomInt(30_000, cancellationMinDelay),
            initialOrder.getCustomer(),
            smallOrderMinItems, smallOrderMaxItems,
            Generators.randomPrice(initialOrder.getUnitPrice() - maxPriceVariation,
                                   initialOrder.getUnitPrice() - 0.01),
            initialOrder.getRegion(),
            initialOrder.getCountry(),
            initialOrder.getDescription(),
            initialOrder.getPriority(),
            initialOrder.getStoreID());
    }
}
