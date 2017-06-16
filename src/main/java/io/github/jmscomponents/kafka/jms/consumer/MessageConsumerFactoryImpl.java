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
package io.github.jmscomponents.kafka.jms.consumer;

import javax.jms.IllegalStateException;
import javax.jms.JMSException;
import javax.jms.Message;
import java.util.Properties;

import io.github.jmscomponents.kafka.jms.common.ConnectionAwareSession;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;

public class MessageConsumerFactoryImpl implements MessageConsumerFactory
{
   private final ConnectionAwareSession session;
   private final ConsumerFactory consumerFactory;
   private static final String QUEUE_GROUP_ID = "queue";

   public MessageConsumerFactoryImpl(ConsumerFactory consumerFactory, ConnectionAwareSession session) {
      this.session = session;
      this.consumerFactory = consumerFactory;
   }

   public Consumer<String, Message> createDurableSubscriber(String name) throws JMSException {
      return create(session.getConfig().getProperty(CommonClientConfigs.CLIENT_ID_CONFIG) + "-" + name, false);
   }

   public Consumer<String, Message> createSubscriber() throws JMSException {
      return create(null, false);
   }

   public Consumer<String, Message> createSharedDurableSubscriber(String subscriptionName) throws JMSException {
      return create(subscriptionName, false);
   }

   public Consumer<String, Message> createReceiver() throws JMSException {
      return create(QUEUE_GROUP_ID, true);
   }
   

   public Consumer<String, Message> create(String groupId, boolean queue) throws JMSException
   {

      if (session.isClosed()){
         throw new IllegalStateException("session is closed");
      }
      Properties properties = new Properties();
      
      session.getConfig().forEach((k,v) -> {
         if(ConsumerConfig.configNames().contains(k)) properties.put(k, v);
      });

      if (groupId != null) {
         properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
      }
      properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.FALSE.toString());
      properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, queue ? "earliest" : "latest");

      return consumerFactory.createConsumer(properties);
   }
}
