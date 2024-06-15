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

import java.util.Arrays;
import java.util.List;

/**
 * Information about a named address.
 */
public class NamedAddress extends Address {

    private final String name;

    /** Schema for the events - the number, street and phones fields are optional. */
    public static final Schema SCHEMA = SchemaBuilder.struct()
            .name("namedaddress")
            .version(1)
            .field("name",      Schema.STRING_SCHEMA)
            .field("number",    Schema.OPTIONAL_INT32_SCHEMA)
            .field("street",    Schema.OPTIONAL_STRING_SCHEMA)
            .field("city",      Schema.STRING_SCHEMA)
            .field("zipcode",   Schema.STRING_SCHEMA)
            .field("country",   Country.SCHEMA)
            .field("phones",    SchemaBuilder.array(Schema.STRING_SCHEMA).optional().build())
            .build();

    /** Creates a named address using the provided details. */
    public NamedAddress(String name, Integer number, String street, String city, String zipcode, Country country, List<String> phones) {
        super(number, street, city, zipcode, country, phones);
        this.name = name;
    }

    public static NamedAddress create(String name, Address address) {
        return new NamedAddress(name, address.getNumber(), address.getStreet(), address.getCity(), address.getZipcode(),
                address.getCountry(), address.getPhones());
    }

    public String getName() {
        return name;
    }

    /** Creates a structure record to use in a Kafka event. */
    public Struct toStruct() {
        Struct struct = new Struct(SCHEMA);
        struct.put(SCHEMA.field("name"),        name);
        struct.put(SCHEMA.field("number"),      getNumber());
        struct.put(SCHEMA.field("street"),      getStreet());
        struct.put(SCHEMA.field("city"),        getCity());
        struct.put(SCHEMA.field("zipcode"),     getZipcode());
        struct.put(SCHEMA.field("country"),     getCountry().toStruct());
        struct.put(SCHEMA.field("phones"),      getPhones());
        return struct;
    }

    @Override
    public String toString() {
        return "NamedAddress [name=" + name + ", number=" + getNumber() + ", street=" + getStreet()
                + ", city=" + getCity() + ", zipcode=" + getZipcode() + ", country=" + getCountry()
                + ", phones=" + getPhones() != null ? Arrays.toString(getPhones().toArray()) : null + "]";
    }
}
