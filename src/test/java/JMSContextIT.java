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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.jms.Topic;
import java.io.IOException;

import org.junit.Test;

public class JMSContextIT extends BaseKafkaJMSIT {

    @Test
    public void testQueue() throws IOException, InterruptedException, JMSException {
        
        String text = "testString";
        TextMessage result;
        try(JMSContext context = connectionFactory.createContext()){
            Queue destination = context.createQueue("testQueue");
            JMSProducer jmsProducer = context.createProducer();
            jmsProducer.send(destination, text);

            JMSConsumer jmsConsumer = context.createConsumer(destination);
            result = (TextMessage) jmsConsumer.receive(100);
        }

        assertEquals(text, result == null ? null : result.getText());
    }

    @Test
    public void testTopic() throws IOException, InterruptedException, JMSException {
        
        String text = "testString";
        TextMessage result;
        try(JMSContext context = connectionFactory.createContext()){
            Topic destination = context.createTopic("testTopic");
            JMSProducer jmsProducer = context.createProducer();
            jmsProducer.send(destination, text);

            JMSConsumer jmsConsumer = context.createConsumer(destination);
            result = (TextMessage) jmsConsumer.receive(100);

            assertNull("topic subscription should only get messages after subscription", result);

            jmsProducer.send(destination, text);

            result = (TextMessage) jmsConsumer.receive(100);
        }

        assertEquals(text, result == null ? null : result.getText());
    }
}
