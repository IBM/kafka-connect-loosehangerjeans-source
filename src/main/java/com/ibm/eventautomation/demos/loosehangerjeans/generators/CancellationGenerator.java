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
import java.util.List;

import org.apache.kafka.common.config.AbstractConfig;

import com.ibm.eventautomation.demos.loosehangerjeans.DatagenSourceConfig;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Cancellation;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Order;
import com.ibm.eventautomation.demos.loosehangerjeans.utils.Generators;

/**
 * Generates a {@link Cancellation} event using randomly generated data.
 */
public class CancellationGenerator extends Generator<Cancellation> {

    /** reasons for cancelling an order will be chosen from this list */
    private final List<String> reasons;

    public CancellationGenerator(AbstractConfig config)
    {
        super(config.getInt(DatagenSourceConfig.CONFIG_TIMES_ORDERS),
              config.getInt(DatagenSourceConfig.CONFIG_DELAYS_CANCELLATIONS),
              config.getDouble(DatagenSourceConfig.CONFIG_DUPLICATE_CANCELLATIONS),
              config.getString(DatagenSourceConfig.CONFIG_FORMATS_TIMESTAMPS));

        this.reasons = config.getList(DatagenSourceConfig.CONFIG_CANCELLATIONS_REASONS);
    }

    public Cancellation generate(Order order) {
        ZonedDateTime timestamp = Generators.nowWithRandomOffset(MAX_DELAY_SECS);
        return new Cancellation(order,
                                Generators.randomItem(reasons),
                                formatTimestamp(timestamp),
                                timestamp);
    }

    @Override
    protected Cancellation generateEvent(ZonedDateTime timestamp) {
        throw new UnsupportedOperationException("Cancellations cannot be generated without an order to cancel");
    }
}
