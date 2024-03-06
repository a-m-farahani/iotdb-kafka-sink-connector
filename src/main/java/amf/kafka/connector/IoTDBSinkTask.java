package amf.kafka.connector;

import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class IoTDBSinkTask extends SinkTask {
    private static final Logger logger = LoggerFactory.getLogger(IoTDBSinkTask.class);
    IoTDBSinkService sinkService;

    @Override
    public String version() {
        return "0.1.2.3";
    }

    @Override
    public void start(Map<String, String> map) {
        logger.info("starting iotdb-kafka-sink-connector...");
        sinkService = new IoTDBSinkService(new IoTDBSinkConnectorConfig(map));
        try {
            sinkService.init();
        } catch (IOException e) {
            logger.error("[start] Error: " + e.toString());
        }
    }

    @Override
    public void put(Collection<SinkRecord> collection) {
        sinkService.process(collection);
    }

    @Override
    public void flush(Map<TopicPartition, OffsetAndMetadata> map) {
        logger.debug("flushing...");
    }

    @Override
    public void stop() {
        logger.info("stopping sink connector and releasing resources...");
        sinkService.release();
    }
}
