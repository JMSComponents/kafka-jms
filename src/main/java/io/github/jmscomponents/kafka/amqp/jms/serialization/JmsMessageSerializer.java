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
package io.github.jmscomponents.kafka.amqp.jms.serialization;

import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;
import javax.jms.Message;
import java.util.Map;

import io.github.jmscomponents.kafka.amqp.serialization.AmqpMessageSerializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.qpid.jms.provider.amqp.message.AmqpJmsConverter;

public class JmsMessageSerializer implements Serializer<Message>
{

      private final AmqpMessageSerializer messageSerializer = new AmqpMessageSerializer();

      public void configure(Map<String, ?> map, boolean b) {
          messageSerializer.configure(map, b);
      }

      public byte[] serialize(String s, Message message) {
         try {
            org.apache.qpid.proton.message.Message amqpMessage = AmqpJmsConverter.toAmqpMessage(message);
            return messageSerializer.serialize(s, amqpMessage);
         } catch (JMSException e)
         {
            throw new JMSRuntimeException(e.getMessage(), e.getErrorCode(), e);
         }
      }

      public void close()
      {
          messageSerializer.close();
      }
   }