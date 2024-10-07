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
import java.util.TimerTask;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.connect.source.SourceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.eventautomation.demos.loosehangerjeans.DatagenSourceConfig;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Cancellation;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Customer;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Order;
import com.ibm.eventautomation.demos.loosehangerjeans.generators.CancellationGenerator;
import com.ibm.eventautomation.demos.loosehangerjeans.generators.OrderGenerator;
import com.ibm.eventautomation.demos.loosehangerjeans.utils.Generators;

/**
 * Timer task intended for repeated execution. Creates new
 *  Kafka events at regular intervals.
 */
public abstract class DatagenTimerTask extends TimerTask {

    private final Logger log = LoggerFactory.getLogger(DatagenTimerTask.class);

    /** Helper class for generating {@link Order} objects. */
    protected final OrderGenerator orderGenerator;
    /** Helper class for generating {@link Cancellation} objects */
    protected final CancellationGenerator cancellationGenerator;

    /** Name of the topic to produce order events to. */
    private final String ordersTopicName;
    /** Name of the topic to produce order cancellation events to. */
    private final String cancellationsTopicName;

    /**
     * Queue of messages waiting to be delivered to Kafka.
     *  Generated messages will be added to this queue.
     */
    protected final Queue<SourceRecord> queue;

    /** Used to schedule message-generation tasks. */
    private final Timer timer;




    protected DatagenTimerTask(OrderGenerator orderGenerator,
                               CancellationGenerator cancellationGenerator,
                               Queue<SourceRecord> queue,
                               Timer generateTimer,
                               AbstractConfig config)
    {
        this.orderGenerator = orderGenerator;
        this.cancellationGenerator = cancellationGenerator;
        this.queue = queue;
        this.timer = generateTimer;

        this.ordersTopicName = config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_ORDERS);
        this.cancellationsTopicName = config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_CANCELLATIONS);
    }

    protected DatagenTimerTask(OrderGenerator orderGenerator,
                               Queue<SourceRecord> queue,
                               Timer generateTimer,
                               AbstractConfig config)
    {
        this.orderGenerator = orderGenerator;
        this.cancellationGenerator = null;
        this.queue = queue;
        this.timer = generateTimer;

        this.ordersTopicName = config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_ORDERS);
        this.cancellationsTopicName = config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_CANCELLATIONS);
    }



    protected void cancelOrder(final Order order, final String origin, final int minDelay, final int maxDelay) {
        if (this.cancellationGenerator == null) {
            log.error("Attempting to cancel order without providing a cancellation generator");
        }

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SourceRecord rec = cancellationGenerator
                                    .generate(order)
                                    .createSourceRecord(cancellationsTopicName, origin);
                queue.add(rec);

                if (cancellationGenerator.shouldDuplicate()) {
                    queue.add(rec);
                }
            }
        }, Generators.randomInt(minDelay, maxDelay));
    }


    protected void scheduleOrder(final String origin, final int delay,
                                 Customer customer)
    {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Order order = orderGenerator.generate(customer);
                queue.add(order.createSourceRecord(ordersTopicName, origin));
            }
        }, delay);
    }



    protected void scheduleOrder(final String origin, final int delay,
            Customer customer,
            int minNumItems, int maxNumItems,
            double unitPrice,
            String region,
            String country,
            String productDescription,
            String priority,
            String storeID)
    {
        final Integer cancelDelayMin = null;
        final Integer cancelDelayMax = null;

        scheduleOrder(origin, delay,
                      customer,
                      minNumItems, maxNumItems,
                      unitPrice,
                      region,
                      country,
                      productDescription,
                      cancelDelayMin, cancelDelayMax,
                      priority,
                      storeID);
    }

    protected void scheduleOrder(final String origin, final int delay,
        Customer customer,
        int minNumItems, int maxNumItems,
        double unitPrice,
        String region,
        String country,
        String productDescription,
        Integer cancelDelayMin, Integer cancelDelayMax,
        String priority,
        String storeID)
    {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Order order = orderGenerator.generate(minNumItems, maxNumItems,
                                                      unitPrice,
                                                      region,
                                                      productDescription,
                                                      customer,
                                                      country,
                                                      priority,
                                                      storeID);
                queue.add(order.createSourceRecord(ordersTopicName, origin));

                if (cancelDelayMin != null && cancelDelayMax != null) {
                    cancelOrder(order, origin, cancelDelayMin, cancelDelayMax);
                }
            }
        }, delay);
    }
}
