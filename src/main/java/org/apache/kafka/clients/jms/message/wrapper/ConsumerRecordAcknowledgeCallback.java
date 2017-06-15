package org.apache.kafka.clients.jms.message.wrapper;

import javax.jms.JMSException;

import java.util.Collections;

import org.apache.kafka.clients.jms.consumer.ConsumerMessageQueue;
import org.apache.kafka.clients.jms.exception.JMSKafkaException;
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
