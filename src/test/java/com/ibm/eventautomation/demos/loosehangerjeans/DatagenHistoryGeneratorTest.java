/**
 * Copyright 2025 IBM Corp. All Rights Reserved.
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

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.connect.source.SourceRecord;




public class DatagenHistoryGeneratorTest {

    @Test
    public void testHistoryEvents() {
        AbstractConfig config = new AbstractConfig(DatagenSourceConfig.CONFIG_DEF, Collections.EMPTY_MAP);

        final long now = Instant.now().toEpochMilli();
        List<String> expectedTopicNames = new ArrayList<>(Arrays.asList(
            config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_ORDERS),
            config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_CANCELLATIONS),
            config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_STOCKMOVEMENTS),
            config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_BADGEINS),
            config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_CUSTOMERS),
            config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_SENSORREADINGS),
            config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_ONLINEORDERS),
            config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_OUTOFSTOCKS),
            config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_RETURNREQUESTS),
            config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_PRODUCTREVIEWS),
            config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_TRANSACTIONS),
            config.getString(DatagenSourceConfig.CONFIG_TOPICNAME_ABANDONEDORDERS)
        ));

        List<SourceRecord> records = new DatagenHistoryGenerator().generateHistory(config);

        assertTrue(
            records.size() > 1_100_000,
            "Unexpectedly small number of events (" + records.size() + ") generated");

        for (SourceRecord record : records) {
            long timestamp = record.timestamp();

            assertFalse(
                timestamp > now,
                "Event timestamp " + timestamp +
                    " for historical event on " + record.topic() +
                    " is in the future (current time: " + now + ")");

            expectedTopicNames.remove(record.topic());
        }

        assertTrue(
            expectedTopicNames.isEmpty(),
            "No historical records generated for topics " +
                String.join(",", expectedTopicNames));
    }
}

