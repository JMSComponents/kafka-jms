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

import java.util.concurrent.Executors;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageListenerRunnable implements Runnable {
   private static final Logger log = LoggerFactory.getLogger(MessageListenerRunnable.class);
   private final KafkaMessageConsumer messageConsumer;
   private final MessageListener messageListener;
   private volatile Thread thread;

   MessageListenerRunnable(KafkaMessageConsumer messageConsumer, MessageListener messageListener) {
      this.messageConsumer = messageConsumer;
      this.messageListener = messageListener;
   }

   public void run() {
      Thread thisThread = Thread.currentThread();
      while(thisThread == thread && !messageConsumer.getSession().isClosed() && messageConsumer.getSession().isStarted()) {
         Message message;
         try {
            message = messageConsumer.receive(1000);
         } catch (IllegalArgumentException iae) {
            log.debug("messageConsumer is already closed");
            continue;
         } catch (JMSException jmse) {
            log.error("JMSException thrown while reading from messageConsumer", jmse);
            continue;
         }

         if(null != message) {
            try {
               this.messageListener.onMessage(message);
            } catch (Throwable e) {
               log.error("{} thrown while processing message", e.getClass().getSimpleName(), e);
            }
         }
      }

      log.info("MessageListenerRunnable thread for {} is stopped", messageListener);
   }

   public void stop() {
      Thread existingThread = thread;
      thread = null;
      existingThread.interrupt();
   }

   public void start() {
      thread = Executors.defaultThreadFactory().newThread(this);
      thread.start();
   }

   public MessageListener getMessageListener() {
      return messageListener;
   }
}
