/**
 * Copyright 2023, 2025 IBM Corp. All Rights Reserved.
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
package com.ibm.eventautomation.demos.loosehangerjeans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.NonEmptyString;
import org.apache.kafka.common.config.ConfigDef.NonNullValidator;
import org.apache.kafka.common.config.ConfigDef.Range;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.apache.kafka.common.config.ConfigDef.Validator;
import org.apache.kafka.common.config.ConfigDef.Width;
import org.apache.kafka.common.config.ConfigException;

public class DatagenSourceConfig {

    private static final String CONFIG_GROUP_FORMATS = "Formats";
    public static final String CONFIG_FORMATS_TIMESTAMPS        = "formats.timestamps";
    public static final String CONFIG_FORMATS_TIMESTAMPS_LTZ    = "formats.timestamps.ltz";

    private static final String CONFIG_GROUP_TOPICNAMES = "Topic names";
    public static final String CONFIG_TOPICNAME_ORDERS           = "topic.name.orders";
    public static final String CONFIG_TOPICNAME_CANCELLATIONS    = "topic.name.cancellations";
    public static final String CONFIG_TOPICNAME_STOCKMOVEMENTS   = "topic.name.stockmovements";
    public static final String CONFIG_TOPICNAME_BADGEINS         = "topic.name.badgeins";
    public static final String CONFIG_TOPICNAME_CUSTOMERS        = "topic.name.newcustomers";
    public static final String CONFIG_TOPICNAME_SENSORREADINGS   = "topic.name.sensorreadings";
    public static final String CONFIG_TOPICNAME_ONLINEORDERS     = "topic.name.onlineorders";
    public static final String CONFIG_TOPICNAME_OUTOFSTOCKS      = "topic.name.outofstocks";
    public static final String CONFIG_TOPICNAME_RETURNREQUESTS   = "topic.name.returnrequests";
    public static final String CONFIG_TOPICNAME_PRODUCTREVIEWS   = "topic.name.productreviews";
    public static final String CONFIG_TOPICNAME_TRANSACTIONS     = "topic.name.transactions";
    public static final String CONFIG_TOPICNAME_ABANDONEDORDERS  = "topic.name.abandonedorders";
    public static final String CONFIG_TOPICNAME_CLICKTRACKING    = "topic.name.clicktracking";

    private static final String CONFIG_GROUP_LOCATIONS = "Locations";
    public static final String CONFIG_LOCATIONS_REGIONS    = "locations.regions";
    public static final String CONFIG_LOCATIONS_WAREHOUSES = "locations.warehouses";
    public static final String CONFIG_LOCATIONS_REGIONS_COUNTRIES_MAP = "locations.regions.countries";

    private static final String CONFIG_GROUP_PRODUCTS = "Products";
    public static final String CONFIG_PRODUCTS_SIZES     = "products.sizes";
    public static final String CONFIG_PRODUCTS_MATERIALS = "products.materials";
    public static final String CONFIG_PRODUCTS_STYLES    = "products.styles";
    public static final String CONFIG_PRODUCTS_NAME      = "products.name";

    private static final String CONFIG_GROUP_DYNAMICPRICING = "Dynamic pricing";
    public static final String CONFIG_PRODUCTS_MIN_PRICE              = "prices.min";
    public static final String CONFIG_PRODUCTS_MAX_PRICE              = "prices.max";
    public static final String CONFIG_DYNAMICPRICING_PRICE_CHANGE_MAX = "prices.maxvariation";

    private static final String CONFIG_GROUP_ORDERS = "Orders";
    public static final String CONFIG_ORDERS_SMALL_MIN = "orders.small.quantity.min";
    public static final String CONFIG_ORDERS_SMALL_MAX = "orders.small.quantity.max";
    public static final String CONFIG_ORDERS_LARGE_MIN = "orders.large.quantity.min";
    public static final String CONFIG_ORDERS_LARGE_MAX = "orders.large.quantity.max";

    private static final String CONFIG_GROUP_CANCELLATIONS = "Cancellations";
    public static final String CONFIG_CANCELLATIONS_RATIO     = "cancellations.ratio";
    public static final String CONFIG_CANCELLATIONS_MIN_DELAY = "cancellations.delay.ms.min";
    public static final String CONFIG_CANCELLATIONS_MAX_DELAY = "cancellations.delay.ms.max";
    public static final String CONFIG_CANCELLATIONS_REASONS   = "cancellations.reasons";

    private static final String CONFIG_GROUP_SUSPICIOUSORDERS = "Suspicious orders";
    public static final String CONFIG_SUSPICIOUSCANCELLATIONS_MIN_DELAY = "suspicious.cancellations.delay.ms.min";
    public static final String CONFIG_SUSPICIOUSCANCELLATIONS_MAX_DELAY = "suspicious.cancellations.delay.ms.max";
    public static final String CONFIG_SUSPICIOUSCANCELLATIONS_NUM       = "suspicious.cancellations.max";
    public static final String CONFIG_SUSPICIOUSCANCELLATIONS_CUSTOMER  = "suspicious.cancellations.customernames";

    private static final String CONFIG_GROUP_NEWCUSTOMERS = "New customers";
    public static final String CONFIG_NEWCUSTOMERS_ORDER_RATIO     = "newcustomers.order.ratio";
    public static final String CONFIG_NEWCUSTOMERS_ORDER_MIN_DELAY = "newcustomers.order.delay.ms.min";
    public static final String CONFIG_NEWCUSTOMERS_ORDER_MAX_DELAY = "newcustomers.order.delay.ms.max";

    private static final String CONFIG_GROUP_ONLINEORDERS = "Online orders";
    public static final String CONFIG_ONLINEORDERS_PRODUCTS_MAX         = "onlineorders.products.max";
    public static final String CONFIG_ONLINEORDERS_CUSTOMER_EMAILS_MIN  = "onlineorders.customer.emails.min";
    public static final String CONFIG_ONLINEORDERS_CUSTOMER_EMAILS_MAX  = "onlineorders.customer.emails.max";
    public static final String CONFIG_ONLINEORDERS_ADDRESS_PHONES_MIN   = "onlineorders.address.phones.min";
    public static final String CONFIG_ONLINEORDERS_ADDRESS_PHONES_MAX   = "onlineorders.address.phones.max";
    public static final String CONFIG_ONLINEORDERS_REUSE_ADDRESS_RATIO  = "onlineorders.reuse.address.ratio";
    public static final String CONFIG_ONLINEORDERS_OUTOFSTOCK_RATIO     = "onlineorders.outofstock.ratio";
    public static final String CONFIG_ONLINEORDERS_CITIES               = "onlineorders.cities";
    public static final String CONFIG_ONLINEORDERS_URL                  = "onlineorders.url";
    public static final String CONFIG_ONLINEORDERS_CLICKEVENTS_MAX      = "onlineorders.clickevents.max";
    public static final String CONFIG_ONLINEORDERS_ABANDONED_RATIO      = "onlineorders.abandoned.ratio";
    public static final String CONFIG_ONLINEORDERS_LOGGEDIN_RATIO       = "onlineorders.loggedin.ratio";
    public static final String CONFIG_ONLINEORDERS_MARKETING_RATIO      = "onlineorders.marketing.ratio";
    public static final String CONFIG_ONLINEORDERS_SESSIONS_MAX         = "onlineorders.sessions.max";

    private static final String CONFIG_GROUP_OUTOFSTOCKS = "Out-of-stocks";
    public static final String CONFIG_OUTOFSTOCKS_RESTOCKING_MIN_DELAY  = "outofstocks.restocking.delay.days.min";
    public static final String CONFIG_OUTOFSTOCKS_RESTOCKING_MAX_DELAY  = "outofstocks.restocking.delay.days.max";
    public static final String CONFIG_OUTOFSTOCKS_MIN_DELAY             = "outofstocks.delay.ms.min";
    public static final String CONFIG_OUTOFSTOCKS_MAX_DELAY             = "outofstocks.delay.ms.max";

    private static final String CONFIG_GROUP_RETURNREQUESTS = "Return requests";
    public static final String CONFIG_RETURNREQUESTS_PRODUCTS_MIN                   = "returnrequests.products.min";
    public static final String CONFIG_RETURNREQUESTS_PRODUCTS_MAX                   = "returnrequests.products.max";
    public static final String CONFIG_RETURNREQUESTS_PRODUCT_QUANTITY_MIN           = "returnrequests.product.quantity.min";
    public static final String CONFIG_RETURNREQUESTS_PRODUCT_QUANTITY_MAX           = "returnrequests.product.quantity.max";
    public static final String CONFIG_RETURNREQUESTS_CUSTOMER_EMAILS_MIN            = "returnrequests.customer.emails.min";
    public static final String CONFIG_RETURNREQUESTS_CUSTOMER_EMAILS_MAX            = "returnrequests.customer.emails.max";
    public static final String CONFIG_RETURNREQUESTS_ADDRESS_PHONES_MIN             = "returnrequests.address.phones.min";
    public static final String CONFIG_RETURNREQUESTS_ADDRESS_PHONES_MAX             = "returnrequests.address.phones.max";
    public static final String CONFIG_RETURNREQUESTS_REUSE_ADDRESS_RATIO            = "returnrequests.reuse.address.ratio";
    public static final String CONFIG_RETURNREQUESTS_REASONS                        = "returnrequests.reasons";
    public static final String CONFIG_RETURNREQUESTS_REVIEW_RATIO                   = "returnrequests.review.ratio";
    public static final String CONFIG_RETURNREQUESTS_PRODUCT_WITH_SIZE_ISSUE_RATIO  = "returnrequests.product.with.size.issue.ratio";

    private static final String CONFIG_GROUP_PRODUCTREVIEWS = "Product reviews";
    public static final String CONFIG_PRODUCTREVIEWS_PRODUCTS_WITH_SIZE_ISSUE_COUNT     = "productreviews.products.with.size.issue.count";
    public static final String CONFIG_PRODUCTREVIEWS_REVIEW_WITH_SIZE_ISSUE_RATIO       = "productreviews.review.with.size.issue.ratio";
    public static final String CONFIG_PRODUCTREVIEWS_MIN_DELAY                          = "productreviews.delay.ms.min";
    public static final String CONFIG_PRODUCTREVIEWS_MAX_DELAY                          = "productreviews.delay.ms.max";

    private static final String CONFIG_GROUP_TRANSACTIONS = "Transactions";
    public static final String CONFIG_TRANSACTIONS_IDS         = "transactions.max.ids";
    public static final String CONFIG_TRANSACTIONS_AMOUNT_MIN  = "transactions.amount.min";
    public static final String CONFIG_TRANSACTIONS_AMOUNT_MAX  = "transactions.amount.max";
    public static final String CONFIG_TRANSACTIONS_VALID_RATIO = "transactions.valid.ratio";

    private static final String CONFIG_GROUP_DELAYS = "Event delays";
    public static final String CONFIG_DELAYS_ORDERS           = "eventdelays.orders.secs.max";
    public static final String CONFIG_DELAYS_CANCELLATIONS    = "eventdelays.cancellations.secs.max";
    public static final String CONFIG_DELAYS_STOCKMOVEMENTS   = "eventdelays.stockmovements.secs.max";
    public static final String CONFIG_DELAYS_BADGEINS         = "eventdelays.badgeins.secs.max";
    public static final String CONFIG_DELAYS_NEWCUSTOMERS     = "eventdelays.newcustomers.secs.max";
    public static final String CONFIG_DELAYS_SENSORREADINGS   = "eventdelays.sensorreadings.secs.max";
    public static final String CONFIG_DELAYS_OUTOFSTOCKS      = "eventdelays.outofstocks.secs.max";
    public static final String CONFIG_DELAYS_RETURNREQUESTS   = "eventdelays.returnrequests.secs.max";
    public static final String CONFIG_DELAYS_PRODUCTREVIEWS   = "eventdelays.productreviews.secs.max";
    public static final String CONFIG_DELAYS_TRANSACTIONS     = "eventdelays.transactions.secs.max";

    private static final String CONFIG_GROUP_DUPLICATES = "Duplicate events";
    public static final String CONFIG_DUPLICATE_ORDERS          = "duplicates.orders.ratio";
    public static final String CONFIG_DUPLICATE_CANCELLATIONS   = "duplicates.cancellations.ratio";
    public static final String CONFIG_DUPLICATE_STOCKMOVEMENTS  = "duplicates.stockmovements.ratio";
    public static final String CONFIG_DUPLICATE_BADGEINS        = "duplicates.badgeins.ratio";
    public static final String CONFIG_DUPLICATE_NEWCUSTOMERS    = "duplicates.newcustomers.ratio";
    public static final String CONFIG_DUPLICATE_SENSORREADINGS  = "duplicates.sensorreadings.ratio";
    public static final String CONFIG_DUPLICATE_ONLINEORDERS    = "duplicates.onlineorders.ratio";
    public static final String CONFIG_DUPLICATE_OUTOFSTOCKS     = "duplicates.outofstocks.ratio";
    public static final String CONFIG_DUPLICATE_RETURNREQUESTS  = "duplicates.returnrequests.ratio";
    public static final String CONFIG_DUPLICATE_PRODUCTREVIEWS  = "duplicates.productreviews.ratio";
    public static final String CONFIG_DUPLICATE_TRANSACTIONS    = "duplicates.transactions.ratio";
    public static final String CONFIG_DUPLICATE_ABANDONEDORDERS = "duplicates.abandonedorders.ratio";
    public static final String CONFIG_DUPLICATE_CLICKTRACKING   = "duplicates.clicktracking.ratio";

    private static final String CONFIG_GROUP_TIMES = "Timings";
    public static final String CONFIG_TIMES_ORDERS             = "timings.ms.orders";
    public static final String CONFIG_TIMES_FALSEPOSITIVES     = "timings.ms.falsepositives";
    public static final String CONFIG_TIMES_SUSPICIOUSORDERS   = "timings.ms.suspiciousorders";
    public static final String CONFIG_TIMES_STOCKMOVEMENTS     = "timings.ms.stockmovements";
    public static final String CONFIG_TIMES_BADGEINS           = "timings.ms.badgeins";
    public static final String CONFIG_TIMES_NEWCUSTOMERS       = "timings.ms.newcustomers";
    public static final String CONFIG_TIMES_SENSORREADINGS     = "timings.ms.sensorreadings";
    public static final String CONFIG_TIMES_HIGHSENSORREADINGS = "timings.ms.highsensorreadings";
    public static final String CONFIG_TIMES_ONLINEORDERS       = "timings.ms.onlineorders";
    public static final String CONFIG_TIMES_RETURNREQUESTS     = "timings.ms.returnrequests";
    public static final String CONFIG_TIMES_PRODUCTREVIEWS     = "timings.ms.productreviews";
    public static final String CONFIG_TIMES_TRANSACTIONS       = "timings.ms.transactions";
    public static final String CONFIG_TIMES_CLICKTRACKING      = "timings.ms.clicktracking";

    private static final String CONFIG_GROUP_BEHAVIOR = "Behavior";
    public static final String CONFIG_BEHAVIOR_STARTUPHISTORY = "startup.history.enabled";

    public static final String CONFIG_GROUP_PRIORITIES = "Priorities";
    public static final String CONFIG_PRIORITIES = "priorities.priority";

    public static final ConfigDef CONFIG_DEF = new ConfigDef()
        //
        // format to use
        //
        .define(CONFIG_FORMATS_TIMESTAMPS,
                    Type.STRING,
                    "yyyy-MM-dd HH:mm:ss.SSS",
                    new NonEmptyString(),
                    Importance.LOW,
                    "Format to use for timestamps generated for events.",
                    CONFIG_GROUP_FORMATS, 1, Width.LONG, "Timestamp format")
        .define(CONFIG_FORMATS_TIMESTAMPS_LTZ,
                    Type.STRING,
                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                    new NonEmptyString(),
                    Importance.LOW,
                    "Format to use for timestamps with local time zone generated for events.",
                    CONFIG_GROUP_FORMATS, 1, Width.LONG, "Timestamp format with local time zone")
        //
        // names of topics to produce messages to
        //
        .define(CONFIG_TOPICNAME_ORDERS,
                    Type.STRING,
                    "ORDERS.NEW",
                    new NonEmptyString(),
                    Importance.LOW,
                    "Name of the topic to use for order events",
                    CONFIG_GROUP_TOPICNAMES, 1, Width.LONG, "Orders topic")
        .define(CONFIG_TOPICNAME_CANCELLATIONS,
                    Type.STRING,
                    "CANCELLATIONS",
                    new NonEmptyString(),
                    Importance.LOW,
                    "Name of the topic to use for cancellation events",
                    CONFIG_GROUP_TOPICNAMES, 2, Width.LONG, "Cancellations topic")
        .define(CONFIG_TOPICNAME_STOCKMOVEMENTS,
                    Type.STRING,
                    "STOCK.MOVEMENT",
                    new NonEmptyString(),
                    Importance.LOW,
                    "Name of the topic to use for stock movement events",
                    CONFIG_GROUP_TOPICNAMES, 3, Width.LONG, "Stock movements topic")
        .define(CONFIG_TOPICNAME_BADGEINS,
                    Type.STRING,
                    "DOOR.BADGEIN",
                    new NonEmptyString(),
                    Importance.LOW,
                    "Name of the topic to use for door badge-in events",
                    CONFIG_GROUP_TOPICNAMES, 4, Width.LONG, "Door badge-ins topic")
        .define(CONFIG_TOPICNAME_CUSTOMERS,
                    Type.STRING,
                    "CUSTOMERS.NEW",
                    new NonEmptyString(),
                    Importance.LOW,
                    "Name of the topic to use for customer registration events",
                    CONFIG_GROUP_TOPICNAMES, 5, Width.LONG, "Customers topic")
        .define(CONFIG_TOPICNAME_SENSORREADINGS,
                    Type.STRING,
                    "SENSOR.READINGS",
                    new NonEmptyString(),
                    Importance.LOW,
                    "Name of the topic to use for sensor reading events",
                    CONFIG_GROUP_TOPICNAMES, 6, Width.LONG, "Sensor readings topic")
        .define(CONFIG_TOPICNAME_ONLINEORDERS,
                    Type.STRING,
                    "ORDERS.ONLINE",
                    new NonEmptyString(),
                    Importance.LOW,
                    "Name of the topic to use for online order events",
                    CONFIG_GROUP_TOPICNAMES, 7, Width.LONG, "Online orders topic")
        .define(CONFIG_TOPICNAME_OUTOFSTOCKS,
                    Type.STRING,
                    "STOCK.NOSTOCK",
                    new NonEmptyString(),
                    Importance.LOW,
                    "Name of the topic to use for out-of-stock events",
                    CONFIG_GROUP_TOPICNAMES, 8, Width.LONG, "Out-of-stocks topic")
        .define(CONFIG_TOPICNAME_RETURNREQUESTS,
                    Type.STRING,
                    "PRODUCT.RETURNS",
                    new NonEmptyString(),
                    Importance.LOW,
                    "Name of the topic to use for return request events",
                    CONFIG_GROUP_TOPICNAMES, 9, Width.LONG, "Return requests topic")
        .define(CONFIG_TOPICNAME_PRODUCTREVIEWS,
                    Type.STRING,
                    "PRODUCT.REVIEWS",
                    new NonEmptyString(),
                    Importance.LOW,
                    "Name of the topic to use for product review events",
                    CONFIG_GROUP_TOPICNAMES, 10, Width.LONG, "Product reviews topic")
        .define(CONFIG_TOPICNAME_TRANSACTIONS,
                    Type.STRING,
                    "TRANSACTIONS",
                    new NonEmptyString(),
                    Importance.LOW,
                    "Name of the topic to use for transaction events",
                    CONFIG_GROUP_TOPICNAMES, 11, Width.LONG, "Transactions topic")
        .define(CONFIG_TOPICNAME_ABANDONEDORDERS,
                    Type.STRING,
                    "ORDERS.ABANDONED",
                    new NonEmptyString(),
                    Importance.LOW,
                    "Name of the topic to use for abandoned order events",
                    CONFIG_GROUP_TOPICNAMES, 12, Width.LONG, "Abandoned orders topic")
        .define(CONFIG_TOPICNAME_CLICKTRACKING,
                    Type.STRING,
                    "CLICKTRACKING",
                    new NonEmptyString(),
                    Importance.LOW,
                    "Name of the topic to use for click tracking events",
                    CONFIG_GROUP_TOPICNAMES, 13, Width.LONG, "Clicktracking topic")
        //
        // how to generate locations
        //
        .define(CONFIG_LOCATIONS_REGIONS,
                    Type.LIST,
                    Arrays.asList("NA", "SA", "EMEA", "APAC", "ANZ"),
                    new ValidTermsList(),
                    Importance.LOW,
                    "List of regions to use for generated locations. Regions cannot contain spaces.",
                    CONFIG_GROUP_LOCATIONS, 1, Width.MEDIUM, "Regions")
        .define(CONFIG_LOCATIONS_WAREHOUSES,
                    Type.LIST,
                    Arrays.asList("North", "South", "West", "East", "Central"),
                    new ValidTermsList(),
                    Importance.LOW,
                    "List of warehouses to use for generated locations. Warehouse names cannot contain spaces.",
                    CONFIG_GROUP_LOCATIONS, 2, Width.MEDIUM, "Warehouses")
        .define(CONFIG_LOCATIONS_REGIONS_COUNTRIES_MAP,
                    Type.STRING,
                    "NA:CA,US,MX;SA:BR,PY,UY;EMEA:BE,FR,CH,GB,DE,ES;APAC:ID,SG,BN,PH;ANZ:AU,NZ",
                    new ValidRegionToCountriesMap(),
                    Importance.LOW,
                    "Mapping of countries to region",
                    CONFIG_GROUP_LOCATIONS, 3, Width.MEDIUM, "Region wise country list")
        //
        // How to generate priorities
        //
        .define(CONFIG_PRIORITIES,
                Type.LIST,
                Arrays.asList("normal", "high", "urgent"),
                new ValidTermsList(),
                Importance.LOW,
                "List of priorities for orders.",
                CONFIG_GROUP_PRIORITIES, 1, Width.MEDIUM, "Priority")
        //
        // How to generate product names
        //
        .define(CONFIG_PRODUCTS_SIZES,
                    Type.LIST,
                    Arrays.asList("XXS", "XS", "S", "M", "L", "XL", "XXL"),
                    new ValidTermsList(),
                    Importance.LOW,
                    "List of sizes to use for generated product names. Sizes cannot contain spaces.",
                    CONFIG_GROUP_PRODUCTS, 1, Width.MEDIUM, "Sizes")
        .define(CONFIG_PRODUCTS_MATERIALS,
                    Type.LIST,
                    Arrays.asList("Classic", "Retro", "Navy", "Stonewashed", "Acid-washed",
                                  "Blue", "Black", "White", "Khaki", "Denim", "Raw-Denim",
                                  "Selvedge", "Stretch-Denim", "Corduroy", "Chambray",
                                  "Twill", "Leather", "Velvet", "Linen-Blend",
                                  "Organic-Cotton", "Recycled-Denim", "Overdyed",
                                  "Coated-Denim", "TENCEL", "Crochet"),
                    new ValidTermsList(),
                    Importance.LOW,
                    "List of materials to use for generated product names. Materials cannot contain spaces.",
                    CONFIG_GROUP_PRODUCTS, 2, Width.MEDIUM, "Materials")
        .define(CONFIG_PRODUCTS_STYLES,
                    Type.LIST,
                    Arrays.asList("Skinny", "Bootcut", "Flare", "Ripped", "Capri", "Jogger",
                                  "High-waist", "Low-rise", "Straight-leg",
                                  "Boyfriend", "Mom", "Wide-leg", "Jorts", "Cargo", "Tall",
                                  "Relaxed", "Slim", "Super-skinny", "Cropped", "Ankle",
                                  "Bell-bottom", "Distressed", "Patchwork", "Painter",
                                  "Carpenter", "Utility", "Paperbag-waist", "Raw-hem", "Vintage",
                                  "Tapered", "Curvy-fit", "Petite", "Plus-size", "Overalls",
                                  "Dungarees", "Jeggings"),
                    new ValidTermsList(),
                    Importance.LOW,
                    "List of styles to use for generated product names. Styles cannot contain spaces.",
                    CONFIG_GROUP_PRODUCTS, 3, Width.MEDIUM, "Styles")
        .define(CONFIG_PRODUCTS_NAME,
                    Type.STRING,
                    "Jeans",
                    new NonEmptyString(),
                    Importance.LOW,
                    "Name of the product to use for generated product names.",
                    CONFIG_GROUP_PRODUCTS, 4, Width.MEDIUM, "Name")
        //
        // Dynamic prices to generate
        //
        .define(CONFIG_PRODUCTS_MIN_PRICE,
                    Type.DOUBLE,
                    14.99,
                    Range.atLeast(0.01),
                    Importance.LOW,
                    "Minimum price for product orders. Must be greater than 0",
                    CONFIG_GROUP_DYNAMICPRICING, 1, Width.SHORT, "Min price")
        .define(CONFIG_PRODUCTS_MAX_PRICE,
                    Type.DOUBLE,
                    59.99,
                    Range.atLeast(0.01),
                    Importance.LOW,
                    "Maximum price for product orders. Must be greater than 0",
                    CONFIG_GROUP_DYNAMICPRICING, 2, Width.SHORT, "Max price")
        .define(CONFIG_DYNAMICPRICING_PRICE_CHANGE_MAX,
                    Type.DOUBLE,
                    9.99,
                    Range.atLeast(0.01),
                    Importance.LOW,
                    "Maximum price variation caused by dynamic pricing. Must be greater than 0",
                    CONFIG_GROUP_DYNAMICPRICING, 3, Width.SHORT, "Dynamic pricing variation limit")
        //
        // Orders to generate
        //
        .define(CONFIG_ORDERS_SMALL_MIN,
                    Type.INT,
                    1,
                    Range.atLeast(1),
                    Importance.LOW,
                    "Minimum number of items in a small order. Must be greater than 0",
                    CONFIG_GROUP_ORDERS, 1, Width.SHORT, "Min quantity (small orders)")
        .define(CONFIG_ORDERS_SMALL_MAX,
                    Type.INT,
                    5,
                    Range.atLeast(1),
                    Importance.LOW,
                    "Maximum number of items in a small order. Must be greater than 0",
                    CONFIG_GROUP_ORDERS, 2, Width.SHORT, "Max quantity (small orders)")
        .define(CONFIG_ORDERS_LARGE_MIN,
                    Type.INT,
                    5,
                    Range.atLeast(1),
                    Importance.LOW,
                    "Minimum number of items in a large order. Must be greater than 0",
                    CONFIG_GROUP_ORDERS, 3, Width.SHORT, "Min quantity (large orders)")
        .define(CONFIG_ORDERS_LARGE_MAX,
                    Type.INT,
                    10,
                    Range.atLeast(1),
                    Importance.LOW,
                    "Maximum number of items in a large order. Must be greater than 0",
                    CONFIG_GROUP_ORDERS, 4, Width.SHORT, "Max quantity (large orders)")
        //
        // Order cancellations to generate
        //
        .define(CONFIG_CANCELLATIONS_RATIO,
                    Type.DOUBLE,
                    0.005,
                    Range.between(0.001, 1),
                    Importance.LOW,
                    "Ratio of orders that are normally cancelled. Must be between 0 and 1",
                    CONFIG_GROUP_CANCELLATIONS, 1, Width.SHORT, "Cancellations ratio")
        .define(CONFIG_CANCELLATIONS_MIN_DELAY,
                    Type.INT,
                    300_000, // 5 minutes
                    Range.atLeast(60_000), // 1 minute
                    Importance.LOW,
                    "Minimum delay before an order should normally be cancelled, in milliseconds. Must be at least 60000.",
                    CONFIG_GROUP_CANCELLATIONS, 2, Width.SHORT, "Min cancellations delay")
        .define(CONFIG_CANCELLATIONS_MAX_DELAY,
                    Type.INT,
                    7_200_000, // 2 hours
                    Range.atLeast(120_000), // 2 minutes
                    Importance.LOW,
                    "Maximum delay before an order should normally be cancelled, in milliseconds. Must be at least 120000.",
                    CONFIG_GROUP_CANCELLATIONS, 3, Width.SHORT, "Max cancellations delay")
        .define(CONFIG_CANCELLATIONS_REASONS,
                    Type.LIST,
                    Arrays.asList("CHANGEDMIND", "BADFIT", "SHIPPINGDELAY", "DELIVERYERROR", "CHEAPERELSEWHERE"),
                    new ValidTermsList(),
                    Importance.LOW,
                    "List of reasons to use for generated cancellations. Reasons cannot contain spaces.",
                    CONFIG_GROUP_CANCELLATIONS, 4, Width.SHORT, "Cancellation reason codes")
        //
        // Generating suspicious orders
        //
        .define(CONFIG_SUSPICIOUSCANCELLATIONS_MIN_DELAY,
                    Type.INT,
                    900_000, // 15 minutes
                    Range.atLeast(60_000), // 1 minute
                    Importance.LOW,
                    "Minimum delay before a suspicious order should be cancelled, in milliseconds. Must be at least 60000.",
                    CONFIG_GROUP_SUSPICIOUSORDERS, 1, Width.SHORT, "Min cancellations delay")
        .define(CONFIG_SUSPICIOUSCANCELLATIONS_MAX_DELAY,
                    Type.INT,
                    1_800_000, // 30 minutes
                    Range.atLeast(120_000), // 2 minutes
                    Importance.LOW,
                    "Maximum delay before a suspicious order should be cancelled, in milliseconds. Must be at least 120000.",
                    CONFIG_GROUP_SUSPICIOUSORDERS, 2, Width.SHORT, "Max cancellations delay")
        .define(CONFIG_SUSPICIOUSCANCELLATIONS_NUM,
                    Type.INT,
                    3,
                    Range.atLeast(1),
                    Importance.LOW,
                    "How many large orders to cancel when making a suspicious order. Must be at least 1",
                    CONFIG_GROUP_SUSPICIOUSORDERS, 3, Width.SHORT, "Number of cancelled large orders")
        .define(CONFIG_SUSPICIOUSCANCELLATIONS_CUSTOMER,
                    Type.LIST,
                    Arrays.asList("Suspicious Bob", "Naughty Nigel", "Criminal Clive", "Dastardly Derek"),
                    new NonNullValidator(),
                    Importance.LOW,
                    "Names of customers that make suspicious orders",
                    CONFIG_GROUP_SUSPICIOUSORDERS, 4, Width.LONG, "Suspicious customer names")
        //
        // generating new-customer registration events
        //
        .define(CONFIG_NEWCUSTOMERS_ORDER_RATIO,
                    Type.DOUBLE,
                    0.22,
                    Range.between(0.001, 1),
                    Importance.LOW,
                    "Ratio of new customers that should make an order soon after registering. Must be between 0 and 1",
                    CONFIG_GROUP_NEWCUSTOMERS, 1, Width.SHORT, "New customers immediate-order ratio")
        .define(CONFIG_NEWCUSTOMERS_ORDER_MIN_DELAY,
                    Type.INT,
                    180_000, // 3 minutes
                    Range.atLeast(10_000), // 10 seconds
                    Importance.LOW,
                    "Minimum delay before a new customer (who is going to make an immediate order) should create their first order, in milliseconds. Must be at least 10000.",
                    CONFIG_GROUP_NEWCUSTOMERS, 2, Width.SHORT, "New customers immediate-order min delay")
        .define(CONFIG_NEWCUSTOMERS_ORDER_MAX_DELAY,
                    Type.INT,
                    1_380_000, // 23 minutes
                    Range.atLeast(30_000), // 30 seconds
                    Importance.LOW,
                    "Maximum delay before a new customer (who is going to make an immediate order) should create their first order, in milliseconds. Must be at least 30000.",
                    CONFIG_GROUP_NEWCUSTOMERS, 3, Width.SHORT, "New customers immediate-order max delay")
        //
        // Generating online orders
        //
        .define(CONFIG_ONLINEORDERS_PRODUCTS_MAX,
                    Type.INT,
                    5,
                    Range.atLeast(1),
                    Importance.LOW,
                    "Maximum number of products in an online order. Must be greater than 0.",
                    CONFIG_GROUP_ONLINEORDERS, 1, Width.SHORT, "Max product count")
        .define(CONFIG_ONLINEORDERS_CUSTOMER_EMAILS_MIN,
                    Type.INT,
                    1,
                    Range.atLeast(1),
                    Importance.LOW,
                    "Minimum number of emails for a customer in an online order. Must be greater than 0.",
                    CONFIG_GROUP_ONLINEORDERS, 2, Width.SHORT, "Min customer email count")
        .define(CONFIG_ONLINEORDERS_CUSTOMER_EMAILS_MAX,
                    Type.INT,
                    2,
                    Range.atLeast(1),
                    Importance.LOW,
                    "Maximum number of emails for a customer in an online order. Must be greater than 0.",
                    CONFIG_GROUP_ONLINEORDERS, 3, Width.SHORT, "Max customer email count")
        .define(CONFIG_ONLINEORDERS_ADDRESS_PHONES_MIN,
                    Type.INT,
                    0,
                    Range.atLeast(0),
                    Importance.LOW,
                    "Minimum number of phones in an address for an online order. Must be at least 0.",
                    CONFIG_GROUP_ONLINEORDERS, 4, Width.SHORT, "Min address phone count")
        .define(CONFIG_ONLINEORDERS_ADDRESS_PHONES_MAX,
                    Type.INT,
                    2,
                    Range.atLeast(0),
                    Importance.LOW,
                    "Maximum number of phones in an address for an online order. Must be at least 0.",
                    CONFIG_GROUP_ONLINEORDERS, 5, Width.SHORT, "Max address phone count")
        .define(CONFIG_ONLINEORDERS_REUSE_ADDRESS_RATIO,
                    Type.DOUBLE,
                    0.55,
                    Range.between(0, 1),
                    Importance.LOW,
                    "Ratio of orders that use the same address as shipping and billing address. Must be between 0 and 1.",
                    CONFIG_GROUP_ONLINEORDERS, 6, Width.SHORT, "Reuse address ratio")
        .define(CONFIG_ONLINEORDERS_OUTOFSTOCK_RATIO,
                    Type.DOUBLE,
                    0.22,
                    Range.between(0, 1),
                    Importance.LOW,
                    "Ratio of orders that have at least one product that runs out-of-stock after the order has been placed. Must be between 0 and 1.",
                    CONFIG_GROUP_ONLINEORDERS, 7, Width.SHORT, "Out-of-stock product ratio")
        .define(CONFIG_ONLINEORDERS_CITIES,
                    Type.LIST,
                    new ArrayList<String>(),
                    new NonNullValidator(),
                    Importance.LOW,
                    "List of cities used for generated online orders",
                    CONFIG_GROUP_ONLINEORDERS, 8, Width.LONG, "Cities")
        .define(CONFIG_ONLINEORDERS_URL,
                    Type.STRING,
                    "https://loosehangerjeans.com",
                    new NonEmptyString(),
                    Importance.LOW,
                    "Base URL for generating web addresses for click tracking events",
                    CONFIG_GROUP_ONLINEORDERS, 9, Width.LONG, "Base URL")
        .define(CONFIG_ONLINEORDERS_CLICKEVENTS_MAX,
                    Type.INT,
                    100,
                    Range.atLeast(5),
                    Importance.LOW,
                    "Maximum number of click tracking events to generate for a single user session",
                    CONFIG_GROUP_ONLINEORDERS, 10, Width.SHORT, "Maximum number of click tracking events for online orders")
        .define(CONFIG_ONLINEORDERS_ABANDONED_RATIO,
                    Type.DOUBLE,
                    0.2,
                    Range.between(0, 1),
                    Importance.LOW,
                    "Likelihood that a customer will abandon their cart at each step during a user session",
                    CONFIG_GROUP_ONLINEORDERS, 11, Width.SHORT, "Abandoned cart ratio")
        .define(CONFIG_ONLINEORDERS_LOGGEDIN_RATIO,
                    Type.DOUBLE,
                    0.2,
                    Range.between(0, 1),
                    Importance.LOW,
                    "Likelihood that a customer will already be logged in at the start a new user session",
                    CONFIG_GROUP_ONLINEORDERS, 12, Width.SHORT, "Logged-in ratio")
        .define(CONFIG_ONLINEORDERS_MARKETING_RATIO,
                    Type.DOUBLE,
                    0.4,
                    Range.between(0, 1),
                    Importance.LOW,
                    "Proportion of user sessions that include a referral from a digital marketing campaign",
                    CONFIG_GROUP_ONLINEORDERS, 13, Width.SHORT, "Digital marketing ratio")
        .define(CONFIG_ONLINEORDERS_SESSIONS_MAX,
                    Type.INT,
                    50,
                    Range.atLeast(2),
                    Importance.LOW,
                    "Maximum number of concurrent user sessions to generate online events for",
                    CONFIG_GROUP_ONLINEORDERS, 14, Width.SHORT, "Max online users")
        //
        // Generating out-of-stock events
        //
        .define(CONFIG_OUTOFSTOCKS_RESTOCKING_MIN_DELAY,
                    Type.INT,
                    1, // 1 day
                    Range.atLeast(1),  // 1 day
                    Importance.LOW,
                    "Minimum delay (in *days*) between the time that the product was out-of-stock and the restocking date. Must be at least 1.",
                    CONFIG_GROUP_OUTOFSTOCKS, 1, Width.SHORT, "Out-of-stock events - restocking min delay")
        .define(CONFIG_OUTOFSTOCKS_RESTOCKING_MAX_DELAY,
                    Type.INT,
                    5, // 5 days
                    Range.atLeast(3),  // 3 days
                    Importance.LOW,
                    "Maximum delay (in *days*) between the time that the product was out-of-stock and the restocking date. Must be at least 3.",
                    CONFIG_GROUP_OUTOFSTOCKS, 2, Width.SHORT, "Out-of-stock events - restocking max delay")
        .define(CONFIG_OUTOFSTOCKS_MIN_DELAY,
                    Type.INT,
                    300_000, // 5 minutes
                    Range.atLeast(60_000), // 1 minute
                    Importance.LOW,
                    "Minimum delay before an out-of-stock event is generated after an order has been placed, in milliseconds. Must be at least 60000.",
                    CONFIG_GROUP_OUTOFSTOCKS, 3, Width.SHORT, "Min out-of-stock event delay")
        .define(CONFIG_OUTOFSTOCKS_MAX_DELAY,
                    Type.INT,
                    7_200_000, // 2 hours
                    Range.atLeast(120_000), // 2 minutes
                    Importance.LOW,
                    "Maximum delay before an out-of-stock event is generated after an order has been placed, in milliseconds. Must be at least 120000.",
                    CONFIG_GROUP_OUTOFSTOCKS, 4, Width.SHORT, "Max out-of-stock event delay")
        //
        // Generating return requests
        //
        .define(CONFIG_RETURNREQUESTS_PRODUCTS_MIN,
                    Type.INT,
                    1,
                    Range.atLeast(1),
                    Importance.LOW,
                    "Minimum number of products in a return request. Must be greater than 0.",
                    CONFIG_GROUP_RETURNREQUESTS, 1, Width.SHORT, "Min product count")
        .define(CONFIG_RETURNREQUESTS_PRODUCTS_MAX,
                    Type.INT,
                    4,
                    Range.atLeast(1),
                    Importance.LOW,
                    "Maximum number of products in a return request. Must be greater than 0.",
                    CONFIG_GROUP_RETURNREQUESTS, 2, Width.SHORT, "Max product count")
        .define(CONFIG_RETURNREQUESTS_PRODUCT_QUANTITY_MIN,
                    Type.INT,
                    1,
                    Range.atLeast(1),
                    Importance.LOW,
                    "Minimum quantity for each product in a return request. Must be greater than 0.",
                    CONFIG_GROUP_RETURNREQUESTS, 3, Width.SHORT, "Min quantity per product")
        .define(CONFIG_RETURNREQUESTS_PRODUCT_QUANTITY_MAX,
                    Type.INT,
                    3,
                    Range.atLeast(1),
                    Importance.LOW,
                    "Maximum quantity for each product in a return request. Must be greater than 0.",
                    CONFIG_GROUP_RETURNREQUESTS, 4, Width.SHORT, "Max quantity per product")
        .define(CONFIG_RETURNREQUESTS_CUSTOMER_EMAILS_MIN,
                    Type.INT,
                    1,
                    Range.atLeast(1),
                    Importance.LOW,
                    "Minimum number of emails for a customer in a return request. Must be greater than 0.",
                    CONFIG_GROUP_RETURNREQUESTS, 5, Width.SHORT, "Min customer email count")
        .define(CONFIG_RETURNREQUESTS_CUSTOMER_EMAILS_MAX,
                    Type.INT,
                    2,
                    Range.atLeast(1),
                    Importance.LOW,
                    "Maximum number of emails for a customer in a return request. Must be greater than 0.",
                    CONFIG_GROUP_RETURNREQUESTS, 6, Width.SHORT, "Max customer email count")
        .define(CONFIG_RETURNREQUESTS_ADDRESS_PHONES_MIN,
                    Type.INT,
                    0,
                    Range.atLeast(0),
                    Importance.LOW,
                    "Minimum number of phones in an address for a return request. Must be at least 0.",
                    CONFIG_GROUP_RETURNREQUESTS, 7, Width.SHORT, "Min address phone count")
        .define(CONFIG_RETURNREQUESTS_ADDRESS_PHONES_MAX,
                    Type.INT,
                    2,
                    Range.atLeast(0),
                    Importance.LOW,
                    "Maximum number of phones in an address for a return request. Must be at least 0.",
                    CONFIG_GROUP_RETURNREQUESTS, 8, Width.SHORT, "Max address phone count")
        .define(CONFIG_RETURNREQUESTS_REUSE_ADDRESS_RATIO,
                    Type.DOUBLE,
                    0.75,
                    Range.between(0, 1),
                    Importance.LOW,
                    "Ratio of return requests that use the same address as shipping and billing address. Must be between 0 and 1.",
                    CONFIG_GROUP_RETURNREQUESTS, 9, Width.SHORT, "Reuse address ratio")
        .define(CONFIG_RETURNREQUESTS_REASONS,
                    Type.LIST,
                    Arrays.asList("CHANGEDMIND", "BADFIT", "SHIPPINGDELAY", "DELIVERYERROR", "CHEAPERELSEWHERE", "OTHER"),
                    new ValidTermsList(),
                    Importance.LOW,
                    "List of reasons to use for return requests. Reasons cannot contain spaces.",
                    CONFIG_GROUP_RETURNREQUESTS, 10, Width.SHORT, "Return reason codes")
        .define(CONFIG_RETURNREQUESTS_REVIEW_RATIO,
                    Type.DOUBLE,
                    0.32,
                    Range.between(0, 1),
                    Importance.LOW,
                    "Ratio of return requests that have at least one product that has a review that is posted after the return request is issued. Must be between 0 and 1.",
                    CONFIG_GROUP_RETURNREQUESTS, 11, Width.SHORT, "Product review ratio")
        .define(CONFIG_RETURNREQUESTS_PRODUCT_WITH_SIZE_ISSUE_RATIO,
                    Type.DOUBLE,
                    0.22,
                    Range.between(0, 1),
                    Importance.LOW,
                    "Ratio of products in return requests that have a size issue. Must be between 0 and 1.",
                    CONFIG_GROUP_RETURNREQUESTS, 12, Width.SHORT, "Product with size issue ratio")
        //
        // Generating product reviews
        //
        .define(CONFIG_PRODUCTREVIEWS_PRODUCTS_WITH_SIZE_ISSUE_COUNT,
                    Type.INT,
                    10,
                    Range.atLeast(1),
                    Importance.LOW,
                    "Number of products that have a size issue. Must be greater than 0.",
                    CONFIG_GROUP_PRODUCTREVIEWS, 1, Width.SHORT, "Product with size issue count")
        .define(CONFIG_PRODUCTREVIEWS_REVIEW_WITH_SIZE_ISSUE_RATIO,
                    Type.DOUBLE,
                    0.75,
                    Range.between(0, 1),
                    Importance.LOW,
                    "Ratio of product reviews with a size issue for products that are supposed to have a size issue. Must be between 0 and 1.",
                    CONFIG_GROUP_PRODUCTREVIEWS, 2, Width.SHORT, "Product review with size issue ratio")
        .define(CONFIG_PRODUCTREVIEWS_MIN_DELAY,
                    Type.INT,
                    300_000, // 5 minutes
                    Range.atLeast(60_000), // 1 minute
                    Importance.LOW,
                    "Minimum delay before a product review event is generated after a return request has been issued, in milliseconds. Must be at least 60000.",
                    CONFIG_GROUP_PRODUCTREVIEWS, 3, Width.SHORT, "Min product review event delay")
        .define(CONFIG_PRODUCTREVIEWS_MAX_DELAY,
                    Type.INT,
                    3_600_000, // 1 hour
                    Range.atLeast(120_000), // 2 minutes
                    Importance.LOW,
                    "Maximum delay before a product review event is generated after a return request has been issued, in milliseconds. Must be at least 120000.",
                    CONFIG_GROUP_PRODUCTREVIEWS, 4, Width.SHORT, "Max product review event delay")

        //
        // Generating transactions
        //
        .define(CONFIG_TRANSACTIONS_IDS,
                    Type.INT,
                    6,
                    Range.atLeast(1),
                    Importance.LOW,
                    "Number of transactions identifiers",
                    CONFIG_GROUP_TRANSACTIONS, 1, Width.SHORT, "Number of transaction identifiers")
        .define(CONFIG_TRANSACTIONS_AMOUNT_MIN,
                    Type.DOUBLE,
                    100,
                    Range.atLeast(100),
                    Importance.LOW,
                    "Minimum amount of a transaction",
                    CONFIG_GROUP_TRANSACTIONS, 2, Width.SHORT, "Min transaction amount")
        .define(CONFIG_TRANSACTIONS_AMOUNT_MAX,
                    Type.DOUBLE,
                    1000,
                    Range.between(100, 1000),
                    Importance.LOW,
                    "Maximum amount of a transaction",
                    CONFIG_GROUP_TRANSACTIONS, 3, Width.SHORT, "Max transaction amount")
        .define(CONFIG_TRANSACTIONS_VALID_RATIO,
                    Type.DOUBLE,
                    0.8,
                    Range.between(0, 1),
                    Importance.LOW,
                    "Ratio of transactions that are valid and complete. Must be between 0 (no transactions are valid) and 1 (all transactions are valid)",
                    CONFIG_GROUP_TRANSACTIONS, 4, Width.SHORT, "Valid transactions ratio")

        //
        // how long to delay messages before producing them to Kafka
        //
        .define(CONFIG_DELAYS_ORDERS,
                    Type.INT,
                    0, // payload time matching event time by default
                    Range.between(0, 900),  // up to 15 mins max
                    Importance.LOW,
                    "Maximum delay (in *seconds*) to produce new order events (this is the maximum difference allowed between the timestamp string in the event payload, and the Kafka message's metadata timestamp)",
                    CONFIG_GROUP_DELAYS, 1, Width.SHORT, "Order events - max produce delay")
        .define(CONFIG_DELAYS_CANCELLATIONS,
                    Type.INT,
                    0, // payload time matching event time by default
                    Range.between(0, 900),  // up to 15 mins max
                    Importance.LOW,
                    "Maximum delay (in *seconds*) to produce cancellation events (this is the maximum difference allowed between the timestamp string in the event payload, and the Kafka message's metadata timestamp)",
                    CONFIG_GROUP_DELAYS, 2, Width.SHORT, "Cancellation events - max produce delay")
        .define(CONFIG_DELAYS_STOCKMOVEMENTS,
                    Type.INT,
                    0, // payload time matching event time by default
                    Range.between(0, 900),  // up to 15 mins max
                    Importance.LOW,
                    "Maximum delay (in *seconds*) to produce stock movement events (this is the maximum difference allowed between the timestamp string in the event payload, and the Kafka message's metadata timestamp)",
                    CONFIG_GROUP_DELAYS, 3, Width.SHORT, "Stock movement events - max produce delay")
        .define(CONFIG_DELAYS_BADGEINS,
                    Type.INT,
                    180,  // payload time can be up to 3 minutes behind event time by default
                    Range.between(0, 900),  // up to 15 mins max
                    Importance.LOW,
                    "Maximum delay (in *seconds*) to produce badge events (this is the maximum difference allowed between the timestamp string in the event payload, and the Kafka message's metadata timestamp)",
                    CONFIG_GROUP_DELAYS, 4, Width.SHORT, "Badge events - max produce delay")
        .define(CONFIG_DELAYS_NEWCUSTOMERS,
                    Type.INT,
                    0, // payload time matching event time by default
                    Range.between(0, 900),  // up to 15 mins max
                    Importance.LOW,
                    "Maximum delay (in *seconds*) to produce customer registration events (this is the maximum difference allowed between the timestamp string in the event payload, and the Kafka message's metadata timestamp)",
                    CONFIG_GROUP_DELAYS, 5, Width.SHORT, "New customer events - max produce delay")
        .define(CONFIG_DELAYS_SENSORREADINGS,
                    Type.INT,
                    300, // payload time can be up to 5 minutes behind event time by default
                    Range.between(0, 900),  // up to 15 mins max
                    Importance.LOW,
                    "Maximum delay (in *seconds*) to produce sensor reading events (this is the maximum difference allowed between the timestamp string in the event payload, and the Kafka message's metadata timestamp)",
                    CONFIG_GROUP_DELAYS, 6, Width.SHORT, "Sensor reading events - max produce delay")
        .define(CONFIG_DELAYS_OUTOFSTOCKS,
                    Type.INT,
                    0, // payload time matching event time by default
                    Range.between(0, 900),  // up to 15 mins max
                    Importance.LOW,
                    "Maximum delay (in *seconds*) to produce new out-of-stock events (this is the maximum difference allowed between the timestamp string in the event payload, and the Kafka message's metadata timestamp)",
                    CONFIG_GROUP_DELAYS, 7, Width.SHORT, "Out-of-stock events - max produce delay")
        .define(CONFIG_DELAYS_RETURNREQUESTS,
                    Type.INT,
                    0, // payload time matching event time by default
                    Range.between(0, 900),  // up to 15 mins max
                    Importance.LOW,
                    "Maximum delay (in *seconds*) to produce new return request events (this is the maximum difference allowed between the timestamp string in the event payload, and the Kafka message's metadata timestamp)",
                    CONFIG_GROUP_DELAYS, 8, Width.SHORT, "Return request events - max produce delay")
        .define(CONFIG_DELAYS_PRODUCTREVIEWS,
                    Type.INT,
                    0, // payload time matching event time by default
                    Range.between(0, 900),  // up to 15 mins max
                    Importance.LOW,
                    "Maximum delay (in *seconds*) to produce new product review events (this is the maximum difference allowed between the timestamp string in the event payload, and the Kafka message's metadata timestamp)",
                    CONFIG_GROUP_DELAYS, 9, Width.SHORT, "Product review events - max produce delay")
        .define(CONFIG_DELAYS_TRANSACTIONS,
                    Type.INT,
                    0, // payload time matching event time by default
                    Range.between(0, 900),  // up to 15 mins max
                    Importance.LOW,
                    "Maximum delay (in *seconds*) to produce transaction events (this is the maximum difference allowed between the timestamp string in the event payload, and the Kafka message's metadata timestamp)",
                    CONFIG_GROUP_DELAYS, 10, Width.SHORT, "Transaction events - max produce delay")

        //
        // likelihood of producing duplicate messages
        //
        .define(CONFIG_DUPLICATE_ORDERS,
                    Type.DOUBLE,
                    0,   // don't create duplicate events by default
                    Range.between(0.0, 1.0), // ratio should be between 0 (don't create duplicates) and 1 (duplicate every message)
                    Importance.LOW,
                    "Ratio of order events that should be duplicated. Must be between 0 and 1.",
                    CONFIG_GROUP_DUPLICATES, 1, Width.SHORT, "Duplicate order events ratio")
        .define(CONFIG_DUPLICATE_CANCELLATIONS,
                    Type.DOUBLE,
                    0,   // don't create duplicate events by default
                    Range.between(0.0, 1.0), // ratio should be between 0 (don't create duplicates) and 1 (duplicate every message)
                    Importance.LOW,
                    "Ratio of cancellation events that should be duplicated. Must be between 0 and 1.",
                    CONFIG_GROUP_DUPLICATES, 2, Width.SHORT, "Duplicate cancellation events ratio")
        .define(CONFIG_DUPLICATE_STOCKMOVEMENTS,
                    Type.DOUBLE,
                    0.1,   // duplicate approximately 10 percent of the stock movement events by default
                    Range.between(0.0, 1.0), // ratio should be between 0 (don't create duplicates) and 1 (duplicate every message)
                    Importance.LOW,
                    "Ratio of stock movement events that should be duplicated. Must be between 0 and 1.",
                    CONFIG_GROUP_DUPLICATES, 3, Width.SHORT, "Duplicate stock movement events ratio")
        .define(CONFIG_DUPLICATE_BADGEINS,
                    Type.DOUBLE,
                    0,   // don't create duplicate events by default
                    Range.between(0.0, 1.0), // ratio should be between 0 (don't create duplicates) and 1 (duplicate every message)
                    Importance.LOW,
                    "Ratio of badge events that should be duplicated. Must be between 0 and 1.",
                    CONFIG_GROUP_DUPLICATES, 4, Width.SHORT, "Duplicate badge events ratio")
        .define(CONFIG_DUPLICATE_NEWCUSTOMERS,
                    Type.DOUBLE,
                    0,   // don't create duplicate events by default
                    Range.between(0.0, 1.0), // ratio should be between 0 (don't create duplicates) and 1 (duplicate every message)
                    Importance.LOW,
                    "Ratio of new customer events that should be duplicated. Must be between 0 and 1.",
                    CONFIG_GROUP_DUPLICATES, 5, Width.SHORT, "Duplicate new customer events ratio")
        .define(CONFIG_DUPLICATE_SENSORREADINGS,
                    Type.DOUBLE,
                    0,   // don't create duplicate events by default
                    Range.between(0.0, 1.0), // ratio should be between 0 (don't create duplicates) and 1 (duplicate every message)
                    Importance.LOW,
                    "Ratio of sensor reading events that should be duplicated. Must be between 0 and 1.",
                    CONFIG_GROUP_DUPLICATES, 6, Width.SHORT, "Duplicate sensor reading events ratio")
        .define(CONFIG_DUPLICATE_ONLINEORDERS,
                    Type.DOUBLE,
                    0,   // don't create duplicate events by default
                    Range.between(0.0, 1.0), // ratio should be between 0 (don't create duplicates) and 1 (duplicate every message)
                    Importance.LOW,
                    "Ratio of online order events that should be duplicated. Must be between 0 and 1.",
                    CONFIG_GROUP_DUPLICATES, 7, Width.SHORT, "Duplicate online order events ratio")
        .define(CONFIG_DUPLICATE_OUTOFSTOCKS,
                    Type.DOUBLE,
                    0,   // don't create duplicate events by default
                    Range.between(0.0, 1.0), // ratio should be between 0 (don't create duplicates) and 1 (duplicate every message)
                    Importance.LOW,
                    "Ratio of out-of-stock events that should be duplicated. Must be between 0 and 1.",
                    CONFIG_GROUP_DUPLICATES, 8, Width.SHORT, "Duplicate out-of-stock events ratio")
        .define(CONFIG_DUPLICATE_RETURNREQUESTS,
                    Type.DOUBLE,
                    0,   // don't create duplicate events by default
                    Range.between(0.0, 1.0), // ratio should be between 0 (don't create duplicates) and 1 (duplicate every message)
                    Importance.LOW,
                    "Ratio of return request events that should be duplicated. Must be between 0 and 1.",
                    CONFIG_GROUP_DUPLICATES, 9, Width.SHORT, "Duplicate return request events ratio")
        .define(CONFIG_DUPLICATE_PRODUCTREVIEWS,
                    Type.DOUBLE,
                    0,   // don't create duplicate events by default
                    Range.between(0.0, 1.0), // ratio should be between 0 (don't create duplicates) and 1 (duplicate every message)
                    Importance.LOW,
                    "Ratio of product review events that should be duplicated. Must be between 0 and 1.",
                    CONFIG_GROUP_DUPLICATES, 10, Width.SHORT, "Duplicate product review events ratio")
        .define(CONFIG_DUPLICATE_TRANSACTIONS,
                    Type.DOUBLE,
                    0,   // don't create duplicate events by default
                    Range.between(0.0, 1.0), // ratio should be between 0 (don't create duplicates) and 1 (duplicate every message)
                    Importance.LOW,
                    "Ratio of transaction events that should be duplicated. Must be between 0 and 1.",
                    CONFIG_GROUP_DUPLICATES, 11, Width.SHORT, "Duplicate transaction events ratio")
       .define(CONFIG_DUPLICATE_ABANDONEDORDERS,
                    Type.DOUBLE,
                    0,   // don't create duplicate events by default
                    Range.between(0.0, 1.0), // ratio should be between 0 (don't create duplicates) and 1 (duplicate every message)
                    Importance.LOW,
                    "Ratio of abandoned order events that should be duplicated. Must be between 0 and 1.",
                    CONFIG_GROUP_DUPLICATES, 12, Width.SHORT, "Duplicate abandoned order events ratio")
        .define(CONFIG_DUPLICATE_CLICKTRACKING,
                    Type.DOUBLE,
                    0,   // don't create duplicate events by default
                    Range.between(0.0, 1.0), // ratio should be between 0 (don't create duplicates) and 1 (duplicate every message)
                    Importance.LOW,
                    "Ratio of click tracking events that should be duplicated. Must be between 0 and 1.",
                    CONFIG_GROUP_DUPLICATES, 13, Width.SHORT, "Duplicate clicktracking events ratio")
        //
        // How frequently to generate messages
        //
        .define(CONFIG_TIMES_ORDERS,
                    Type.INT,
                    30_000, // 30 seconds
                    Range.atLeast(500),
                    Importance.LOW,
                    "Delay, in milliseconds, between each order that should be generated.",
                    CONFIG_GROUP_TIMES, 1, Width.MEDIUM, "Orders delay")
        .define(CONFIG_TIMES_FALSEPOSITIVES,
                    Type.INT,
                    600_000, // 10 minutes
                    Range.atLeast(60_000), // 1 minute
                    Importance.LOW,
                    "Delay, in milliseconds, between each unusual-but-legitimate order that should be generated.",
                    CONFIG_GROUP_TIMES, 2, Width.MEDIUM, "False positives delay")
        .define(CONFIG_TIMES_SUSPICIOUSORDERS,
                    Type.INT,
                    3_600_000, // 1 hour
                    Range.atLeast(60_000), // 1 minute
                    Importance.LOW,
                    "Delay, in milliseconds, between each suspicious order that should be generated.",
                    CONFIG_GROUP_TIMES, 3, Width.MEDIUM, "Suspicious orders delay")
        .define(CONFIG_TIMES_STOCKMOVEMENTS,
                    Type.INT,
                    300_000, // 5 minutes
                    Range.atLeast(60_000), // 1 minute
                    Importance.LOW,
                    "Delay, in milliseconds, between each stock movement event that should be generated.",
                    CONFIG_GROUP_TIMES, 4, Width.MEDIUM, "Stock movements delay")
        .define(CONFIG_TIMES_BADGEINS,
                    Type.INT,
                    600,    // sub-second
                    Range.atLeast(500),
                    Importance.LOW,
                    "Delay, in milliseconds, between each badge in event that should be generated.",
                    CONFIG_GROUP_TIMES, 5, Width.MEDIUM, "Badges delay")
        .define(CONFIG_TIMES_NEWCUSTOMERS,
                    Type.INT,
                    543_400, // a little over 9 minutes
                    Range.atLeast(5_000),  // 5 seconds
                    Importance.LOW,
                    "Delay, in milliseconds, between each new customer that should be generated.",
                    CONFIG_GROUP_TIMES, 6, Width.MEDIUM, "New customers delay")
        .define(CONFIG_TIMES_SENSORREADINGS,
                    Type.INT,
                    27_000, // 27 seconds
                    Range.atLeast(5_000),  // 5 seconds
                    Importance.LOW,
                    "Delay, in milliseconds, between each sensor reading that should be generated.",
                    CONFIG_GROUP_TIMES, 7, Width.MEDIUM, "Sensor readings delay")
        .define(CONFIG_TIMES_HIGHSENSORREADINGS,
                    Type.INT,
                    18_000, // 18 seconds
                    Range.atLeast(4_000),  // 4 seconds
                    Importance.LOW,
                    "Delay, in milliseconds, between each sensor reading that should be generated for a problematic sensor that periodically emits very high and increasing readings.",
                    CONFIG_GROUP_TIMES, 8, Width.MEDIUM, "High sensor readings delay")
        .define(CONFIG_TIMES_ONLINEORDERS,
                    Type.INT,
                    5_000, // 5 seconds
                    Range.atLeast(500),
                    Importance.LOW,
                    "Delay, in milliseconds, between each online user session that should be started.",
                    CONFIG_GROUP_TIMES, 9, Width.MEDIUM, "Online user sessions delay")
        .define(CONFIG_TIMES_RETURNREQUESTS,
                    Type.INT,
                    300_000, // 5 minutes
                    Range.atLeast(60_000), // 1 minute
                    Importance.LOW,
                    "Delay, in milliseconds, between each return request that should be generated.",
                    CONFIG_GROUP_TIMES, 10, Width.MEDIUM, "Return requests delay")
        .define(CONFIG_TIMES_PRODUCTREVIEWS,
                    Type.INT,
                    60_000, // 1 minute
                    Range.atLeast(30_000), // 30 seconds
                    Importance.LOW,
                    "Delay, in milliseconds, between each product review that should be generated.",
                    CONFIG_GROUP_TIMES, 11, Width.MEDIUM, "Product reviews delay")
        .define(CONFIG_TIMES_TRANSACTIONS,
                    Type.INT,
                    20_000, // 20 seconds
                    Range.atLeast(5_000),  // 5 seconds
                    Importance.LOW,
                    "Delay, in milliseconds, between each transaction that should be generated.",
                    CONFIG_GROUP_TIMES, 12, Width.MEDIUM, "Sensor readings delay")
        .define(CONFIG_TIMES_CLICKTRACKING,
                    Type.INT,
                    15_000, // 15 seconds
                    Range.atLeast(5_000),
                    Importance.LOW,
                    "Maximum interval, in milliseconds, between clicktracking events generated for a single user session.",
                    CONFIG_GROUP_TIMES, 13, Width.MEDIUM, "Clicktracking events interval")
        //
        // Startup behaviour
        //
        .define(CONFIG_BEHAVIOR_STARTUPHISTORY,
                    Type.BOOLEAN,
                    false, // disabled by default
                    Importance.LOW,
                    "If enabled, the connector will generate a week of historical events on startup for the first time.",
                    CONFIG_GROUP_BEHAVIOR, 1, Width.SHORT, "Generate history on first start");


    private static class ValidTermsList implements Validator {
        @Override
        public void ensureValid(final String name, final Object value) {
            @SuppressWarnings("unchecked")
            List<String> values = (List<String>) value;
            if (values.isEmpty()) {
                throw new ConfigException(name, value, "must contain at least one item.");
            }
            for (String item : values) {
                if (item.contains(" ")) {
                    throw new ConfigException(name, item, "must not contain items with spaces.");
                }
            }
        }

        @Override
        public String toString() {
            return "List containing at least one element, where all elements don't have a space.";
        }
    }

    private static class ValidRegionToCountriesMap implements Validator {
        @Override
        public void ensureValid(final String name, final Object value) {
            Map<String, List<String>> parsedMap = parseCountriesList((String)value);
            if (parsedMap.isEmpty()) {
                throw new ConfigException(name, value, "must contain at least one region");
            }
        }

        @Override
        public String toString() {
            return "Expected format is REGION1:COUNTRY1,COUNTRY2,COUNTRY3;REGION2:COUNTRY4,COUNTRY5";
        }
    }


    /*
     * expected input: "REGION1:COUNTRY1,COUNTRY2,COUNTRY3;REGION2:COUNTRY4,COUNTRY5"
     * expected output:
     *   {
     *       "REGION1": [ "COUNTRY1", "COUNTRY2", "COUNTRY3" ],
     *       "REGION2": [ "COUNTRY4", "COUNTRY5" ]
     *   }
     */
    public static Map<String, List<String>> parseCountriesList(String input) {
        NonEmptyString regionNameValidator = new NonEmptyString();
        ValidTermsList countriesListValidator = new ValidTermsList();

        Map<String, List<String>> regionToCountries = new HashMap<>();
        String[] regionsWithCountries = input.split(";");
        for (String regionWithCountries : regionsWithCountries) {
            String[] parts = regionWithCountries.split(":");
            if (parts.length != 2) {
                throw new ConfigException("Expected format is REGION1:COUNTRY1,COUNTRY2,COUNTRY3;REGION2:COUNTRY4,COUNTRY5");
            }

            // check the region
            String region = parts[0];
            regionNameValidator.ensureValid("region", region);

            // check the list of countries for this region
            List<String> countries = Arrays.asList(parts[1].split(","));
            countriesListValidator.ensureValid(region, countries);

            // both look good - add to the map
            regionToCountries.put(region, countries);
        }
        return regionToCountries;
    }
}
