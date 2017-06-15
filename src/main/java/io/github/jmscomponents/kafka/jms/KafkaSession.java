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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.jms.BytesMessage;
import javax.jms.IllegalStateException;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.QueueBrowser;
import javax.jms.StreamMessage;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.jms.TopicSubscriber;

import io.github.jmscomponents.kafka.jms.common.ConnectionAwareSession;
import io.github.jmscomponents.kafka.jms.consumer.ConsumerFactory;
import io.github.jmscomponents.kafka.jms.consumer.ConsumerFactoryImpl;
import io.github.jmscomponents.kafka.jms.producer.ProducerFactory;
import io.github.jmscomponents.kafka.jms.producer.ProducerFactoryImpl;
import io.github.jmscomponents.kafka.jms.util.Preconditions;
import io.github.jmscomponents.kafka.jms.util.Unsupported;
import org.apache.qpid.jms.provider.amqp.message.KafkaAmqpJmsMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaSession implements ConnectionAwareSession
{
   static final Logger log = LoggerFactory.getLogger(KafkaSession.class);
   private final boolean transacted;
   private final int acknowledgeMode;
   private final KafkaConnection connection;
   private MessageFactory jmsMessageFactory = new KafkaAmqpJmsMessageFactory();
   private ConsumerFactory consumerFactory;
   private ProducerFactory producerFactory;
   private List<KafkaMessageConsumer> consumers = new ArrayList<>();
   private List<KafkaMessageProducer> producers = new ArrayList<>();
   private volatile boolean closed = false;
   
   
   public KafkaSession(KafkaConnection connection, boolean transacted, int acknowledgeMode) throws JMSException {
      Preconditions.checkTransacted(transacted);
      Preconditions.checkAcknowledgeMode(acknowledgeMode);
      
      this.transacted = transacted;
      this.acknowledgeMode = acknowledgeMode;
      
      this.connection = connection;
      this.consumerFactory = new ConsumerFactoryImpl(this);
      this.producerFactory = new ProducerFactoryImpl(this);
   }

   public BytesMessage createBytesMessage() throws JMSException {
      return jmsMessageFactory.createBytesMessage();
   }

   public MapMessage createMapMessage() throws JMSException {
      return jmsMessageFactory.createMapMessage();
   }

   public Message createMessage() throws JMSException {
      return jmsMessageFactory.createMessage();
   }

   public ObjectMessage createObjectMessage() throws JMSException {
      return jmsMessageFactory.createObjectMessage();
   }

   public ObjectMessage createObjectMessage(Serializable serializable) throws JMSException {
      return jmsMessageFactory.createObjectMessage(serializable);
   }

   public StreamMessage createStreamMessage() throws JMSException {
      return jmsMessageFactory.createStreamMessage();
   }

   public TextMessage createTextMessage() throws JMSException {
      return jmsMessageFactory.createTextMessage();
   }

   public TextMessage createTextMessage(String payload) throws JMSException {
      return jmsMessageFactory.createTextMessage(payload);
   }

   public boolean getTransacted() throws JMSException {
      return transacted;
   }

   public int getAcknowledgeMode() throws JMSException {
      return acknowledgeMode;
   }

   public void commit() throws JMSException {
      if (!getTransacted()){
         throw new IllegalStateException("session is not transacted");
      }
   }

   public void rollback() throws JMSException {
      if (!getTransacted()){
         throw new IllegalStateException("session is not transacted");
      }
   }

   public void close() throws JMSException {
      closed = true;
      log.debug("closing {} consumer(s) for {}", consumers.size(), this);
      for(MessageConsumer consumer : consumers){
         consumer.close();
      }
      log.debug("closing {} producer(s) for {}", producers.size(), this);
      for(MessageProducer producer : producers){
         producer.close();
      }
      
      connection.removeSession(this);
   }

   public void recover() throws JMSException {
      if (!getTransacted()){
         throw new IllegalStateException("session is not transacted");
      }
   }

   @Unsupported("session.messageListener is not supported")
   public MessageListener getMessageListener() throws JMSException {
      return null;
   }

   @Unsupported("session.messageListener is not supported")
   public void setMessageListener(MessageListener messageListener) throws JMSException {
   }

   public void run() {
      consumers.forEach(KafkaMessageConsumer::run);
   }

   public MessageProducer createProducer(javax.jms.Destination destination) throws JMSException {
      KafkaMessageProducer messageProducer = new KafkaMessageProducer(this, destination);
      this.producers.add(messageProducer);
      return messageProducer;
   }

   public MessageConsumer createConsumer(javax.jms.Destination destination) throws JMSException {
      return this.createConsumer(destination, (String)null, false);
   }

   @Unsupported("messageSelector is not supported")
   public MessageConsumer createConsumer(javax.jms.Destination destination, String messageSelector) throws JMSException {
      return this.createConsumer(destination, messageSelector, false);
   }

   @Unsupported("messageSelector is not supported")
   public MessageConsumer createConsumer(javax.jms.Destination destination, String messageSelector, boolean noLocal) throws JMSException {
      KafkaMessageConsumer messageConsumer = new KafkaMessageConsumer(this, destination, messageSelector, null, false);
      this.consumers.add(messageConsumer);
      return messageConsumer;
   }

   public javax.jms.Queue createQueue(String queueName) throws JMSException {
      return new Queue(queueName);
   }

   public javax.jms.Topic createTopic(String topicName) throws JMSException {
      return new Topic(topicName);
   }

   public TopicSubscriber createDurableSubscriber(javax.jms.Topic topic, String subscriptionName) throws JMSException {
      return createDurableSubscriber(topic, subscriptionName, null, false);
   }

   public TopicSubscriber createDurableSubscriber(javax.jms.Topic topic, String subscriptionName, String messageSelector, boolean noLocal) throws JMSException {
      KafkaTopicSubscriber topicSubscriber = new KafkaTopicSubscriber(this, topic, messageSelector, subscriptionName, false);
      this.consumers.add(topicSubscriber);
      return topicSubscriber;
   }
   

   public QueueBrowser createBrowser(javax.jms.Queue queue) throws JMSException {
      return this.createBrowser(queue, (String)null);
   }

   @Unsupported("messageSelector is not supported")
   public QueueBrowser createBrowser(javax.jms.Queue queue, String messageSelector) throws JMSException {
      KafkaQueueBrowser queueBrowser = new KafkaQueueBrowser(this, queue, messageSelector);
      return queueBrowser;
      
   }
   
   long pollTimeoutMs(){
      return Long.parseLong(connection.getConfig().getProperty("poll.timeout.ms", "1000"));
   }

   @Unsupported("createTemporaryQueue is not supported")
   public TemporaryQueue createTemporaryQueue() throws JMSException {
      throw new UnsupportedOperationException();
   }

   @Unsupported("createTemporaryTopic is not supported")
   public TemporaryTopic createTemporaryTopic() throws JMSException {
      throw new UnsupportedOperationException();
   }

   @Unsupported("unsubscribe is not supported")
   public void unsubscribe(String s) throws JMSException {
      throw new UnsupportedOperationException();
   }

   @Override
   @Unsupported("createSharedConsumer is not supported")
   public MessageConsumer createSharedConsumer(javax.jms.Topic topic, String subscriptionName) throws JMSException {
      return createSharedDurableConsumer(topic, subscriptionName);
   }

   @Override
   @Unsupported("createSharedConsumer is not supported")
   public MessageConsumer createSharedConsumer(javax.jms.Topic topic, String subscriptionName, String messageSelector) throws JMSException {
      return createSharedDurableConsumer(topic, subscriptionName, messageSelector);
   }

   @Override
   @Unsupported("createDurableConsumer is not supported")
   public MessageConsumer createDurableConsumer(javax.jms.Topic topic, String subscriptionName) throws JMSException {
      return createDurableConsumer(topic, subscriptionName, null, false);
   }

   @Override
   @Unsupported("createDurableConsumer is not supported")
   public MessageConsumer createDurableConsumer(javax.jms.Topic topic, String subscriptionName, String messageSelector, boolean noLocal) throws JMSException {
      return createDurableSubscriber(topic, subscriptionName, messageSelector, noLocal);
   }

   @Override
   public MessageConsumer createSharedDurableConsumer(javax.jms.Topic topic, String subscriptionName) throws JMSException {
      return createSharedDurableConsumer(topic, subscriptionName, null);
   }

   @Override
   public MessageConsumer createSharedDurableConsumer(javax.jms.Topic topic, String subscriptionName, String messageSelector) throws JMSException {
      KafkaMessageConsumer messageConsumer = new KafkaMessageConsumer(this, topic, messageSelector, subscriptionName, true);
      this.consumers.add(messageConsumer);
      return messageConsumer;
   }

   @Override
   public boolean isClosed() {
      return closed;
   }

   @Override
   public boolean isStarted(){
      return connection.isStarted();
   }

   @Override
   public Properties getConfig(){
      return connection.getConfig();
   }
   
   ConsumerFactory getConsumerFactory() {
      return consumerFactory;
   }

   ProducerFactory getProducerFactory() {
      return producerFactory;
   }

   
   public static final String JMSXKEY_ID = "JMSXKeyId";
   public static final String JMSXGROUP_ID = "JMSXGroupID";

   public static String toJMSMessageID(String topic, int partition, long offset) {
      return String.format("topic=%s:partition=%d:offset=%d", new Object[]{topic, Integer.valueOf(partition), Long.valueOf(offset)});
   }
}
