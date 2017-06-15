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

package io.github.jmscomponents.kafka.jms.util;

import javax.jms.InvalidDestinationException;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import io.github.jmscomponents.kafka.jms.Destination;
import io.github.jmscomponents.kafka.jms.Queue;
import io.github.jmscomponents.kafka.jms.Topic;
import io.github.jmscomponents.kafka.jms.exception.IllegalArgumentException;

public class Preconditions
{
   private Preconditions() {
   }
   
   public static Destination ensureKafkaDestination(javax.jms.Destination destination) throws JMSException {
      if(null == destination) {
         return null;
      } else if(destination instanceof Destination) {
         return (Destination)destination;
      } else if(destination instanceof javax.jms.Queue) {
         return new Queue(((javax.jms.Queue)destination).getQueueName());
      } else {
         return new Topic(((javax.jms.Topic)destination).getTopicName());
      }
   }

   public static Destination checkDestination(javax.jms.Destination destination) throws JMSException {
      Destination kafkaDestination = ensureKafkaDestination(destination);
      if(null == kafkaDestination) {
         throw new InvalidDestinationException("destination cannot be null");
      }
      return kafkaDestination;
   }

   public static Queue checkQueueDestination(javax.jms.Destination destination) throws JMSException {
      if(null == destination) {
         throw new InvalidDestinationException("destination cannot be null");
      } else if(destination instanceof Queue) {
         return (Queue)destination;
      } else if(destination instanceof javax.jms.Queue) {
         return new Queue(((javax.jms.Queue)destination).getQueueName());
      } else {
         return new Queue(((javax.jms.Topic)destination).getTopicName());
      }
   }

   public static Message checkMessage(Message message) throws JMSException {
      if(null == message) {
         throw new IllegalArgumentException("message cannot be null");
      } else {
         return message;
      }
   }

   public static String checkMessageSelector(String messageSelector) throws JMSException {
      if(!(messageSelector == null || messageSelector.isEmpty())){
         throw new IllegalArgumentException("message selectors are not supported");
      }
      return messageSelector;
   }

   public static int checkAcknowledgeMode(int acknowledgeMode) throws JMSException {
      switch (acknowledgeMode){
         case Session.AUTO_ACKNOWLEDGE:
            return acknowledgeMode;
         default:
            throw new IllegalArgumentException("acknowledgeMode " +  acknowledgeMode + " is not supported");
      }
   }

   public static boolean checkTransacted(boolean transacted) throws JMSException {
      if(transacted){
         throw new IllegalArgumentException("transactions are not supported");
      }
      return transacted;
   }
   
   public static <T> T checkNotNull(T reference, String errorMessage) throws JMSException {
      if(reference == null) {
         throw new IllegalArgumentException(errorMessage);
      } else {
         return reference;
      }
   }
}
