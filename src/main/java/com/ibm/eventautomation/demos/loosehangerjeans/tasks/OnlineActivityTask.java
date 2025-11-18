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

import java.time.ZonedDateTime;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.connect.source.SourceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.eventautomation.demos.loosehangerjeans.DatagenSourceConfig;
import com.ibm.eventautomation.demos.loosehangerjeans.data.AbandonedOrder;
import com.ibm.eventautomation.demos.loosehangerjeans.data.ClickEvent;
import com.ibm.eventautomation.demos.loosehangerjeans.data.NewCustomer;
import com.ibm.eventautomation.demos.loosehangerjeans.data.OnlineActivityData;
import com.ibm.eventautomation.demos.loosehangerjeans.data.OnlineOrder;
import com.ibm.eventautomation.demos.loosehangerjeans.data.OutOfStock;
import com.ibm.eventautomation.demos.loosehangerjeans.generators.OnlineActivityGenerator;
import com.ibm.eventautomation.demos.loosehangerjeans.generators.OutOfStockGenerator;
import com.ibm.eventautomation.demos.loosehangerjeans.utils.Generators;

/**
 * Timer task intended for repeated execution.
 *
 * Starts new user sessions at regular intervals, where a user session results in
 *  a series of events being generated, such as click stream events {@link ClickEvent},
 *  online order events {@link OnlineOrder}, or abandoned shopping carts {@link AbandonedOrder}
 */
public class OnlineActivityTask extends TimerTask {

    private static final Logger log = LoggerFactory.getLogger(OnlineActivityTask.class);


    /** Identifies the task that generated the orders and cancellations */
    private static final String ORIGIN = OnlineActivityTask.class.getName();

    /** Helper class for generating online activity events. */
    private final OnlineActivityGenerator activityGenerator;

    /** Helper class for generating OutOfStock events. */
    private final OutOfStockGenerator outOfStockGenerator;

    /**
     * Queue of messages waiting to be delivered to Kafka.
     *  Generated OnlineOrder and OutOfStock events will be added to this queue.
     */
    private final Queue<SourceRecord> queue;

    /** Timer used to schedule message-generation tasks. */
    private final Timer timer;

    /**
     * Minimum time (in milliseconds) to wait after creating an {@link OnlineOrder} before
     * possibly generating an {@link OutOfStock}.
     */
    private final int outOfStockMinDelay;

    /**
     * Maximum time (in milliseconds) to wait after creating an {@link OnlineOrder} before
     * possibly generating an {@link OutOfStock}.
     */
    private final int outOfStockMaxDelay;

    /**
     * Maximum time (in milliseconds) to wait between each clickstream event within a single
     *  user session.
     */
    private final int clickTrackingMaxInterval;

    /* Name of topics to produce events to. */
    private final String orderTopicName;
    private final String newCustomersTopicName;
    private final String clickTrackingTopicName;
    private final String abandonedCartTopicName;
    private final String outOfStockTopicName;

    /*
     * Generator can simulate a source of events that offers
     *  at-least-once delivery semantics by occasionally
     *  producing duplicate messages.
     *
     * These value is the proportion of events that will be
     *  duplicated, between 0.0 and 1.0.
     *
     * Setting this to 0 will mean no events are duplicated.
     * Setting this to 1 will mean every message is produced twice.
     */
    private final double duplicateClickTrackingRatio;
    private final double duplicateOnlineOrderRatio;
    private final double duplicateAbandonedCartRatio;


