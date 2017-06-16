import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import java.io.IOException;
import java.util.Properties;

import io.github.jmscomponents.kafka.jms.KafkaConnectionFactory;
import org.apache.kafka.streams.integration.utils.EmbeddedKafkaCluster;
import org.junit.After;
import org.junit.Before;

/**
 * Created by pearcem on 16/06/2017.
 */
public class BaseKafkaJMSTest {

    static String TOPIC_NAME = "topic";
    static String QUEUE_NAME = "queue";
    
    EmbeddedKafkaCluster embeddedKafkaCluster;
    KafkaConnectionFactory connectionFactory;

    @Before
    public void before() throws IOException, InterruptedException, JMSException {
        Properties properties = new Properties();
        properties.setProperty("zookeeper.session.timeout.ms", "10000");
        properties.setProperty("zookeeper.sync.time.ms", "5000");
        properties.setProperty("fetch.max.wait.ms", "1000");
        properties.setProperty("session.timeout.ms", "50000");
        embeddedKafkaCluster = new EmbeddedKafkaCluster(1, properties);
        embeddedKafkaCluster.start();
        embeddedKafkaCluster.createTopic(TOPIC_NAME);
        embeddedKafkaCluster.createTopic(QUEUE_NAME);
        Thread.sleep(1000);
        connectionFactory = new KafkaConnectionFactory(embeddedKafkaCluster.bootstrapServers());
        connectionFactory.setProperties(properties);

    }
    
    @After
    public void after(){
        embeddedKafkaCluster.stop();
    }

}
