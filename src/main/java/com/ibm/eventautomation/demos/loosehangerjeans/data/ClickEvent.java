/**
 * Copyright 2025 IBM Corp. All Rights Reserved.
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

import java.net.URL;
import java.time.ZonedDateTime;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;

import com.ibm.eventautomation.demos.loosehangerjeans.utils.Generators;

/**
 * Record of a single user action on the Loosehanger website.
 */
public class ClickEvent extends OnlineActivityData {

    public static final String PARTITION = "clickstream";

    /** ID for the session that this online activity event is a part of. */
    private final String sessionId;

    /** Unique ID for this individual online activity event. */
    private final String eventId;

    /** What type of online activity this event describes. */
    private final ClickEventType type;

    /** Logged in user that has performed some online activity. Can be null if user is not logged in. */
    private final OnlineCustomer customer;

    /** Context for the user, such as their device and IP address. */
    private final UserContext context;

    /** URL for the page that was the origin of the online activity. */
    private final String url;

    /** Referrer URL for the visit to the loosehanger page. */
    private final URL referrer;

    /** Product that the user performed an action on. Only relevant to ADD/REMOVE FROM CART events. Null for other event types */
    private final String product;

    /** String representation of the timestamp for the event */
    private final String timestamp;

    /** Schema for the events */
    private static final Schema SCHEMA = SchemaBuilder.struct()
        .name("clickstream")
        .version(1)
        .field("sessionid", Schema.STRING_SCHEMA)
        .field("eventid",   Schema.STRING_SCHEMA)
        .field("type",      Schema.STRING_SCHEMA)
        .field("context",   UserContext.SCHEMA)
        .field("referrer",  Schema.OPTIONAL_STRING_SCHEMA)
        .field("customer",  OnlineCustomer.OPTIONAL_SCHEMA)
        .field("url",       Schema.STRING_SCHEMA)
        .field("product",   Schema.OPTIONAL_STRING_SCHEMA)
        .field("timestamp", Schema.STRING_SCHEMA)
        .build();


    public ClickEvent(ClickEventType type, String timestamp, ZonedDateTime recordTimestamp, String sessionId, UserContext context, OnlineCustomer customer, String url, String product) {
        super(recordTimestamp);
        this.type = type;
        this.sessionId = sessionId;
        this.eventId = Generators.randomString(VALID_EVENTID_CHARS, 12);
        this.timestamp = timestamp;
        this.context = context;
        this.customer = customer;
        this.url = url;
        this.product = product;
        this.referrer = null;
    }
    public ClickEvent(ClickEventType type, String timestamp, ZonedDateTime recordTimestamp, String sessionId, UserContext context, OnlineCustomer customer, String url, URL referrer) {
        super(recordTimestamp);
        this.type = type;
        this.sessionId = sessionId;
        this.eventId = Generators.randomString(VALID_EVENTID_CHARS, 12);
        this.timestamp = timestamp;
        this.context = context;
        this.customer = customer;
        this.url = url;
        this.product = null;
        this.referrer = referrer;
    }
    public ClickEvent(ClickEventType type, String timestamp, ZonedDateTime recordTimestamp, String sessionId, UserContext context, OnlineCustomer customer, String url) {
        super(recordTimestamp);
        this.type = type;
        this.sessionId = sessionId;
        this.eventId = Generators.randomString(VALID_EVENTID_CHARS, 12);
        this.timestamp = timestamp;
        this.context = context;
        this.customer = customer;
        this.url = url;
        this.product = null;
        this.referrer = null;
    }

    @Override
    protected String getKey() {
        return sessionId;
    }

    @Override
    protected Schema getValueSchema() {
        return SCHEMA;
    }

    @Override
    protected Struct getValue() {
        final Struct struct = new Struct(SCHEMA);
        struct.put(SCHEMA.field("sessionid"), sessionId);
        struct.put(SCHEMA.field("eventid"), eventId);
        struct.put(SCHEMA.field("type"), type.name());
        struct.put(SCHEMA.field("context"), context.toStruct());
        if (referrer != null) {
            struct.put(SCHEMA.field("referrer"), referrer.toString());
        }
        if (customer != null) {
            struct.put(SCHEMA.field("customer"), customer.toStruct(true));
        }
        struct.put(SCHEMA.field("url"), url);
        if (product != null) {
            struct.put(SCHEMA.field("product"), product);
        }
        struct.put(SCHEMA.field("timestamp"), timestamp);
        return struct;
    }

    @Override
    public String toString() {
        return "ClickEvent[type=" + type + ", sessionid=" + sessionId + ", eventid=" + eventId + ", url=" + url + ", context=" + context +
            (product == null ? "" : ", product=" + product) +
            (customer == null ? "" : ", customer=" + customer) +
            "]";
    }



    private static final String VALID_EVENTID_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";

    public enum ClickEventType {
        PAGE_VIEW,
        SEARCH,
        PRODUCT_VIEW,
        ADD_TO_CART,
        REMOVE_FROM_CART,
        CART_VIEW,
        CHECKOUT_START,
        CHECKOUT_COMPLETE,
        LOGIN
    }
}
