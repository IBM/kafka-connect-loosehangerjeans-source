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

import java.util.Collections;
import java.util.Map;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

/**
 * Represents an event capturing a new customer registering with
 *  the company website.
 */
public class NewCustomer {

    /** time that the event was recorded */
    private String timestamp;

    /** details of the customer */
    private Customer customer;

    /** schema for the events - all fields are required */
    private static final Schema SCHEMA = SchemaBuilder.struct()
        .name("customer")
            .field("customerid",   Schema.STRING_SCHEMA)
            .field("customername", Schema.STRING_SCHEMA)
            .field("registered",   Schema.STRING_SCHEMA)
        .build();

    public NewCustomer(String timestamp, Customer customer) {
        this.timestamp = timestamp;
        this.customer = customer;
    }

    public SourceRecord createSourceRecord(String topicname, String origin) {
        Struct struct = new Struct(SCHEMA);
        struct.put(SCHEMA.field("customerid"),   customer.getId());
        struct.put(SCHEMA.field("customername"), customer.getName());
        struct.put(SCHEMA.field("registered"),   timestamp);

        return new SourceRecord(createSourcePartition(origin),
                                createSourceOffset(),
                                topicname,
                                Schema.STRING_SCHEMA, customer.getId(),
                                SCHEMA,
                                struct);
    }

    private Map<String, Object> createSourcePartition(String origin) {
        return Collections.singletonMap("partition", origin);
    }
    private Map<String, Object> createSourceOffset() {
        return Collections.singletonMap("offset", timestamp);
    }

    public Customer getCustomer() {
        return customer;
    }

    @Override
    public String toString() {
        return "NewCustomer [timestamp=" + timestamp + ", customer=" + customer + "]";
    }
}
