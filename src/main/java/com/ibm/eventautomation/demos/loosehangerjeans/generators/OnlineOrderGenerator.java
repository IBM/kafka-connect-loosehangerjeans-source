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
import com.ibm.eventautomation.demos.loosehangerjeans.data.Address;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Country;
import com.ibm.eventautomation.demos.loosehangerjeans.data.OnlineAddress;
import com.ibm.eventautomation.demos.loosehangerjeans.data.OnlineCustomer;
import com.ibm.eventautomation.demos.loosehangerjeans.data.OnlineOrder;
import com.ibm.eventautomation.demos.loosehangerjeans.utils.Generators;
import org.apache.kafka.common.config.AbstractConfig;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates an {@link OnlineOrder} event using randomly generated data.
 */
public class OnlineOrderGenerator extends Generator<OnlineOrder> {

    /** Helper class to randomly generate the details of a product. */
    private final ProductGenerator productGenerator;

    /** Minimum number of products to include in the order. */
    private final int minProducts;

    /** Maximum number of products to include in the order. */
    private final int maxProducts;

    /** Minimum number of emails for the customer who makes the order. */
    private final int minEmails;

    /** Maximum number of emails for the customer who makes the order. */
    private final int maxEmails;

    /** Minimum number of phones in an address for the given order. */
    private final int minPhones;

    /** Maximum number of phones in an address for the given order. */
    private final int maxPhones;

    /**
     * Ratio of orders that use the same address as shipping and billing address.
     *
     * Between 0.0 and 1.0.
     *
     * Setting this to 0 will mean no events will use the same address as shipping and billing address.
     * Setting this to 1 will mean every event uses the same address as shipping and billing address.
     */
    private final double reuseAddressRatio;

    /**
     * Ratio of orders that have at least one product that runs out-of-stock after the order has been placed.
     * Must be between 0.0 and 1.0.
     *
     * Setting this to 0 will mean that no out-of-stock event is generated.
     * Setting this to 1 will mean that one out-of-stock event will be generated for each new order.
     */
    private final double outOfStockRatio;

    /** Custom list of cities to be used instead of faker generated */
    private final List<String> cities;




    /** Creates an {@link OnlineOrderGenerator} using the provided configuration. */
    public OnlineOrderGenerator(AbstractConfig config) {
        super(config.getInt(DatagenSourceConfig.CONFIG_TIMES_ONLINEORDERS),
              config.getInt(DatagenSourceConfig.CONFIG_DELAYS_ONLINEORDERS),
              config.getDouble(DatagenSourceConfig.CONFIG_DUPLICATE_ONLINEORDERS),
              config.getString(DatagenSourceConfig.CONFIG_FORMATS_TIMESTAMPS_LTZ));

        this.productGenerator = new ProductGenerator(config);

        this.minProducts = config.getInt(DatagenSourceConfig.CONFIG_ONLINEORDERS_PRODUCTS_MIN);
        this.maxProducts = config.getInt(DatagenSourceConfig.CONFIG_ONLINEORDERS_PRODUCTS_MAX);

        this.minEmails = config.getInt(DatagenSourceConfig.CONFIG_ONLINEORDERS_CUSTOMER_EMAILS_MIN);
        this.maxEmails = config.getInt(DatagenSourceConfig.CONFIG_ONLINEORDERS_CUSTOMER_EMAILS_MAX);

        this.minPhones = config.getInt(DatagenSourceConfig.CONFIG_ONLINEORDERS_ADDRESS_PHONES_MIN);
        this.maxPhones = config.getInt(DatagenSourceConfig.CONFIG_ONLINEORDERS_ADDRESS_PHONES_MAX);

        this.reuseAddressRatio = config.getDouble(DatagenSourceConfig.CONFIG_ONLINEORDERS_REUSE_ADDRESS_RATIO);
        this.outOfStockRatio = config.getDouble(DatagenSourceConfig.CONFIG_ONLINEORDERS_OUTOFSTOCK_RATIO);

        this.cities = config.getList(DatagenSourceConfig.CONFIG_ONLINEORDERS_CITIES);
    }


    @Override
    protected OnlineOrder generateEvent(ZonedDateTime timestamp) {
        // Generate some products randomly.
        int productCount = Generators.randomInt(minProducts, maxProducts);
        List<String> products = new ArrayList<>();
        for (int i = 0; i < productCount; i++) {
            products.add(productGenerator.generate().getDescription());
        }

        // Generate a random customer.
        OnlineCustomer customer = OnlineCustomer.create(faker, minEmails, maxEmails);

        // Generate the country for the addresses.
        Country country = new Country(DEFAULT_LOCALE.getCountry(), DEFAULT_LOCALE.getDisplayCountry(DEFAULT_LOCALE));

        // Generate a random shipping address.
        Address shippingAddress = Address.create(faker, country, minPhones, maxPhones);

        // If the city list is not empty, we will randomly select a city 
        // and replace one created by the faker
        if (cities.size() > 0) {
            String city = Generators.randomItem(cities);
            shippingAddress.setCity(city);
        }

        // Possibly reuse the shipping address as billing address.
        Address billingAddress = Generators.shouldDo(reuseAddressRatio)
                ? shippingAddress
                : Address.create(faker, country, minPhones, maxPhones);

        return new OnlineOrder(formatTimestamp(timestamp),
                               customer,
                               products,
                               new OnlineAddress(shippingAddress, billingAddress),
                               timestamp);
    }

    public boolean shouldGenerateOutOfStockEvent() {
        return Generators.shouldDo(outOfStockRatio);
    }
}
