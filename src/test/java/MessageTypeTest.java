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

import javax.jms.BytesMessage;
import javax.jms.Connection;
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

import org.junit.Test;

public class MessageTypeTest extends BaseKafkaJMSIT
{
   
   @Test
   public void testTextMessage() throws IOException, InterruptedException, JMSException {
      
      String text = "testStringtestString";
      TextMessage result;
      try(Connection connection = connectionFactory.createConnection()){
         connection.start();
         try(Session session = connection.createSession()){
            Queue destination = session.createQueue(QUEUE_NAME);
            MessageProducer messageProducer = session.createProducer(destination);

            messageProducer.send(session.createTextMessage(text));
            
            messageProducer.close();
            
            MessageConsumer messageConsumer = session.createConsumer(destination);
            
            result = (TextMessage)messageConsumer.receive(10000);
         }
      }

      assertEquals(text, result == null ? null : result.getText());
   }

   @Test
   public void testBytesMessage() throws IOException, InterruptedException, JMSException {
      
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

            result = (BytesMessage)messageConsumer.receive(10000);
         }
      }
      assertEquals(5, result.readInt());

      //assertEquals(text, result.readUTF());
   }

   @Test
   public void testMapMessage() throws IOException, InterruptedException, JMSException {
      
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

            result = (MapMessage)messageConsumer.receive(10000);
         }
      }

      assertEquals(value, result == null ? null : result.getString(key));
   }

   @Test
   public void testObjectMessage() throws IOException, InterruptedException, JMSException {
      
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

            result = (ObjectMessage)messageConsumer.receive(10000);
         }
      }

      assertEquals(value, result == null ? null : result.getObject());
   }


   @Test
   public void testStreamMessage() throws IOException, InterruptedException, JMSException {
      
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

            result = (StreamMessage)messageConsumer.receive(10000);
         }
      }

      assertEquals(value, result == null ? null : result.readString());
   }
}
