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
package com.ibm.eventautomation.demos.loosehangerjeans.generators;

import com.ibm.eventautomation.demos.loosehangerjeans.DatagenSourceConfig;
import com.ibm.eventautomation.demos.loosehangerjeans.data.Transaction;
import com.ibm.eventautomation.demos.loosehangerjeans.utils.Generators;

import org.apache.kafka.common.config.AbstractConfig;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Generates a {@link Transaction} event using randomly generated data.
 */
public class TransactionGenerator extends Generator<Transaction> {

    /** transaction states. */
    enum TransactionState {
        STARTED, PROCESSING, COMPLETED
    }

    /** status of generated transactions. */
    protected static Map<String, LinkedList<TransactionState>> transactionStatuses = new HashMap<>();

    /** transaction context keys. */
    protected final List<String> transactionIds;

    /** minimum amount for randomly selected transaction. */
    protected final double minAmount;

    /** maximum amount for randomly selected transaction. */
    protected final double maxAmount;

    public TransactionGenerator(AbstractConfig config) {
        super(config.getInt(DatagenSourceConfig.CONFIG_TIMES_TRANSACTIONS),
              config.getInt(DatagenSourceConfig.CONFIG_DELAYS_TRANSACTIONS),
              config.getDouble(DatagenSourceConfig.CONFIG_DUPLICATE_TRANSACTIONS),
              config.getString(DatagenSourceConfig.CONFIG_FORMATS_TIMESTAMPS));

        this.transactionIds = IntStream.range(1, config.getInt(DatagenSourceConfig.CONFIG_TRANSACTIONS_IDS))
                                       .mapToObj(number -> "T" + number)
                                       .collect(Collectors.toList());

        this.minAmount = config.getDouble(DatagenSourceConfig.CONFIG_TRANSACTIONS_AMOUNT_MIN);
        this.maxAmount = config.getDouble(DatagenSourceConfig.CONFIG_TRANSACTIONS_AMOUNT_MAX);
    }

    /**
     * Generation of sequences of transaction events.
     * For each transactionId, the sequence can be either
     *   STARTED -> PROCESSING -> PROCESSING -> COMPLETED
     * or
     *   STARTED -> PROCESSING -> PROCESSING
     * The purpose is to be able to detect a pattern like this
     *   (1 x STARTED) followedBy (2 x PROCESSING) notFollowedBy (1 x COMPLETED) within a timeframe
     */
    @Override
    protected Transaction generateEvent(ZonedDateTime timestamp) {
        final Transaction transaction;
        final TransactionState newState;

        String id = Generators.randomItem(transactionIds);

        // outstanding transaction
        if (transactionStatuses.containsKey(id)) {
            final LinkedList<TransactionState> states = transactionStatuses.get(id);
            final TransactionState state = states.getLast();

            // transition STARTED -> PROCESSING
            if (state == TransactionState.STARTED) {
                newState = TransactionState.PROCESSING;
                states.add(newState);
                transactionStatuses.put(id, states);

            // transition PROCESSING -> PROCESSING or PROCESSING -> COMPLETED
            } else if (state == TransactionState.PROCESSING) {
                if (states.stream().filter(s -> s.equals(TransactionState.PROCESSING)).count() < 2) {
                    newState = TransactionState.PROCESSING;
                    states.add(newState);
                    transactionStatuses.put(id, states);
                } else {
                    // randomly generates a transaction completed before closing the transaction
                    if (new Random().nextDouble() <= 0.2) {
                        newState = TransactionState.COMPLETED;
                        transactionStatuses.remove(id);

                    } else {
                        newState = TransactionState.STARTED;
                        states.clear();
                        states.add(newState);
                        transactionStatuses.put(id, states);
                    }
                }

            // default case
            } else {
                newState = TransactionState.STARTED;
                states.add(newState);
                transactionStatuses.put(id, states);
            }

        // new transaction
        } else {
            final LinkedList<TransactionState> states = new LinkedList<>();
            newState = TransactionState.STARTED;
            states.add(newState);
            transactionStatuses.put(id, states);
        }

        transaction = new Transaction(id,
                                      newState.name(),
                                      Generators.randomDouble(minAmount, maxAmount),
                                      formatTimestamp(timestamp),
                                      timestamp);
        return transaction;
    }
}
