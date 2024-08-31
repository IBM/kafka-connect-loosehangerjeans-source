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
package com.ibm.eventautomation.demos.loosehangerjeans;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.connect.source.SourceRecord;

import com.ibm.eventautomation.demos.loosehangerjeans.data.BadgeIn;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Cancellation;
import com.ibm.eventautomation.demos.loosehangerjeans.data.LoosehangerData;
import com.ibm.eventautomation.demos.loosehangerjeans.data.NewCustomer;
import com.ibm.eventautomation.demos.loosehangerjeans.data.OnlineOrder;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Order;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Product;
import com.ibm.eventautomation.demos.loosehangerjeans.data.ProductReview;
import com.ibm.eventautomation.demos.loosehangerjeans.data.ReturnRequest;
import com.ibm.eventautomation.demos.loosehangerjeans.data.SensorReading;
import com.ibm.eventautomation.demos.loosehangerjeans.data.StockMovement;
import com.ibm.eventautomation.demos.loosehangerjeans.generators.BadgeInGenerator;
import com.ibm.eventautomation.demos.loosehangerjeans.generators.CancellationGenerator;
import com.ibm.eventautomation.demos.loosehangerjeans.generators.NewCustomerGenerator;
import com.ibm.eventautomation.demos.loosehangerjeans.generators.OnlineOrderGenerator;
import com.ibm.eventautomation.demos.loosehangerjeans.generators.OrderGenerator;
import com.ibm.eventautomation.demos.loosehangerjeans.generators.OutOfStockGenerator;
import com.ibm.eventautomation.demos.loosehangerjeans.generators.ProductGenerator;
import com.ibm.eventautomation.demos.loosehangerjeans.generators.ProductReviewGenerator;
import com.ibm.eventautomation.demos.loosehangerjeans.generators.ReturnRequestGenerator;
import com.ibm.eventautomation.demos.loosehangerjeans.generators.SensorReadingGenerator;
import com.ibm.eventautomation.demos.loosehangerjeans.generators.StockMovementGenerator;
import com.ibm.eventautomation.demos.loosehangerjeans.generators.SuspiciousOrderGenerator;
import com.ibm.eventautomation.demos.loosehangerjeans.tasks.NewCustomerTask;
import com.ibm.eventautomation.demos.loosehangerjeans.tasks.NormalOrdersTask;
import com.ibm.eventautomation.demos.loosehangerjeans.tasks.SuspiciousOrdersTask;
import com.ibm.eventautomation.demos.loosehangerjeans.utils.Generators;

/**
 * Generates a historical week of Loosehanger events.
 *
 *  The intention is that this is used the first time the connector
 *  is run, to provide an instant history of events for processing,
 *  to avoid new demos needing to wait for a backlog of events to
 *  be naturally generated.
 *
 *  The timestamp of these historical events will be artificially
 *  set to times within the last seven days, to give the appearance
 *  of a Kafka topic that has been receiving events steadily over
 *  the previous week.
 *
 *  It isn't appropriate to use this function with topics that already
 *  have events, as this will result in a large batch of historical
 *  events appearing to be out of sequence in the topic.
 */
public class DatagenHistoryGenerator {

    /**
     * Create a list containing seven days' worth of Loosehanger events.
     */
    public static List<SourceRecord> generateHistory(AbstractConfig config)
    {
        List<SourceRecord> historicalRecords = new ArrayList<>();

        addNewCustomerRecords(historicalRecords, config);
        addStockMovementRecords(historicalRecords, config);
        addBadgeInRecords(historicalRecords, config);
        addSensorReadingRecords(historicalRecords, config);
        addOnlineOrderRecords(historicalRecords, config);
        addOrderAndCancellationRecords(historicalRecords, config);
        addReturnsRecords(historicalRecords, config);

        Collections.sort(historicalRecords, (r1, r2) -> {
            return Math.toIntExact(r1.timestamp() - r2.timestamp());
        });

        return historicalRecords;
    }



    private static void addBadgeInRecords(List<SourceRecord> historicalRecords, AbstractConfig config) {
        final String TOPIC = config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_BADGEINS);

