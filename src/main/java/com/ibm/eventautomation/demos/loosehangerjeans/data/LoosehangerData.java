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
    private long recordTimestampEpoch;

    protected LoosehangerData(ZonedDateTime timestamp) {
        this.recordTimestampEpoch = timestamp.toEpochSecond();
    }

    public SourceRecord createSourceRecord(String topicName, String origin) {
        final Integer topicPartition = null;
        return new SourceRecord(createSourcePartition(origin),
                                createSourceOffset(),
                                topicName, topicPartition,
                                Schema.STRING_SCHEMA, getKey(),
                                getValueSchema(), getValue(),
                                recordTimestampEpoch);
    }

    private Map<String, Object> createSourcePartition(String origin) {
        return Collections.singletonMap("partition", origin);
    }

    private Map<String, Object> createSourceOffset() {
        return Collections.singletonMap("offset", recordTimestampEpoch);
    }
}
