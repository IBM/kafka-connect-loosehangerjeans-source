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

import java.util.Queue;
import java.util.TimerTask;

import com.ibm.eventautomation.demos.loosehangerjeans.DatagenSourceConfig;
import com.ibm.eventautomation.demos.loosehangerjeans.generators.TransactionGenerator;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.connect.source.SourceRecord;

public class TransactionTask extends TimerTask {

    /** Helper class for generating Transaction events. */
    private TransactionGenerator generator;

    /**
     * Queue of messages waiting to be delivered to Kafka.
     *  Generated Transaction events will be added to this queue.
     */
    private Queue<SourceRecord> queue;

	/** Name of the topic to produce transaction to. */
    private String topicname;


    public TransactionTask(AbstractConfig config, Queue<SourceRecord> queue) {
        this(new TransactionGenerator(config), queue, config);
    }

    protected TransactionTask(TransactionGenerator transactionGenerator, Queue<SourceRecord> queue, AbstractConfig config) {
        this.generator = transactionGenerator;
        this.queue = queue;
        this.topicname = config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_TRANSACTIONS);
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
