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
package com.ibm.eventautomation.demos.loosehangerjeans.data;

import java.time.ZonedDateTime;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.header.ConnectHeaders;

/**
 * Represents an event for a new order that has been placed by
 *  a customer for some quantity of a single product.
 *
 * The price is included in the event, to represent a retailer
 *  with a dynamic pricing algorithm.
 */
public class Order extends LoosehangerData {

    /** unique ID for this order */
    private String id;

    /** time that the order was placed */
    private String timestamp;

    /** details of the customer who made the order */
    private Customer customer;

    /** description of the product ordered */
    private String description;

    /** unit price for the product ordered */
    private double unitPrice;

    /** number of items ordered */
    private int quantity;

    /** location of the customer */
    private String region;

    /** country code of the order **/
    private String countryCode;

    private String priority;

    private String storeId;

    /** schema for the events - all fields are required */
    private static final Schema SCHEMA = SchemaBuilder.struct()
        .name("order")
        .version(1)
            .field("id",          Schema.STRING_SCHEMA)
            .field("customer",    Schema.STRING_SCHEMA)
            .field("customerid",  Schema.STRING_SCHEMA)
            .field("description", Schema.STRING_SCHEMA)
            .field("price",       Schema.FLOAT64_SCHEMA)
            .field("quantity",    Schema.INT32_SCHEMA)
            .field("region",      Schema.STRING_SCHEMA)
            .field("ordertime",   Schema.STRING_SCHEMA)
        .build();

    public Order(String id, String timestamp, Customer customer, String description, double unitPrice, int quantity, String region, ZonedDateTime recordTimestamp, String countryCode, String priority, String storeId) {
        super(recordTimestamp);

        this.id = id;
        this.timestamp = timestamp;
        this.customer = customer;
        this.description = description;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.region = region;
        this.countryCode = countryCode;
        this.priority = priority;
        this.storeId = storeId;
    }

    @Override
    protected String getKey() {
        return countryCode;
    }

    @Override
    public ConnectHeaders getHeaders() {
        ConnectHeaders headers = new ConnectHeaders();
        headers.addString("priority", priority);
        headers.addString("storeid", storeId);
        return headers;
    }

    @Override
    protected Schema getValueSchema() {
        return SCHEMA;
    }

    @Override
    protected Struct getValue() {
        Struct struct = new Struct(SCHEMA);
        struct.put(SCHEMA.field("id"),          id);
        struct.put(SCHEMA.field("customer"),    customer.getName());
        struct.put(SCHEMA.field("customerid"),  customer.getId());
        struct.put(SCHEMA.field("description"), description);
        struct.put(SCHEMA.field("price"),       unitPrice);
        struct.put(SCHEMA.field("quantity"),    quantity);
        struct.put(SCHEMA.field("region"),      region);
        struct.put(SCHEMA.field("ordertime"),   timestamp);
        return struct;
    }


    public String getId() {
        return id;
    }
    public String getDescription() {
        return description;
    }
    public double getUnitPrice() {
        return unitPrice;
    }
    public String getRegion() {
        return region;
    }
    public Customer getCustomer() {
        return customer;
    }
    public String getCountryCode() {
        return countryCode;
    }
    public String getPriority() {
        return priority;
    }
    public String getStoreId() {
        return storeId;
    }

    @Override
    public String toString() {
        return "Order [id=" + id + ", timestamp=" + timestamp + ", customer=" + customer + ", description="
                + description + ", unitPrice=" + unitPrice + ", quantity=" + quantity + ", region=" + region + "]";
    }

}
