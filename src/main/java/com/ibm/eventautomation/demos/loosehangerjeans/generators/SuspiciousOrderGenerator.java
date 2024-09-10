/**
 * Copyright 2024 IBM Corp. All Rights Reserved.
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
package com.ibm.eventautomation.demos.loosehangerjeans.generators;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.ArrayList;
import org.apache.kafka.common.config.AbstractConfig;

import com.ibm.eventautomation.demos.loosehangerjeans.DatagenSourceConfig;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Cancellation;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Customer;
import com.ibm.eventautomation.demos.loosehangerjeans.data.LoosehangerData;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Order;
import com.ibm.eventautomation.demos.loosehangerjeans.utils.Generators;

public class SuspiciousOrderGenerator {

    public static List<LoosehangerData> generateHistory(AbstractConfig config,
                                                        OrderGenerator orderGenerator,
                                                        CancellationGenerator cancellationGenerator)
    {
        int smallOrderMinItems = config.getInt(DatagenSourceConfig.CONFIG_ORDERS_SMALL_MIN);
        int smallOrderMaxItems = config.getInt(DatagenSourceConfig.CONFIG_ORDERS_SMALL_MAX);
        int largeOrderMinItems = config.getInt(DatagenSourceConfig.CONFIG_ORDERS_LARGE_MIN);
        int largeOrderMaxItems = config.getInt(DatagenSourceConfig.CONFIG_ORDERS_LARGE_MAX);
        int cancellationMinDelay = config.getInt(DatagenSourceConfig.CONFIG_SUSPICIOUSCANCELLATIONS_MIN_DELAY);
        int cancellationMaxDelay = config.getInt(DatagenSourceConfig.CONFIG_SUSPICIOUSCANCELLATIONS_MAX_DELAY);
        int maxNumCancelledOrders = config.getInt(DatagenSourceConfig.CONFIG_SUSPICIOUSCANCELLATIONS_NUM);
        double maxPriceVariation = config.getDouble(DatagenSourceConfig.CONFIG_DYNAMICPRICING_PRICE_CHANGE_MAX);
        List<Customer> customers = config.getList(DatagenSourceConfig.CONFIG_SUSPICIOUSCANCELLATIONS_CUSTOMER)
                                    .stream()
                                        .map((customername) -> new Customer(customername))
                                    .toList();


        final List<LoosehangerData> history = new ArrayList<LoosehangerData>();

        final ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime timestamp = ZonedDateTime.now().minusDays(7);

        while (timestamp.isBefore(now)) {

            // random start for the suspicious activity
            ZonedDateTime nextTimestamp = now.plusMinutes(Generators.randomInt(1, 120));

            // generate a product that this activity will be based on
            Order initialOrder = orderGenerator.generate(largeOrderMinItems, largeOrderMaxItems, nextTimestamp);

            // make multiple large orders to manipulate the price
            for (int i = 0; i < Generators.randomInt(1, maxNumCancelledOrders); i++)
            {
                // random offset
                nextTimestamp = nextTimestamp.plusSeconds(Generators.randomInt(60, 300));

                // make a large order (that will reduce the price)
                Order largeOrder = orderGenerator.generate(largeOrderMinItems, largeOrderMaxItems,
                                                           initialOrder.getUnitPrice(),
                                                           initialOrder.getRegion(),
                                                           initialOrder.getDescription(),
                                                           initialOrder.getCustomer(),
                                                           nextTimestamp);
                history.add(largeOrder);

                // cancel the order after a short delay
                int minDelay = Generators.randomInt(1000, cancellationMinDelay);
                int delayMs = Generators.randomInt(minDelay, cancellationMaxDelay);
                nextTimestamp = nextTimestamp.plusNanos(delayMs * 1_000_000L);

                Cancellation largeOrderCancellation = cancellationGenerator.generate(nextTimestamp, largeOrder);
                history.add(largeOrderCancellation);
            }

            // make a small order at the reduced price
            nextTimestamp = nextTimestamp.plusSeconds(Generators.randomInt(60, 300));
            Order smallOrder = orderGenerator.generate(
                        smallOrderMinItems, smallOrderMaxItems,
                        Generators.randomPrice(initialOrder.getUnitPrice() - maxPriceVariation,
                                            initialOrder.getUnitPrice() - 0.01),
                        initialOrder.getRegion(),
                        initialOrder.getDescription(),
                        Generators.randomItem(customers),
                        nextTimestamp);
            history.add(smallOrder);


            // generate one suspicious order a day
            timestamp = timestamp.plusDays(1);
        }

        return history;
    }

}
