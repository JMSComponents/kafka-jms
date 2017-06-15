/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import io.github.jmscomponents.kafka.jms.KafkaConnectionFactory;
import org.apache.kafka.streams.integration.utils.EmbeddedKafkaCluster;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConsumerSemanticsTest
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
   public void testQueueEnsureMessageSentBeforeSubscribeIsConsumed() throws IOException, InterruptedException, JMSException {

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
   public void testTopicEnsureMessageSentBeforeSubscribeIsNotConsumed() throws IOException, InterruptedException, JMSException {

      ConnectionFactory connectionFactory = new KafkaConnectionFactory(embeddedKafkaCluster.bootstrapServers());

      String text = "testString";
      TextMessage result;
      try(Connection connection = connectionFactory.createConnection()){
         connection.start();
         try(Session session = connection.createSession()){
            Topic destination = session.createTopic(TOPIC_NAME);
            MessageProducer messageProducer = session.createProducer(destination);

            messageProducer.send(session.createTextMessage(text));

            messageProducer.close();

            MessageConsumer messageConsumer = session.createConsumer(destination);

            result = (TextMessage)messageConsumer.receive(1000);
         }
      }

      assertNull(result);
   }

   @Test
   public void testTopicEnsureMessageSentAfterSubscribeIsConsumed() throws IOException, InterruptedException, JMSException {

      ConnectionFactory connectionFactory = new KafkaConnectionFactory(embeddedKafkaCluster.bootstrapServers());

      String text = "testString";
      TextMessage result;
      try(Connection connection = connectionFactory.createConnection()){
         connection.start();
         try(Session session = connection.createSession()){
            Topic destination = session.createTopic(TOPIC_NAME);
            MessageConsumer messageConsumer = session.createConsumer(destination);
            messageConsumer.receive(100);
            MessageProducer messageProducer = session.createProducer(destination);

            messageProducer.send(session.createTextMessage(text));

            messageProducer.close();


            result = (TextMessage)messageConsumer.receive(1000);
         }
      }

      assertEquals(text, result == null ? null : result.getText());
   }


   @Test
   public void testTopicTwoConsumersEachGetMessage() throws IOException, InterruptedException, JMSException {

      ConnectionFactory connectionFactory = new KafkaConnectionFactory(embeddedKafkaCluster.bootstrapServers());

      String text = "testString";
      TextMessage result;
      TextMessage result2;

      try(Connection connection = connectionFactory.createConnection()){
         connection.start();
         try(Session session = connection.createSession()){
            Topic destination = session.createTopic(TOPIC_NAME);

            MessageProducer messageProducer = session.createProducer(destination);
            
            AtomicReference<TextMessage> messageAtomicReference = new AtomicReference<>();

            Thread thread = new Thread(() -> {
               try (Connection connection2 = connectionFactory.createConnection()) {
                  connection2.setClientID("1");
                  connection2.start();
                  try (Session session2 = connection2.createSession()) {
                     Topic destination2 = session2.createTopic(TOPIC_NAME);
                     MessageConsumer messageConsumer2 = session2.createConsumer(destination2);

                     for(int i = 0; i < 100; i++){
                        TextMessage message = (TextMessage) messageConsumer2.receive(1000);
                        if (message!=null){
                           messageAtomicReference.set(message);
                           break;
                        }
                     }
                  }
               } catch (JMSException jmse){

               }
            });
            thread.start();
            AtomicReference<TextMessage> messageAtomicReference2 = new AtomicReference<>();

            Thread thread2 = new Thread(() -> {
               try (Connection connection2 = connectionFactory.createConnection()) {
                  connection2.setClientID("2");
                  connection2.start();
                  try (Session session2 = connection2.createSession()) {
                     Topic destination2 = session2.createTopic(TOPIC_NAME);
                     MessageConsumer messageConsumer2 = session2.createConsumer(destination2);
                     for(int i = 0; i < 100; i++){
                        TextMessage message = (TextMessage) messageConsumer2.receive(1000);
                        if (message!=null){
                           messageAtomicReference2.set(message);
                           break;
                        }
                     }
                  }
               } catch (JMSException jmse){

               }
            });
            thread2.start();
            
            Thread.sleep(1000);


            messageProducer.send(session.createTextMessage(text));

            messageProducer.close();

            Thread.sleep(2000);
            result = messageAtomicReference.get();
            result2 = messageAtomicReference2.get();
         }
      }

      assertEquals(text, result == null ? null : result.getText());
      assertEquals(text, result2 == null ? null : result2.getText());

   }



   @Test
   public void testTopicTwoSharedDurableConsumersGetOnlyOneMessage() throws IOException, InterruptedException, JMSException {

      ConnectionFactory connectionFactory = new KafkaConnectionFactory(embeddedKafkaCluster.bootstrapServers());

      String text = "testString";
      TextMessage result;
      TextMessage result2;

      try(Connection connection = connectionFactory.createConnection()){
         connection.start();
         try(Session session = connection.createSession()){
            Topic destination = session.createTopic(TOPIC_NAME);

            MessageProducer messageProducer = session.createProducer(destination);



            AtomicReference<TextMessage> messageAtomicReference = new AtomicReference<>();

            Thread thread = new Thread(() -> {
               try (Connection connection2 = connectionFactory.createConnection()) {
                  connection2.start();
                  try (Session session2 = connection2.createSession()) {
                     Topic destination2 = session2.createTopic(TOPIC_NAME);
                     MessageConsumer messageConsumer2 = session2.createSharedDurableConsumer(destination2, "shared");

                     for(int i = 0; i < 100; i++){
                        TextMessage message = (TextMessage) messageConsumer2.receive(1000);
                        if (message!=null){
                           messageAtomicReference.set(message);
                           break;
                        }
                     }                  }
               } catch (JMSException jmse){

               }
            });
            thread.start();
            AtomicReference<TextMessage> messageAtomicReference2 = new AtomicReference<>();

            Thread thread2 = new Thread(() -> {
               try (Connection connection2 = connectionFactory.createConnection()) {
                  connection2.start();
                  try (Session session2 = connection2.createSession()) {
                     Topic destination2 = session2.createTopic(TOPIC_NAME);
                     MessageConsumer messageConsumer2 = session2.createSharedDurableConsumer(destination2, "shared");
                  
                     for(int i = 0; i < 100; i++){
                        TextMessage message = (TextMessage) messageConsumer2.receive(1000);
                        if (message!=null){
                           messageAtomicReference2.set(message);
                           break;
                        }
                     }
                  }
               } catch (JMSException jmse){

               }
            });
            thread2.start();

            Thread.sleep(1000);

            messageProducer.send(session.createTextMessage(text));

            messageProducer.close();

            Thread.sleep(2000);
            result = messageAtomicReference.get();
            result2 = messageAtomicReference2.get();
         }
      }

      assertEquals(text, result == null ? result2 == null ? null : result2.getText() : result.getText());
      assertTrue(result == null ? result2 != null : true);

   }


   @Test
   public void testQueueTwoConsumersGetOnlyOneMessage() throws IOException, InterruptedException, JMSException {

      ConnectionFactory connectionFactory = new KafkaConnectionFactory(embeddedKafkaCluster.bootstrapServers());

      String text = "testString";
      TextMessage result;
      TextMessage result2;

      try(Connection connection = connectionFactory.createConnection()){
         connection.start();
         try(Session session = connection.createSession()){
            Queue destination = session.createQueue(QUEUE_NAME);
            
            MessageProducer messageProducer = session.createProducer(destination);



            AtomicReference<TextMessage> messageAtomicReference = new AtomicReference<>();

            Thread thread = new Thread(() -> {
               try (Connection connection2 = connectionFactory.createConnection()) {
                  connection2.start();
                  try (Session session2 = connection2.createSession()) {
                     Queue destination2 = session2.createQueue(QUEUE_NAME);
                     MessageConsumer messageConsumer2 = session2.createConsumer(destination2);

                     messageAtomicReference.set( (TextMessage) messageConsumer2.receive(1000));
                  }
               } catch (JMSException jmse){

               }
            });
            thread.start();
            AtomicReference<TextMessage> messageAtomicReference2 = new AtomicReference<>();

            Thread thread2 = new Thread(() -> {
               try (Connection connection2 = connectionFactory.createConnection()) {
                  connection2.start();
                  try (Session session2 = connection2.createSession()) {
                     Queue destination2 = session2.createQueue(QUEUE_NAME);
                     MessageConsumer messageConsumer2 = session2.createConsumer(destination2);

                     messageAtomicReference2.set( (TextMessage) messageConsumer2.receive(1000));
                  }
               } catch (JMSException jmse){

               }
            });
            thread2.start();


            messageProducer.send(session.createTextMessage(text));

            messageProducer.close();
            
            Thread.sleep(2000);
            result = messageAtomicReference.get();
            result2 = messageAtomicReference2.get();
         }
      }

      assertEquals(text, result == null ? result2 == null ? null : result2.getText() : result.getText());
      assertTrue(result == null ? result2 != null : true);

   }

   @Test
   public void testTopicNonDurableDoesntReceiveMessagesWhilstNotConnected() throws IOException, InterruptedException, JMSException {

      ConnectionFactory connectionFactory = new KafkaConnectionFactory(embeddedKafkaCluster.bootstrapServers());

      String text = "testString";
      TextMessage result;
      TextMessage result2;

      try(Connection connection = connectionFactory.createConnection()){
         connection.start();
         try(Session session = connection.createSession()){
            Queue destination = session.createQueue(QUEUE_NAME);
            MessageConsumer messageConsumer = session.createConsumer(destination);

            messageConsumer.receive(100);

            MessageProducer messageProducer = session.createProducer(destination);

            messageProducer.send(session.createTextMessage(text));

            messageProducer.close();

            result = (TextMessage)messageConsumer.receive(1000);

            AtomicReference<TextMessage> messageAtomicReference = new AtomicReference<>();

            Thread thread = new Thread(() -> {
               try (Connection connection2 = connectionFactory.createConnection()) {
                  connection.start();
                  try (Session session2 = connection2.createSession()) {
                     Queue destination2 = session2.createQueue(QUEUE_NAME);
                     MessageConsumer messageConsumer2 = session2.createConsumer(destination2);

                     messageAtomicReference.set( (TextMessage) messageConsumer2.receive(1000));
                  }
               } catch (JMSException jmse){
                  
               }
            });
            
            thread.start();
            Thread.sleep(2000);
            result2 = messageAtomicReference.get();
            
            
         }
      }

      assertEquals(text, result == null ? null : result.getText());
      assertNull(result2);
      

   }
  
}