    public OnlineActivityTask(AbstractConfig config, Queue<SourceRecord> queue, Timer generateTimer)
    {
        this.activityGenerator = new OnlineActivityGenerator(config);
        this.outOfStockGenerator = new OutOfStockGenerator(config);

        this.outOfStockMinDelay = config.getInt(DatagenSourceConfig.CONFIG_OUTOFSTOCKS_MIN_DELAY);
        this.outOfStockMaxDelay = config.getInt(DatagenSourceConfig.CONFIG_OUTOFSTOCKS_MAX_DELAY);

        this.clickTrackingMaxInterval = config.getInt(DatagenSourceConfig.CONFIG_TIMES_CLICKTRACKING);

        this.orderTopicName = config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_ONLINEORDERS);
        this.newCustomersTopicName = config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_CUSTOMERS);
        this.clickTrackingTopicName = config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_CLICKTRACKING);
        this.abandonedCartTopicName = config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_ABANDONEDORDERS);
        this.outOfStockTopicName = config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_OUTOFSTOCKS);

        this.duplicateClickTrackingRatio = config.getDouble(DatagenSourceConfig.CONFIG_DUPLICATE_ONLINEORDERS);
        this.duplicateOnlineOrderRatio = config.getDouble(DatagenSourceConfig.CONFIG_DUPLICATE_ONLINEORDERS);
        this.duplicateAbandonedCartRatio = config.getDouble(DatagenSourceConfig.CONFIG_DUPLICATE_ABANDONEDORDERS);

        this.queue = queue;
        this.timer = generateTimer;
    }


    /**
     * Called to initiate a new user session - which will generate a series of
     *  online activity events, that could end with an online order or
     *  abandoned cart event.
     */
    @Override
    public void run() {
        final NewCustomer newCustomer = activityGenerator.registerNewOnlineCustomer();
        if (newCustomer != null) {
            queue.add(newCustomer.createSourceRecord(newCustomersTopicName, ORIGIN));
        }

        final String sessionId = activityGenerator.startNewSession();
        if (sessionId != null) {
            generateActivity(sessionId);
        }
    }


    private void generateActivity(final String sessionId) {
        OnlineActivityData nextActivity = activityGenerator.nextActivity(ZonedDateTime.now(), sessionId);
        if (nextActivity == null) {
            return;
        }

        if (nextActivity instanceof ClickEvent) {
            // click stream events - e.g. user has clicked on a product
            SourceRecord clickRecord = nextActivity.createSourceRecord(clickTrackingTopicName, ORIGIN);
            emitEvent(clickRecord, duplicateClickTrackingRatio);
        }
        else if (nextActivity instanceof OnlineOrder) {
            // order events - e.g. user has completed an online order
            SourceRecord orderRecord = nextActivity.createSourceRecord(orderTopicName, ORIGIN);
            emitEvent(orderRecord, duplicateOnlineOrderRatio);

            if (activityGenerator.shouldGenerateOutOfStockEvent()) {
                // Sometimes generate an out-of-stock event for a given order.
                OutOfStock outOfStock = outOfStockGenerator.generate((OnlineOrder) nextActivity);
                if (outOfStock != null) {
                    generateOutOfStockEvent(outOfStock);
                }
            }
        }
        else if (nextActivity instanceof AbandonedOrder) {
            // abandoned card events - e.g. user logged in, added at least one product to a basket, but went no further
            SourceRecord abandonedCartRecord = nextActivity.createSourceRecord(abandonedCartTopicName, ORIGIN);
            emitEvent(abandonedCartRecord, duplicateAbandonedCartRatio);
        }
        else {
            log.error("Unexpected activity type {}", nextActivity.getClass().getCanonicalName());
            return;
        }


        // if the user session is not complete, schedule the next event
        if (activityGenerator.hasMore(sessionId)) {
            scheduleNextActivity(sessionId);
        }
    }



    private void emitEvent(SourceRecord record, double duplicateRatio) {
        queue.add(record);

        if (Generators.shouldDo(duplicateRatio)) {
            queue.add(record);
        }
    }

    private void scheduleNextActivity(final String sessionId) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                generateActivity(sessionId);
            }
        }, Generators.randomInt(3_000, clickTrackingMaxInterval));
    }




    private void generateOutOfStockEvent(final OutOfStock outOfStock) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SourceRecord rec = outOfStock.createSourceRecord(outOfStockTopicName);
                queue.add(rec);

                // Possibly duplicate the event.
                if (outOfStockGenerator.shouldDuplicate()) {
                    queue.add(rec);
                }
            }
        }, Generators.randomInt(outOfStockMinDelay, outOfStockMaxDelay));
    }
}
