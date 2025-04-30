/**
 * Copyright 2025 IBM Corp. All Rights Reserved.
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

import com.ibm.eventautomation.demos.loosehangerjeans.DatagenSourceConfig;
import com.ibm.eventautomation.demos.loosehangerjeans.data.AbandonedOrder;
import com.ibm.eventautomation.demos.loosehangerjeans.data.OnlineCustomer;
import com.ibm.eventautomation.demos.loosehangerjeans.utils.Generators;
import org.apache.kafka.common.config.AbstractConfig;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates an {@link AbandonedOrder} event using randomly generated data.
 */
public class AbandonedOrderGenerator extends Generator<AbandonedOrder> {

    /** Helper class to randomly generate product details */
    private final ProductGenerator productGenerator;

    /** Minimum number of products in a order */
    private final int minProducts;

    /** Maximum number of products in a order */
    private final int maxProducts;

    /** Minimum number of emails for the customer who abandons the cart */
    private final int minEmails;

    /** Maximum number of emails for the customer who abandons the cart */
    private final int maxEmails;

    /** Creates an {@link AbandonedOrderGenerator} using the provided configuration. */
    public AbandonedOrderGenerator(AbstractConfig config) {
        super(config.getInt(DatagenSourceConfig.CONFIG_TIMES_ABANDONEDORDERS),
                config.getInt(DatagenSourceConfig.CONFIG_DELAYS_ABANDONEDORDERS),
                config.getDouble(DatagenSourceConfig.CONFIG_DUPLICATE_ABANDONEDORDERS),
                config.getString(DatagenSourceConfig.CONFIG_FORMATS_TIMESTAMPS_LTZ));

        this.productGenerator = new ProductGenerator(config);
        this.minProducts = config.getInt(DatagenSourceConfig.CONFIG_ABANDONEDORDERS_PRODUCTS_MIN);
        this.maxProducts = config.getInt(DatagenSourceConfig.CONFIG_ABANDONEDORDERS_PRODUCTS_MAX);
        this.minEmails = config.getInt(DatagenSourceConfig.CONFIG_ABANDONEDORDERS_CUSTOMER_EMAILS_MIN);
        this.maxEmails = config.getInt(DatagenSourceConfig.CONFIG_ABANDONEDORDERS_CUSTOMER_EMAILS_MIN);
    }

    @Override
    protected AbandonedOrder generateEvent(ZonedDateTime timestamp) {
        // Generate a random customer
        OnlineCustomer customer = OnlineCustomer.create(faker, minEmails, maxEmails);
        // Generate some products randomly.
        int productCount = Generators.randomInt(minProducts, maxProducts);
        List<String> cartProducts = new ArrayList<>();
        for (int i = 0; i < productCount; i++) {
            cartProducts.add(productGenerator.generate().getShortDescription());
        }

        return new AbandonedOrder(formatTimestamp(timestamp), customer, cartProducts, timestamp);
    }
}
