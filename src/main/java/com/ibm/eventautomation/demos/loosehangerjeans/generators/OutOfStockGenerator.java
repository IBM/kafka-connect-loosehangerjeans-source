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
package com.ibm.eventautomation.demos.loosehangerjeans.generators;

import com.ibm.eventautomation.demos.loosehangerjeans.DatagenSourceConfig;
import com.ibm.eventautomation.demos.loosehangerjeans.data.OutOfStock;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Product;
import com.ibm.eventautomation.demos.loosehangerjeans.utils.Generators;
import org.apache.kafka.common.config.AbstractConfig;

import java.time.ZonedDateTime;

/**
 * Generates an {@link OutOfStock} event for a given product using randomly generated data.
 */
public class OutOfStockGenerator {

    /**
     * Minimum time (in days) between the time that the product was out-of-stock and the restocking date.
     */
    private final int restockingMinDelay;

    /**
     * Maximum time (in days) between the time that the product was out-of-stock and the restocking date.
     */
    private final int restockingMaxDelay;

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
    private final double duplicatesRatio;

    /**
     * Generator can simulate a delay in events being produced
     *  to Kafka by putting a timestamp in the message payload
     *  that is earlier than the current time.
     *
     * The amount of the delay will be randomized to simulate
     *  a delay due to network or infrastructure reasons.
     *
     * This value is the maximum delay (in seconds) that it will
     *  use. (Setting this to 0 will mean all events are
     *  produced with the current time).
     */
    private final int MAX_DELAY_SECS;

    /** Creates an {@link OutOfStockGenerator} using the provided configuration. */
    public OutOfStockGenerator(AbstractConfig config) {
        this.restockingMinDelay = config.getInt(DatagenSourceConfig.CONFIG_OUTOFSTOCKS_RESTOCKING_MIN_DELAY);
        this.restockingMaxDelay = config.getInt(DatagenSourceConfig.CONFIG_OUTOFSTOCKS_RESTOCKING_MAX_DELAY);
        this.duplicatesRatio = config.getDouble(DatagenSourceConfig.CONFIG_DUPLICATE_OUTOFSTOCKS);

        this.MAX_DELAY_SECS = config.getInt(DatagenSourceConfig.CONFIG_DELAYS_OUTOFSTOCKS);
    }

    /** Generates a random out-of-stock for a given product. */
    public OutOfStock generate(Product product) {
        ZonedDateTime dateTime = Generators.nowWithRandomOffset(MAX_DELAY_SECS);
        long timestamp = dateTime.toInstant().toEpochMilli();
        int restockingDelay = Generators.randomInt(restockingMinDelay, restockingMaxDelay);
        int restockingDate = (int) dateTime.plusDays(restockingDelay).toLocalDate().toEpochDay();
        return new OutOfStock(timestamp, product, restockingDate);
    }

    public boolean shouldDuplicate() {
        return Generators.shouldDo(duplicatesRatio);
    }
}
