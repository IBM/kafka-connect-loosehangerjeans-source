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
import java.util.Collections;
import java.util.Map;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

/**
 * Data that will be submitted to Kafka Connect for producing
 *  to a Kafka topic.
 *
 * An instance of this class represents a single source record.
 */
public abstract class LoosehangerData {

    /** Message key. */
    protected abstract String getKey();

    /** Schema for the message value. */
    protected abstract Schema getValueSchema();
    /** Message value. */
    protected abstract Struct getValue();

    /** Message timestamp */
    private ZonedDateTime recordTimestamp;

    protected LoosehangerData(ZonedDateTime timestamp) {
        this.recordTimestamp = timestamp;
    }

    public SourceRecord createSourceRecord(String topicName, String origin) {
        final Integer topicPartition = null;
        final long timestamp = recordTimestamp.toEpochSecond() * 1000L;
        return new SourceRecord(partition(origin),
                                Collections.singletonMap("offset", timestamp),
                                topicName, topicPartition,
                                Schema.STRING_SCHEMA, getKey(),
                                getValueSchema(), getValue(),
                                timestamp);
    }

    /**
     * Timestamp that will be applied to the SourceRecord delivered
     *  to Kafka.
     */
    public ZonedDateTime recordTimestamp() {
        return recordTimestamp;
    }

    public static Map<String, Object> partition(String origin) {
        return Collections.singletonMap("partition", origin);
    }
}
