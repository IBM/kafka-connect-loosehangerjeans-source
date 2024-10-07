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
package com.ibm.eventautomation.demos.loosehangerjeans.generators;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import org.apache.kafka.common.config.AbstractConfig;

import com.ibm.eventautomation.demos.loosehangerjeans.DatagenSourceConfig;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Cancellation;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Customer;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Order;
import com.ibm.eventautomation.demos.loosehangerjeans.utils.Generators;

/**
 * Generates an {@link Order} event using randomly generated data.
 */
public class OrderGenerator extends Generator<Order> {

    /** order regions (e.g. NA, EMEA) will be chosen at random from this list */
    private final List<String> regions;
    /** order country (e.g. India, France) */
    private final List<String> countries;

    /** priorities list for orders */
    private final List<String> priorities;

    /** list for store IDs */
    private final List<String> storeIDs;

    /** minimum price for randomly selected unit price for generated orders */
    private final double minPrice;
    /** maximum price for randomly selected unit price for generated orders */
    private final double maxPrice;

    /** helper class to randomly generate the name of a product */
    private ProductGenerator productGenerator;

    /** minimum number of items to order */
    private int minOrders;
    /** maximum number of items to order */
    private int maxOrders;

    /**
     * Proportion of {@link Order} events that should have an associated
     *  {@link Cancellation} event generated.
     *
     * Between 0.0 and 1.0
     *
     * Set this to 0.0 for no cancellation events.
     * Set this to 1.0 for every order to have a corresponding cancellation.
     */
    private double cancellationRatio;


    public OrderGenerator(AbstractConfig config)
    {
        super(config.getInt(DatagenSourceConfig.CONFIG_TIMES_ORDERS),
              config.getInt(DatagenSourceConfig.CONFIG_DELAYS_ORDERS),
              config.getDouble(DatagenSourceConfig.CONFIG_DUPLICATE_ORDERS),
              config.getString(DatagenSourceConfig.CONFIG_FORMATS_TIMESTAMPS));

        this.productGenerator = new ProductGenerator(config);

        this.regions = config.getList(DatagenSourceConfig.CONFIG_LOCATIONS_REGIONS);
        this.countries = config.getList(DatagenSourceConfig.CONFIG_LOCATIONS_COUNTRIES);
        this.storeIDs = config.getList(DatagenSourceConfig.CONFIG_LOCATIONS_STORE_IDS);
        this.minPrice = config.getDouble(DatagenSourceConfig.CONFIG_PRODUCTS_MIN_PRICE);
        this.maxPrice = config.getDouble(DatagenSourceConfig.CONFIG_PRODUCTS_MAX_PRICE);

        this.priorities = config.getList(DatagenSourceConfig.CONFIG_PRIORITIES);

        this.minOrders = config.getInt(DatagenSourceConfig.CONFIG_ORDERS_SMALL_MIN);
        this.maxOrders = config.getInt(DatagenSourceConfig.CONFIG_ORDERS_LARGE_MAX);

        this.cancellationRatio = config.getDouble(DatagenSourceConfig.CONFIG_CANCELLATIONS_RATIO);
    }


    /** generates a random order */
    public Order generate(int minItems, int maxItems) {
        return generate(minItems, maxItems, ZonedDateTime.now());
    }

    public Order generate(int minItems, int maxItems, ZonedDateTime timestamp) {
        int quantity = Generators.randomInt(minItems, maxItems);
        double unitPrice = Generators.randomPrice(minPrice, maxPrice);
        String description = productGenerator.generate().getDescription();
        String region = Generators.randomItem(regions);
        String country = Generators.randomItem(countries);
        String storeID = Generators.randomItem(storeIDs);
        String priority = Generators.randomItem(priorities);
        Customer customer = new Customer(faker);

        return new Order(UUID.randomUUID().toString(),
                         formatTimestamp(timestamp),
                         customer,
                         description,
                         unitPrice, quantity,
                         region,
                         timestamp,
                         country,
                         priority,
                         storeID);
    }

    /**
     * Generates an order for a known customer. Useful when
     *  creating events representing multiple orders made
     *  by the same customer.
     */
    public Order generate(Customer customer) {
        int minItems = 1;
        int maxItems = 1;
        double unitPrice = Generators.randomPrice(minPrice, maxPrice);
        String description = productGenerator.generate().getDescription();
        String region = Generators.randomItem(regions);
        String country = Generators.randomItem(countries);
        String priority = Generators.randomItem(priorities);
        String storeID = Generators.randomItem(storeIDs);

        return generate(minItems, maxItems,
                        unitPrice,
                        region,
                        description,
                        customer,
                        country,
                        priority,
                        storeID);
    }

    /** Creates an order with known details */
    public Order generate(int minItems, int maxItems,
                                     double unitPrice,
                                     String region,
                                     String description,
                                     Customer customer,
                                     String country,
                                     String priority,
                                     String storeID)
    {
        return generate(minItems, maxItems,
                        unitPrice,
                        region,
                        description,
                        customer,
                        ZonedDateTime.now(),
                        country,
                        priority,
                        storeID);
    }

    public Order generate(int minItems, int maxItems,
                          double unitPrice,
                          String region,
                          String description,
                          Customer customer,
                          ZonedDateTime timestamp,
                          String country,
                          String priority,
                          String storeID)
    {
        int quantity = Generators.randomInt(minItems, maxItems);
        if (customer == null) {
            customer = new Customer(faker);
        }

        return new Order(UUID.randomUUID().toString(),
                         formatTimestamp(timestamp),
                         customer,
                         description,
                         unitPrice, quantity,
                         region,
                         timestamp,
                         country,
                         priority,
                         storeID);
    }


    @Override
    protected Order generateEvent(ZonedDateTime timestamp) {
        double unitPrice = Generators.randomPrice(minPrice, maxPrice);
        String description = productGenerator.generate().getDescription();
        String region = Generators.randomItem(regions);
        String country = Generators.randomItem(countries);
        Customer customer = new Customer(faker);
        String priority = Generators.randomItem(priorities);
        String storeID = Generators.randomItem(storeIDs);

        int quantity = Generators.randomInt(minOrders, maxOrders);

        return new Order(UUID.randomUUID().toString(),
                         formatTimestamp(timestamp),
                         customer,
                         description,
                         unitPrice, quantity,
                         region,
                         timestamp,
                         country,
                         priority,
                         storeID);
    }

    /**
     * Returns a random decision of whether an order should be cancelled.
     *  The frequency for how often this returns true is determined by
     *  a ratio provided to the generator constructor.
     *
     * @return true if the order should be cancelled
     */
    public boolean shouldCancel() {
        return Generators.shouldDo(cancellationRatio);
    }
}
