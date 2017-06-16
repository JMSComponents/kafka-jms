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

import javax.jms.Message;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;


public class DefaultConsumerFactory implements ConsumerFactory {

    private final Deserializer<String> keyDeserializer;
    private final Deserializer<Message> messageDeserializer;

    public DefaultConsumerFactory(){
        this(null, null);
    }
    
    public DefaultConsumerFactory(Deserializer<Message> messageDeserializer){
        this(new StringDeserializer(), messageDeserializer);
    }
    
    public DefaultConsumerFactory(Deserializer<String> keyDeserializer, Deserializer<Message> messageDeserializer){
        this.keyDeserializer = keyDeserializer;
        this.messageDeserializer = messageDeserializer;
    }
    
    @Override
    public Consumer<String, Message> createConsumer(Properties properties) {
        return new KafkaConsumer<String, Message>(properties, keyDeserializer, messageDeserializer);
    }
}
