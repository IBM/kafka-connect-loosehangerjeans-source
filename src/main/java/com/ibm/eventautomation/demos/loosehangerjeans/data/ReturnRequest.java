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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Represents an event for a product return request.
 * Several products can be returned in the same request.
 */
public class ReturnRequest {

    /** Unique ID for this return request. */
    private final String id;

    /** Time that the return request was issued. */
    private final String timestamp;

    /** Details of the customer who issued the return request. */
    private final OnlineCustomer customer;

    /** Addresses used for the product order.
     * There should be at least one address (that is the billing address) and there
     * may be an additional address (that is the shipping address) if the shipping
     * address is different from the billing address.
     */
    private final List<NamedAddress> addresses;

    /** Requested product returns. */
    private final List<ProductReturn> returns;

    /** Schema for the events - all fields are required. */
    private static final Schema SCHEMA = SchemaBuilder.struct()
            .name("returnrequest")
            .version(1)
            .field("id",            Schema.STRING_SCHEMA)
            .field("customer",      OnlineCustomer.SCHEMA)
            .field("addresses",     SchemaBuilder.array(NamedAddress.SCHEMA))
            .field("returns",       SchemaBuilder.array(ProductReturn.SCHEMA))
            .field("returntime",    Schema.STRING_SCHEMA)
            .build();

    /** Creates a {@link ReturnRequest} using the provided details. */
    public ReturnRequest(String id, String timestamp, OnlineCustomer customer, List<NamedAddress> addresses, List<ProductReturn> returns) {
        this.id = id;
        this.timestamp = timestamp;
        this.customer = customer;
        this.addresses = addresses;
        this.returns = returns;
    }

    /** Creates a {@link ReturnRequest} using the provided details.
     * The ID is generated randomly.
     * */
    public ReturnRequest(String timestamp, OnlineCustomer customer, List<NamedAddress> addresses, List<ProductReturn> returns) {
        this(UUID.randomUUID().toString(),timestamp, customer, addresses, returns);
    }

    public String getId() {
        return id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public OnlineCustomer getCustomer() {
        return customer;
    }

    public List<NamedAddress> getAddresses() {
        return addresses;
    }

    public List<ProductReturn> getReturns() {
        return returns;
    }

    /** Creates a source record to pass to Kafka Connect for storage in Kafka. */
    public SourceRecord createSourceRecord(String topicname) {
        Struct struct = new Struct(SCHEMA);
        struct.put(SCHEMA.field("id"),              id);
        struct.put(SCHEMA.field("customer"),        customer.toStruct());
        struct.put(SCHEMA.field("addresses"),       addresses.stream().map(NamedAddress::toStruct).collect(Collectors.toList()));
        struct.put(SCHEMA.field("returns"),         returns.stream().map(ProductReturn::toStruct).collect(Collectors.toList()));
        struct.put(SCHEMA.field("returntime"),      timestamp);

        return new SourceRecord(createSourcePartition(),
                createSourceOffset(),
                topicname,
                Schema.STRING_SCHEMA, id,
                SCHEMA,
                struct);
    }

    private Map<String, Object> createSourcePartition() {
        return Collections.singletonMap("partition", "returnrequest");
    }

    private Map<String, Object> createSourceOffset() {
        return Collections.singletonMap("offset", timestamp);
    }

    @Override
    public String toString() {
        return "ReturnRequest [id=" + id + ", timestamp=" + timestamp + ", customer=" + customer
                + ", addresses=" + Arrays.toString(addresses.toArray()) + ", returns="
                + Arrays.toString(returns.toArray()) + "]";
    }
}
