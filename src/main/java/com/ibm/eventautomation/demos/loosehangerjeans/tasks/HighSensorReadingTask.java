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
package com.ibm.eventautomation.demos.loosehangerjeans.tasks;

import java.util.Queue;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.connect.source.SourceRecord;

import com.ibm.eventautomation.demos.loosehangerjeans.generators.HighSensorReadingGenerator;

/**
 * Timer task intended for repeated execution. Creates new
 *  {@link SensorReading} events at regular intervals.
 */
public class HighSensorReadingTask extends SensorReadingTask {

    public HighSensorReadingTask(AbstractConfig config, Queue<SourceRecord> queue) {
        super(new HighSensorReadingGenerator(config), queue, config);
    }
}
