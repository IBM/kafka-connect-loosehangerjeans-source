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

import com.ibm.eventautomation.demos.loosehangerjeans.DatagenSourceConfig;
import com.ibm.eventautomation.demos.loosehangerjeans.data.AbandonedShoppingCart;
import com.ibm.eventautomation.demos.loosehangerjeans.generators.AbandonedShoppingCartGenerator;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.connect.source.SourceRecord;

import java.util.Queue;
import java.util.TimerTask;

/**
 * Timer task intended for repeated execution. Creates new
 * {@link AbandonedShoppingCart} events at regular intervals
 */
public class AbandonedShoppingCartsTask extends TimerTask {

    /** Helper class for generating AbandonedShoppingCart events */
    private final AbandonedShoppingCartGenerator abandonedShoppingCartGenerator;

    /**
     * Queue of messages waiting to be delivered to Kafka.
     * Generated AbandonedShoppingCart events will be added to this queue
     */
    private final Queue<SourceRecord> queue;

    /** Name of the topic to produce abandoned shopping cart events to. */
    private final String abandonedCartTopicName;

    public AbandonedShoppingCartsTask(AbstractConfig config,
                                      Queue<SourceRecord> queue) {
        this.abandonedShoppingCartGenerator = new AbandonedShoppingCartGenerator(config);
        this.queue = queue;

        this.abandonedCartTopicName = config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_ABANDONEDSHOPPINGCARTS);
    }

    @Override
    public void run() {
        // Generate a random abandoned shopping cart event.
        AbandonedShoppingCart abandonedCart = abandonedShoppingCartGenerator.generate();
        SourceRecord rec = abandonedCart.createSourceRecord(abandonedCartTopicName);
        queue.add(rec);

        // Possibly duplicate the event.
        if (abandonedShoppingCartGenerator.shouldDuplicate()) {
            queue.add(rec);
        }
    }
}
