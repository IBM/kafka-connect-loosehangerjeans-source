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

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.apache.kafka.common.config.AbstractConfig;

import com.github.javafaker.Faker;
import com.ibm.eventautomation.demos.loosehangerjeans.DatagenSourceConfig;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Customer;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Order;
import com.ibm.eventautomation.demos.loosehangerjeans.utils.Generators;

/**
 * Generates a {@link Order} event using randomly generated data.
 */
public class OrderGenerator {

    /** order regions (e.g. NA, EMEA) will be chosen at random from this list */
    private final List<String> regions;

    /** minimum price for randomly selected unit price for generated orders */
    private final double minPrice;
    /** maximum price for randomly selected unit price for generated orders */
    private final double maxPrice;

    /** helper class to randomly generate the name of a product */
    private ProductGenerator productDescriptionGenerator;

    /** formatter for event timestamps */
    private final DateTimeFormatter timestampFormatter;

    /**
     * Generator can simulate a delay in events being produced
     *  to Kafka by putting a timestamp in the message payload
     *  that is earlier than the current time.
     *
     * The amount of the delay will be randomized to simulate
     *  a delay due to network or infrastructure reasons.
     *
     * This value is the maximum delay (in seconds) that it will
     *  use. (Setting this to 0 will mean all events are
     *  produced with the current time).
     */
    private final int MAX_DELAY_SECS;

    /** customer name generator */
    private final Faker faker = new Faker();


    public OrderGenerator(AbstractConfig config)
    {
        this.productDescriptionGenerator = new ProductGenerator(config);

        this.regions = config.getList(DatagenSourceConfig.CONFIG_LOCATIONS_REGIONS);
        this.minPrice = config.getDouble(DatagenSourceConfig.CONFIG_PRODUCTS_MIN_PRICE);
        this.maxPrice = config.getDouble(DatagenSourceConfig.CONFIG_PRODUCTS_MAX_PRICE);

        this.timestampFormatter = DateTimeFormatter.ofPattern(config.getString(DatagenSourceConfig.CONFIG_FORMATS_TIMESTAMPS));
        this.MAX_DELAY_SECS = config.getInt(DatagenSourceConfig.CONFIG_DELAYS_ORDERS);
    }


    /** generates a random order */
    public Order generate(int minItems, int maxItems) {
        double unitPrice = Generators.randomPrice(minPrice, maxPrice);
        String description = productDescriptionGenerator.generate();
        String region = Generators.randomItem(regions);
        Customer customer = new Customer(faker);

        return generate(minItems, maxItems,
                        unitPrice,
                        region,
                        description,
                        customer);
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
        String description = productDescriptionGenerator.generate();
        String region = Generators.randomItem(regions);

        return generate(minItems, maxItems,
                        unitPrice,
                        region,
                        description,
                        customer);
    }

    /** Creates an order with known details */
    public Order generate(int minItems, int maxItems,
            double unitPrice,
            String region,
            String description,
            Customer customer)
    {
        int quantity = Generators.randomInt(minItems, maxItems);
        if (customer == null) {
            customer = new Customer(faker);
        }

        return new Order(UUID.randomUUID().toString(),
                         timestampFormatter.format(Generators.nowWithRandomOffset(MAX_DELAY_SECS)),
                         customer,
                         description,
                         unitPrice, quantity,
                         region);
    }
}
