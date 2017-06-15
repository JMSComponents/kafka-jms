package io.github.jmscomponents.kafka.amqp.producer;

import java.util.Map;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import io.github.jmscomponents.kafka.amqp.serialization.AmqpMessageSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.qpid.proton.message.Message;

/**
 * Created by pearcem on 01/02/2017.
 */
public class AmqpMessageProducer extends KafkaProducer<String, Message> implements Producer<String, Message>
{
   public AmqpMessageProducer(Map<String, Object> configs) {
      super(configs, new StringSerializer(), new AmqpMessageSerializer());
   }
   
}
