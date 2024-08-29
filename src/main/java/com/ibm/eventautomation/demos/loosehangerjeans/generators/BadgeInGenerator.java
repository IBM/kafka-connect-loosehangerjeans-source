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

import java.util.UUID;
import java.time.ZonedDateTime;

import org.apache.kafka.common.config.AbstractConfig;

import com.ibm.eventautomation.demos.loosehangerjeans.DatagenSourceConfig;
import com.ibm.eventautomation.demos.loosehangerjeans.data.BadgeIn;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Locations;
import com.ibm.eventautomation.demos.loosehangerjeans.utils.Generators;

/**
 * Generates a {@link BadgeIn} event using randomly generated data.
 */
public class BadgeInGenerator extends Generator<BadgeIn> {

    public BadgeInGenerator(AbstractConfig config)
    {
        super(config.getInt(DatagenSourceConfig.CONFIG_TIMES_BADGEINS),
              config.getInt(DatagenSourceConfig.CONFIG_DELAYS_BADGEINS),
              config.getDouble(DatagenSourceConfig.CONFIG_DUPLICATE_BADGEINS),
              config.getString(DatagenSourceConfig.CONFIG_FORMATS_TIMESTAMPS));
    }

    @Override
    protected BadgeIn generateEvent(ZonedDateTime timestamp) {
        return new BadgeIn(UUID.randomUUID().toString(),
                           formatTimestamp(timestamp),
                           generateDoorId(),
                           faker.name().username(),
                           timestamp);
    }


    private String generateDoorId() {
        int floor = Generators.randomInt(0, 3);
        int door  = Generators.randomInt(10, 60);
        return Generators.randomItem(Locations.BUILDINGS) + "-" +
               floor + "-" +
               door;
    }
}
