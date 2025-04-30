/**
 * Copyright 2024, 2025 IBM Corp. All Rights Reserved.
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

import com.ibm.eventautomation.demos.loosehangerjeans.data.AbandonedOrder;
import com.ibm.eventautomation.demos.loosehangerjeans.generators.AbandonedOrderGenerator;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTaskContext;
import org.apache.kafka.connect.storage.OffsetStorageReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.eventautomation.demos.loosehangerjeans.data.BadgeIn;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Cancellation;
import com.ibm.eventautomation.demos.loosehangerjeans.data.LoosehangerData;
import com.ibm.eventautomation.demos.loosehangerjeans.data.NewCustomer;
import com.ibm.eventautomation.demos.loosehangerjeans.data.OnlineOrder;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Order;
import com.ibm.eventautomation.demos.loosehangerjeans.data.OutOfStock;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Product;
import com.ibm.eventautomation.demos.loosehangerjeans.data.ProductReview;
import com.ibm.eventautomation.demos.loosehangerjeans.data.ReturnRequest;
import com.ibm.eventautomation.demos.loosehangerjeans.data.SensorReading;
import com.ibm.eventautomation.demos.loosehangerjeans.data.StockMovement;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Transaction;
import com.ibm.eventautomation.demos.loosehangerjeans.generators.BadgeInGenerator;
import com.ibm.eventautomation.demos.loosehangerjeans.generators.CancellationGenerator;
import com.ibm.eventautomation.demos.loosehangerjeans.generators.HighSensorReadingGenerator;
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
import com.ibm.eventautomation.demos.loosehangerjeans.generators.TransactionGenerator;
import com.ibm.eventautomation.demos.loosehangerjeans.tasks.FalsePositivesTask;
import com.ibm.eventautomation.demos.loosehangerjeans.tasks.NewCustomerTask;
import com.ibm.eventautomation.demos.loosehangerjeans.tasks.NormalOrdersTask;
import com.ibm.eventautomation.demos.loosehangerjeans.tasks.SuspiciousOrdersTask;
import com.ibm.eventautomation.demos.loosehangerjeans.tasks.TransactionTask;
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

    private static final Logger log = LoggerFactory.getLogger(DatagenHistoryGenerator.class);

    /**
     * Create a list containing seven days' worth of Loosehanger events.
     */
    public List<SourceRecord> generateHistory(AbstractConfig config)
    {
        log.info("Generating historical events to warm up the topics");

        List<SourceRecord> historicalRecords = new ArrayList<>();

        addNewCustomerRecords(historicalRecords, config);
        addStockMovementRecords(historicalRecords, config);
        addBadgeInRecords(historicalRecords, config);
        addSensorReadingRecords(historicalRecords, config);
        addOnlineOrderRecords(historicalRecords, config);
        addOrderAndCancellationRecords(historicalRecords, config);
        addReturnsRecords(historicalRecords, config);
        addTransactionRecords(historicalRecords, config);
        addAbandonedOrderRecords(historicalRecords, config);

        Collections.sort(historicalRecords, (r1, r2) -> {
            return Math.toIntExact(r1.timestamp() - r2.timestamp());
        });

        return historicalRecords;
    }



    private void addBadgeInRecords(List<SourceRecord> historicalRecords, AbstractConfig config) {
        log.debug("generating historical badgein records");
        final String TOPIC = config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_BADGEINS);

        for (BadgeIn badgein : new BadgeInGenerator(config).generateHistory()) {
            SourceRecord record = badgein.createSourceRecord(TOPIC);
            historicalRecords.add(record);
        }
    }

    private void addNewCustomerRecords(List<SourceRecord> historicalRecords, AbstractConfig config) {
        log.debug("generating historical customer records");
        final String TOPIC = config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_CUSTOMERS);

        for (NewCustomer customer : new NewCustomerGenerator(config).generateHistory()) {
            SourceRecord record = customer.createSourceRecord(TOPIC, NewCustomerTask.class.getName());
            historicalRecords.add(record);
        }
    }

    private void addSensorReadingRecords(List<SourceRecord> historicalRecords, AbstractConfig config) {
        log.debug("generating historical sensor records");
        final String TOPIC = config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_SENSORREADINGS);

        List<SensorReading> readings = new SensorReadingGenerator(config).generateHistory();
        List<SensorReading> highReadings = new HighSensorReadingGenerator(config).generateHistory();
        readings.addAll(highReadings);
        Collections.sort(readings, (r1, r2) -> {
            return r1.recordTimestamp().compareTo(r2.recordTimestamp());
        });

        for (SensorReading reading : readings) {
            SourceRecord record = reading.createSourceRecord(TOPIC);
            historicalRecords.add(record);
        }
    }

    private void addTransactionRecords(List<SourceRecord> historicalRecords, AbstractConfig config) {
    	log.debug("generating historical transaction records");
    	final String TOPIC = config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_TRANSACTIONS);

    	for (Transaction transaction : new TransactionGenerator(config).generateHistory()) {
    		SourceRecord record = transaction.createSourceRecord(TOPIC, TransactionTask.class.getName());
    		historicalRecords.add(record);
    	}
    }

    private void addStockMovementRecords(List<SourceRecord> historicalRecords, AbstractConfig config) {
        log.debug("generating historical stock movement records");
        final String TOPIC = config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_STOCKMOVEMENTS);

        for (StockMovement movement : new StockMovementGenerator(config).generateHistory()) {
            SourceRecord record = movement.createSourceRecord(TOPIC);
            historicalRecords.add(record);
        }
    }

    private void addAbandonedOrderRecords(List<SourceRecord> historicalRecords, AbstractConfig config) {
        log.debug("generating historical abandoned order records");
        final String TOPIC = config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_ABANDONEDORDERS);

        AbandonedOrderGenerator abandonedOrderGenerator = new AbandonedOrderGenerator(config);

        for (AbandonedOrder abandonedOrder : abandonedOrderGenerator.generateHistory()) {
            SourceRecord abandonedOrderRecord = abandonedOrder.createSourceRecord(TOPIC);
            historicalRecords.add(abandonedOrderRecord);
        }
    }

    private void addOrderAndCancellationRecords(List<SourceRecord> historicalRecords, AbstractConfig config) {
        log.debug("generating historical order records");
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
                    order.recordTimestamp().plusNanos(delayMs * 1_000_000L),
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

    private void addReturnsRecords(List<SourceRecord> historicalRecords, AbstractConfig config) {
        log.debug("generating historical returns records");
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
                    ZonedDateTime timestamp = returnRequest.recordTimestamp().plusNanos(delay * 1_000_000L);

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

    private void addOnlineOrderRecords(List<SourceRecord> historicalRecords, AbstractConfig config) {
        log.debug("generating historical online order records");
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


    private List<Map<String, Object>> getExpectedPartitions() {
        return List.of(
            LoosehangerData.partition(NormalOrdersTask.class.getName()),
            LoosehangerData.partition(NewCustomerTask.class.getName()),
            LoosehangerData.partition(SuspiciousOrdersTask.class.getName()),
            LoosehangerData.partition(FalsePositivesTask.class.getName()),
            LoosehangerData.partition(BadgeIn.PARTITION),
            LoosehangerData.partition(OnlineOrder.PARTITION),
            LoosehangerData.partition(OutOfStock.PARTITION),
            LoosehangerData.partition(ProductReview.PARTITION),
            LoosehangerData.partition(ReturnRequest.PARTITION),
            LoosehangerData.partition(SensorReading.PARTITION),
            LoosehangerData.partition(StockMovement.PARTITION),
            LoosehangerData.partition(AbandonedOrder.PARTITION)
        );
    }

    /**
     * Optionally, the Connector can generate a week's worth of historical
     *  events when it starts for the first time, so that events are
     *  immediately available for demos, without needing to wait for events
     *  to be generated over time.
     *
     * This should only be done the first time that the Connector starts,
     *  and not be repeated on every restart, reconfiguration, or rebalance.
     *
     * This method is used to look for indications that the Connector has
     *  run before. Note that this depends on looking for offsets, so
     *  restarting the connector with a different name or administratively
     *  deleting it's offset will appear as if the connector is starting
     *  for the first time.
     */
    public boolean startingForFirstTime(SourceTaskContext context) {
        if (context == null) {
            log.debug("No context");
            return true;
        }
        if (context.offsetStorageReader() == null) {
            log.debug("No offset reader");
            return true;
        }

        OffsetStorageReader offsetReader = context.offsetStorageReader();

        final List<Map<String, Object>> EXPECTED_PARTITIONS = getExpectedPartitions();
        Map<Map<String, Object>, Map<String, Object>> allOffsets = offsetReader.offsets(EXPECTED_PARTITIONS);
        if (allOffsets.isEmpty()) {
            log.debug("No offsets found");
            return true;
        }

        for (Map<String, Object> partition : EXPECTED_PARTITIONS) {
            if (allOffsets.get(partition) != null) {
                log.debug("Offset found for {}", partition);
                return false;
            }
        }

        // no evidence of previous runs found
        log.debug("No offsets found for any partition");
        return true;
    }
}
