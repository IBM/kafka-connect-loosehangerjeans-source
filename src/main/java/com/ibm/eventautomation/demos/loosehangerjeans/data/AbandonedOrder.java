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

package com.ibm.eventautomation.demos.loosehangerjeans.data;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Represents an event for an abandoned order.
 * Each event includes customer information and a list of products
 * left in the cart when it was abandoned.
 */

public class AbandonedOrder extends OnlineActivityData {

    public static final String PARTITION = "abandonedorder";

    /** Unique ID for the abandoned cart */
    private final String cartId;

    /** Time that the cart was considered abandoned */
    private final String abandonedTimestamp;

    /** Details of the customer who made the order. */
    private final OnlineCustomer customer;

    /** List of products left in the cart */
    private final List<String> products;

    /** Schema for the events - all fields are required. */
    private static final Schema SCHEMA = SchemaBuilder.struct()
            .name("abandonedorder")
            .version(1)
            .field("cartid",        Schema.STRING_SCHEMA)
            .field("customer",      OnlineCustomer.SCHEMA)
            .field("products",      SchemaBuilder.array(Schema.STRING_SCHEMA).build())
            .field("abandonedtime", Schema.STRING_SCHEMA)
            .build();

    /** Creates an {@link AbandonedOrder} using the provided details */
    public AbandonedOrder(String cartId, String abandonedTimestamp, OnlineCustomer customer, List<String> products, ZonedDateTime recordTimestamp) {
        super(recordTimestamp);
        this.cartId = cartId;
        this.abandonedTimestamp = abandonedTimestamp;
        this.customer = customer;
        this.products = products;
    }

    /** Creates an {@link AbandonedOrder} using the provided details.
     * The ID is generated randomly.
     * */
    public AbandonedOrder(String abandonedTimestamp, OnlineCustomer customer, List<String> products, ZonedDateTime recordTimestamp) {
        this(UUID.randomUUID().toString(), abandonedTimestamp, customer, products, recordTimestamp);
    }

    public String getAbandonedTimestamp() {
        return abandonedTimestamp;
    }

    public String getCartId() {
        return cartId;
    }

    public OnlineCustomer getCustomer() {
        return customer;
    }

    public List<String> getProducts() {
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
        struct.put(SCHEMA.field("cartid"),      cartId);
        struct.put(SCHEMA.field("customer"),    customer.toStruct(false));
        struct.put(SCHEMA.field("products"),    products);
        struct.put(SCHEMA.field("abandonedtime"), abandonedTimestamp);
        return struct;
    }

    @Override
    public String toString() {
        return "AbandonedOrder [cartId=" + cartId + ", abandonedTimestamp=" + abandonedTimestamp + ", customer=" + customer + ", products="
                + Arrays.toString(products.toArray()) + "]";
    }
}
