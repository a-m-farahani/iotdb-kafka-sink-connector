## A Sink Connector for Apache IoTDB

iotdb-kafka-sink-connector is a **Sink** connector for [Apache IoTDB](https://iotdb.apache.org/). 

While [Confluent Platform](https://confluent.io) offers a diverse selection of connectors for Kafka services, an official Sink connector for integrating with IoTDB is currently not available (You can see official Kafka connectors here: https://docs.confluent.io/platform/current/connect/kafka_connectors.html).

This project is a simple example of implementing a minimal Kafka sink connector for integration with Apache IoTDB.

Dependencies:
* org.apache.kafka 0.11.0
* org.apache.iotdb 1.3.0
* org.slf4j 2.0.9

#### Build:
This project uses Maven as the build tools. To build it, run ```mvn package``` command. Example:
```
$ mvn clean package -P get-jar-with-dependencies
```

The 'get-jar-with-dependencies' profile, defined in the ```pom.xml``` file, packages the entire sink connector module into a single executable JAR file. The JAR file is compatible with confluent/kafka-connect service. After building, JAR file would be in the ```target``` directory.

---

### Details:
A Sink connector primarily needs to inherit and extend three classes:
* `org.apache.kafka.common.config.AbstractConfig`
    * serves as a foundation for configuration management in custom connectors.
* `org.apache.kafka.connect.sink.SinkTask`
    * responsible for processing and sending data from Kafka topics to external systems (in this case SinkConnector object).
* `org.apache.kafka.connect.sink.SinkConnector`
    * serves as the blueprint for applications that move data from Kafka topics to external systems.

We also implemented an IoTDBSinkService class to isolate the code interacting with IoTDB from the main module.

We assume each Kafka message we consume has two required fileds:
* `timestamp`
* `id`

Consider a scenario where services publish messages to a Kafka topic named 'PV_DATA'. These messages, representing data from photovoltaic (PV) panels, could be formatted like this:
```
{
  'pv_id': 'dkJh1o0wxAM',
  'timestamp': 1709999058,
  'dc_power': 2.3,
  'irradiation': 4.67,
  'module_temperature': 16.8
}
```

Note that the message format can be PlainText, JSON, or Protobuf. Kafka-Connect handles decoding the message content automatically, so you don't need to concern yourself with the decoding stuff. For more detail about encoding and decondig Kafka messages [see this](https://github.com/a-m-farahani/kafka-tutorial#schema-registry). Schema-registry settings in the config file ([iotdb-sink-config.json](https://github.com/a-m-farahani/iotdb-kafka-sink-connector/blob/main/iotdb-sink-config.json)) is for this purpose.

---

### Usage:
We've previously mentioned this SinkConnector is tested with the `confluentinc/cp-kafka-connect` Docker image. However, to integrate your custom iotdb-sink-connector, you'll need to take the following steps: 
1. **Build** and Package the project to create a JAR file.
2. **Mount** the JAR file as a volume at `/usr/share/java/` within the Kafka-Connect Docker container. ([Example](https://github.com/confluentinc/demo-scene/blob/master/kafka-connect-zero-to-hero/docker-compose.yml#L82-L87))
3. **Prepare config** file, you can use a file like [iotdb-sink-config.json](https://github.com/a-m-farahani/iotdb-kafka-sink-connector/blob/main/iotdb-sink-config.json) as a reference. This file provides an example of the settings required for the iotdb-kafka-sink-connector. (Note: AbstractConfig class reads the config file contents.)
4. **Register** the Sink connector. To register your custom connector you can send a `POST` request to Kafka-Connect REST service.

    `$ curl -s -S -XPOST -H Accept:application/json -H Content-Type:application/json http://localhost:8083/connectors/ -d @./iotdb-sink-config.json`

After registering the connector you can check it by tracing Kafka-Connect logs.
