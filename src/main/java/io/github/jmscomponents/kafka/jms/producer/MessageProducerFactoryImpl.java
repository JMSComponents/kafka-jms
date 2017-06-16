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
package io.github.jmscomponents.kafka.jms.producer;

import javax.jms.IllegalStateException;
import javax.jms.JMSException;
import javax.jms.Message;
import java.util.Properties;

import io.github.jmscomponents.kafka.jms.common.ConnectionAwareSession;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;

public class MessageProducerFactoryImpl implements MessageProducerFactory {
   
   private final ConnectionAwareSession session;
   private final ProducerFactory producerFactory;

   public MessageProducerFactoryImpl(ProducerFactory producerFactory, ConnectionAwareSession session) {
      this.session = session;
      this.producerFactory = producerFactory;
   }

   public Producer<String, Message> createProducer() throws JMSException
   {
      if (session.isClosed()){
         throw new IllegalStateException("session is closed");
      }
      Properties properties = new Properties();

      //Default to using Message Groups for partitioning, can be overridden by properties set in session/connection.
      properties.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, MessageGroupPartitioner.class.getName());

      session.getConfig().forEach((k,v) -> {
         if(ProducerConfig.configNames().contains(k)) properties.put(k, v);
      });
      
      return producerFactory.createProducer(properties);
   }
}
