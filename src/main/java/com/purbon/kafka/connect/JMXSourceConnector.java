package com.purbon.kafka.connect;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;

import java.util.List;
import java.util.Map;

public class JMXSourceConnector extends SourceConnector {


    private JMXConnectorConfig config;

    public void start(Map<String, String> config) {

        this.config = new JMXConnectorConfig(config);
    }

    public Class<? extends Task> taskClass() {
        return JMXSourceTask.class;
    }

    public List<Map<String, String>> taskConfigs(int i) {
        return null;
    }

    public void stop() {

    }

    public ConfigDef config() {
        return JMXConnectorConfig.CONFIG;
    }

    public String version() {
        return Version.getVersion();
    }
}
