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
import java.util.UUID;

import org.apache.kafka.common.config.AbstractConfig;

import com.ibm.eventautomation.demos.loosehangerjeans.DatagenSourceConfig;
import com.ibm.eventautomation.demos.loosehangerjeans.data.StockMovement;
import com.ibm.eventautomation.demos.loosehangerjeans.utils.Generators;

/**
 * Generates a {@link StockMovement} event using randomly generated data.
 */
public class StockMovementGenerator extends Generator<StockMovement> {

    /** warehouse codes will be randomly selected from this list */
    private final List<String> warehouses;

    /** helper class to randomly generate the name of a product */
    private ProductGenerator productGenerator;


    public StockMovementGenerator(AbstractConfig config)
    {
        super(config.getInt(DatagenSourceConfig.CONFIG_TIMES_STOCKMOVEMENTS),
              config.getInt(DatagenSourceConfig.CONFIG_DELAYS_STOCKMOVEMENTS),
              config.getDouble(DatagenSourceConfig.CONFIG_DUPLICATE_STOCKMOVEMENTS),
              config.getString(DatagenSourceConfig.CONFIG_FORMATS_TIMESTAMPS));

        this.productGenerator = new ProductGenerator(config);

        this.warehouses = config.getList(DatagenSourceConfig.CONFIG_LOCATIONS_WAREHOUSES);
    }

    @Override
    protected StockMovement generateEvent(ZonedDateTime timestamp) {
        int quantity = Generators.randomInt(20, 500);

        return new StockMovement(UUID.randomUUID().toString(),
                                 formatTimestamp(timestamp),
                                 Generators.randomItem(warehouses),
                                 productGenerator.generate().getDescription(),
                                 // stock movement quantities are always
                                 //  multiples of ten
                                 quantity - (quantity % 10));
    }
}
