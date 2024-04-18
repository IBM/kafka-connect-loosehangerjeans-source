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

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Information about a customer who makes an online order.
 */
public class OnlineCustomer extends Customer {

    /** Emails of the customer. */
    private final List<String> emails;

    /** Schema for the events - all fields are required. */
    public static final Schema SCHEMA = SchemaBuilder.struct()
            .name("onlinecustomer")
            .version(1)
            .field("id",        Schema.STRING_SCHEMA)
            .field("name",      Schema.STRING_SCHEMA)
            .field("emails",    SchemaBuilder.array(Schema.STRING_SCHEMA).build())
            .build();

    /** Creates a customer using the provided details. */
    public OnlineCustomer(String id, String name, List<String> emails) {
        super(id, name);
        this.emails = emails;
    }

    /**
     * Creates an object to represent the customer with the
     *  provided name and emails. Generates an uuid for the customer id.
     */
    public OnlineCustomer(String name, List<String> emails) {
        this(UUID.randomUUID().toString(), name, emails);
    }

    public List<String> getEmails() {
        return emails;
    }

    /** Creates a structure record to use in a Kafka event. */
    public Struct toStruct() {
        Struct struct = new Struct(SCHEMA);
        struct.put(SCHEMA.field("id"),      getId());
        struct.put(SCHEMA.field("name"),    getName());
        struct.put(SCHEMA.field("emails"),  emails);
        return struct;
    }

    @Override
    public String toString() {
        return "Customer [id=" + getId() + ", name=" + getName() + ", emails="  + Arrays.toString(emails.toArray()) + "]";
    }
}
