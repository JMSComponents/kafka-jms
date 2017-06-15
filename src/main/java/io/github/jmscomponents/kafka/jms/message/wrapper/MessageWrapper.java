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
package io.github.jmscomponents.kafka.jms.message.wrapper;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

public class MessageWrapper<T extends Message> extends DelegatingMessage<T> implements Message {
   
   private final AcknowledgeCallback callback;
   private final T message;

   public MessageWrapper(T message, AcknowledgeCallback callback){
      this.callback = callback;
      this.message = message;
   }

   @Override
   public void acknowledge() throws JMSException
   {
      delegate().acknowledge();
      if (callback != null) {
         callback.acknowledge();
      }
   }
   
   public T delegate() {
      return message;
   }
   

   public static Message wrap(Message message, AcknowledgeCallback acknowledgeCallback){
      if (message instanceof BytesMessage) {
         return new BytesMessageWrapper((BytesMessage)message, acknowledgeCallback);
      } else if (message instanceof MapMessage) {
         return new MapMessageWrapper((MapMessage)message, acknowledgeCallback);
      } else if (message instanceof ObjectMessage) {
         return new ObjectMessageWrapper((ObjectMessage)message, acknowledgeCallback);
      } else if (message instanceof StreamMessage) {
         return new StreamMessageWrapper((StreamMessage) message, acknowledgeCallback);
      } else if (message instanceof TextMessage) {
         return new TextMessageWrapper((TextMessage) message, acknowledgeCallback);
      } else {
         return new MessageWrapper(message, acknowledgeCallback);
      }
   }
   
}
