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
package io.github.jmscomponents.kafka.amqp;

import java.util.Map;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import io.github.jmscomponents.kafka.amqp.serialization.AmqpMessageDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.qpid.proton.message.Message;

public class AmqpMessageConsumer extends KafkaConsumer<String, Message> implements Consumer<String, Message>
{
   public AmqpMessageConsumer(Map<String, Object> configs)
   {
      super(configs, new StringDeserializer(), new AmqpMessageDeserializer());
   }
   
}
