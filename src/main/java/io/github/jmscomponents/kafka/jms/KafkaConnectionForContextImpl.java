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

import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.XAJMSContext;

import io.github.jmscomponents.kafka.jms.exception.UnsupportedOperationException;
import io.github.jmscomponents.kafka.jms.exception.JmsExceptionSupport;
import io.github.jmscomponents.kafka.jms.util.ReferenceCounter;
import io.github.jmscomponents.kafka.jms.util.ReferenceCounterUtil;

public abstract class KafkaConnectionForContextImpl implements KafkaConnectionForContext {

   final Runnable closeRunnable = new Runnable() {
      @Override
      public void run() {
         try {
            close();
         } catch (JMSException e) {
            throw JmsExceptionSupport.convertToRuntimeException(e);
         }
      }
   };

   final ReferenceCounter refCounter = new ReferenceCounterUtil(closeRunnable);

   protected final ThreadAwareContext threadAwareContext = new ThreadAwareContext();

   @Override
   public JMSContext createContext(int sessionMode) {
      KafkaConnectionFactory.validateSessionMode(sessionMode);
      refCounter.increment();

      return new KafkaJMSContext(this, sessionMode, threadAwareContext);
   }

   @Override
   public XAJMSContext createXAContext() {
      throw JmsExceptionSupport.convertToRuntimeException(new UnsupportedOperationException("XA is currently not supported"));
   }

   @Override
   public void closeFromContext() {
      refCounter.decrement();
   }

   protected void incrementRefCounter() {
      refCounter.increment();
   }

   public ThreadAwareContext getThreadAwareContext() {
      return threadAwareContext;
   }
}