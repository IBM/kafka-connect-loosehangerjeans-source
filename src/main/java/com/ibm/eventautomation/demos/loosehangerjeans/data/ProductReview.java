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
import org.apache.kafka.connect.source.SourceRecord;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Represents an event for a product review.
 */
public class ProductReview extends LoosehangerData {

    /** Unique ID for this product review. */
    private final String id;

    /** Time that the product review was posted. */
    private final String timestamp;

    /** Short description of the product. */
    private final String product;

    /** Size of the product. */
    private final String size;

    /** Review for the product. */
    private final Review review;

    /** Schema for the events - the size is optional. */
    private static final Schema SCHEMA = SchemaBuilder.struct()
            .name("productreview")
            .version(1)
            .field("id",            Schema.STRING_SCHEMA)
            .field("product",       Schema.STRING_SCHEMA)
            .field("size",          Schema.OPTIONAL_STRING_SCHEMA)
            .field("review",        Review.SCHEMA)
            .field("reviewtime",    Schema.STRING_SCHEMA)
            .build();

    /** Creates a {@link ProductReview} object using the provided details. */
    public ProductReview(String id, String timestamp, String product, String size, Review review, ZonedDateTime recordTimestamp) {
        super(recordTimestamp);

        this.id = id;
        this.timestamp = timestamp;
        this.product = product;
        this.size = size;
        this.review = review;
    }

    /** Creates a {@link ProductReview} using the provided details.
     * The ID is generated randomly.
     * */
    public ProductReview(String timestamp, String product, String size, Review review, ZonedDateTime recordTimestamp) {
        this(UUID.randomUUID().toString(), timestamp, product, size, review, recordTimestamp);
    }

    public String getId() {
        return id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getProduct() {
        return product;
    }

    public String getSize() {
        return size;
    }

    public Review getReview() {
        return review;
    }

    public SourceRecord createSourceRecord(String topicName) {
        return super.createSourceRecord(topicName, "productreview");
    }

    @Override
    protected String getKey() {
        return id;
    }

    @Override
    protected Schema getValueSchema() {
        return SCHEMA;
    }

    @Override
    protected Struct getValue() {
        Struct struct = new Struct(SCHEMA);
        struct.put(SCHEMA.field("id"),              id);
        struct.put(SCHEMA.field("product"),         product);
        struct.put(SCHEMA.field("size"),            size);
        struct.put(SCHEMA.field("review"),          review.toStruct());
        struct.put(SCHEMA.field("reviewtime"),      timestamp);
        return struct;
    }

    @Override
    public String toString() {
        return "ProductReview [id=" + id + ", timestamp=" + timestamp + ", product=" + product + ", size=" + size
                + ", review=" + review + "]";
    }
}
