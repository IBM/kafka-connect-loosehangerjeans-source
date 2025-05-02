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

package com.ibm.eventautomation.demos.loosehangerjeans.tasks;

import com.ibm.eventautomation.demos.loosehangerjeans.DatagenSourceConfig;
import com.ibm.eventautomation.demos.loosehangerjeans.data.AbandonedOrder;
import com.ibm.eventautomation.demos.loosehangerjeans.generators.AbandonedOrderGenerator;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.connect.source.SourceRecord;

import java.util.Queue;
import java.util.TimerTask;

/**
 * Timer task intended for repeated execution. Creates new
 * {@link AbandonedOrder} events at regular intervals
 */
public class AbandonedOrdersTask extends TimerTask {

    /** Helper class for generating AbandonedOrder events */
    private final AbandonedOrderGenerator abandonedOrderGenerator;

    /**
     * Queue of messages waiting to be delivered to Kafka.
     * Generated AbandonedOrder events will be added to this queue
     */
    private final Queue<SourceRecord> queue;

    /** Name of the topic to produce abandoned order events to. */
    private final String abandonedOrderTopicName;

    public AbandonedOrdersTask(AbstractConfig config, Queue<SourceRecord> queue) {
        this.abandonedOrderGenerator = new AbandonedOrderGenerator(config);
        this.queue = queue;
        this.abandonedOrderTopicName = config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_ABANDONEDORDERS);
    }

    @Override
    public void run() {
        // Generate a random abandoned order event.
        AbandonedOrder abandonedOrder = abandonedOrderGenerator.generate();
        SourceRecord rec = abandonedOrder.createSourceRecord(abandonedOrderTopicName);
        queue.add(rec);

        // Possibly duplicate the event.
        if (abandonedOrderGenerator.shouldDuplicate()) {
            queue.add(rec);
        }
    }
}
