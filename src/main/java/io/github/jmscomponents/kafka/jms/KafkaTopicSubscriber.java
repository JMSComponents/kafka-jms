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

package io.github.jmscomponents.kafka.jms;

import javax.jms.*;
import javax.jms.Topic;

class KafkaTopicSubscriber extends KafkaMessageConsumer implements TopicSubscriber {

   private final javax.jms.Topic topic;
   
   KafkaTopicSubscriber(KafkaSession kafkaSession, javax.jms.Topic topic, String messageSelector, String subscriptionName, boolean shared) throws JMSException {
      super(kafkaSession, topic, messageSelector, subscriptionName, shared);
      this.topic = topic;
   }

   @Override
   public Topic getTopic() throws JMSException
   {
      return this.topic;
   }

   @Override
   public boolean getNoLocal() throws JMSException
   {
      return false;
   }
}
