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
package io.github.jmscomponents.kafka.amqp.jms;

import io.github.jmscomponents.kafka.amqp.jms.serialization.JmsMessageDeserializer;
import io.github.jmscomponents.kafka.amqp.jms.serialization.JmsMessageSerializer;
import io.github.jmscomponents.kafka.jms.KafkaConnectionFactory;
import io.github.jmscomponents.kafka.jms.consumer.DefaultConsumerFactory;
import io.github.jmscomponents.kafka.jms.producer.DefaultProducerFactory;

public class JmsKafkaConnectionFactory extends KafkaConnectionFactory {
    
    public JmsKafkaConnectionFactory(String bootstrapServers) {
        super(bootstrapServers, new DefaultProducerFactory(new JmsMessageSerializer()), new DefaultConsumerFactory(new JmsMessageDeserializer()), new JmsMessageFactory());
    }
}
