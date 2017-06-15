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

import java.util.Properties;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;

import org.apache.kafka.clients.jms.security.PlainLogin;
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
      return new KafkaConnection(bootstrapServers, this.properties);
   }

   public Connection createConnection(String username, String password) throws JMSException {
      javax.security.auth.login.Configuration.setConfiguration(PlainLogin.createJaasConfig(username, password));
      return this.createConnection();
   }

   @Override
   public JMSContext createContext()
   {
      return null;
   }

   @Override
   public JMSContext createContext(int i)
   {
      return null;
   }

   @Override
   public JMSContext createContext(String s, String s1)
   {
      return null;
   }

   @Override
   public JMSContext createContext(String s, String s1, int i)
   {
      return null;
   }
   
   public void setProperties(Properties properties){
      this.properties.putAll(properties);
   }
   
   public void setProperty(String key, String value){
      this.properties.setProperty(key, value);
   }
}
