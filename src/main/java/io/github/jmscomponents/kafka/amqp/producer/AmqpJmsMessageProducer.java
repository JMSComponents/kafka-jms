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
package io.github.jmscomponents.kafka.amqp.producer;

import javax.jms.Message;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import io.github.jmscomponents.kafka.amqp.serialization.AmqpJmsMessageSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

public class AmqpJmsMessageProducer extends KafkaProducer<String, Message> implements Producer<String, Message>
{
   public AmqpJmsMessageProducer(Map<String, Object> configs)
   {
      super(configs, new StringSerializer(), new AmqpJmsMessageSerializer());
   }

   public AmqpJmsMessageProducer(Properties properties)
   {
      super(properties, new StringSerializer(), new AmqpJmsMessageSerializer());
   }
   


}