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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.jms.Connection;
import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;

import io.github.jmscomponents.kafka.jms.common.ConnectionAwareSession;
import io.github.jmscomponents.kafka.jms.exception.UnsupportedOperationException;

import io.github.jmscomponents.kafka.jms.util.Unsupported;
import org.apache.kafka.clients.CommonClientConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaConnection implements Connection {
   private static final Logger log = LoggerFactory.getLogger(KafkaConnection.class);
   private final Properties config;
   private ExceptionListener exceptionListener;
   private List<ConnectionAwareSession> sessions = new ArrayList<>();
   private AtomicBoolean isStarted = new AtomicBoolean(false);
   
   KafkaConnection(String bootstrapServers, Properties config) {
      this.config = new Properties(config);
      this.config.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
   }

   Properties getConfig() {
      return config;
   }

   public Session createSession(boolean transacted, int acknowledgeMode) throws JMSException {
      ConnectionAwareSession kafkaSession = new KafkaSession(this, transacted, acknowledgeMode);
      sessions.add(kafkaSession);
      return kafkaSession;
   }

   @Override
   public Session createSession(int acknowledgeMode) throws JMSException
   {
      return createSession(false, acknowledgeMode);
   }

   @Override
   public Session createSession() throws JMSException
   {
      return createSession(Session.AUTO_ACKNOWLEDGE);
   }

   void removeSession(Session session){
      sessions.remove(session);
   }

   public String getClientID() throws JMSException {
      return getConfig().getProperty(CommonClientConfigs.CLIENT_ID_CONFIG);
   }

   public void setClientID(String clientID) throws JMSException {
      getConfig().setProperty(CommonClientConfigs.CLIENT_ID_CONFIG, clientID);
   }

   public ConnectionMetaData getMetaData() throws JMSException {
      return new KafkaConnectionMetaData();
   }

   @Unsupported("ExceptionListeners are not supported")
   public ExceptionListener getExceptionListener() throws JMSException {
      return this.exceptionListener;
   }

   @Unsupported("ExceptionListeners are not supported.")
   public void setExceptionListener(ExceptionListener exceptionListener) throws JMSException {
      this.exceptionListener = exceptionListener;
   }

   public void start() throws JMSException {
      if (isStarted.compareAndSet(false, true)){
         sessions.forEach(ConnectionAwareSession::run);
      }
   }

   public void stop() throws JMSException {
      isStarted.compareAndSet(true, false);
   }
   
   public void close() throws JMSException {
      this.stop();
      //We do this to avoid concurrent modification as when a session closes it removes itself
      for(ConnectionAwareSession session : new ArrayList<>(sessions)) {
         session.close();
      }
   }

   public ConnectionConsumer createConnectionConsumer(Destination destination, String s, ServerSessionPool serverSessionPool, int i) throws JMSException {
      throw new UnsupportedOperationException("connection.createConnectionConsumer is unsupported");
   }

   public ConnectionConsumer createDurableConnectionConsumer(Topic topic, String s, String s1, ServerSessionPool serverSessionPool, int i) throws JMSException {
      throw new UnsupportedOperationException("connection.createDurableConnectionConsumer is unsupported");
   }

   @Override
   public ConnectionConsumer createSharedDurableConnectionConsumer(Topic topic, String s, String s1, ServerSessionPool serverSessionPool,
                                                                   int i) throws JMSException {
      throw new UnsupportedOperationException("connection.createSharedDurableConnectionConsumer is unsupported");
   }

   @Override
   public ConnectionConsumer createSharedConnectionConsumer(Topic topic, String s, String s1, ServerSessionPool serverSessionPool, int i)
      throws JMSException {
      throw new UnsupportedOperationException("connection.createSharedConnectionConsumer is unsupported");
   }
   
   public boolean isStarted(){
      return isStarted.get();
   }
}
