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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;

import com.ibm.eventautomation.demos.loosehangerjeans.utils.Generators;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

/**
 * Represents an event capturing an employee using their id badge
 *  to go through a door.
 */
public class BadgeIn {

    /** unique ID for this event */
    private String recordId;

    /** time that the event was recorded */
    private String timestamp;

    /** location code for the door that the event was recorded by */
    private String doorLocation;

    /** username for the employee whose badge was recorded */
    private String employee;

    /** schema for the events - all fields are required */
    private static final Schema SCHEMA = SchemaBuilder.struct()
        .name("badgein")
        .version(1)
            .field("recordid",  Schema.STRING_SCHEMA)
            .field("door",      Schema.STRING_SCHEMA)
            .field("employee",  Schema.STRING_SCHEMA)
            .field("badgetime", Schema.STRING_SCHEMA)
        .build();

    public BadgeIn(String id, String timestamp, String door, String employee) {
        this.recordId = id;
        this.timestamp = timestamp;
        this.doorLocation = door;
        this.employee = employee;
    }

    public SourceRecord createSourceRecord(String topicName) {
        Struct struct = new Struct(SCHEMA);
        struct.put(SCHEMA.field("recordid"),  recordId);
        struct.put(SCHEMA.field("door"),      doorLocation);
        struct.put(SCHEMA.field("employee"),  employee);
        struct.put(SCHEMA.field("badgetime"), timestamp);

        return new SourceRecord(createSourcePartition(),
                                createSourceOffset(),
                                topicName,
                                Schema.STRING_SCHEMA,
                                recordId,
                                SCHEMA,
                                struct);
    }

    public SourceRecord createSourceRecordWithTimestamp(String topicName, DateTimeFormatter dateTimeFormatter, int maxOffset) {
        Struct struct = new Struct(SCHEMA);
        struct.put(SCHEMA.field("recordid"),  recordId);
        struct.put(SCHEMA.field("door"),      doorLocation);
        struct.put(SCHEMA.field("employee"),  employee);
        struct.put(SCHEMA.field("badgetime"), timestamp);

        // Retrieve the date time used in the message payload.
        LocalDateTime localDateTime = LocalDateTime.parse(timestamp, dateTimeFormatter);
        // Compute the date time to be used to produce the Kafka message.
        // A delay is added so that the timestamp used to produce the message in Kafka is later than
        // the timestamp in the message payload.
        ZonedDateTime zonedDateTime = ZonedDateTime.of(Generators.addMaxOffset(localDateTime, maxOffset), ZoneId.systemDefault());
        // Get the corresponding timestamp in milliseconds.
        Long timestampMillis = zonedDateTime.toInstant().toEpochMilli();

        return new SourceRecord(createSourcePartition(),
                createSourceOffset(),
                topicName,
                0, // partition
                Schema.STRING_SCHEMA,
                recordId,
                SCHEMA,
                struct,
                timestampMillis);
    }

    private Map<String, Object> createSourcePartition() {
        return Collections.singletonMap("partition", "badgein");
    }
    private Map<String, Object> createSourceOffset() {
        return Collections.singletonMap("offset", timestamp);
    }

    @Override
    public String toString() {
        return "BadgeIn [recordId=" + recordId + ", timestamp=" + timestamp + ", doorLocation=" + doorLocation
                + ", employee=" + employee + "]";
    }
}
