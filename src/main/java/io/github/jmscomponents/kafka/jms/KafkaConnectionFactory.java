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

import io.github.jmscomponents.kafka.jms.exception.JmsExceptionSupport;
import io.github.jmscomponents.kafka.jms.security.PlainLogin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaConnectionFactory implements ConnectionFactory {
   private static final Logger log = LoggerFactory.getLogger(KafkaConnectionFactory.class);
   private final Properties properties = new Properties();
   private final String bootstrapServers;
   
   public KafkaConnectionFactory(String bootstrapServers) {
      this.bootstrapServers = bootstrapServers;
   }

   
   public Connection createConnection() throws JMSException {
      return createConnection(null, null);
   }

   public Connection createConnection(String username, String password) throws JMSException {
      return this.createConnectionInternal(username, password);
   }

   @Override
   public JMSContext createContext() {
      return createContext(null, null);
   }

   @Override
   public JMSContext createContext(final int sessionMode) {
      return createContext(null, null, sessionMode);
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
   
   private KafkaConnection createConnectionInternal(String username, String password) throws JMSException {
      if (username != null && password != null) {
         javax.security.auth.login.Configuration.setConfiguration(PlainLogin.createJaasConfig(username, password));
      }
      return new KafkaConnection(bootstrapServers, this.properties);
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
