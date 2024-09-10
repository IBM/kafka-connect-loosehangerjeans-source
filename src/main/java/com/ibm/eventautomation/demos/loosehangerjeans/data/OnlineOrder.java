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

/**
 * Represents an event for a new online order that has been placed by
 * a customer for several products.
 * The event also includes a shipping and billing address which may
 * be identical.
 */
public class OnlineOrder extends LoosehangerData {

    public static final String PARTITION = "onlineorder";

    /** Unique ID for this order. */
    private final String id;

    /** Time that the order was placed. */
    private final String timestamp;

    /** Details of the customer who made the order. */
    private final OnlineCustomer customer;

    /** Descriptions of the ordered products. */
    private final List<String> products;

    /** Details about the address used for the order. */
    private final OnlineAddress address;

    /** Schema for the events - all fields are required. */
    private static final Schema SCHEMA = SchemaBuilder.struct()
            .name("onlineorder")
            .version(1)
            .field("id",          Schema.STRING_SCHEMA)
            .field("customer",    OnlineCustomer.SCHEMA)
            .field("products",    SchemaBuilder.array(Schema.STRING_SCHEMA).build())
            .field("address",     OnlineAddress.SCHEMA)
            .field("ordertime",   Schema.STRING_SCHEMA)
            .build();

    /** Creates an {@link OnlineOrder} using the provided details. */
    public OnlineOrder(String id, String timestamp, OnlineCustomer customer, List<String> products, OnlineAddress address, ZonedDateTime recordTimestamp) {
        super(recordTimestamp);

        this.id = id;
        this.timestamp = timestamp;
        this.customer = customer;
        this.products = products;
        this.address = address;
    }

    /** Creates an {@link OnlineOrder} using the provided details.
     * The ID is generated randomly.
     * */
    public OnlineOrder(String timestamp, OnlineCustomer customer, List<String> products, OnlineAddress address, ZonedDateTime recordTimestamp) {
        this(UUID.randomUUID().toString(), timestamp, customer, products, address, recordTimestamp);
    }

    public String getId() {
        return id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public OnlineCustomer getCustomer() {
        return customer;
    }

    public List<String> getProducts() {
        return products;
    }

    public OnlineAddress getAddress() {
        return address;
    }

    public SourceRecord createSourceRecord(String topicName) {
        return super.createSourceRecord(topicName, PARTITION);
    }

    @Override
    protected String getKey() {
        return id;
    }

    @Override
    protected Schema getValueSchema() {
        return SCHEMA;
    }

    @Override
    protected Struct getValue() {
        Struct struct = new Struct(SCHEMA);
        struct.put(SCHEMA.field("id"),          id);
        struct.put(SCHEMA.field("customer"),    customer.toStruct());
        struct.put(SCHEMA.field("products"),    products);
        struct.put(SCHEMA.field("address"),     address.toStruct());
        struct.put(SCHEMA.field("ordertime"),   timestamp);
        return struct;
    }

    @Override
    public String toString() {
        return "Order [id=" + id + ", timestamp=" + timestamp + ", customer=" + customer + ", products="
                + Arrays.toString(products.toArray()) + ", address=" + address + "]";
    }
}
