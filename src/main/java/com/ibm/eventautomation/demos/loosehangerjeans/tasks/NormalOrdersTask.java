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
import java.util.Timer;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.connect.source.SourceRecord;

import com.ibm.eventautomation.demos.loosehangerjeans.DatagenSourceConfig;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Cancellation;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Order;
import com.ibm.eventautomation.demos.loosehangerjeans.generators.CancellationGenerator;
import com.ibm.eventautomation.demos.loosehangerjeans.generators.OrderGenerator;
import com.ibm.eventautomation.demos.loosehangerjeans.utils.Generators;

/**
 * Timer task intended for repeated execution. Creates new
 *  {@link Order} and {@link Cancellation} events at regular intervals.
 *
 * Described as "normal" orders as these are randomly generated
 *  orders intended to fill the Kafka topic with noise, to make
 *  it harder to find the "interesting" orders generated by
 *  {@link SuspiciousOrdersTask}. As such, this task is run
 *  much more frequently, to create a haystack to search for the
 *  suspicious order needles in!
 */
public class NormalOrdersTask extends DatagenTimerTask {

    /** Identifies the task that generated the orders and cancellations */
    private static final String ORIGIN = NormalOrdersTask.class.getName();

    /**
     * Proportion of {@link Order} events that should have an associated
     *  {@link Cancellation} event generated.
     *
     * Between 0.0 and 1.0
     *
     * Set this to 0.0 for no cancellation events.
     * Set this to 1.0 for every order to have a corresponding cancellation.
     */
    private double cancellationRatio;

    /**
     * minimum time to wait after creating an {@link Order} before
     *  generating the corresponding {@link Cancellation}
     */
    private int cancellationMinDelay;
    /**
     * maximum time to wait after creating an {@link Order} before
     *  generating the corresponding {@link Cancellation}
     */
    private int cancellationMaxDelay;

    /** minimum number of items to order */
    private int minItems;
    /** maximum number of items to order */
    private int maxItems;

    /**
     * Generator can simulate a source of events that offers
     *  at-least-once delivery semantics by occasionally
     *  producing duplicate messages.
     *
     * This value is the proportion of events that will be
     *  duplicated, between 0.0 and 1.0.
     *
     * Setting this to 0 will mean no events are duplicated.
     * Setting this to 1 will mean every message is produced twice.
     */
    private double duplicatesRatio;

    /** Name of the topic to produce order events to. */
    private String topicname;


    public NormalOrdersTask(AbstractConfig config,
                            OrderGenerator orderGenerator,
                            CancellationGenerator cancellationGenerator,
                            Queue<SourceRecord> queue,
                            Timer timer)
    {
        super(orderGenerator, cancellationGenerator, queue, timer, config);

        cancellationRatio = config.getDouble(DatagenSourceConfig.CONFIG_CANCELLATIONS_RATIO);
        cancellationMinDelay = config.getInt(DatagenSourceConfig.CONFIG_CANCELLATIONS_MIN_DELAY);
        cancellationMaxDelay = config.getInt(DatagenSourceConfig.CONFIG_CANCELLATIONS_MAX_DELAY);

        minItems = config.getInt(DatagenSourceConfig.CONFIG_ORDERS_SMALL_MIN);
        maxItems = config.getInt(DatagenSourceConfig.CONFIG_ORDERS_LARGE_MAX);

        this.duplicatesRatio = config.getDouble(DatagenSourceConfig.CONFIG_DUPLICATE_ORDERS);
        
        this.topicname = config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_ORDERS);
    }


    @Override
    public void run() {
        // generate a random order
        Order order = orderGenerator.generate(minItems, maxItems);
        SourceRecord rec = order.createSourceRecord(topicname, ORIGIN);
        queue.add(rec);

        // possibly duplicate it
        if (Generators.shouldDo(duplicatesRatio)) {
            queue.add(rec);
        }

        // sometimes cancel it
        if (Generators.shouldDo(cancellationRatio)) {
            cancelOrder(order, ORIGIN,
                        cancellationMinDelay,
                        cancellationMaxDelay);
        }
    }
}
