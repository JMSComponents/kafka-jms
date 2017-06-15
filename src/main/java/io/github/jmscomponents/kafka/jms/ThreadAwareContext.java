/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.jmscomponents.kafka.jms;

import javax.jms.IllegalStateException;
import javax.jms.IllegalStateRuntimeException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Restricts what can be called on context passed in wrapped CompletionListener.
 */
public class ThreadAwareContext {

   /**
    * Necessary in order to assert some methods ({@link javax.jms.JMSContext#stop()}
    * {@link javax.jms.JMSContext#close()} etc) are not getting called from within a
    * {@link javax.jms.CompletionListener}.
    *
    * @see ThreadAwareContext#assertNotMessageListenerThread()
    */
   private Thread completionListenerThread;

   /**
    * Use a set because JMSContext can create more than one JMSConsumer
    * to receive asynchronously from different destinations.
    */
   private final Set<Long> messageListenerThreads = Collections.newSetFromMap(new ConcurrentHashMap<>());

   /**
    * Sets current thread to the context
    * <p>
    * Meant to inform an JMSContext which is the thread that CANNOT call some of its methods.
    * </p>
    *
    * @param isCompletionListener : indicating whether current thread is from CompletionListener
    *                             or from MessageListener.
    */
   public void setCurrentThread(boolean isCompletionListener) {
      if (isCompletionListener) {
         completionListenerThread = Thread.currentThread();
      } else {
         messageListenerThreads.add(Thread.currentThread().getId());
      }
   }

   /**
    * Clear current thread from the context
    *
    * @param isCompletionListener : indicating whether current thread is from CompletionListener
    *                             or from MessageListener.
    */
   public void clearCurrentThread(boolean isCompletionListener) {
      if (isCompletionListener) {
         completionListenerThread = null;
      } else {
         messageListenerThreads.remove(Thread.currentThread().getId());
      }
   }

   /**
    * Asserts a {@link javax.jms.CompletionListener} is not calling from its own {@link javax.jms.JMSContext}.
    * <p>
    * Note that the code must work without any need for further synchronization, as there is the
    * requirement that only one CompletionListener be called at a time. In other words,
    * CompletionListener calling is single-threaded.
    *
    * @see javax.jms.JMSContext#close()
    * @see javax.jms.JMSContext#stop()
    * @see javax.jms.JMSContext#commit()
    * @see javax.jms.JMSContext#rollback()
    */
   public void assertNotCompletionListenerThreadRuntime() {
      if (completionListenerThread == Thread.currentThread()) {
         throw new IllegalStateRuntimeException("It is illegal to call this method from within a Completion Listener");
      }
   }

   /**
    * Asserts a {@link javax.jms.CompletionListener} is not calling from its own {@link javax.jms.Connection} or from
    * a {@link javax.jms.MessageProducer} .
    * <p>
    * Note that the code must work without any need for further synchronization, as there is the
    * requirement that only one CompletionListener be called at a time. In other words,
    * CompletionListener calling is single-threaded.
    *
    * @see javax.jms.Connection#close()
    * @see javax.jms.MessageProducer#close()
    */
   public void assertNotCompletionListenerThread() throws IllegalStateException {
      if (completionListenerThread == Thread.currentThread()) {
         throw new IllegalStateException("It is illegal to call this method from within a Completion Listener");
      }
   }

   /**
    * Asserts a {@link javax.jms.MessageListener} is not calling from its own {@link javax.jms.JMSContext}.
    * <p>
    * Note that the code must work without any need for further synchronization, as there is the
    * requirement that only one MessageListener be called at a time. In other words,
    * MessageListener calling is single-threaded.
    *
    * @see javax.jms.JMSContext#close()
    * @see javax.jms.JMSContext#stop()
    */
   public void assertNotMessageListenerThreadRuntime() {
      if (messageListenerThreads.contains(Thread.currentThread().getId())) {
         throw new IllegalStateRuntimeException("It is illegal to call this method from within a Message Listener");
      }
   }

   /**
    * Asserts a {@link javax.jms.MessageListener} is not calling from its own {@link javax.jms.Connection} or
    * {@link javax.jms.MessageConsumer}.
    * <p>
    * Note that the code must work without any need for further synchronization, as there is the
    * requirement that only one MessageListener be called at a time. In other words,
    * MessageListener calling is single-threaded.
    *
    * @see javax.jms.Connection#close()
    * @see javax.jms.MessageConsumer#close()
    */
   public void assertNotMessageListenerThread() throws IllegalStateException {
      if (messageListenerThreads.contains(Thread.currentThread().getId())) {
         throw new IllegalStateException("It is illegal to call this method from within a Message Listener");
      }
   }

}
