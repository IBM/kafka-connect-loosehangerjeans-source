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

package com.ibm.eventautomation.demos.loosehangerjeans.data;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Represents an event for an abandoned shopping cart.
 * Each event includes customer information and a list of products
 * left in the cart when it was abandoned.
 */

public class AbandonedShoppingCart extends LoosehangerData {

    public static final String PARTITION = "abandonedshoppingcart";

    /** Unique ID for the abandoned cart */
    private final String cartId;

    /** Time that the cart was considered abandoned */
    private final String abandonTime;

    /** Email address of the customer */
    private final String email;

    /** Details of the customer who abandoned the cart */
    private final OnlineCustomer customer;

    /** List of products left in the cart */
    private final List<Product> products;

    /** Schema for the events - all fields are required */
    private static final Schema SCHEMA = SchemaBuilder.struct()
            .name("abandonedshoppingcart")
            .version(1)
            .field("cartId",         Schema.STRING_SCHEMA)
            .field("abandonTime",    Schema.STRING_SCHEMA)
            .field("email",          Schema.STRING_SCHEMA)
            .field("customer",       OnlineCustomer.SCHEMA)
            .field("products",       SchemaBuilder.array(Product.SCHEMA))
            .build();

    /** Creates an {@link AbandonedShoppingCart} using the provided details */
    public AbandonedShoppingCart(String cartId, String abandonTime, String email, OnlineCustomer customer,
                                 List<Product> products, ZonedDateTime recordTimestamp) {
        super(recordTimestamp);
        this.cartId = cartId;
        this.abandonTime = abandonTime;
        this.email = email;
        this.customer = customer;
        this.products = products;
    }

    /** Creates an {@link AbandonedShoppingCart} with a generated cart ID */
    public AbandonedShoppingCart(String abandonTime, String email, OnlineCustomer customer,
                                 List<Product> products, ZonedDateTime recordTimestamp) {
        this(UUID.randomUUID().toString(), abandonTime, email, customer, products, recordTimestamp);
    }

    public String getCartId() {
        return cartId;
    }

    public String getAbandonTime() {
        return abandonTime;
    }

    public String getEmail() {
        return email;
    }

    public OnlineCustomer getCustomer() {
        return customer;
    }

    public List<Product> getProducts() {
        return products;
    }

    public SourceRecord createSourceRecord(String topicName) {
        return super.createSourceRecord(topicName, PARTITION);
    }

    @Override
    protected String getKey() {
        return cartId;
    }

    @Override
    protected Schema getValueSchema() {
        return SCHEMA;
    }

    @Override
    protected Struct getValue() {
        Struct struct = new Struct(SCHEMA);
        struct.put("cartId", cartId);
        struct.put("abandonTime", abandonTime);
        struct.put("email", email);
        struct.put("customer", customer.toStruct());
        struct.put("products", products.stream().map(Product::toStruct).collect(Collectors.toList()));
        return struct;
    }

    @Override
    public String toString() {
        return "AbandonedShoppingCart [cartId=" + cartId + ", abandonTime=" + abandonTime
                + ", email=" + email + ", customer=" + customer
                + ", products=" + Arrays.toString(products.toArray()) + "]";
    }
}
