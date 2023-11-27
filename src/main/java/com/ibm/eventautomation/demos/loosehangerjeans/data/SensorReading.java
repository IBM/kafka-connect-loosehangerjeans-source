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
 * Represents an event with readings captured from an IoT sensor.
 */
public class SensorReading {

    /** unique id for the sensor that captured the reading */
    private String sensorid;

    /** time that the event was recorded */
    private String timestamp;

    /** temperature reading in celsius */
    private double temperature;

    /** humidity reading as a percentage */
    private int humidity;

    /** schema for the events - all fields are required */
    private static final Schema SCHEMA = SchemaBuilder.struct()
        .name("sensorreading")
            .field("sensortime",  Schema.STRING_SCHEMA)
            .field("sensorid",    Schema.STRING_SCHEMA)
            .field("temperature", Schema.FLOAT64_SCHEMA)
            .field("humidity",    Schema.INT32_SCHEMA)
        .build();

    public SensorReading(String id, String timestamp, String sensor, double temp, int humidity) {
        this.sensorid = id;
        this.timestamp = timestamp;
        this.sensorid = sensor;
        this.temperature = temp;
        this.humidity = humidity;
    }

    public SourceRecord createSourceRecord(String topicname) {
        Struct struct = new Struct(SCHEMA);
        struct.put(SCHEMA.field("sensorid"),    sensorid);
        struct.put(SCHEMA.field("sensortime"),  timestamp);
        struct.put(SCHEMA.field("temperature"), temperature);
        struct.put(SCHEMA.field("humidity"),    humidity);

        return new SourceRecord(createSourcePartition(),
                                createSourceOffset(),
                                topicname,
                                Schema.STRING_SCHEMA, sensorid,
                                SCHEMA,
                                struct);
    }

    private Map<String, Object> createSourcePartition() {
        return Collections.singletonMap("partition", "sensor");
    }
    private Map<String, Object> createSourceOffset() {
        return Collections.singletonMap("offset", timestamp);
    }

    @Override
    public String toString() {
        return "SensorReading [timestamp=" + timestamp + ", sensorid=" + sensorid + ", temperature=" + temperature
                + ", humidity=" + humidity + "]";
    }
}
