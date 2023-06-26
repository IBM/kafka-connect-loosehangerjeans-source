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

import java.util.List;

import org.apache.kafka.common.config.AbstractConfig;

import com.ibm.eventautomation.demos.loosehangerjeans.DatagenSourceConfig;
import com.ibm.eventautomation.demos.loosehangerjeans.utils.Generators;

/**
 * Generates a product name, by combining randomly selected words
 *  from a few lists.
 */
public class ProductGenerator {

    private final List<String> sizes;
    private final List<String> materials;
    private final List<String> styles;

    private final String product;


    public ProductGenerator(AbstractConfig config)
    {
        this.sizes = config.getList(DatagenSourceConfig.CONFIG_PRODUCTS_SIZES);
        this.materials = config.getList(DatagenSourceConfig.CONFIG_PRODUCTS_MATERIALS);
        this.styles = config.getList(DatagenSourceConfig.CONFIG_PRODUCTS_STYLES);
        this.product = config.getString(DatagenSourceConfig.CONFIG_PRODUCTS_NAME);
    }

    public String generate() {
        return Generators.randomItem(sizes) + " " +
               Generators.randomItem(materials) + " " +
               Generators.randomItem(styles) + " " +
               product;
    }
}
