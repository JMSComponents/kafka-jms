package io.github.jmscomponents.kafka.amqp.consumer;

import javax.jms.Message;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import io.github.jmscomponents.kafka.amqp.AmqpJmsMessageDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

/**
 * Created by pearcem on 01/02/2017.
 */
public class AmqpJmsMessageConsumer extends KafkaConsumer<String, Message> implements Consumer<String, Message>
{
   
   public AmqpJmsMessageConsumer(Map<String, Object> configs)
   {
      super(configs, new StringDeserializer(), new AmqpJmsMessageDeserializer());
   }

   public AmqpJmsMessageConsumer(Properties properties)
   {
      super(properties, new StringDeserializer(), new AmqpJmsMessageDeserializer());
   }
}
