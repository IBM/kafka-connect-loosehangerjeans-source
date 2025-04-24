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

/**
 * Information about a transaction.
 * 
 * A commercial website has to perform financial transactions with its bank.
 * Each transaction allows to transfer money in several steps instead of a single huge transfer. 
 * Each step of the transaction generates an event containing the unique identifier of the transaction, 
 * the transmitted amount of money, a timestamp and the state of the transaction that can be one of STARTED, PROCESSING or COMPLETED.
 * 
 * Assuming that valid transactions are composed of four transaction event with states STARTED, PROCESSING, PROCESSING and COMPLETED
 * respectively, it is possible to use the detect pattern node of IBM Event Processing to detect if a transaction does not yield
 * an event with a state COMPLETED in a given timeframe.
 */
public class Transaction extends LoosehangerData {

    public static final String PARTITION = "transaction";

    /** Unique ID for this event. */
    private String id;

    /** The state of the transaction. */
    private String state;

    /** The amount of the transaction. */
    private Double amount;

    /** Time that the event was recorded. */
    private String timestamp;

    /** Schema for the events - all fields are required. */
    private static final Schema SCHEMA = SchemaBuilder.struct()
            .name("transaction")
            .version(1)
                .field("id",          Schema.STRING_SCHEMA)
                .field("state",       Schema.STRING_SCHEMA)
                .field("amount",      Schema.FLOAT64_SCHEMA)
                .field("timestamp",   Schema.STRING_SCHEMA)
            .build();

    public Transaction(String id, String state, Double amount, String timestamp, ZonedDateTime recordTimestamp) {
        super(recordTimestamp);

        this.id = id;
        this.state = state;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }

    public String getState() { return state; }

    public Double getAmount() { return amount; }

    public SourceRecord createSourceRecord(String topicName) {
        return super.createSourceRecord(topicName, PARTITION);
    }

    @Override
    protected String getKey() { return id; }

    @Override
    protected Schema getValueSchema() {
        return SCHEMA;
    }

    @Override
    protected Struct getValue() {
        Struct struct = new Struct(SCHEMA);
        struct.put(SCHEMA.field("id"),        id);
        struct.put(SCHEMA.field("state"),     state);
        struct.put(SCHEMA.field("amount"),    amount);
        struct.put(SCHEMA.field("timestamp"), timestamp);
        return struct;
    }
    @Override
    public String toString() {
        return "Transaction [id=" + id + ", state=" + state + ", amount=" + amount + ", timestamp=" + timestamp + "]";
    }
}
