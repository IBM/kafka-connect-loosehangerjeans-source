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
 * Information about the addresses (shipping and billing) used for an online order.
 */
public class OnlineAddress {

    /** Shipping address used for an online order. */
    private final Address shippingAddress;

    /** Billing address used for an online order. */
    private final Address billingAddress;

    /** Schema for the events - all fields are required. */
    static final Schema SCHEMA = SchemaBuilder.struct()
            .name("onlineaddress")
            .version(1)
            .field("shippingaddress", Address.SCHEMA)
            .field("billingaddress", Address.SCHEMA)
            .build();

    /** Creates a new OnlineAddress object with the given shipping and billing addresses. */
    public OnlineAddress(Address shippingAddress, Address billingAddress) {
        this.shippingAddress = shippingAddress;
        this.billingAddress = billingAddress;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }

    /** Creates a structure record to use in a Kafka event. */
    public Struct toStruct() {
        Struct struct = new Struct(SCHEMA);
        struct.put(SCHEMA.field("shippingaddress"), shippingAddress.toStruct());
        struct.put(SCHEMA.field("billingaddress"), billingAddress.toStruct());
        return struct;
    }

    @Override
    public String toString() {
        return "Address [shippingAddress=" + shippingAddress + ", billingAddress=" + billingAddress + "]";
    }
}
