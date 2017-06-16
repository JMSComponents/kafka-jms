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

package io.github.jmscomponents.kafka.jms.producer;

import javax.jms.Message;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;


public class DefaultProducerFactory implements ProducerFactory {

    private final Serializer<String> keySerializer;
    private final Serializer<Message> messageSerializer;

    public DefaultProducerFactory(){
        this(null, null);
    }
    
    public DefaultProducerFactory(Serializer<Message> messageSerializer){
        this(new StringSerializer(), messageSerializer);
    }
    
    public DefaultProducerFactory(Serializer<String> keySerializer, Serializer<Message> messageSerializer){
        this.keySerializer = keySerializer;
        this.messageSerializer = messageSerializer;
    }
    
    @Override
    public Producer<String, Message> createProducer(Properties properties) {
        return new KafkaProducer<String, Message>(properties, keySerializer, messageSerializer);
    }
}
