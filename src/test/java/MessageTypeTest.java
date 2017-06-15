import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;
import java.io.IOException;

import org.apache.kafka.clients.jms.KafkaConnectionFactory;
import org.apache.kafka.streams.integration.utils.EmbeddedKafkaCluster;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by pearcem on 23/03/2017.
 */
public class MessageTypeTest
{

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
   public void testTextMessage() throws IOException, InterruptedException, JMSException {

      ConnectionFactory connectionFactory = new KafkaConnectionFactory(embeddedKafkaCluster.bootstrapServers());
      
      String text = "testString";
      TextMessage result;
      try(Connection connection = connectionFactory.createConnection()){
         connection.start();
         try(Session session = connection.createSession()){
            Queue destination = session.createQueue(QUEUE_NAME);
            MessageProducer messageProducer = session.createProducer(destination);

            messageProducer.send(session.createTextMessage(text));
            
            messageProducer.close();
            
            MessageConsumer messageConsumer = session.createConsumer(destination);
            
            result = (TextMessage)messageConsumer.receive(100);
         }
      }

      assertEquals(text, result.getText());
   }

   @Test
   public void testBytesMessage() throws IOException, InterruptedException, JMSException {

      ConnectionFactory connectionFactory = new KafkaConnectionFactory(embeddedKafkaCluster.bootstrapServers());

      String text = "testString";
      BytesMessage result;
      try(Connection connection = connectionFactory.createConnection()){
         connection.start();
         try(Session session = connection.createSession()){
            Queue destination = session.createQueue(QUEUE_NAME);
            MessageProducer messageProducer = session.createProducer(destination);

            BytesMessage bytesMessage = session.createBytesMessage();
            bytesMessage.writeInt(5);
            messageProducer.send(bytesMessage);

            messageProducer.close();

            MessageConsumer messageConsumer = session.createConsumer(destination);

            result = (BytesMessage)messageConsumer.receive(100);
         }
      }
      assertEquals(5, result.readInt());

      //assertEquals(text, result.readUTF());
   }

   @Test
   public void testMapMessage() throws IOException, InterruptedException, JMSException {

      ConnectionFactory connectionFactory = new KafkaConnectionFactory(embeddedKafkaCluster.bootstrapServers());

      String key = "key";
      String value = "value";

      MapMessage result;
      try(Connection connection = connectionFactory.createConnection()){
         connection.start();
         try(Session session = connection.createSession()){
            Queue destination = session.createQueue(QUEUE_NAME);
            MessageProducer messageProducer = session.createProducer(destination);

            MapMessage mapMessage = session.createMapMessage();
            mapMessage.setString(key, value);
            messageProducer.send(mapMessage);

            messageProducer.close();

            MessageConsumer messageConsumer = session.createConsumer(destination);

            result = (MapMessage)messageConsumer.receive(100);
         }
      }

      assertEquals(value, result.getString(key));
   }

   @Test
   public void testObjectMessage() throws IOException, InterruptedException, JMSException {

      ConnectionFactory connectionFactory = new KafkaConnectionFactory(embeddedKafkaCluster.bootstrapServers());

      String value = "value";

      ObjectMessage result;
      try(Connection connection = connectionFactory.createConnection()){
         connection.start();
         try(Session session = connection.createSession()){
            Queue destination = session.createQueue(QUEUE_NAME);
            MessageProducer messageProducer = session.createProducer(destination);

            ObjectMessage objectMessage = session.createObjectMessage();
            objectMessage.setObject(value);
            messageProducer.send(objectMessage);

            messageProducer.close();

            MessageConsumer messageConsumer = session.createConsumer(destination);

            result = (ObjectMessage)messageConsumer.receive(100);
         }
      }

      assertEquals(value, result.getObject());
   }


   @Test
   public void testStreamMessage() throws IOException, InterruptedException, JMSException {

      ConnectionFactory connectionFactory = new KafkaConnectionFactory(embeddedKafkaCluster.bootstrapServers());

      String value = "value";

      StreamMessage result;
      try(Connection connection = connectionFactory.createConnection()){
         connection.start();
         try(Session session = connection.createSession()){
            Queue destination = session.createQueue(QUEUE_NAME);
            MessageProducer messageProducer = session.createProducer(destination);

            StreamMessage streamMessage = session.createStreamMessage();
            streamMessage.writeString(value);
            messageProducer.send(streamMessage);

            messageProducer.close();

            MessageConsumer messageConsumer = session.createConsumer(destination);

            result = (StreamMessage)messageConsumer.receive(100);
         }
      }

      assertEquals(value, result.readString());
   }
}
