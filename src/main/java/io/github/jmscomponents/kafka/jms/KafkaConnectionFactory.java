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

import java.util.Properties;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;
import javax.jms.JMSSecurityException;
import javax.jms.JMSSecurityRuntimeException;
import javax.jms.Message;

import io.github.jmscomponents.kafka.jms.consumer.ConsumerFactory;
import io.github.jmscomponents.kafka.jms.consumer.DefaultConsumerFactory;
import io.github.jmscomponents.kafka.jms.exception.JmsExceptionSupport;
import io.github.jmscomponents.kafka.jms.message.DefaultMessageFactory;
import io.github.jmscomponents.kafka.jms.message.MessageFactory;
import io.github.jmscomponents.kafka.jms.producer.DefaultProducerFactory;
import io.github.jmscomponents.kafka.jms.producer.ProducerFactory;
import io.github.jmscomponents.kafka.jms.security.PlainLogin;
import org.apache.kafka.common.serialization.Serde;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaConnectionFactory implements ConnectionFactory {
   private static final Logger log = LoggerFactory.getLogger(KafkaConnectionFactory.class);
   private final Properties properties = new Properties();
   private final String bootstrapServers;
   private final ProducerFactory producerFactory;
   private final ConsumerFactory consumerFactory;
   private final MessageFactory messageFactory;
   private String username;
   private String password;
   
   public KafkaConnectionFactory(String bootstrapServers) {
      this(bootstrapServers, new DefaultProducerFactory(), new DefaultConsumerFactory());
   }

   public KafkaConnectionFactory(String bootstrapServers, Serde<Message> messageSerde) {
      this(bootstrapServers, new DefaultProducerFactory(messageSerde.serializer()), new DefaultConsumerFactory(messageSerde.deserializer()));
   }

   public KafkaConnectionFactory(String bootstrapServers, ProducerFactory producerFactory, ConsumerFactory consumerFactory) {
      this(bootstrapServers, producerFactory, consumerFactory, new DefaultMessageFactory());
   }
   
   public KafkaConnectionFactory(String bootstrapServers, ProducerFactory producerFactory, ConsumerFactory consumerFactory, MessageFactory messageFactory) {
      this.bootstrapServers = bootstrapServers;
      this.producerFactory = producerFactory;
      this.consumerFactory = consumerFactory;
      this.messageFactory = messageFactory;
   }

   
   public Connection createConnection() throws JMSException {
      return createConnection(username, password);
   }

   public Connection createConnection(String username, String password) throws JMSException {
      return this.createConnectionInternal(username, password);
   }

   @Override
   public JMSContext createContext() {
      return createContext(username, password);
   }

   @Override
   public JMSContext createContext(final int sessionMode) {
      return createContext(username, password, sessionMode);
   }

   @Override
   public JMSContext createContext(final String userName, final String password) {
      return createContext(userName, password, JMSContext.AUTO_ACKNOWLEDGE);
   }

   @Override
   public JMSContext createContext(String userName, String password, int sessionMode) {
      validateSessionMode(sessionMode);
      try {
         KafkaConnection kafkaConnection = createConnectionInternal(userName, password);
         return kafkaConnection.createContext(sessionMode);
      } catch (JMSSecurityException e) {
         throw new JMSSecurityRuntimeException(e.getMessage(), e.getErrorCode(), e);
      } catch (JMSException e) {
         throw JmsExceptionSupport.convertToRuntimeException(e);
      }
   }

   public void setProperties(Properties properties){
      this.properties.putAll(properties);
   }
   
   public void setProperty(String key, String value){
      this.properties.setProperty(key, value);
   }

   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   private KafkaConnection createConnectionInternal(String username, String password) throws JMSException {
      if (username != null && password != null) {
         javax.security.auth.login.Configuration.setConfiguration(PlainLogin.createJaasConfig(username, password));
      }
      return new KafkaConnection(bootstrapServers, this.properties, producerFactory, consumerFactory, messageFactory);
   }

   static void validateSessionMode(int mode) {
      switch (mode) {
         case JMSContext.AUTO_ACKNOWLEDGE:
         case JMSContext.CLIENT_ACKNOWLEDGE:
         case JMSContext.DUPS_OK_ACKNOWLEDGE:
         case JMSContext.SESSION_TRANSACTED: {
            return;
         }
         default:
            throw new JMSRuntimeException("Invalid Session Mode: " + mode);
      }
   }
}
