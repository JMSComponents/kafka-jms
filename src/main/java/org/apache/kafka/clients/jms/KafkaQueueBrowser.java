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

package org.apache.kafka.clients.jms;

import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import javax.jms.JMSException;
import javax.jms.QueueBrowser;

import org.apache.kafka.clients.jms.consumer.ConsumerMessageQueue;
import org.apache.kafka.clients.jms.exception.JmsExceptionSupport;
import org.apache.kafka.clients.jms.util.Preconditions;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;

class KafkaQueueBrowser implements QueueBrowser {
   private final Queue destination;
   private final String messageSelector;
   private final ConsumerMessageQueue messageQueue;

   KafkaQueueBrowser(KafkaSession kafkaSession, javax.jms.Queue queue, String messageSelector) throws JMSException {
      this.messageSelector = Preconditions.checkMessageSelector(messageSelector);
      this.destination = Preconditions.checkQueueDestination(queue);
      
      Consumer consumer = kafkaSession.getConsumerFactory().createSubscriber();
      List<PartitionInfo> partitionInfos = consumer.partitionsFor(this.destination.getName());
      List<TopicPartition> topicPartitions = partitionInfos.stream().map(pi -> new TopicPartition(pi.topic(), pi.partition())).collect(
         Collectors.toList());
      consumer.assign(topicPartitions);
      this.messageQueue = new ConsumerMessageQueue(consumer, kafkaSession.pollTimeoutMs());
   }

   public javax.jms.Queue getQueue() throws JMSException {
      return this.destination;
   }

   public String getMessageSelector() throws JMSException {
      return this.messageSelector;
   }

   public Enumeration getEnumeration() throws JMSException {
      return messageQueue.getEnumeration();
   }

   public void close() throws JMSException {
      try {
         this.messageQueue.close();
      } catch (java.io.IOException ioe) {
         throw JmsExceptionSupport.toJMSException(ioe);
      }
   }
}
