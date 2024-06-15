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
 * Information about a product return.
 */
public class ProductReturn {

    /** Product that is returned. */
    private final Product product;

    /** Quantity of the product that is returned. */
    private final int quantity;

    /** Reason that the customer gave for returning the product. */
    private final String reason;

    private static final Schema PRODUCT_INFO_SCHEMA = SchemaBuilder.struct()
            .name("productinfo")
            .version(1)
            .field("id",        Schema.STRING_SCHEMA)
            .field("size",      Schema.STRING_SCHEMA)
            .build();

    /** Schema for the events - all fields are required. */
    public static final Schema SCHEMA = SchemaBuilder.struct()
            .name("productreturn")
            .version(1)
            .field("product",       PRODUCT_INFO_SCHEMA)
            .field("quantity",      Schema.INT32_SCHEMA)
            .field("reason",        Schema.STRING_SCHEMA)
            .build();

    /** Creates a product return with the provided details. */
    public ProductReturn(Product product, int quantity, String reason) {
        this.product = product;
        this.quantity = quantity;
        this.reason = reason;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getReason() {
        return reason;
    }

    /** Creates a structure record to use in a Kafka event. */
    public Struct toStruct() {
        Struct productInfoStruct = new Struct(PRODUCT_INFO_SCHEMA);
        productInfoStruct.put(PRODUCT_INFO_SCHEMA.field("id"),      product.getShortDescription());
        productInfoStruct.put(PRODUCT_INFO_SCHEMA.field("size"),    product.getSize());
        Struct struct = new Struct(SCHEMA);
        struct.put(SCHEMA.field("product"),     productInfoStruct);
        struct.put(SCHEMA.field("quantity"),    quantity);
        struct.put(SCHEMA.field("reason"),      reason);
        return struct;
    }

    @Override
    public String toString() {
        return "ProductReturn [product=" + product + ", quantity=" + quantity + ", reason=" + reason + "]";
    }
}
