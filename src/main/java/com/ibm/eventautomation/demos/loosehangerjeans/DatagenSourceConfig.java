/**
 * Copyright 2023 IBM Corp. All Rights Reserved.
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

import java.util.Arrays;
import java.util.List;

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
    public static final String CONFIG_FORMATS_TIMESTAMPS = "formats.timestamps";

    private static final String CONFIG_GROUP_TOPICNAMES = "Topic names";
    public static final String CONFIG_TOPICNAME_ORDERS         = "topic.name.orders";
    public static final String CONFIG_TOPICNAME_CANCELLATIONS  = "topic.name.cancellations";
    public static final String CONFIG_TOPICNAME_STOCKMOVEMENTS = "topic.name.stockmovements";
    public static final String CONFIG_TOPICNAME_BADGEINS       = "topic.name.badgeins";
    public static final String CONFIG_TOPICNAME_CUSTOMERS      = "topic.name.newcustomers";
    public static final String CONFIG_TOPICNAME_SENSORREADINGS = "topic.name.sensorreadings";
    
    private static final String CONFIG_GROUP_LOCATIONS = "Locations";
    public static final String CONFIG_LOCATIONS_REGIONS = "locations.regions";
    public static final String CONFIG_LOCATIONS_WAREHOUSES = "locations.warehouses";

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

    private static final String CONFIG_GROUP_DELAYS = "Event delays";
    public static final String CONFIG_DELAYS_ORDERS         = "eventdelays.orders.secs.max";
    public static final String CONFIG_DELAYS_CANCELLATIONS  = "eventdelays.cancellations.secs.max";
    public static final String CONFIG_DELAYS_STOCKMOVEMENTS = "eventdelays.stockmovements.secs.max";
    public static final String CONFIG_DELAYS_BADGEINS       = "eventdelays.badgeins.secs.max";
    public static final String CONFIG_DELAYS_NEWCUSTOMERS   = "eventdelays.newcustomers.secs.max";
    public static final String CONFIG_DELAYS_SENSORREADINGS = "eventdelays.sensorreadings.secs.max";

    private static final String CONFIG_GROUP_DUPLICATES = "Duplicate events";
    public static final String CONFIG_DUPLICATE_ORDERS         = "duplicates.orders.ratio";
    public static final String CONFIG_DUPLICATE_CANCELLATIONS  = "duplicates.cancellations.ratio";
    public static final String CONFIG_DUPLICATE_STOCKMOVEMENTS = "duplicates.stockmovements.ratio";
    public static final String CONFIG_DUPLICATE_BADGEINS       = "duplicates.badgeins.ratio";
    public static final String CONFIG_DUPLICATE_NEWCUSTOMERS   = "duplicates.newcustomers.ratio";
    public static final String CONFIG_DUPLICATE_SENSORREADINGS = "duplicates.sensorreadings.ratio";

    private static final String CONFIG_GROUP_TIMES = "Timings";
    public static final String CONFIG_TIMES_ORDERS           = "timings.ms.orders";
    public static final String CONFIG_TIMES_FALSEPOSITIVES   = "timings.ms.falsepositives";
    public static final String CONFIG_TIMES_SUSPICIOUSORDERS = "timings.ms.suspiciousorders";
    public static final String CONFIG_TIMES_STOCKMOVEMENTS   = "timings.ms.stockmovements";
    public static final String CONFIG_TIMES_BADGEINS         = "timings.ms.badgeins";
    public static final String CONFIG_TIMES_NEWCUSTOMERS     = "timings.ms.newcustomers";
    public static final String CONFIG_TIMES_SENSORREADINGS   = "timings.ms.sensorreadings";


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
                                  "Blue", "Black", "White", "Khaki", "Denim", "Jeggings"),
                    new ValidTermsList(),
                    Importance.LOW,
                    "List of materials to use for generated product names. Materials cannot contain spaces.",
                    CONFIG_GROUP_PRODUCTS, 2, Width.MEDIUM, "Materials")
        .define(CONFIG_PRODUCTS_STYLES,
                    Type.LIST,
                    Arrays.asList("Skinny", "Bootcut", "Flare", "Ripped", "Capri", "Jogger",
                                  "Crochet", "High-waist", "Low-rise", "Straight-leg",
                                  "Boyfriend", "Mom", "Wide-leg", "Jorts", "Cargo", "Tall"),
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
                    Arrays.asList("CHANGEDMIND", "BADFIT"),
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
        // how long to delay messages before producing them to Kafka
        //
        .define(CONFIG_DELAYS_ORDERS,
                    Type.INT,
                    0, // payload time matching event time by default
                    Range.between(0, 900),  // up to 15 mins max
                    Importance.LOW,
                    "Maximum delay (in *seconds*) to produce new order events (this is the maximum difference allowed the timestamp string in the event payload, and the Kafka message's metadata timestamp)",
                    CONFIG_GROUP_DELAYS, 1, Width.SHORT, "Order events - max produce delay")
        .define(CONFIG_DELAYS_CANCELLATIONS,
                    Type.INT,
                    0, // payload time matching event time by default
                    Range.between(0, 900),  // up to 15 mins max
                    Importance.LOW,
                    "Maximum delay (in *seconds*) to produce cancellation events (this is the maximum difference allowed the timestamp string in the event payload, and the Kafka message's metadata timestamp)",
                    CONFIG_GROUP_DELAYS, 2, Width.SHORT, "Cancellation events - max produce delay")
        .define(CONFIG_DELAYS_STOCKMOVEMENTS,
                    Type.INT,
                    0, // payload time matching event time by default
                    Range.between(0, 900),  // up to 15 mins max
                    Importance.LOW,
                    "Maximum delay (in *seconds*) to produce stock movement events (this is the maximum difference allowed the timestamp string in the event payload, and the Kafka message's metadata timestamp)",
                    CONFIG_GROUP_DELAYS, 3, Width.SHORT, "Stock movement events - max produce delay")
        .define(CONFIG_DELAYS_BADGEINS,
                    Type.INT,
                    180,  // payload time can be up to 3 minutes behind event time by default
                    Range.between(0, 900),  // up to 15 mins max
                    Importance.LOW,
                    "Maximum delay (in *seconds*) to produce badge events (this is the maximum difference allowed the timestamp string in the event payload, and the Kafka message's metadata timestamp)",
                    CONFIG_GROUP_DELAYS, 4, Width.SHORT, "Badge events - max produce delay")
        .define(CONFIG_DELAYS_NEWCUSTOMERS,
                    Type.INT,
                    0, // payload time matching event time by default
                    Range.between(0, 900),  // up to 15 mins max
                    Importance.LOW,
                    "Maximum delay (in *seconds*) to produce customer registration events (this is the maximum difference allowed the timestamp string in the event payload, and the Kafka message's metadata timestamp)",
                    CONFIG_GROUP_DELAYS, 5, Width.SHORT, "New customer events - max produce delay")
        .define(CONFIG_DELAYS_SENSORREADINGS,
                    Type.INT,
                    300, // payload time can be up to 5 minutes behind event time by default
                    Range.between(0, 900),  // up to 15 mins max
                    Importance.LOW,
                    "Maximum delay (in *seconds*) to produce sensor reading events (this is the maximum difference allowed the timestamp string in the event payload, and the Kafka message's metadata timestamp)",
                    CONFIG_GROUP_DELAYS, 5, Width.SHORT, "Sensor reading events - max produce delay")
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
                    "Ratio of cancellation events that should be duplicated. Must be between 0 and 1.",
                    CONFIG_GROUP_DUPLICATES, 3, Width.SHORT, "Duplicate stock movement events ratio")
        .define(CONFIG_DUPLICATE_BADGEINS,
                    Type.DOUBLE,
                    0,   // don't create duplicate events by default
                    Range.between(0.0, 1.0), // ratio should be between 0 (don't create duplicates) and 1 (duplicate every message)
                    Importance.LOW,
                    "Ratio of cancellation events that should be duplicated. Must be between 0 and 1.",
                    CONFIG_GROUP_DUPLICATES, 4, Width.SHORT, "Duplicate badge events ratio")
        .define(CONFIG_DUPLICATE_NEWCUSTOMERS,
                    Type.DOUBLE,
                    0,   // don't create duplicate events by default
                    Range.between(0.0, 1.0), // ratio should be between 0 (don't create duplicates) and 1 (duplicate every message)
                    Importance.LOW,
                    "Ratio of cancellation events that should be duplicated. Must be between 0 and 1.",
                    CONFIG_GROUP_DUPLICATES, 5, Width.SHORT, "Duplicate new customer events ratio")
        .define(CONFIG_DUPLICATE_SENSORREADINGS,
                    Type.DOUBLE,
                    0,   // don't create duplicate events by default
                    Range.between(0.0, 1.0), // ratio should be between 0 (don't create duplicates) and 1 (duplicate every message)
                    Importance.LOW,
                    "Ratio of sensor reading events that should be duplicated. Must be between 0 and 1.",
                    CONFIG_GROUP_DUPLICATES, 5, Width.SHORT, "Duplicate sensor events ratio")
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
                    CONFIG_GROUP_TIMES, 4, Width.MEDIUM, "Stock movements delay")
        .define(CONFIG_TIMES_NEWCUSTOMERS,
                    Type.INT,
                    543_400, // a little over 9 minutes
                    Range.atLeast(5_000),  // 5 seconds
                    Importance.LOW,
                    "Delay, in milliseconds, between each new customer that should be generated.")
        .define(CONFIG_TIMES_SENSORREADINGS,
                    Type.INT,
                    27_000, // 27 seconds
                    Range.atLeast(5_000),  // 5 seconds
                    Importance.LOW,
                    "Delay, in milliseconds, between each sensor reading that should be generated.");




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
}
