package amf.kafka.connector;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;

import java.util.Map;

public class IoTDBSinkConnectorConfig extends AbstractConfig{
    public static final String IOTDB_URL = "iotdb.url";
    private static final String IOTDB_URL_DOC = "IoTDB URL to connect.";
    public static final String IOTDB_PORT = "iotdb.port";
    private static final String IOTDB_PORT_DOC = "IoTDB transport port to connect.";

    public static final String STORAGE_GROUP = "storage.group";
    private static final String STORAGE_GROUP_DOC = "storage group in which the data should store";

    public static final String TIMESERIES_KEY = "timeseries.key";
    private static final String TIMESERIES_KEY_DOC = "the field which is used as the timeseries key";

    public static final String TIMESERIES_VALUES = "timeseries.values";
    private static final String TIMESERIES_VALUES_DOC = "field in timeseries that should be stored";

    public static final String TIMESERIES_TYPES = "timeseries.types";
    private static final String TIMESERIES_TYPES_DOC = "type of fields in timeseries.values";

    public static final String TIMESTAMP_FIELD_NAME = "timeseries.timestamp";
    private static final String TIMESTAMP_FIELD_NAME_DOC = "the field name of timestamp in the kafka data";

    public static final String USERNAME = "iotdb.user";
    private static final String USERNAME_DOC = "username for iotdb";
    public static final String PASSWORD = "iotdb.password";
    public static final String PASSWORD_DOC = "password for iotdb";

    public IoTDBSinkConnectorConfig(ConfigDef config, Map<String, String> parsedConfig) {
        super(config, parsedConfig);
    }
    public IoTDBSinkConnectorConfig(Map<String, String> parsedConfig) {
        this(conf(), parsedConfig);
    }

    public static ConfigDef conf() {
        return new ConfigDef()
                .define(IOTDB_URL, Type.STRING, Importance.HIGH, IOTDB_URL_DOC)
                .define(IOTDB_PORT, Type.INT, Importance.HIGH, IOTDB_PORT_DOC)
                .define(STORAGE_GROUP, Type.STRING, Importance.HIGH, STORAGE_GROUP_DOC)
                .define(TIMESERIES_KEY, Type.STRING, Importance.HIGH, TIMESERIES_KEY_DOC)
                .define(TIMESERIES_VALUES, Type.STRING, Importance.HIGH, TIMESERIES_VALUES_DOC)
                .define(TIMESERIES_TYPES, Type.STRING, Importance.HIGH, TIMESERIES_TYPES_DOC)
                .define(TIMESTAMP_FIELD_NAME, Type.STRING, Importance.HIGH, TIMESTAMP_FIELD_NAME)
                .define(USERNAME, Type.STRING, Importance.HIGH, USERNAME_DOC)
                .define(PASSWORD, Type.STRING, Importance.HIGH, PASSWORD_DOC);
    }

    public String getURL(){
        return this.getString(IOTDB_URL);
    }

    public Integer getPort(){
        return this.getInt(IOTDB_PORT);
    }

    public String getStorageGroup(){
        return this.getString(STORAGE_GROUP);
    }

    public String getTimeseriesKey(){
        return this.getString(TIMESERIES_KEY);
    }

    public String getTimeseriesValues(){
        return this.getString(TIMESERIES_VALUES);
    }

    public String getTimeseriesTypes(){
        return this.getString(TIMESERIES_TYPES);
    }

    public String getTimestampFieldName(){
        return this.getString(TIMESTAMP_FIELD_NAME);
    }

    public String getUsername(){
        return this.getString(USERNAME);
    }

    public String getPassword(){
        return this.getString(PASSWORD);
    }
}
