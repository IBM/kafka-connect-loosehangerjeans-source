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
package com.ibm.eventautomation.demos.loosehangerjeans.generators;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.kafka.common.config.AbstractConfig;

import com.ibm.eventautomation.demos.loosehangerjeans.DatagenSourceConfig;
import com.ibm.eventautomation.demos.loosehangerjeans.data.BadgeIn;
import com.ibm.eventautomation.demos.loosehangerjeans.data.StockMovement;
import com.ibm.eventautomation.demos.loosehangerjeans.utils.Generators;

/**
 * Generates a {@link StockMovement} event using randomly generated data.
 */
public class StockMovementGenerator {

    /** warehouse codes will be randomly selected from this list */
    private final List<String> warehouses;

    /** helper class to randomly generate the name of a product */
    private ProductGenerator productGenerator;

    /** formatter for event timestamps */
    private final DateTimeFormatter timestampFormatter;

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
    private final int INTERVAL;


    public StockMovementGenerator(AbstractConfig config)
    {
        this.productGenerator = new ProductGenerator(config);

        this.warehouses = config.getList(DatagenSourceConfig.CONFIG_LOCATIONS_WAREHOUSES);

        this.timestampFormatter = DateTimeFormatter.ofPattern(config.getString(DatagenSourceConfig.CONFIG_FORMATS_TIMESTAMPS));
        this.MAX_DELAY_SECS = config.getInt(DatagenSourceConfig.CONFIG_DELAYS_STOCKMOVEMENTS);
        this.INTERVAL = config.getInt(DatagenSourceConfig.CONFIG_TIMES_STOCKMOVEMENTS);
    }


    public StockMovement generate() {
        int quantity = Generators.randomInt(20, 500);

        return new StockMovement(UUID.randomUUID().toString(),
                                 timestampFormatter.format(Generators.nowWithRandomOffset(MAX_DELAY_SECS)),
                                 Generators.randomItem(warehouses),
                                 productGenerator.generate().getDescription(),
                                 // stock movement quantities are always
                                 //  multiples of ten
                                 quantity - (quantity % 10));
    }

     /**
     * Generates one week's worth of events to create a fake history.
     *  This is intended to be used on the first run of the connector
     *  to create an instant history of events that can be used for
     *  historical aggregations.
     */
    public List<StockMovement> generateHistory() {
        final List<StockMovement> histList = new ArrayList<StockMovement>();

        final ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime pastEvent = ZonedDateTime.now().minusDays(7);

        while (pastEvent.isBefore(now)) {
            int quantity = Generators.randomInt(20, 500);
            StockMovement event = new StockMovement(UUID.randomUUID().toString(),
                                                    timestampFormatter.format(pastEvent),
                                                    Generators.randomItem(warehouses),
                                                    productGenerator.generate().getDescription(),
                                                    // stock movement quantities are always
                                                    //  multiples of ten
                                                    quantity - (quantity % 10));

            histList.add(event);
            pastEvent = pastEvent.plusNanos(INTERVAL * 1_000_000);
        }
        
        return histList;
    }
}
