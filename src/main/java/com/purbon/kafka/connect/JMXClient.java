package com.purbon.kafka.connect;

import javassist.bytecode.AttributeInfo;

import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public class JMXClient {


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

    public JMXClient(String url) {
        this.url = url;
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

    public void get(String metric) {
        ObjectName objectName = null;
        try {
            objectName = new ObjectName(metric);

            //System.out.println(mbsc.getMBeanInfo(objectName));
            MBeanInfo info = mbsc.getMBeanInfo(objectName);

            for(MBeanAttributeInfo attr : info.getAttributes()) {
                System.out.println(attr.getName()+" "+mbsc.getAttribute(objectName, attr.getName()));
            }

        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ReflectionException e) {
            e.printStackTrace();
        } catch (InstanceNotFoundException e) {
            e.printStackTrace();
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (AttributeNotFoundException e) {
            e.printStackTrace();
        } catch (MBeanException e) {
            e.printStackTrace();
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

        JMXClient client = new JMXClient("service:jmx:rmi:///jndi/rmi://192.168.1.5:9292/jmxrmi");

        try {
            client.connect();

            client.start();

            client.get("kafka.server:type=BrokerTopicMetrics,name=BytesInPerSec");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            client.close();
        }


    }
}
