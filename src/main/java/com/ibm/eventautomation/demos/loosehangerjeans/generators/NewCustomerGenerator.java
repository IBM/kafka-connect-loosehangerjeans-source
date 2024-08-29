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

import org.apache.kafka.common.config.AbstractConfig;

import com.github.javafaker.Faker;
import com.ibm.eventautomation.demos.loosehangerjeans.DatagenSourceConfig;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Customer;
import com.ibm.eventautomation.demos.loosehangerjeans.data.NewCustomer;
import com.ibm.eventautomation.demos.loosehangerjeans.utils.Generators;

/**
 * Generates a {@link NewCustomer} event using randomly generated data.
 */
public class NewCustomerGenerator {

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

    /** customer name generator */
    private final Faker faker = new Faker();


    public NewCustomerGenerator(AbstractConfig config) {
        this.timestampFormatter = DateTimeFormatter.ofPattern(config.getString(DatagenSourceConfig.CONFIG_FORMATS_TIMESTAMPS));
        this.MAX_DELAY_SECS = config.getInt(DatagenSourceConfig.CONFIG_DELAYS_NEWCUSTOMERS);
        this.INTERVAL = config.getInt(DatagenSourceConfig.CONFIG_TIMES_NEWCUSTOMERS);
    }

    public NewCustomer generate() {
        return new NewCustomer(timestampFormatter.format(Generators.nowWithRandomOffset(MAX_DELAY_SECS)),
                               new Customer(faker));
    }

     /**
     * Generates one week's worth of events to create a fake history.
     *  This is intended to be used on the first run of the connector
     *  to create an instant history of events that can be used for
     *  historical aggregations.
     */
    public List<NewCustomer> generateHistory() {
        final List<NewCustomer> histList = new ArrayList<NewCustomer>();

        final ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime pastEvent = ZonedDateTime.now().minusDays(7);

        while (pastEvent.isBefore(now)) {
            NewCustomer event = new NewCustomer(timestampFormatter.format(pastEvent),
                                                    new Customer(faker));            
            histList.add(event);
            pastEvent = pastEvent.plusNanos(INTERVAL * 1_000_000);
        }
        
        return histList;
    }
}
