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

import com.ibm.eventautomation.demos.loosehangerjeans.DatagenSourceConfig;
import com.ibm.eventautomation.demos.loosehangerjeans.data.AbandonedShoppingCart;
import com.ibm.eventautomation.demos.loosehangerjeans.data.OnlineCustomer;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Product;
import com.ibm.eventautomation.demos.loosehangerjeans.utils.Generators;
import org.apache.kafka.common.config.AbstractConfig;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates an {@link AbandonedShoppingCart} event using randomly generated data.
 */
public class AbandonedShoppingCartGenerator extends Generator<AbandonedShoppingCart> {

    /** Helper class to randomly generate product details */
    private final ProductGenerator productGenerator;

    /** Minimum number of products in a shopping cart */
    private final int minProducts;

    /** Maximum number of products in a shopping cart */
    private final int maxProducts;

    /** Minimum number of emails for the customer who abandons the cart */
    private final int minEmails;

    /** Maximum number of emails for the customer who abandons the cart */
    private final int maxEmails;

    /**
     * Ratio of abandoned carts that contain at least one product from the products list.
     * Between 0.0 and 1.0.
     */
    private final double productRatio;

    /** Products that are added to the cart */
    private final List<Product> products;

    /** Creates an {@link AbandonedShoppingCartGenerator} using the provided configuration */
    public AbandonedShoppingCartGenerator(AbstractConfig config, List<Product> products) {
        super(config.getInt(DatagenSourceConfig.CONFIG_TIMES_ABANDONEDSHOPPINGCARTS),
                config.getInt(DatagenSourceConfig.CONFIG_DELAYS_ABANDONEDSHOPPINGCARTS),
                config.getDouble(DatagenSourceConfig.CONFIG_DUPLICATE_ABANDONEDSHOPPINGCARTS),
                config.getString(DatagenSourceConfig.CONFIG_FORMATS_TIMESTAMPS_LTZ));

        this.productGenerator = new ProductGenerator(config);
        this.minProducts = config.getInt(DatagenSourceConfig.CONFIG_ABANDONEDSHOPPINGCARTS_PRODUCTS_MIN);
        this.maxProducts = config.getInt(DatagenSourceConfig.CONFIG_ABANDONEDSHOPPINGCARTS_PRODUCTS_MAX);
        this.minEmails = config.getInt(DatagenSourceConfig.CONFIG_ABANDONEDSHOPPINGCARTS_CUSTOMER_EMAILS_MIN);
        this.maxEmails = config.getInt(DatagenSourceConfig.CONFIG_ABANDONEDSHOPPINGCARTS_CUSTOMER_EMAILS_MAX);
        this.productRatio = config.getDouble(DatagenSourceConfig.CONFIG_ABANDONEDSHOPPINGCARTS_PRODUCT_RATIO);
        this.products = products;
    }

    @Override
    protected AbandonedShoppingCart generateEvent(ZonedDateTime timestamp) {
        // Generate a random customer
        OnlineCustomer customer = OnlineCustomer.create(faker, minEmails, maxEmails);
        List<String> emails = customer.getEmails();
        String abandonTime = formatTimestamp(timestamp);
        // Generate the list of products added to the cart
        int productCount = Generators.randomInt(minProducts, maxProducts);
        List<Product> cartProducts = new ArrayList<>();
        for (int i = 0; i < productCount; i++) {
            Product product = Generators.shouldDo(productRatio)
                    ? Generators.randomItem(products)
                    : productGenerator.generate();
            cartProducts.add(product);
        }

        return new AbandonedShoppingCart(abandonTime, emails, customer, cartProducts, timestamp);
    }
}
