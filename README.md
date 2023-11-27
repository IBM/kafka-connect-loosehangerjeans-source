# kafka-connect-loosehangerjeans-source

Kafka Connect source connector used for generating simulated events for demos and tests.

It produces messages simulating the following events:

| **Topic name** | **Description** |
| -------------- | --------------- |
| `DOOR.BADGEIN`  | An employee using their id badge to go through a door |
| `CANCELLATIONS` | An order being cancelled |
| `CUSTOMERS.NEW` | A new customer has registered on the website |
| `ORDERS.NEW`    | An order has been placed |
| `SENSOR.READINGS` | A sensor reading captured from an IoT sensor |
| `STOCK.MOVEMENT` | Stock shipment received by a warehouse |


Avro schemas and sample messages for each of these topics can be found in the `./doc` folder.


## Config

### Minimal

By default, minimal config is needed. Specifying that keys are strings and payloads are json is enough to start it running.

```yaml
apiVersion: eventstreams.ibm.com/v1beta2
kind: KafkaConnector
metadata:
  name: kafka-datagen
  labels:
    eventstreams.ibm.com/cluster: kafka-connect-cluster
spec:
  class: com.ibm.eventautomation.demos.loosehangerjeans.DatagenSourceConnector
  tasksMax: 1
  config:
    key.converter: org.apache.kafka.connect.storage.StringConverter
    key.converter.schemas.enable: false
    value.converter: org.apache.kafka.connect.json.JsonConverter
    value.converter.schemas.enable: false
```

### Custom

Config overrides are available to allow demos based on different domains or industries.

Example config is listed below with all possible options, each shown with their default values.

