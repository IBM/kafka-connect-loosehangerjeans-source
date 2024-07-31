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

import com.github.javafaker.Faker;
import com.ibm.eventautomation.demos.loosehangerjeans.DatagenSourceConfig;
import com.ibm.eventautomation.demos.loosehangerjeans.data.BadgeIn;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Locations;
import com.ibm.eventautomation.demos.loosehangerjeans.utils.Generators;

/**
 * Generates a {@link BadgeIn} event using randomly generated data.
 */
public class BadgeInGenerator {

    /** username generator */
    private final Faker faker = new Faker();

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


    public BadgeInGenerator(AbstractConfig config)
    {
        this.timestampFormatter = DateTimeFormatter.ofPattern(config.getString(DatagenSourceConfig.CONFIG_FORMATS_TIMESTAMPS));
        this.MAX_DELAY_SECS = config.getInt(DatagenSourceConfig.CONFIG_DELAYS_BADGEINS);
    }

    public DateTimeFormatter getTimestampFormatter() {
        return this.timestampFormatter;
    }

    public int getMaxDelayInSeconds() {
        return this.MAX_DELAY_SECS;
    }

    private String generateDoorId() {
        int floor = Generators.randomInt(0, 3);
        int door  = Generators.randomInt(10, 60);
        return Generators.randomItem(Locations.BUILDINGS) + "-" +
               floor + "-" +
               door;
    }

    public BadgeIn generate() {
        return new BadgeIn(UUID.randomUUID().toString(),
                           timestampFormatter.format(Generators.nowWithRandomOffset(MAX_DELAY_SECS)),
                           generateDoorId(),
                           faker.name().username());
    }

    // Generate one BadgeIn event on every day during the previous week.
    public List<BadgeIn> generateForPreviousWeek() {
        List<BadgeIn> badgeIns = new ArrayList<>();
        for (int i = 7; i >=1; i--) {
            badgeIns.add(new BadgeIn(UUID.randomUUID().toString(),
                    timestampFormatter.format(ZonedDateTime.now().minusDays(i)),
                    generateDoorId(),
                    faker.name().username()));
        }
        return badgeIns;
    }
}
