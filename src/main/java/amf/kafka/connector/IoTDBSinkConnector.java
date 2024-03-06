package amf.kafka.connector;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.sink.SinkConnector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IoTDBSinkConnector extends SinkConnector {
    private Map<String, String> configProperties;

    @Override
    public String version() {
        return "0.1.2.3";
    }

    @Override
    public void start(Map<String, String> props) {
        try {
            configProperties = props;
            new IoTDBSinkConnectorConfig(props);
        } catch (ConfigException ex) {
            throw new ConnectException("Couldn't start IoTDBSinkConnector due to configuration error", ex);
        }
    }

    @Override
    public Class<? extends Task> taskClass() {
        return IoTDBSinkTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        List<Map<String, String>> taskConfigs = new ArrayList<>();
        Map<String, String> taskProps = new HashMap<>();
        taskProps.putAll(configProperties);
        for (int i = 0; i < maxTasks; i++) {
            taskConfigs.add(taskProps);
        }
        return taskConfigs;
    }

    @Override
    public void stop() {
    }

    @Override
    public ConfigDef config() {
        return IoTDBSinkConnectorConfig.conf();
    }
}

