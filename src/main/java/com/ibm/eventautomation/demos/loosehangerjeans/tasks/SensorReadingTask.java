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
import java.util.TimerTask;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.connect.source.SourceRecord;

import com.ibm.eventautomation.demos.loosehangerjeans.DatagenSourceConfig;
import com.ibm.eventautomation.demos.loosehangerjeans.data.SensorReading;
import com.ibm.eventautomation.demos.loosehangerjeans.generators.SensorReadingGenerator;
import com.ibm.eventautomation.demos.loosehangerjeans.utils.Generators;

/**
 * Timer task intended for repeated execution. Creates new
 *  {@link SensorReading} events at regular intervals.
 */
public class SensorReadingTask extends TimerTask {

    /** Helper class for generating SensorReading events. */
    private SensorReadingGenerator generator;

    /**
     * Queue of messages waiting to be delivered to Kafka.
     *  Generated SensorReading events will be added to this queue.
     */
    private Queue<SourceRecord> queue;

    /** Name of the topic to produce sensor readings to. */
    private String topicname;


    public SensorReadingTask(AbstractConfig config, Queue<SourceRecord> queue) {
        this.generator = new SensorReadingGenerator(config);
        this.queue = queue;
        this.topicname = config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_SENSORREADINGS);
    }


    @Override
    public void run() {
        SourceRecord rec = generator.generate().createSourceRecord(topicname);
        queue.add(rec);

        if (generator.shouldDuplicate()) {
            queue.add(rec);
        }
    }
}
