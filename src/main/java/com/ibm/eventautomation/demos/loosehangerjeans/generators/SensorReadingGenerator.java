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
package com.ibm.eventautomation.demos.loosehangerjeans.generators;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.kafka.common.config.AbstractConfig;

import com.ibm.eventautomation.demos.loosehangerjeans.DatagenSourceConfig;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Locations;
import com.ibm.eventautomation.demos.loosehangerjeans.data.SensorReading;
import com.ibm.eventautomation.demos.loosehangerjeans.utils.Generators;

/**
 * Generates a {@link SensorReading} event using randomly generated data.
 */
public class SensorReadingGenerator extends Generator<SensorReading> {

    private static final String TIMESTAMP_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";

    /** formatter for event timestamps */
    private final DateTimeFormatter timestampFormatter;

    /**
     * Generator can simulate a delay in events being produced
     *  to Kafka by putting a timestamp in the message payload
     *  that is earlier than the current time.
     *
     * The amount of the delay will be randomized to simulate
     *  a delay due to network or infrastructure reasons.
     *
     * This value is the maximum delay (in seconds) that it will
     *  use. (Setting this to 0 will mean all events are
     *  produced with the current time).
     */
    private final int MAX_DELAY_SECS;

    /** minimum temperature for randomly selected temperature reading */
    private final static double TEMP_MIN = 19.5;
    /** maximum temperature for randomly selected temperature reading */
    private final static double TEMP_MAX = 23.5;

    /** minimum humidity percentage for randomly selected humidity reading */
    private final static int HUMIDITY_MIN = 41;
    /** maximum humidity percentage for randomly selected humidity reading */
    private final static int HUMIDITY_MAX = 59;


    public SensorReadingGenerator(AbstractConfig config)
    {
        super(config.getInt(DatagenSourceConfig.CONFIG_TIMES_SENSORREADINGS));

        this.timestampFormatter = DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT).withZone(ZoneId.systemDefault());
        this.MAX_DELAY_SECS = config.getInt(DatagenSourceConfig.CONFIG_DELAYS_SENSORREADINGS);
    }

    private String generateSensorId() {
        int floor  = Generators.randomInt(0, 3);
        int sensor = Generators.randomInt(10, 30);
        return Generators.randomItem(Locations.BUILDINGS) + "-" +
               floor + "-" +
               sensor;
    }

    public SensorReading generate() {
        return generateEvent(Generators.nowWithRandomOffset(MAX_DELAY_SECS));
    }

    @Override
    protected SensorReading generateEvent(ZonedDateTime timestamp) {
        return new SensorReading(UUID.randomUUID().toString(),
                                 timestampFormatter.format(timestamp),
                                 generateSensorId(),
                                 Generators.randomDouble(TEMP_MIN, TEMP_MAX),
                                 Generators.randomInt(HUMIDITY_MIN, HUMIDITY_MAX));
    }
}
