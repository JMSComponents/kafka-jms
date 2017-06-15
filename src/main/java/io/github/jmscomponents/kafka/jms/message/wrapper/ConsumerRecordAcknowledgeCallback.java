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
package io.github.jmscomponents.kafka.jms.message.wrapper;

import javax.jms.JMSException;

import java.util.Collections;

import io.github.jmscomponents.kafka.jms.consumer.ConsumerMessageQueue;
import io.github.jmscomponents.kafka.jms.exception.JMSKafkaException;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerRecordAcknowledgeCallback implements AcknowledgeCallback {

   private static final Logger log = LoggerFactory.getLogger(ConsumerMessageQueue.class);

   private Consumer consumer;
   private String topic;
   private int partition;
   private long offset;
   
   public ConsumerRecordAcknowledgeCallback(Consumer consumer, ConsumerRecord record){
      this(consumer, record.topic(), record.partition(), record.offset());
   }

   public ConsumerRecordAcknowledgeCallback(Consumer consumer, String topic, int partition, long offset){
      this.consumer = consumer;
      this.topic = topic;
      this.partition = partition;
      this.offset = offset;
   }

   @Override
   public void acknowledge() throws JMSException {
      log.debug("acknowledge(), committing offset for message. topic=\'{}\' partition=\'{}\' offset=\'{}\'", 
                this.topic,
                this.partition,
                this.offset);
      try {
         this.consumer.commitSync(Collections.singletonMap(new TopicPartition(topic, partition), new OffsetAndMetadata(this.offset)));
      } catch (KafkaException var2)
      {
         throw new JMSKafkaException(var2);
      }
   }
}
