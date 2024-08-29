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

import org.apache.kafka.common.config.AbstractConfig;

import com.ibm.eventautomation.demos.loosehangerjeans.DatagenSourceConfig;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Customer;
import com.ibm.eventautomation.demos.loosehangerjeans.data.NewCustomer;

/**
 * Generates a {@link NewCustomer} event using randomly generated data.
 */
public class NewCustomerGenerator extends Generator<NewCustomer> {

    public NewCustomerGenerator(AbstractConfig config) {
        super(config.getInt(DatagenSourceConfig.CONFIG_TIMES_NEWCUSTOMERS),
              config.getInt(DatagenSourceConfig.CONFIG_DELAYS_NEWCUSTOMERS),
              config.getDouble(DatagenSourceConfig.CONFIG_DUPLICATE_NEWCUSTOMERS),
              config.getString(DatagenSourceConfig.CONFIG_FORMATS_TIMESTAMPS));
    }

    @Override
    protected NewCustomer generateEvent(ZonedDateTime timestamp) {
        return new NewCustomer(formatTimestamp(timestamp),
                               new Customer(faker),
                               timestamp);
    }
}
