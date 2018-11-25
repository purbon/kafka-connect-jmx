package com.purbon.kafka.connect;


import java.util.Map;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

public class JMXConnectorConfig extends AbstractConfig {

  public static final ConfigDef CONFIG = baseConfigDef();

  public static final String SERVER_URL = "jmx.server.url";
  public static final String MONITOR_METRIC = "monitor.metric";

  public JMXConnectorConfig(Map<String, String> config) {
    super(CONFIG, config);

  }

  protected static ConfigDef baseConfigDef() {
    final ConfigDef configDef = new ConfigDef();
    addConnectorConfigs(configDef);
    addConversionConfigs(configDef);
    return configDef;
  }

  private static void addConnectorConfigs(ConfigDef configDef) {

  }

  private static void addConversionConfigs(ConfigDef configDef) {

  }

}

