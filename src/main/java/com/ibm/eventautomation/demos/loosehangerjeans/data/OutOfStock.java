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

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * Represents an event for a product that runs out-of-stock.
 */
public class OutOfStock {

    /** Unique ID for the out-of-stock event. */
    private final String id;

    /** Time that the product was out-of-stock.
     * Number of milliseconds from the unix epoch, 1 January 1970 00:00:00.000 UTC.
     */
    private final long timestamp;

    /**  Product that is out-of-stock. */
    private final Product product;

    /** Estimated restocking date.
     * Number of days from the unix epoch, 1 January 1970 00:00:00.000 UTC.
     */
    private final int restockingDate;

    /** Schema for the events - all fields are required. */
    private static final Schema SCHEMA = SchemaBuilder.struct()
            .name("out-of-stock")
            .version(1)
            .field("id",                        Schema.STRING_SCHEMA)
            .field("product",                   Product.SCHEMA)
            .field("restockingdate",            Schema.INT32_SCHEMA)
            .field("outofstocktime",            Schema.INT64_SCHEMA)
            .build();

    /** Creates an {@link OutOfStock} object using the provided details. */
    public OutOfStock(String id, long timestamp, Product product, int restockingDate) {
        this.id = id;
        this.timestamp = timestamp;
        this.product = product;
        this.restockingDate = restockingDate;
    }

    /** Creates an {@link OutOfStock} object using the provided details.
     * The ID is generated randomly.
     * */
    public OutOfStock(long timestamp, Product product, int restockingDate) {
        this(UUID.randomUUID().toString(), timestamp, product, restockingDate);
    }

    public String getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Product getProduct() {
        return product;
    }

    public int getRestockingDate() {
        return restockingDate;
    }

    /** Creates a source record to pass to Kafka Connect for storage in Kafka. */
    public SourceRecord createSourceRecord(String topicname) {
        Struct struct = new Struct(SCHEMA);
        struct.put(SCHEMA.field("id"),                  id);
        struct.put(SCHEMA.field("product"),             product.toStruct());
        struct.put(SCHEMA.field("restockingdate"),      restockingDate);
        struct.put(SCHEMA.field("outofstocktime"),      timestamp);

        return new SourceRecord(createSourcePartition(),
                createSourceOffset(),
                topicname,
                Schema.STRING_SCHEMA, id,
                SCHEMA,
                struct);
    }

    private Map<String, Object> createSourcePartition() {
        return Collections.singletonMap("partition", "outofstock");
    }

    private Map<String, Object> createSourceOffset() {
        return Collections.singletonMap("offset", timestamp);
    }

    @Override
    public String toString() {
        return "OutOfStock [id=" + id + ", timestamp=" + DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochMilli(timestamp))
                + ", product=" + product + ", restockingDate="
                + DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.ofEpochDay(restockingDate)) + "]";
    }
}
