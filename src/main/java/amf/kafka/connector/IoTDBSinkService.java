package amf.kafka.connector;

import org.apache.iotdb.session.Session;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.apache.iotdb.tsfile.write.schema.MeasurementSchema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.sink.SinkRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class IoTDBSinkService {
    private static final Logger logger = LoggerFactory.getLogger(IoTDBSinkTask.class);
    IoTDBSinkConnectorConfig config;
    private static Session _session;
    private List<MeasurementSchema> _schemaList;
    private Boolean _isInitialized = false;

    public IoTDBSinkService(IoTDBSinkConnectorConfig config) {
        this.config = config;
    }

    public void init() throws IOException {
        logger.info("initializing sink service...");
        logger.info(config.getURL() + ":" + config.getPort() + ", Username: " + config.getUsername());
        try {
            _session = new Session(config.getURL(), config.getPort(), config.getUsername(), config.getPassword());
            _session.open();
            initTimeseriesSchema();
        } catch (Exception ex) {
            logger.error("initialization failed, Error: " + ex.getMessage());
        }
    }

    public void release() {
        try {
            _session.close();
        } catch (org.apache.iotdb.rpc.IoTDBConnectionException ex) {
            logger.error("releasing resources failed, Error: " + ex.getMessage());
        }
    }

    private void initTimeseriesSchema() {
        try {
            String[] fields = config.getTimeseriesValues().split(",");
            String[] types = config.getTimeseriesTypes().split(",");

            if (fields.length != types.length) {
                throw new Exception("timeseries.values and timeseries.types does not match in the config file.");
            }

            _schemaList = new ArrayList<>();
            for (int i = 0; i < fields.length; i++) {
                switch (types[i]) {
                    case "int":
                        _schemaList.add(new MeasurementSchema(fields[i], TSDataType.INT64));
                        break;
                    case "text":
                        _schemaList.add(new MeasurementSchema(fields[i], TSDataType.TEXT));
                        break;
                    case "bool":
                        _schemaList.add(new MeasurementSchema(fields[i], TSDataType.BOOLEAN));
                        break;
                    case "double":
                        _schemaList.add(new MeasurementSchema(fields[i], TSDataType.DOUBLE));
                        break;
                    default:
                        throw new Exception("invalid type in timeseries.types");
                }
            }
            logger.info("IoTDB sink connector schema: " + _schemaList.toString());
        } catch (Exception ex) {
            logger.error("an error occurred in schema initialization, Error: " + ex.getMessage());
        }
    }

    // Insert using 'InsertAlignedRecords', fast and efficient
    public void process(Collection<SinkRecord> records) {
        List<String> deviceIds = new ArrayList<>();
        List<Long> timestamps = new ArrayList<>();
        List<List<String>> measurementNames = new ArrayList<>();
        List<List<TSDataType>> measurementTypes = new ArrayList<>();
        List<List<Object>> measurementValues = new ArrayList<>();

        for (SinkRecord record : records) {
            Struct data = (Struct) record.value();
            String id = ((String) data.get(config.getTimeseriesKey())).replace('-', '_');
            String timeSeriesName = config.getStorageGroup() + "." + id;

            List<String> names = new ArrayList<>();
            List<TSDataType> types = new ArrayList<>();
            List<Object> values = new ArrayList<>();
            for (MeasurementSchema schema : _schemaList) {
                names.add(schema.getMeasurementId());
                types.add(schema.getType());
                values.add(data.get(schema.getMeasurementId()));
            }
            deviceIds.add(timeSeriesName);
            timestamps.add(data.getInt64(config.getTimestampFieldName()));
            measurementNames.add(names);
            measurementTypes.add(types);
            measurementValues.add(values);
        }

        // Storing records
        if(deviceIds.size()>0){
            try{
                _session.insertAlignedRecords(deviceIds, timestamps, measurementNames, measurementTypes, measurementValues);
            }
            catch (Exception ex){
                logger.error("an error occurred in processing records, Error: " + ex.getMessage());
            }
        }
    }


    // Single Insert using SQL 'INSERT' statement - Slow and Inefficient
    public void process(SinkRecord record) {
        String[] fields = config.getTimeseriesValues().split(",");
        String[] types = config.getTimeseriesTypes().split(",");
        Struct data = (Struct) record.value();
        String id = (String) data.get(config.getTimeseriesKey());

        String sql = String.format("INSERT INTO %s.%s(timestamp,%s) VALUES(%s", config.getStorageGroup(),
                id, config.getTimeseriesValues(),
                data.get(config.getTimestampFieldName()));
        for (String f : fields) {
            sql += "," + data.get(f);
        }
        sql += ")";

        try {
            _session.executeNonQueryStatement(sql);
        } catch (Exception ex) {
            logger.info("[IoTDBSinkService][process] Error: " + ex.getMessage());
        }
    }
}