```yaml
apiVersion: eventstreams.ibm.com/v1beta2
kind: KafkaConnector
metadata:
  name: kafka-datagen
  labels:
    eventstreams.ibm.com/cluster: kafka-connect-cluster
spec:
  class: com.ibm.eventautomation.demos.loosehangerjeans.DatagenSourceConnector
  tasksMax: 1
  config:
    #
    # format of messages to produce
    #
    key.converter: org.apache.kafka.connect.storage.StringConverter
    key.converter.schemas.enable: false
    value.converter: org.apache.kafka.connect.json.JsonConverter
    value.converter.schemas.enable: false

    #
    # name of the topics to produce to
    #
    topic.name.orders: ORDERS.NEW
    topic.name.cancellations: CANCELLATIONS
    topic.name.stockmovements: STOCK.MOVEMENT
    topic.name.badgeins: DOOR.BADGEIN
    topic.name.newcustomers: CUSTOMERS.NEW
    topic.name.sensorreadings: SENSOR.READINGS

    #
    # format of timestamps to produce
    #
    #    default is chosen to be suitable for use with Event Processing
    #    but you could modify this if you want to demo how to reformat
    #    timestamps to be compatible with Event Processing
    #
    #    NOTE: sensor readings topic is an exception. Events on this topic
    #           ignore this config option
    #
    formats.timestamps: yyyy-MM-dd HH:mm:ss.SSS

    #
    # how often events should be created
    #
    # 'normal' random orders
    timings.ms.orders: 30000              # every 30 seconds
    # cancellations of a large order followed by a small order of the same item
    timings.ms.falsepositives: 600000     # every 10 minutes
    # repeated cancellations of a large order followed by a small order of the same item
    timings.ms.suspiciousorders: 3600000  # every hour
    # stock movement events
    timings.ms.stockmovements: 300000     # every 5 minutes
    # door badge events
    timings.ms.badgeins: 600              # sub-second
    # new customer events
    timings.ms.newcustomers: 543400       # a little over 9 minutes
    # sensor reading events
    timings.ms.sensorreadings: 27000      # every 27 seconds

    #
    # how much of a delay to introduce when producing events
    #
    #    this is to simulate events from systems that are slow to
    #    produce to Kafka
    #
    #    events with a delay will be produced to Kafka a short
    #    time after the timestamp contained in the message payload
    #
    #    the result is that the timestamp in the message metadata
    #    will be later then the message in the message payload
    #
    #    because the delay will be random (up to the specified max)
    #    the impact of this is that messages on the topic will be
    #    slightly out of sequence (according to the timestamp in
    #    the message payload)
    #
    # orders
    eventdelays.orders.secs.max: 0             # payload time matches event time by default
    # cancellations
    eventdelays.cancellations.secs.max: 0      # payload time matches event time by default
    # stock movements
    eventdelays.stockmovements.secs.max: 0     # payload time matches event time by default
    # door badge events
    eventdelays.badgeins.secs.max: 180         # payload time can be up to 3 minutes (180 secs) behind event time
    # new customer events
    eventdelays.newcustomers.secs.max: 0       # payload time matches event time by default
    # sensor readings events
    eventdelays.sensorreadings.secs.max: 300   # payload time can be up to 5 minutes (300 secs) behind event time

    #
    # how many events should be duplicated
    #
    #   this is to simulate events from systems that offer
    #   at-least once semantics
    #
    #   messages will occasionally be duplicated, according
    #   to the specified ratio
    #   between 0.0 and 1.0 : 0.0 means events will never be duplicated,
    #                         0.5 means approximately half of the events will be duplicated
    #                         1.0 means all events will be duplicated
    #
    # orders
    duplicates.orders.ratio: 0             # events not duplicated
    # cancellations
    duplicates.cancellations.ratio: 0      # events not duplicated
    # stock movements
    duplicates.stockmovements.ratio: 0.1   # duplicate roughly 10% of the events
    # door badge events
    duplicates.badgeins.ratio: 0           # events not duplicated
    # new customer events
    duplicates.newcustomers.ratio: 0       # events not duplicated
    # sensor reading events
    duplicates.sensorreadings.ratio: 0     # events not duplicated

    #
    # product names to use in events
    #
    #    these are combined into description strings, to allow for
    #    use of Event Processing string functions like regexp extracts
    #    e.g. "XL Stonewashed Bootcut Jeans"
    #
    #    any or all of these can be modified to theme the demo for a
    #    different industry
    products.sizes: XXS,XS,S,M,L,XL,XXL
    products.materials: Classic,Retro,Navy,Stonewashed,Acid-washed,Blue,Black,White,Khaki,Denim,Jeggings
    products.styles: Skinny,Bootcut,Flare,Ripped,Capri,Jogger,Crochet,High-waist,Low-rise,Straight-leg,Boyfriend,Mom,Wide-leg,Jorts,Cargo,Tall
    products.name: Jeans

    #
    # prices to use for individual products
    #
    #    prices will be randomly generated between the specified range
    prices.min: 14.99
    prices.max: 59.99
    # prices following large order cancellations will be reduced by a random value up to this limit
    prices.maxvariation: 9.99

    #
    # number of items to include in an order
    #
    # "normal" orders will be between small.min and large.max
    #   (i.e. between 1 and 15, inclusive)
    #
    # a "small" order is between 1 and 5 items (inclusive)
    orders.small.quantity.min: 1
    orders.small.quantity.max: 5
    # a "large" order is between 5 and 15 items (inclusive)
    orders.large.quantity.min: 5
    orders.large.quantity.max: 15

    #
    # controlling when orders should be cancelled
    #
    # how many orders on the ORDERS topic should be cancelled (between 0.0 and 1.0)
    cancellations.ratio: 0.005
    # how long after an order should the cancellation happen
    cancellations.delay.ms.min: 300000   # 5 minutes
    cancellations.delay.ms.max: 7200000  # 2 hours
    # reason given for a cancellation
    cancellations.reasons: CHANGEDMIND,BADFIT

    #
    # suspicious orders
    #
    #  these are the events that are looked for in lab 5 and lab 6
    #
    # how quickly will the large order will be cancelled
    suspicious.cancellations.delay.ms.min: 900000    # at least 15 minutes
    suspicious.cancellations.delay.ms.max: 1800000   # within 30 minutes
    # how many large orders will be made and cancelled
    suspicious.cancellations.max: 3   # up to three large orders
    # customer names to be used for suspicious orders will be selected from this
    #  list, to make it easier in lab 5 and 6 to see that you have created the
    #  flow correctly, and to make it easier in lab 4 to see that there are false
    #  positives in the simplified implementation
    suspicious.cancellations.customernames: Suspicious Bob,Naughty Nigel,Criminal Clive,Dastardly Derek

    #
    # new customers
    #
    #  these events are intended to represent new customers that
    #   have registered with the company
    #
    # how many new customers should quickly create their first order
    #  between 0.0 and 1.0 : 0.0 means new customers will still be created, but they will
    #                           never create orders,
    #                         1.0 means all new customers will create an order
    newcustomers.order.ratio: 0.22
    # if a new customer is going to quickly create their first order, how long
    #  should they wait before making their order
    newcustomers.order.delay.ms.min: 180000     # wait at least 3 minutes
    newcustomers.order.delay.ms.max: 1380000    # order within 23 minutes

    #
    # locations that are referred to in generated events
    #
    locations.regions: NA,SA,EMEA,APAC,ANZ
    locations.warehouses: North,South,West,East,Central
```

For example, if you want to theme the demo to be based on products in a different industry, you could adjust product names/styles/materials/name to match your demo (the options don't need to actually be "styles" or "materials" - they just need to be lists that will make sense when combined into a single string).

You may also want to modify the prices.min and prices.max to match the sort of products in your demo.


## Build

```sh
mvn package
```

