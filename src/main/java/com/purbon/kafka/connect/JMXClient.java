package com.purbon.kafka.connect;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JMXClient {


    private static final Logger log = LoggerFactory.getLogger(JMXClient.class);

    class ClientNotificationListener implements NotificationListener {

        /**
         * Invoked when a JMX notification occurs.
         * The implementation of this method should return as soon as possible, to avoid
         * blocking its notification broadcaster.
         *
         * @param notification The notification.
         * @param handback     An opaque object which helps the listener to associate
         *                     information regarding the MBean emitter. This object is passed to the
         *                     addNotificationListener call and resent, without modification, to the
         */
        public void handleNotification(Notification notification, Object handback) {
            System.out.println(notification);
        }
    }

    private final String url;
    private JMXConnector connector;
    private MBeanServerConnection mbsc;

    private ObjectMapper mapper;

    public JMXClient(String url) {
        this.url = url;
        this.mapper = new ObjectMapper();
    }

    public void connect() throws IOException {
        JMXServiceURL serviceURL  = new JMXServiceURL(url);
        connector    = JMXConnectorFactory.connect(serviceURL);
        mbsc = connector.getMBeanServerConnection();

        System.out.println("\nDomains:");
        String domains[] = mbsc.getDomains();
        Arrays.sort(domains);
        for (String domain : domains) {
            System.out.println("\tDomain = " + domain);
        }

        int beanCount = mbsc.getMBeanCount();
        System.out.println("Count: "+beanCount);

        Set<ObjectName> names =
                new TreeSet<ObjectName>(mbsc.queryNames(null, null));
        for (ObjectName name : names) {
            System.out.println("\tObjectName = " + name);
        }

    }

    public String get(String metric) throws JMXConnectorException {
        ObjectName objectName;
        try {
            objectName = new ObjectName(metric);

            MBeanInfo info = mbsc.getMBeanInfo(objectName);
            Map<String, Object> attributes = new HashMap<String, Object>();

            for(MBeanAttributeInfo attr : info.getAttributes()) {
                attributes.put(attr.getName(), mbsc.getAttribute(objectName, attr.getName()));
            }
            return mapper.writeValueAsString(attributes);
        } catch (Exception e) {
            log.error("Can not get the JMX metric", e);
            throw new JMXConnectorException(e);
        }

    }

    public void start() {
        ClientNotificationListener listener = new ClientNotificationListener();
        connector.addConnectionNotificationListener(listener, null, null);
    }

    public void close() {
        try {
            connector.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        JMXClient client = new JMXClient("service:jmx:rmi:///jndi/rmi://127.0.0.1:9292/jmxrmi");

        try {
            client.connect();

            client.start();

            client.get("kafka.server:type=BrokerTopicMetrics,name=BytesInPerSec");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();
        }


    }
}
