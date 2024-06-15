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
import com.ibm.eventautomation.demos.loosehangerjeans.generators.ProductReviewGenerator;
import com.ibm.eventautomation.demos.loosehangerjeans.generators.ReturnRequestGenerator;
import com.ibm.eventautomation.demos.loosehangerjeans.utils.Generators;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.connect.source.SourceRecord;

import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Timer task intended for repeated execution. Creates new
 *  {@link ReturnRequest} and {@link ProductReview} events at regular intervals.
 */
public class ReturnRequestsTask extends TimerTask {

    /** Helper class for generating ReturnRequest events. */
    private final ReturnRequestGenerator returnRequestGenerator;

    /** Helper class for generating ProductReview events. */
    private final ProductReviewGenerator productReviewGenerator;

    /**
     * Queue of messages waiting to be delivered to Kafka.
     *  Generated ReturnRequest and ProductReview events will be added to this queue.
     */
    private final Queue<SourceRecord> queue;

    /** Timer used to schedule message-generation tasks. */
    private final Timer timer;

    /**
     * Ratio of return requests that have at least one product that has a review that is
     * posted after the return request is issued.
     * Must be between 0.0 and 1.0.
     *
     * Setting this to 0 will mean that no product review event is generated.
     * Setting this to 1 will mean that one product review event will be generated for each
     *  new return request.
     */
    private final double reviewRatio;

    /**
     * Minimum time (in milliseconds) to wait after creating a {@link ReturnRequest} before
     * possibly generating a {@link ProductReview}.
     */
    private final int reviewMinDelay;

    /**
     * Maximum time (in milliseconds) to wait after creating a {@link ReturnRequest} before
     * possibly generating a {@link ProductReview}.
     */
    private final int reviewMaxDelay;

    /** Name of the topic to produce return request events to. */
    private final String returnRequestTopicName;

    /** Name of the topic to produce product review events to. */
    private final String productReviewTopicName;

    public ReturnRequestsTask(AbstractConfig config,
                              Queue<SourceRecord> queue,
                              Timer generateTimer,
                              ProductReviewGenerator productReviewGenerator) {
        this.returnRequestGenerator = new ReturnRequestGenerator(config);
        this.queue = queue;
        this.timer = generateTimer;
        this.productReviewGenerator = productReviewGenerator;

        this.reviewRatio = config.getDouble(DatagenSourceConfig.CONFIG_RETURNREQUESTS_REVIEW_RATIO);
        this.reviewMinDelay = config.getInt(DatagenSourceConfig.CONFIG_PRODUCTREVIEWS_MIN_DELAY);
        this.reviewMaxDelay = config.getInt(DatagenSourceConfig.CONFIG_PRODUCTREVIEWS_MAX_DELAY);

        this.returnRequestTopicName = config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_RETURNREQUESTS);
        this.productReviewTopicName = config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_PRODUCTREVIEWS);
    }

    @Override
    public void run() {
        // Generate a random return request.
        ReturnRequest returnRequest = returnRequestGenerator.generate();
        SourceRecord rec = returnRequest.createSourceRecord(returnRequestTopicName);
        queue.add(rec);

        // Possibly duplicate the event.
        if (returnRequestGenerator.shouldDuplicate()) {
            queue.add(rec);
        }

        // Sometimes generate a review for a given product of a given return request.
        if (Generators.shouldDo(reviewRatio)) {
            // Retrieve a product randomly in the return request.
            Product product = Generators.randomItem(returnRequest.getReturns()).getProduct();
            if (product != null) {
                generateProductReview(product);
            }
        }
    }

    private void generateProductReview(final Product product) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SourceRecord rec = productReviewGenerator
                        .generate(product)
                        .createSourceRecord(productReviewTopicName);
                queue.add(rec);

                // Possibly duplicate the event.
                if (productReviewGenerator.shouldDuplicate()) {
                    queue.add(rec);
                }
            }
        }, Generators.randomInt(reviewMinDelay, reviewMaxDelay));
    }
}
