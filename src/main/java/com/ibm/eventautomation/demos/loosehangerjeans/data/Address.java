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

import com.github.javafaker.Faker;
import com.ibm.eventautomation.demos.loosehangerjeans.utils.Generators;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Information about an address.
 */
public class Address {

    /** The number of the address. */
    private final Integer number;

    /** The street of the address. */
    private final String street;

    /** The city of the address. */
    private final String city;

    /** The zipcode of the address. */
    private final String zipcode;

    /** The country of the address. */
    private final Country country;

    /** A list of phone numbers. */
    private final List<String> phones;

    /** Schema for the events - the number, street and phones fields are optional. */
    public static final Schema SCHEMA = SchemaBuilder.struct()
            .name("address")
            .version(1)
            .field("number",    Schema.OPTIONAL_INT32_SCHEMA)
            .field("street",    Schema.OPTIONAL_STRING_SCHEMA)
            .field("city",      Schema.STRING_SCHEMA)
            .field("zipcode",   Schema.STRING_SCHEMA)
            .field("country",   Country.SCHEMA)
            .field("phones",    SchemaBuilder.array(Schema.STRING_SCHEMA).optional().build())
            .build();

    /** Creates an address using the provided details. */
    public Address(Integer number, String street, String city, String zipcode, Country country, List<String> phones) {
        this.number = number;
        this.street = street;
        this.city = city;
        this.zipcode = zipcode;
        this.country = country;
        this.phones = phones;
    }

    /**
     * Uses the provided faker object, locale and min, max phone count to create an address.
     */
    public static Address create(Faker faker, Country country, int minPhones, int maxPhones) {
        // Generate some phone numbers randomly.
        int phoneCount = Generators.randomInt(minPhones, maxPhones);
        List<String> phones = null;
        if (phoneCount > 0 ) {
            phones = new ArrayList<>();
            for (int i = 0; i < phoneCount; i++) {
                phones.add(faker.phoneNumber().cellPhone());
            }
        }

        // Generate the address.
        com.github.javafaker.Address fakerAddress = faker.address();
        return new Address(Integer.valueOf(fakerAddress.streetAddressNumber()),
                fakerAddress.streetName(),
                fakerAddress.cityName(),
                fakerAddress.zipCode(),
                country,
                phones);
    }

    public Integer getNumber() {
        return number;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getZipcode() {
        return zipcode;
    }

    public Country getCountry() {
        return country;
    }

    public List<String> getPhones() {
        return phones;
    }

    /** Creates a structure record to use in a Kafka event. */
    public Struct toStruct() {
        Struct struct = new Struct(SCHEMA);
        struct.put(SCHEMA.field("number"),      number);
        struct.put(SCHEMA.field("street"),      street);
        struct.put(SCHEMA.field("city"),        city);
        struct.put(SCHEMA.field("zipcode"),     zipcode);
        struct.put(SCHEMA.field("country"),     country.toStruct());
        struct.put(SCHEMA.field("phones"),      phones);
        return struct;
    }

    @Override
    public String toString() {
        return "Address [number=" + number + ", street=" + street + ", city=" + city
                + ", zipcode=" + zipcode + ", country=" + country + ", phones="
                + phones != null ? Arrays.toString(phones.toArray()) : null + "]";
    }
}
