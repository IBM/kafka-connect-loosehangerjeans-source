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
package com.ibm.eventautomation.demos.loosehangerjeans.tasks;

import java.util.Queue;
import java.util.Timer;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.connect.source.SourceRecord;

import com.ibm.eventautomation.demos.loosehangerjeans.DatagenSourceConfig;
import com.ibm.eventautomation.demos.loosehangerjeans.data.NewCustomer;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Order;
import com.ibm.eventautomation.demos.loosehangerjeans.generators.NewCustomerGenerator;
import com.ibm.eventautomation.demos.loosehangerjeans.generators.OrderGenerator;
import com.ibm.eventautomation.demos.loosehangerjeans.utils.Generators;

/**
 * Timer task intended for repeated execution. Creates new
 *  {@link NewCustomer} events at regular intervals.
 *
 *  It will generate:
 *   - new customer event
 *   - (sometimes) an order by that new customer
 */
public class NewCustomerTask extends DatagenTimerTask {

    /** Identifies the task that generated the events */
    private static final String ORIGIN = NewCustomerTask.class.getName();

    /**
     * The generator can simulate that some new customers place an order.
     *  This variable indicates the ratio of new customers to create
     *  a corresponding {@link Order} for.
     *
     * Ratio is between 0.0 and 1.0.
     *
     * Setting this to 0.0 means no Order events will be generated.
     * Setting this to 1.0 means an Order event will be generated for every NewCustomer
     */
    private double firstOrderRatio;

    /** Minimum time to wait (in milliseconds) before creating the Order */
    private int firstOrderMinDelay;
    /** Maximum time to wait (in milliseconds) before creating the Order */
    private int firstOrderMaxDelay;

    /** Helper class for generating NewCustomer events. */
    private NewCustomerGenerator generator;

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
    private double duplicatesRatio;


    public NewCustomerTask(AbstractConfig config,
                           OrderGenerator orderGenerator,
                           Queue<SourceRecord> queue,
                           Timer timer)
    {
        super(orderGenerator, queue, timer);

        generator = new NewCustomerGenerator(config);

        firstOrderRatio = config.getDouble(DatagenSourceConfig.CONFIG_NEWCUSTOMERS_ORDER_RATIO);
        firstOrderMinDelay = config.getInt(DatagenSourceConfig.CONFIG_NEWCUSTOMERS_ORDER_MIN_DELAY);
        firstOrderMaxDelay = config.getInt(DatagenSourceConfig.CONFIG_NEWCUSTOMERS_ORDER_MAX_DELAY);

        this.duplicatesRatio = config.getDouble(DatagenSourceConfig.CONFIG_DUPLICATE_NEWCUSTOMERS);
    }


    @Override
    public void run() {
        // create the new customer
        NewCustomer newCustomer = generator.generate();
        SourceRecord rec = newCustomer.createSourceRecord(ORIGIN);
        queue.add(rec);

        // optionally, duplicate the new customer event
        if (Generators.shouldDo(duplicatesRatio)) {
            queue.add(rec);
        }

        // sometimes, let the customer make their first order
        if (Generators.shouldDo(firstOrderRatio)) {
            int orderDelay = Generators.randomInt(firstOrderMinDelay, firstOrderMaxDelay);

            scheduleOrder(ORIGIN,
                          orderDelay,
                          newCustomer.getCustomer());
        }
    }

}
