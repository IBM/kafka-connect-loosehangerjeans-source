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

import java.time.ZonedDateTime;
import java.util.UUID;

import org.apache.kafka.common.config.AbstractConfig;

import com.ibm.eventautomation.demos.loosehangerjeans.DatagenSourceConfig;
import com.ibm.eventautomation.demos.loosehangerjeans.data.SensorReading;
import com.ibm.eventautomation.demos.loosehangerjeans.utils.Generators;

/**
 * Generates a {@link SensorReading} event using randomly generated data
 *  for a single sensor.
 *
 * It will mostly generate sensor readings that are consistent with
 *  {@link SensorReadingGenerator}, but at random intervals it will start
 *  reporting high and increasing values for a random series of readings,
 *  before returning to normal values.
 *
 * Decisions for when to start generating high values are made
 *  independently for temperature and humidity. They may coincide or not.
 */
public class HighSensorReadingGenerator extends SensorReadingGenerator {

    /** sensor ID for the sensor that will report high values */
    private final static String SENSOR_ID = generateSensorId();

    /** minimum temperature used when reporting high temperatures */
    private final static double HIGH_TEMP_MIN = 23.0;
    /** maximum temperature used when reporting high temperatures */
    private final static double HIGH_TEMP_MAX = 40.0;
    /** maximum temperature increase between readings */
    private final static double MAX_TEMP_INCREASE = 1;

    /** minimum humidity percentage when reporting high humidities */
    private final static int HIGH_HUMIDITY_MIN = 56;
    /** maximum humidity percentage when reporting high humidities */
    private final static int HIGH_HUMIDITY_MAX = 75;
    /** maximum humidity increase between readings */
    private final static int MAX_HUMIDITY_INCREASE = 3;

    /** most recent temperature reading from the problem sensor */
    private double highTemperature = HIGH_TEMP_MIN;
    /** most recent humidity reading from the problem sensor */
    private int highHumidity = HIGH_HUMIDITY_MIN;

    /** number of sensor readings to generate before starting a series of high temperature readings */
    private int countdownToHighTemperatureSeries;
    /** number of sensor readings to generate before starting a series of high humidity readings */
    private int countdownToHighHumiditySeries;



    public HighSensorReadingGenerator(AbstractConfig config)
    {
        super(config.getInt(DatagenSourceConfig.CONFIG_TIMES_HIGHSENSORREADINGS),
              config.getInt(DatagenSourceConfig.CONFIG_DELAYS_SENSORREADINGS),
              config.getDouble(DatagenSourceConfig.CONFIG_DUPLICATE_SENSORREADINGS));

        resetHighTemperatureSeries();
        resetHighHumiditySeries();
    }


    @Override
    protected SensorReading generateEvent(ZonedDateTime timestamp) {
        countdownToHighHumiditySeries--;
        countdownToHighTemperatureSeries--;

        double temp = Generators.randomDouble(SensorReadingGenerator.TEMP_MIN, SensorReadingGenerator.TEMP_MAX);
        int humidity = Generators.randomInt(SensorReadingGenerator.HUMIDITY_MIN, SensorReadingGenerator.HUMIDITY_MAX);

        if (countdownToHighTemperatureSeries <= 0) {
            // generate a high temperature reading that is
            //  slightly higher than the previous reading
            temp = Generators.randomDouble(Math.max(highTemperature,
                                                    HIGH_TEMP_MIN),
                                           Math.min(highTemperature + MAX_TEMP_INCREASE,
                                                    HIGH_TEMP_MAX),
                                           false);
            highTemperature = temp;

            // should we continue to report high temperatures?
            if (shouldEndHighReadings()) {
                resetHighTemperatureSeries();
            }
        }

        if (countdownToHighHumiditySeries <= 0) {
            // generate a high humidity reading that is
            //  slightly higher than the previous reading
            humidity = Generators.randomInt(Math.max(highHumidity,
                                                     HIGH_HUMIDITY_MIN),
                                            Math.min(highHumidity + MAX_HUMIDITY_INCREASE,
                                                     HIGH_HUMIDITY_MAX));
            highHumidity = humidity;

            // should we continue to report high humidities?
            if (shouldEndHighReadings()) {
                resetHighHumiditySeries();
            }
        }

        return new SensorReading(UUID.randomUUID().toString(),
                                 formatTimestamp(timestamp),
                                 SENSOR_ID,
                                 temp,
                                 humidity,
                                 timestamp);
    }

    private static boolean shouldEndHighReadings() {
        return Generators.shouldDo(0.08);
    }

    private void resetHighTemperatureSeries() {
        countdownToHighTemperatureSeries = Generators.randomInt(50, 200);
        highTemperature = HIGH_TEMP_MIN;
    }

    private void resetHighHumiditySeries() {
        countdownToHighHumiditySeries = Generators.randomInt(50, 200);
        highHumidity = HIGH_HUMIDITY_MIN;
    }
}
