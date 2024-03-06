## A Sink Connector for Kafka-Connect service

iotdb-kafka-sink-connector is a **Sink** connector for [Apache IoTDB](https://iotdb.apache.org/). 

While [Confluent Platform](https://confluent.io) offers a diverse selection of connectors for Kafka services, an official Sink connector for integrating with IoTDB is currently not available (You can see official Kafka connectors here: https://docs.confluent.io/platform/current/connect/kafka_connectors.html).

This project is a simple example of implementing a minimal Kafka sink connector for integration with Apache IoTDB.

Dependencies:
* org.apache.kafka 0.11.0
* org.apache.iotdb 1.3.0
* org.slf4j 2.0.9

#### Build
This project uses Maven as the build tools. To build it, run ```mvn package``` command. Example:
```
$ mvn clean package -P get-jar-with-dependencies
```

The 'get-jar-with-dependencies' profile, defined in the ```pom.xml``` file, packages the entire sink connector module into a single executable JAR file. The JAR file is compatible with confluent/kafka-connect service. After building, JAR file would be in the ```target``` directory.

#### Usage
TODO: add contents about how to use this sink connector
