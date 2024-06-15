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
package com.ibm.eventautomation.demos.loosehangerjeans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.ibm.eventautomation.demos.loosehangerjeans.generators.ProductReviewGenerator;
import com.ibm.eventautomation.demos.loosehangerjeans.tasks.*;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.eventautomation.demos.loosehangerjeans.generators.CancellationGenerator;
import com.ibm.eventautomation.demos.loosehangerjeans.generators.OrderGenerator;

public class DatagenSourceTask extends SourceTask {

    private static final Logger log = LoggerFactory.getLogger(DatagenSourceTask.class);

    private OrderGenerator orderGenerator;
    private CancellationGenerator cancellationGenerator;

    /** Schedules the random event generators */
    private final Timer generateTimer = new Timer();

    /**
     * Queue of messages waiting to be delivered to Kafka.
     *
     *  When Kafka Connect calls the connector in the poll() method, it
     *  will retrieve messages from this queue.
     *
     *  When the scheduled timers fire to generate randomly created
     *  messages, they will add messages to this queue.
     */
    private final Queue<SourceRecord> queue = new ConcurrentLinkedQueue<>();



    @Override
    public void start(Map<String, String> props) {
        log.info("Starting task {}", props);

        AbstractConfig config = new AbstractConfig(DatagenSourceConfig.CONFIG_DEF, props);
        orderGenerator = new OrderGenerator(config);
        cancellationGenerator = new CancellationGenerator(config);

        // schedule the timer tasks that will periodically generate
        //  new messages and add them to the queue

        // new customer registrations
        NewCustomerTask newCustomers = new NewCustomerTask(config, orderGenerator, queue, generateTimer);
        generateTimer.scheduleAtFixedRate(newCustomers, 0, config.getInt(DatagenSourceConfig.CONFIG_TIMES_NEWCUSTOMERS));

        // "normal" orders
        //  create regular, innocent, "normal" orders and cancellations
        NormalOrdersTask normalOrders = new NormalOrdersTask(config, orderGenerator, cancellationGenerator, queue, generateTimer);
        generateTimer.scheduleAtFixedRate(normalOrders, 0, config.getInt(DatagenSourceConfig.CONFIG_TIMES_ORDERS));

        // innocent but unusual-looking orders
        //  create orders and cancellations that are innocent
        FalsePositivesTask falsePositiveOrders = new FalsePositivesTask(config, orderGenerator, cancellationGenerator, queue, generateTimer);
        generateTimer.scheduleAtFixedRate(falsePositiveOrders, 0, config.getInt(DatagenSourceConfig.CONFIG_TIMES_FALSEPOSITIVES));

        // suspicious, possibly fraudulent orders
        SuspiciousOrdersTask suspiciousOrders = new SuspiciousOrdersTask(config, orderGenerator, cancellationGenerator, queue, generateTimer);
        generateTimer.scheduleAtFixedRate(suspiciousOrders, 0, config.getInt(DatagenSourceConfig.CONFIG_TIMES_SUSPICIOUSORDERS));

        // stock movements
        StockMovementsTask stockMovements = new StockMovementsTask(config, queue);
        generateTimer.scheduleAtFixedRate(stockMovements, 0, config.getInt(DatagenSourceConfig.CONFIG_TIMES_STOCKMOVEMENTS));

        // door-badge events
        BadgeInTask badgeIns = new BadgeInTask(config, queue);
        generateTimer.scheduleAtFixedRate(badgeIns, 0, config.getInt(DatagenSourceConfig.CONFIG_TIMES_BADGEINS));

        // IoT sensor readings
        SensorReadingTask sensorReadings = new SensorReadingTask(config, queue);
        generateTimer.scheduleAtFixedRate(sensorReadings, 0, config.getInt(DatagenSourceConfig.CONFIG_TIMES_SENSORREADINGS));

        // online orders
        // create online orders and out-of-stock events
        OnlineOrdersTask onlineOrders = new OnlineOrdersTask(config, queue, generateTimer);
        generateTimer.scheduleAtFixedRate(onlineOrders, 0, config.getInt(DatagenSourceConfig.CONFIG_TIMES_ONLINEORDERS));

        // return requests
        // create return requests and product reviews
        ProductReviewGenerator productReviewGenerator = new ProductReviewGenerator(config);
        ReturnRequestsTask returnRequests = new ReturnRequestsTask(config, queue, generateTimer, productReviewGenerator);
        generateTimer.scheduleAtFixedRate(returnRequests, 0, config.getInt(DatagenSourceConfig.CONFIG_TIMES_RETURNREQUESTS));

        // product reviews
        ProductReviewsTask productReviews = new ProductReviewsTask(config, queue, productReviewGenerator);
        generateTimer.scheduleAtFixedRate(productReviews, 0, config.getInt(DatagenSourceConfig.CONFIG_TIMES_PRODUCTREVIEWS));
    }


    @Override
    public void stop() {
        log.info("Stopping task");

        generateTimer.cancel();
        queue.clear();
    }


    @Override
    public List<SourceRecord> poll() {
        List<SourceRecord> currentRecords = new ArrayList<>();

        SourceRecord nextItem = queue.poll();
        while (nextItem != null) {
            currentRecords.add(nextItem);

            nextItem = queue.poll();
        }
        return currentRecords;
    }


    @Override
    public String version() {
        return DatagenSourceConnector.VERSION;
    }
}
