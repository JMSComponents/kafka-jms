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

import static io.github.jmscomponents.kafka.jms.KafkaSession.toJMSMessageID;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import javax.jms.JMSException;
import javax.jms.Message;

import io.github.jmscomponents.kafka.jms.common.DelegateBlockingQueue;
import io.github.jmscomponents.kafka.jms.message.wrapper.ConsumerRecordAcknowledgeCallback;
import io.github.jmscomponents.kafka.jms.message.wrapper.MessageWrapper;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerMessageQueue extends DelegateBlockingQueue<Message> implements Closeable {
   private static final Logger log = LoggerFactory.getLogger(ConsumerMessageQueue.class);
   private final Consumer<String, Message> consumer;
   private final BlockingQueue<Message> messageQueue = new LinkedBlockingDeque<>();
   private final ExecutorService executorService = Executors.newSingleThreadExecutor();
   private final AtomicBoolean closed = new AtomicBoolean(false);
   private final long consumerPollTimeoutMS;
   
   public ConsumerMessageQueue(Consumer<String, Message> consumer, long consumerPollTimeoutMS) {
      this.consumer = consumer;
      this.consumerPollTimeoutMS = consumerPollTimeoutMS;
   }

   @Override
   public BlockingQueue<Message> delegate() {
      if(this.messageQueue.isEmpty()) {
         log.debug("messageQueue.isEmpty() calling consumer.poll()");
         Future<ConsumerRecords<String, Message>> recordsFuture = this.executorService.submit(() -> consumer.poll(consumerPollTimeoutMS));
         try {
            ConsumerRecords<String, Message> consumerRecords = recordsFuture.get();
            
            ArrayList messages = new ArrayList(consumerRecords.count());
            consumerRecords.forEach(record -> {
                  Message message = MessageWrapper.wrap(
                     record.value(), new ConsumerRecordAcknowledgeCallback(this.consumer, record)
                  );
                  String messageID = toJMSMessageID(record.topic(), record.partition(), record.offset());
                  try {
                     message.setJMSMessageID(messageID);
                  } catch (JMSException jmse) {
                     log.error("JMSException during setJMSMessageID({})", messageID, jmse);
                  }
                  messages.add(message);
               });
            this.messageQueue.addAll(messages);
            if(messages.isEmpty()) {
               log.debug("consumer.poll() returned no messages");
            }
         } catch (InterruptedException ie) {
            log.debug("InterruptedException thrown while getting records", ie);
         } catch (ExecutionException ee) {
            if(!closed.get()) {
               log.error("ExecutionException thrown while getting records", ee);
            }
         }
      }

      return this.messageQueue;
   }

   public void close() throws IOException {
      Future voidFuture = this.executorService.submit(() ->
         {
            Map<TopicPartition, OffsetAndMetadata> offsets =  consumer.assignment().stream().collect(
               Collectors.toMap(
                  topicPartition -> topicPartition,
                  topicPartition -> new OffsetAndMetadata(consumer.position(topicPartition))
               )
            );
            log.debug("consumer.commitSync(offsets)");
            consumer.commitSync(offsets);
            log.debug("consumer.close()");
            consumer.close();
         });

      try {
         voidFuture.get(30L, TimeUnit.SECONDS);
      } catch (InterruptedException | ExecutionException | TimeoutException e) {
         log.error("{} thrown during commit offset and closing consumer", e.getClass().getSimpleName(), e);
      }

      this.closed.set(true);
   }
   
   public java.util.Enumeration getEnumeration() throws JMSException {
      return new ConsumerMessageQueue.Enumeration(this);
   }
   
   
   public static class Enumeration implements java.util.Enumeration<Message> {
      final ConsumerMessageQueue messageQueue;

      public Enumeration(ConsumerMessageQueue messageQueue) {
         this.messageQueue = messageQueue;
      }

      public boolean hasMoreElements() {
         return !this.messageQueue.isEmpty();
      }

      public Message nextElement() {
         return (Message)this.messageQueue.poll();
      }
   }
}
