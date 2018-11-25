import static org.junit.Assert.assertEquals;

import com.purbon.kafka.connect.JMXSourceConnector;
import com.purbon.kafka.connect.JMXSourceTask;
import org.junit.Before;
import org.junit.Test;

public class JMXSourceConnectorTest {

  private JMXSourceConnector connector;

  @Before
  public void setup() {
    connector = new JMXSourceConnector();
  }

  @Test
  public void testTaskClass() {
    assertEquals(JMXSourceTask.class, connector.taskClass());
  }
}
