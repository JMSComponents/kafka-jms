import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import java.io.IOException;

import io.github.jmscomponents.kafka.jms.KafkaConnectionFactory;
import org.apache.kafka.streams.integration.utils.EmbeddedKafkaCluster;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by pearcem on 15/06/2017.
 */
public class JMSContextTest {

    private static String TOPIC_NAME = "topic";
    private static String QUEUE_NAME = "queue";

    private EmbeddedKafkaCluster embeddedKafkaCluster;

    @Before
    public void before() throws IOException, InterruptedException
    {
        embeddedKafkaCluster = new EmbeddedKafkaCluster(1);
        embeddedKafkaCluster.start();
        embeddedKafkaCluster.createTopic(TOPIC_NAME);
        embeddedKafkaCluster.createTopic(QUEUE_NAME);

    }

    @After
    public void after(){
        embeddedKafkaCluster.stop();
    }

    @Test
    public void testQueue() throws IOException, InterruptedException, JMSException {

        ConnectionFactory connectionFactory = new KafkaConnectionFactory(embeddedKafkaCluster.bootstrapServers());

        String text = "testString";
        TextMessage result;
        try(JMSContext context = connectionFactory.createContext()){
            Queue destination = context.createQueue(QUEUE_NAME);
            JMSProducer jmsProducer = context.createProducer();
            jmsProducer.send(destination, text);

            JMSConsumer jmsConsumer = context.createConsumer(destination);
            result = (TextMessage) jmsConsumer.receive(100);
        }

        assertEquals(text, result.getText());
    }

    @Test
    public void testTopic() throws IOException, InterruptedException, JMSException {

        ConnectionFactory connectionFactory = new KafkaConnectionFactory(embeddedKafkaCluster.bootstrapServers());

        String text = "testString";
        TextMessage result;
        try(JMSContext context = connectionFactory.createContext()){
            Topic destination = context.createTopic(TOPIC_NAME);
            JMSProducer jmsProducer = context.createProducer();
            jmsProducer.send(destination, text);

            JMSConsumer jmsConsumer = context.createConsumer(destination);
            result = (TextMessage) jmsConsumer.receive(100);

            assertNull("topic subscription should only get messages after subscription", result);

            jmsProducer.send(destination, text);

            result = (TextMessage) jmsConsumer.receive(100);
        }

        assertEquals(text, result.getText());
    }
}
