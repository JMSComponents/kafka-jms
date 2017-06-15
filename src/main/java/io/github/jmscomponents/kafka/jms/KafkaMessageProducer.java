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
package io.github.jmscomponents.kafka.jms;

import static io.github.jmscomponents.kafka.jms.KafkaSession.JMSXKEY_ID;
import static io.github.jmscomponents.kafka.jms.KafkaSession.toJMSMessageID;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.jms.CompletionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;

import io.github.jmscomponents.kafka.jms.exception.JmsExceptionSupport;
import io.github.jmscomponents.kafka.jms.util.Preconditions;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaMessageProducer implements MessageProducer {
   private static final Logger log = LoggerFactory.getLogger(KafkaMessageProducer.class);
   private boolean disableMessageID;
   private boolean disableMessageTimestamp;
   private int deliveryMode = 2;
   private long deliveryDelay = 0;
   private int priority = 4;
   private long timeToLive = 0L;
   private Destination destination;
   private final Producer<String, Message> producer;

   KafkaMessageProducer(KafkaSession session, javax.jms.Destination destination) throws JMSException {
      this.destination = Preconditions.ensureKafkaDestination(destination);

      this.producer = session.getProducerFactory().createProducer();
   }

   public boolean getDisableMessageID() throws JMSException {
      return this.disableMessageID;
   }

   public void setDisableMessageID(boolean b) throws JMSException {
      this.disableMessageID = b;
   }

   public boolean getDisableMessageTimestamp() throws JMSException {
      return this.disableMessageTimestamp;
   }

   public void setDisableMessageTimestamp(boolean b) throws JMSException {
      this.disableMessageTimestamp = b;
   }

   public int getDeliveryMode() throws JMSException {
      return this.deliveryMode;
   }

   public void setDeliveryMode(int i) throws JMSException {
      this.deliveryMode = i;
   }

   public int getPriority() throws JMSException {
      return this.priority;
   }

   public void setPriority(int i) throws JMSException {
      this.priority = i;
   }

   public long getTimeToLive() throws JMSException {
      return this.timeToLive;
   }

   public void setTimeToLive(long timeToLive) throws JMSException {
      if(timeToLive < 0L) {
         throw new JMSException("timeToLive must be greater than 0.");
      } else {
         this.timeToLive = timeToLive;
      }
   }

   public javax.jms.Destination getDestination() throws JMSException {
      return this.destination;
   }

   public void close() throws JMSException {
      this.producer.close();
   }

   public void send(Message message) throws JMSException {
      send(this.destination, message, this.deliveryMode, this.priority, this.timeToLive);
   }

   public void send(Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
      send(this.destination, message, deliveryMode, priority, timeToLive);
   }

   public void send(javax.jms.Destination destination, Message message) throws JMSException {
      send(destination, message, this.deliveryMode, this.priority, this.timeToLive);
   }

   @Override
   public void send(Message message, CompletionListener completionListener) throws JMSException {
      send(destination, message, completionListener);
   }

   public void send(javax.jms.Destination destination, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
      send(destination, message, deliveryMode, priority, timeToLive, null);
   }

   @Override
   public void send(javax.jms.Destination destination, Message message, CompletionListener completionListener) throws JMSException {
      send(destination, message, deliveryMode, priority, timeToLive, completionListener);
   }

   @Override
   public void send(Message message, int deliveryMode, int priority, long timeToLive, CompletionListener completionListener) throws JMSException {
      send(destination, message, deliveryMode, priority, timeToLive, completionListener);
   }

   @Override
   public void send(javax.jms.Destination destination, Message message, int deliveryMode, int priority, long timeToLive, CompletionListener completionListener) throws JMSException {
      Destination kafkaDestination = Preconditions.checkDestination(destination);
      Message jmsMessage = Preconditions.checkMessage(message);
      if(timeToLive < 0L) {
         throw new JMSException("timeToLive must be greater than or equal to 0.");
      } else {
         long time = System.currentTimeMillis();
         if(jmsMessage.getJMSTimestamp() <= 0L) {
            jmsMessage.setJMSTimestamp(time);
         }
         jmsMessage.setJMSDeliveryMode(deliveryMode);
         jmsMessage.setJMSDeliveryTime(message.getJMSTimestamp() + deliveryDelay);
         jmsMessage.setJMSPriority(priority);
         if(timeToLive == 0L) {
            jmsMessage.setJMSExpiration(0L);
         } else {
            jmsMessage.setJMSExpiration(message.getJMSTimestamp() + timeToLive);
         }

         jmsMessage.setJMSDestination(kafkaDestination);
         String key = jmsMessage.propertyExists(JMSXKEY_ID) ? jmsMessage.getStringProperty(JMSXKEY_ID) : null;
         ProducerRecord producerRecord = new ProducerRecord(kafkaDestination.getName(), null, jmsMessage.getJMSTimestamp(), key, jmsMessage);
         
         if (completionListener == null){
            Future<RecordMetadata> result = this.producer.send(producerRecord);
            try {
               RecordMetadata recordMetadata = result.get();
               TopicPartition topicPartition = new TopicPartition(recordMetadata.topic(), recordMetadata.partition());

               jmsMessage.setJMSMessageID(toJMSMessageID(topicPartition.topic(), topicPartition.partition(), recordMetadata.offset()));
               if(log.isDebugEnabled()) {
                  log.debug("Message accepted to partition {} offset {}", recordMetadata.partition(), recordMetadata.offset());
               }
            } catch (ExecutionException | InterruptedException e) {
               if(log.isErrorEnabled()) {
                  log.error("Exception thrown while sending message.", e);
               }
               throw JmsExceptionSupport.toJMSException(e);
            }
         } else {
            producer.send(producerRecord, (recordMetadata, e) ->
            {
               if (e == null){
                  TopicPartition topicPartition = new TopicPartition(recordMetadata.topic(), recordMetadata.partition());

                  try {
                     jmsMessage.setJMSMessageID(toJMSMessageID(topicPartition.topic(), topicPartition.partition(), recordMetadata.offset()));
                  } catch (JMSException e1) {
                  }
                  if(log.isDebugEnabled()) {
                     log.debug("Message accepted to partition {} offset {}", recordMetadata.partition(), recordMetadata.offset());
                  }
                  completionListener.onCompletion(message);
               } else {
                  if (e instanceof ExecutionException || e instanceof InterruptedException){
                     if(log.isErrorEnabled()) {
                        log.error("Exception thrown while sending message.", e);
                     }
                     e = JmsExceptionSupport.toJMSException(e);
                  }
                  completionListener.onException(message, e);
               }
            });
         }
      }
   }

   @Override
   public long getDeliveryDelay() throws JMSException {
      return deliveryDelay;
   }

   @Override
   public void setDeliveryDelay(long deliveryDelay) throws JMSException {
      this.deliveryDelay = deliveryDelay;
   }
   

}
