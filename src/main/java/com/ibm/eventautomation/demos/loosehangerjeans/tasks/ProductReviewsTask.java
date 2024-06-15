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
import com.ibm.eventautomation.demos.loosehangerjeans.data.ProductReview;
import com.ibm.eventautomation.demos.loosehangerjeans.generators.ProductReviewGenerator;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.connect.source.SourceRecord;

import java.util.Queue;
import java.util.TimerTask;

/**
 * Timer task intended for repeated execution. Creates new
 *  {@link ProductReview} events at regular intervals.
 */
public class ProductReviewsTask extends TimerTask {

    /** Helper class for generating ProductReview events. */
    private final ProductReviewGenerator productReviewGenerator;

    /**
     * Queue of messages waiting to be delivered to Kafka.
     *  Generated ProductReview events will be added to this queue.
     */
    private final Queue<SourceRecord> queue;

    /** Name of the topic to produce product review events to. */
    private final String productReviewTopicName;

    public ProductReviewsTask(AbstractConfig config,
                              Queue<SourceRecord> queue,
                              ProductReviewGenerator productReviewGenerator) {
        this.productReviewGenerator = productReviewGenerator;
        this.queue = queue;

        this.productReviewTopicName = config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_PRODUCTREVIEWS);
    }

    @Override
    public void run() {
        // Generate a random product review.
        ProductReview productReview = productReviewGenerator.generate();
        SourceRecord rec = productReview.createSourceRecord(productReviewTopicName);
        queue.add(rec);

        // Possibly duplicate the event.
        if (productReviewGenerator.shouldDuplicate()) {
            queue.add(rec);
        }
    }

}