        for (BadgeIn badgein : new BadgeInGenerator(config).generateHistory()) {
            SourceRecord record = badgein.createSourceRecord(TOPIC);
            historicalRecords.add(record);
        }
    }

    private static void addNewCustomerRecords(List<SourceRecord> historicalRecords, AbstractConfig config) {
        final String TOPIC = config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_CUSTOMERS);

        for (NewCustomer customer : new NewCustomerGenerator(config).generateHistory()) {
            SourceRecord record = customer.createSourceRecord(TOPIC, NewCustomerTask.class.getName());
            historicalRecords.add(record);
        }
    }

    private static void addSensorReadingRecords(List<SourceRecord> historicalRecords, AbstractConfig config) {
        final String TOPIC = config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_SENSORREADINGS);

        for (SensorReading reading : new SensorReadingGenerator(config).generateHistory()) {
            SourceRecord record = reading.createSourceRecord(TOPIC);
            historicalRecords.add(record);
        }
    }

    private static void addStockMovementRecords(List<SourceRecord> historicalRecords, AbstractConfig config) {
        final String TOPIC = config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_STOCKMOVEMENTS);

        for (StockMovement movement : new StockMovementGenerator(config).generateHistory()) {
            SourceRecord record = movement.createSourceRecord(TOPIC);
            historicalRecords.add(record);
        }
    }

    private static void addOnlineOrderRecords(List<SourceRecord> historicalRecords, AbstractConfig config) {
        final String ONLINEORDERS_TOPIC = config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_ONLINEORDERS);
        final String OUTOFSTOCK_TOPIC = config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_OUTOFSTOCKS);

        OnlineOrderGenerator onlineOrderGenerator = new OnlineOrderGenerator(config);
        OutOfStockGenerator outOfStockGenerator = new OutOfStockGenerator(config);

        for (OnlineOrder order : onlineOrderGenerator.generateHistory()) {
            SourceRecord orderRecord = order.createSourceRecord(ONLINEORDERS_TOPIC);
            historicalRecords.add(orderRecord);

            if (onlineOrderGenerator.shouldGenerateOutOfStockEvent()) {
                SourceRecord outOfStockRecord = outOfStockGenerator.generate(order).createSourceRecord(OUTOFSTOCK_TOPIC);
                historicalRecords.add(outOfStockRecord);

                if (outOfStockGenerator.shouldDuplicate()) {
                    historicalRecords.add(outOfStockRecord);
                }
            }
        }
    }

    private static void addOrderAndCancellationRecords(List<SourceRecord> historicalRecords, AbstractConfig config) {
        final String ORDERS_TOPIC = config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_ORDERS);
        final String CANCELLATIONS_TOPIC = config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_CANCELLATIONS);

        // normal orders
        int cancellationMinDelay = config.getInt(DatagenSourceConfig.CONFIG_CANCELLATIONS_MIN_DELAY);
        int cancellationMaxDelay = config.getInt(DatagenSourceConfig.CONFIG_CANCELLATIONS_MAX_DELAY);

        OrderGenerator orderGenerator = new OrderGenerator(config);
        CancellationGenerator cancellationGenerator = new CancellationGenerator(config);

        for (Order order : orderGenerator.generateHistory()) {
            SourceRecord orderRecord = order.createSourceRecord(ORDERS_TOPIC, NormalOrdersTask.class.getName());
            historicalRecords.add(orderRecord);

            if (orderGenerator.shouldCancel()) {
                int delayMs = Generators.randomInt(cancellationMinDelay, cancellationMaxDelay);
                Cancellation cancellationRecord = cancellationGenerator.generate(
                    order.recordTimestamp().plusNanos(delayMs * 1_000_000),
                    order);
                historicalRecords.add(cancellationRecord.createSourceRecord(CANCELLATIONS_TOPIC, NormalOrdersTask.class.getName()));
            }
        }

        // suspicious orders
        for (LoosehangerData data : SuspiciousOrderGenerator.generateHistory(config, orderGenerator, cancellationGenerator)) {
            SourceRecord record = null;
            if (data instanceof Order) {
                record = data.createSourceRecord(ORDERS_TOPIC, SuspiciousOrdersTask.class.getName());
            }
            else if (data instanceof Cancellation) {
                record = data.createSourceRecord(CANCELLATIONS_TOPIC, SuspiciousOrdersTask.class.getName());
            }
            historicalRecords.add(record);
        }
    }

    private static void addReturnsRecords(List<SourceRecord> historicalRecords, AbstractConfig config) {
        String RETURN_TOPIC = config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_RETURNREQUESTS);
        String REVIEW_TOPIC = config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_PRODUCTREVIEWS);

        int reviewMinDelay = config.getInt(DatagenSourceConfig.CONFIG_PRODUCTREVIEWS_MIN_DELAY);
        int reviewMaxDelay = config.getInt(DatagenSourceConfig.CONFIG_PRODUCTREVIEWS_MAX_DELAY);

        Map<String, Product> productsWithSizeIssue = new ProductGenerator(config).generate(config.getInt(DatagenSourceConfig.CONFIG_PRODUCTREVIEWS_PRODUCTS_WITH_SIZE_ISSUE_COUNT));
        ProductReviewGenerator productReviewGenerator = new ProductReviewGenerator(config, productsWithSizeIssue);
        ReturnRequestGenerator returnRequestGenerator = new ReturnRequestGenerator(config, productReviewGenerator.getProductsWithSizeIssue());

        for (ReturnRequest returnRequest : returnRequestGenerator.generateHistory()) {
            SourceRecord returnRecord = returnRequest.createSourceRecord(RETURN_TOPIC);
            historicalRecords.add(returnRecord);

            if (returnRequestGenerator.shouldReview()) {
                Product product = Generators.randomItem(returnRequest.getReturns()).getProduct();
                if (product != null) {
                    int delay = Generators.randomInt(reviewMinDelay, reviewMaxDelay);
                    ZonedDateTime timestamp = returnRequest.recordTimestamp().plusNanos(delay * 1_000_000);

                    ProductReview review = productReviewGenerator.generate(product, timestamp);
                    SourceRecord reviewRecord = review.createSourceRecord(REVIEW_TOPIC);
                    historicalRecords.add(reviewRecord);

                    if (productReviewGenerator.shouldDuplicate()) {
                        historicalRecords.add(reviewRecord);
                    }
                }
            }
        }

        for (ProductReview review : productReviewGenerator.generateHistory()) {
            SourceRecord reviewRecord = review.createSourceRecord(REVIEW_TOPIC);
            historicalRecords.add(reviewRecord);
        }
    }
}
