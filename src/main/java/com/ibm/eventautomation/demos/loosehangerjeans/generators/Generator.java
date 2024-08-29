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
import java.util.ArrayList;
import java.util.List;


public abstract class Generator<T> {

    private final int INTERVAL_MS;

    /**
     * @param intervalMs - average interval, in milliseconds, for how
     *  frequently the generator should generate events
     */
    protected Generator(int intervalMs) {
        INTERVAL_MS = intervalMs;
    }

    /**
     * Generates an event for the provided timestamp.
     */
    protected abstract T generateEvent(ZonedDateTime timestamp);

    /**
     * Generates one week's worth of events to create a fake history.
     *  This is intended to be used on the first run of the connector
     *  to create an instant history of events that can be used for
     *  historical aggregations.
     */
    protected List<T> generateHistory() {
        final List<T> history = new ArrayList<T>();

        final ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime timestamp = ZonedDateTime.now().minusDays(7);

        while (timestamp.isBefore(now)) {
            history.add(generateEvent(timestamp));

            timestamp = timestamp.plusNanos(INTERVAL_MS * 1_000_000);
        }

        return history;
    }
}
