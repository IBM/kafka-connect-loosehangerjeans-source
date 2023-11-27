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
 * Represents an event recording a bulk movement of stock into
 *  a warehouse.
 */
public class StockMovement {

    /** unique ID for this event */
    private String movementid;

    /** time that the event was recorded */
    private String timestamp;

    /** code for the warehouse that received the goods */
    private String warehouse;

    /** description of the item that was received */
    private String productDescription;

    /** number of items that were received */
    private int quantity;

    /** schema for the events - all fields are required */
    private static final Schema SCHEMA = SchemaBuilder.struct()
        .name("stockmovement")
            .field("movementid", Schema.STRING_SCHEMA)
            .field("warehouse",  Schema.STRING_SCHEMA)
            .field("product",    Schema.STRING_SCHEMA)
            .field("quantity",   Schema.INT32_SCHEMA)
            .field("updatetime", Schema.STRING_SCHEMA)
        .build();

    public StockMovement(String id, String timestamp, String warehouse, String product, int quantity) {
        this.movementid = id;
        this.timestamp = timestamp;
        this.warehouse = warehouse;
        this.productDescription = product;
        this.quantity = quantity;
    }

    public SourceRecord createSourceRecord(String topicname) {
        Struct struct = new Struct(SCHEMA);
        struct.put(SCHEMA.field("movementid"), movementid);
        struct.put(SCHEMA.field("warehouse"),  warehouse);
        struct.put(SCHEMA.field("product"),    productDescription);
        struct.put(SCHEMA.field("quantity"),   quantity);
        struct.put(SCHEMA.field("updatetime"), timestamp);

        return new SourceRecord(createSourcePartition(),
                                createSourceOffset(),
                                topicname,
                                Schema.STRING_SCHEMA, movementid,
                                SCHEMA,
                                struct);
    }

    private Map<String, Object> createSourcePartition() {
        return Collections.singletonMap("partition", "stock");
    }
    private Map<String, Object> createSourceOffset() {
        return Collections.singletonMap("offset", timestamp);
    }

    @Override
    public String toString() {
        return "StockMovement [movementid=" + movementid + ", timestamp=" + timestamp + ", warehouse=" + warehouse
                + ", productDescription=" + productDescription + ", quantity=" + quantity + "]";
    }
}
