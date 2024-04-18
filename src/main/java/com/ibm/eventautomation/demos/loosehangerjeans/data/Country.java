/**
 * Copyright 2024 IBM Corp. All Rights Reserved.
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
package com.ibm.eventautomation.demos.loosehangerjeans.data;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;

/**
 * Information about a country.
 */
public class Country {

    /** The two-letter country code */
    private final String code;

    /** The name of the country */
    private final String name;

    /** Schema for the events - all fields are required. */
    public static final Schema SCHEMA = SchemaBuilder.struct()
            .name("country")
            .version(1)
            .field("code",      Schema.STRING_SCHEMA)
            .field("name",      Schema.STRING_SCHEMA)
            .build();

    /** Creates a country object using the provided code and name. */
    public Country(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /** Creates a structure record to use in a Kafka event. */
    public Struct toStruct() {
        Struct struct = new Struct(SCHEMA);
        struct.put(SCHEMA.field("code"),    code);
        struct.put(SCHEMA.field("name"),    name);
        return struct;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Country [code=" + code + ", name=" + name + "]";
    }
}
