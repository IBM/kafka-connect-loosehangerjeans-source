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
import org.apache.kafka.connect.source.SourceRecord;

/**
 * Represents an event capturing an employee using their id badge
 *  to go through a door.
 */
public class BadgeIn extends LoosehangerData {

    public static final String PARTITION = "badgein";

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

    public BadgeIn(String id, String timestamp, String door, String employee, ZonedDateTime recordTimestamp) {
        super(recordTimestamp);

        this.recordId = id;
        this.timestamp = timestamp;
        this.doorLocation = door;
        this.employee = employee;
    }

    public SourceRecord createSourceRecord(String topicName) {
        return super.createSourceRecord(topicName, PARTITION);
    }

    @Override
    protected String getKey() {
        return recordId;
    }

    @Override
    protected Schema getValueSchema() {
        return SCHEMA;
    }

    @Override
    protected Struct getValue() {
        Struct struct = new Struct(SCHEMA);
        struct.put(SCHEMA.field("recordid"),  recordId);
        struct.put(SCHEMA.field("door"),      doorLocation);
        struct.put(SCHEMA.field("employee"),  employee);
        struct.put(SCHEMA.field("badgetime"), timestamp);
        return struct;
    }


    @Override
    public String toString() {
        return "BadgeIn [recordId=" + recordId + ", timestamp=" + timestamp + ", doorLocation=" + doorLocation
                + ", employee=" + employee + "]";
    }
}
