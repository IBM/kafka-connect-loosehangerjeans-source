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
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.github.javafaker.Faker;
import com.ibm.eventautomation.demos.loosehangerjeans.utils.Generators;


public abstract class Generator<T> {

    /** Locale used for the data generation. */
    protected static final Locale DEFAULT_LOCALE = Locale.US;

    /** Faker utility available for generators to use. */
    protected final Faker faker = new Faker(DEFAULT_LOCALE);

    /**
     * How frequently (on average) the generator should
     *  emit events.
     */
    private final int INTERVAL_MS;

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
    protected final int MAX_DELAY_SECS;

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
    private final double DUPLICATES_RATIO;


    /** formatter for event timestamps */
    private final DateTimeFormatter timestampFormatter;


    /**
     * @param intervalMs - average interval, in milliseconds, for how
     *  frequently the generator should generate events
     * @param delaySecs - delay, in seconds, to add to the event
     * @param duplicatesRatio - ratio (between 0.0 and 1.0) of the
     *  events that should be duplicated;
     * @param timestampFormatter - formatter to use for generated timestamp strings
     */
    protected Generator(int intervalMs, int delaySecs, double duplicatesRatio, DateTimeFormatter timestampFormatter) {
        INTERVAL_MS = intervalMs;
        MAX_DELAY_SECS = delaySecs;
        DUPLICATES_RATIO = duplicatesRatio;
        this.timestampFormatter = timestampFormatter;
    }

    /**
     * @param intervalMs - average interval, in milliseconds, for how
     *  frequently the generator should generate events
     * @param delaySecs - delay, in seconds, to add to the event
     * @param duplicatesRatio - ratio (between 0.0 and 1.0) of the
     *  events that should be duplicated;
     * @param timestampFormat - format to use for generated timestamp strings
     */
    protected Generator(int intervalMs, int delaySecs, double duplicatesRatio, String timestampFormat) {
        this(intervalMs, delaySecs, duplicatesRatio,
             DateTimeFormatter.ofPattern(timestampFormat));
    }

    /**
     * Generates an event for the provided timestamp.
     *
     * Not appropriate for all generators, in which case a
     *  UnsupportedOperationException will be thrown.
     */
    protected abstract T generateEvent(ZonedDateTime timestamp);

    /**
     * Generates a single event with the current timestamp.
     */
    public T generate() {
        return generateEvent(Generators.nowWithRandomOffset(MAX_DELAY_SECS));
    }


    /**
     * Generates one week's worth of events to create a fake history.
     *  This is intended to be used on the first run of the connector
     *  to create an instant history of events that can be used for
     *  historical aggregations.
     */
    public List<T> generateHistory() {
        final List<T> history = new ArrayList<T>();

        final ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime timestamp = ZonedDateTime.now().minusDays(7);

        while (timestamp.isBefore(now)) {
            T event = generateEvent(timestamp);

            history.add(event);

            if (shouldDuplicate()) {
                history.add(event);
            }

            timestamp = timestamp.plusNanos(INTERVAL_MS * 1_000_000L);
        }

        return history;
    }

    /**
     * Returns a random decision of whether an event should be duplicated.
     *  The frequency for how often this returns true is determined by
     *  a ratio provided to the generator constructor.
     *
     * @return true if the event should be duplicated
     */
    public boolean shouldDuplicate() {
        return Generators.shouldDo(DUPLICATES_RATIO);
    }

    /**
     * Return a string representation of the provided timestamp.
     *
     * The format is determined by a format string provided to the generator
     *  constructor.
     */
    public String formatTimestamp(TemporalAccessor timestamp) {
        return timestampFormatter.format(timestamp);
    }
}
