package com.purbon.kafka.connect;

import com.sun.xml.internal.ws.streaming.SourceReaderFactory;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JMXSourceTask extends SourceTask {

    private static final Logger log = LoggerFactory.getLogger(JMXSourceTask.class);
    private JMXClient client;
    private String metric;


    public String version() {
        return Version.getVersion();
    }

    public void start(Map<String, String> map) {

        JMXConnectorConfig config = new JMXConnectorConfig(map);

        String serverUrl = config.getString(JMXConnectorConfig.SERVER_URL);

        metric = config.getString(JMXConnectorConfig.MONITOR_METRIC);

        client = new JMXClient(serverUrl);
    }

    public List<SourceRecord> poll() throws InterruptedException {
        List<SourceRecord> records = new ArrayList<SourceRecord>();

        try {

            String topic = "monitoring";
            Map<String, String> partition = Collections.singletonMap("jmx.monitoring", metric);
            String content = client.get(metric);


            Map<String, Long> offset = Collections.singletonMap("timestamp.offset", System.currentTimeMillis());
            records.add(new SourceRecord(partition, offset, topic, null, content));

        } catch (JMXConnectorException e) {
            log.error("JMX poll error", e);
        }

        return records;
    }

    public void stop() {
        client.close();
    }
}
