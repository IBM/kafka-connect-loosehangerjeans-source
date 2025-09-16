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

import java.time.Instant;
import java.util.ArrayList;

import org.apache.kafka.connect.source.SourceRecord;

/**
 * A collection of Connect SourceRecord objects, that enforces a
 *  requirement that all records must be historical.
 *
 * SourceRecord elements with a timestamp that is greater than
 *  the timestamp specified when the collection is created can not
 *  be added.
 */
public class DatagenHistory extends ArrayList<SourceRecord> {

    final long max;

    public DatagenHistory(Instant maxTimestamp) {
        max = maxTimestamp.toEpochMilli();
    }

    @Override
    public boolean add(SourceRecord e) {
        if (e.timestamp() < max) {
            return super.add(e);
        }
        return false;
    }
}
