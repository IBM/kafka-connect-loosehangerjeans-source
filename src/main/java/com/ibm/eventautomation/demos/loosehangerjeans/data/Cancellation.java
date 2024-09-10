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
import java.util.UUID;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;

/**
 * Represents an event capturing a customer cancelling an order.
 */
public class Cancellation extends LoosehangerData {

    /** unique ID for this event */
    private String id;

    /** time that the event was recorded */
    private String timestamp;

    /** reference to the order that was cancelled */
    private Order order;

    /** reason that the customer gave for cancelling the order */
    private String reason;

    /** schema for the events - all fields are required */
    private static final Schema SCHEMA = SchemaBuilder.struct()
        .name("cancellation")
        .version(1)
            .field("id",         Schema.STRING_SCHEMA)
            .field("orderid",    Schema.STRING_SCHEMA)
            .field("canceltime", Schema.STRING_SCHEMA)
            .field("reason",     Schema.STRING_SCHEMA)
        .build();

    public Cancellation(Order order, String reason, String timestamp, ZonedDateTime recordTimestamp) {
        super(recordTimestamp);

        this.id = UUID.randomUUID().toString();
        this.order = order;
        this.reason = reason;
        this.timestamp = timestamp;
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
        struct.put(SCHEMA.field("id"),         id);
        struct.put(SCHEMA.field("orderid"),    order.getId());
        struct.put(SCHEMA.field("canceltime"), timestamp);
        struct.put(SCHEMA.field("reason"),     reason);
        return struct;
    }

    @Override
    public String toString() {
        return "Cancellation [id=" + id + ", timestamp=" + timestamp + ", order=" + order + ", reason=" + reason + "]";
    }
}
