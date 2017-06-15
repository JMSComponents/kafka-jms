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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.jms.*;

import io.github.jmscomponents.kafka.jms.common.ConnectionAwareSession;
import io.github.jmscomponents.kafka.jms.consumer.ConsumerMessageQueue;
import io.github.jmscomponents.kafka.jms.consumer.CommitOnRevokedConsumerRebalanceListener;
import io.github.jmscomponents.kafka.jms.exception.IllegalArgumentException;
import io.github.jmscomponents.kafka.jms.exception.JmsExceptionSupport;
import io.github.jmscomponents.kafka.jms.util.Preconditions;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class KafkaMessageConsumer implements MessageConsumer, Runnable {
   private static final Logger log = LoggerFactory.getLogger(KafkaMessageConsumer.class);
   private final Consumer<String, Message> consumer;
   private final ConsumerMessageQueue messageQueue;
   private final io.github.jmscomponents.kafka.jms.Destination destination;
   private final String messageSelector;
   private final KafkaSession session;
   private MessageListenerRunnable messageListenerRunnable;
   
   KafkaMessageConsumer(KafkaSession kafkaSession, javax.jms.Destination destination, String messageSelector, String subscriptionName, boolean shared) throws JMSException
   {
      this.destination = Preconditions.checkDestination(destination);
      this.messageSelector = Preconditions.checkMessageSelector(messageSelector);
      this.session = kafkaSession;

      boolean isAssign;
      if (destination instanceof Queue){
         log.debug("session.getConsumerFactory().createReceiver");
         consumer = session.getConsumerFactory().createReceiver();
         isAssign = false;
      } else if (subscriptionName == null && !shared) {
         log.debug("session.getConsumerFactory().createSubscriber");
         consumer = session.getConsumerFactory().createSubscriber();
         isAssign = true;
      } else if (shared) {
         log.debug("session.getConsumerFactory().createSharedDurableSubscriber");
         consumer = session.getConsumerFactory().createSharedDurableSubscriber(subscriptionName);
         isAssign = false;
      } else {
         log.debug("session.getConsumerFactory().createDurableSubscriber");
         consumer = session.getConsumerFactory().createDurableSubscriber(subscriptionName);
         isAssign = false;
      }
      
      messageQueue = new ConsumerMessageQueue(consumer, kafkaSession.pollTimeoutMs());
      if (isAssign) {
         List<PartitionInfo> partitionInfos = consumer.partitionsFor(this.destination.getName());
         List<TopicPartition> topicPartitions = partitionInfos.stream().map(pi -> new TopicPartition(pi.topic(), pi.partition())).collect(Collectors.toList());
         consumer.assign(topicPartitions);
      } else {
         consumer.subscribe(Arrays.asList(new String[]{this.destination.getName()}), new CommitOnRevokedConsumerRebalanceListener(this.consumer));
      }
   }

   public String getMessageSelector() throws JMSException {
      return null;
   }

   public MessageListener getMessageListener() throws JMSException {
      return messageListenerRunnable == null ? null : messageListenerRunnable.getMessageListener();
   }

   public void setMessageListener(MessageListener messageListener) throws JMSException {
      if(null != this.messageListenerRunnable) {
         messageListenerRunnable.stop();
      }

      this.messageListenerRunnable = new MessageListenerRunnable(this, messageListener);
      if (session.isStarted()){
         messageListenerRunnable.start();
      }
   }

   public Message receive() throws JMSException {
      Message result = null;
      while(null == result) {
         result = this.receive(1000L);
      }
      return result;
   }

   public Message receive(long l) throws JMSException {
      if(l <= 0L) {
         throw new IllegalArgumentException("timeout must be greater than 0.");
      } else {
         try {
            return this.messageQueue.poll(l, TimeUnit.MILLISECONDS);
         } catch (InterruptedException e) {
            JmsExceptionSupport.toJMSException(e);
         }
      }
      return null;
   }

   public Message receiveNoWait() throws JMSException {
      return null == this.messageQueue.peek()?null:(Message)this.messageQueue.poll();
   }

   public ConnectionAwareSession getSession() {
      return session;
   }
   
   public void run() {
      if (messageListenerRunnable != null){
         messageListenerRunnable.start();
      }
   }
   
   public void close() throws JMSException {
      try {
         if (messageListenerRunnable != null){
            messageListenerRunnable.stop();
         }
         this.messageQueue.close();
      } catch (IOException ioe) {
         JmsExceptionSupport.toJMSException(ioe);
      }

   }
}
