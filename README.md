# kafka-connect-jmx
ingesting jmx metrics into kafka.

java.rmi.server.hostname=<JMX_HOSTNAME>
com.sun.management.jmxremote.local.only=false
com.sun.management.jmxremote.rmi.port=<JMX_PORT>
com.sun.management.jmxremote.port=<JMX_PORT>


export KAFKA_JMX_OPTS="-Dcom.sun.management.jmxremote=true -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=127.0.0.1"

export JMX_PORT=9292