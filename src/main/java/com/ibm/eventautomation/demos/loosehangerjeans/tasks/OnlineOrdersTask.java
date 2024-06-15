/**
 * Copyright 2024 IBM Corp. All Rights Reserved.
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
import com.ibm.eventautomation.demos.loosehangerjeans.data.*;
import com.ibm.eventautomation.demos.loosehangerjeans.generators.OnlineOrderGenerator;
import com.ibm.eventautomation.demos.loosehangerjeans.generators.OutOfStockGenerator;
import com.ibm.eventautomation.demos.loosehangerjeans.utils.Generators;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.connect.source.SourceRecord;

import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Timer task intended for repeated execution. Creates new
 *  {@link OnlineOrder} and {@link OutOfStock} events at regular intervals.
 */
public class OnlineOrdersTask extends TimerTask {

    /** Helper class for generating OnlineOrder events. */
    private final OnlineOrderGenerator orderGenerator;

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
     * Ratio of orders that have at least one product that runs out-of-stock after the order has been placed.
     * Must be between 0.0 and 1.0.
     *
     * Setting this to 0 will mean that no out-of-stock event is generated.
     * Setting this to 1 will mean that one out-of-stock event will be generated for each new order.
     */
    private final double outOfStockRatio;

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

    /** Name of the topic to produce online order events to. */
    private final String orderTopicName;

    /** Name of the topic to produce out-of-stock events to. */
    private final String outOfStockTopicName;


    public OnlineOrdersTask(AbstractConfig config,
                            Queue<SourceRecord> queue,
                            Timer generateTimer) {
        this.orderGenerator = new OnlineOrderGenerator(config);
        this.outOfStockGenerator = new OutOfStockGenerator(config);
        this.queue = queue;
        this.timer = generateTimer;

        this.outOfStockRatio = config.getDouble(DatagenSourceConfig.CONFIG_ONLINEORDERS_OUTOFSTOCK_RATIO);
        this.outOfStockMinDelay = config.getInt(DatagenSourceConfig.CONFIG_OUTOFSTOCKS_MIN_DELAY);
        this.outOfStockMaxDelay = config.getInt(DatagenSourceConfig.CONFIG_OUTOFSTOCKS_MAX_DELAY);

        this.orderTopicName = config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_ONLINEORDERS);
        this.outOfStockTopicName = config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_OUTOFSTOCKS);
    }


    @Override
    public void run() {
        // Generate a random online order.
        OnlineOrder order = orderGenerator.generate();
        SourceRecord rec = order.createSourceRecord(orderTopicName);
        queue.add(rec);

        // Possibly duplicate the event.
        if (orderGenerator.shouldDuplicate()) {
            queue.add(rec);
        }

        // Sometimes generate an out-of-stock event for a given order.
        if (Generators.shouldDo(outOfStockRatio)) {
            // Retrieve a product randomly in the order.
            List<String> products = order.getProducts();
            String productDescription = Generators.randomItem(products);
            Product product = Product.parseDescription(productDescription);
            if (product != null) {
                generateOutOfStockEvent(product);
            }
        }
    }

    private void generateOutOfStockEvent(final Product product) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SourceRecord rec = outOfStockGenerator
                        .generate(product)
                        .createSourceRecord(outOfStockTopicName);
                queue.add(rec);

                // Possibly duplicate the event.
                if (outOfStockGenerator.shouldDuplicate()) {
                    queue.add(rec);
                }
            }
        }, Generators.randomInt(outOfStockMinDelay, outOfStockMaxDelay));
    }
}
