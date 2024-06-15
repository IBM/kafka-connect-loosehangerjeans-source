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
import java.util.stream.Collectors;

/**
 * Information about a review.
 */
public class Review {

    /** Global rating (integer between 1 and 5 included). */
    private final int rating;

    /** Review comment. */
    private final String comment;

    /** Characteristics evaluated in the current review. */
    private final List<Characteristic> characteristics;

    /** Schema for the events - the comment is optional. */
    public static final Schema SCHEMA = SchemaBuilder.struct()
            .name("review")
            .version(1)
            .field("rating",                Schema.INT32_SCHEMA)
            .field("comment",               Schema.OPTIONAL_STRING_SCHEMA)
            .field("characteristics",       SchemaBuilder.array(Characteristic.SCHEMA).build())
            .build();

    /** Creates a review using the provided details. */
    public Review(int rating, String comment, List<Characteristic> characteristics) {
        this.rating = rating;
        this.comment = comment;
        this.characteristics = characteristics;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public List<Characteristic> getCharacteristics() {
        return characteristics;
    }

    /** Creates a structure record to use in a Kafka event. */
    public Struct toStruct() {
        Struct struct = new Struct(SCHEMA);
        struct.put(SCHEMA.field("rating"),              rating);
        struct.put(SCHEMA.field("comment"),             comment);
        struct.put(SCHEMA.field("characteristics"),     characteristics.stream().map(Characteristic::toStruct).collect(Collectors.toList()));
        return struct;
    }

    @Override
    public String toString() {
        return "Review [rating=" + rating + ", comment='" + comment + "', characteristics="
                + Arrays.toString(characteristics.toArray()) + "]";
    }

    /** Information about a characteristic evaluated in a review. */
    public static class Characteristic {

        /** ID for the characteristic. */
        private final String id;

        /** Ranking for the characteristic (integer between 1 and 3 included).
         * 1: for small, short.
         * 2: for spot on.
         * 3: for large, long.
         * Can be null if no ranking is provided.
         */
        private final Integer ranking;

        /** Schema for the events - the ranking is optional. */
        public static final Schema SCHEMA = SchemaBuilder.struct()
                .name("characteristic")
                .version(1)
                .field("id",            Schema.STRING_SCHEMA)
                .field("ranking",       Schema.OPTIONAL_INT32_SCHEMA)
                .build();

        /** Creates a characteristic using the provided details. */
        public Characteristic(String id, Integer ranking) {
            this.id = id;
            this.ranking = ranking;
        }

        public String getId() {
            return id;
        }

        public Integer getRanking() {
            return ranking;
        }

        /** A characteristic has an issue if the ranking is different from 2 that corresponds to "spot on". */
        public boolean hasIssue() {
            return ranking != 2;
        }

        /** Creates a structure record to use in a Kafka event. */
        public Struct toStruct() {
            Struct struct = new Struct(SCHEMA);
            struct.put(SCHEMA.field("id"),          id);
            struct.put(SCHEMA.field("ranking"),     ranking);
            return struct;
        }

        @Override
        public String toString() {
            return "Characteristic [id=" + id + ", ranking=" + ranking + "]";
        }
    }
}
