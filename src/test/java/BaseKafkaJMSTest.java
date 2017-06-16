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
import javax.jms.JMSException;
import java.io.IOException;

import com.github.charithe.kafka.KafkaJunitRule;
import io.github.jmscomponents.kafka.amqp.jms.JmsKafkaConnectionFactory;
import io.github.jmscomponents.kafka.jms.KafkaConnectionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;


public class BaseKafkaJMSTest {

    @ClassRule
    public static KafkaJunitRule kafkaRule = KafkaJunitRule.create().waitForStartup();
    
    KafkaConnectionFactory connectionFactory;

    @Before
    public void before() throws IOException, InterruptedException, JMSException {
        kafkaRule.waitForStartup();
        connectionFactory = new JmsKafkaConnectionFactory("localhost:" + kafkaRule.helper().kafkaPort());

    }
    
    @After
    public void after() throws InterruptedException {
        
    }

}
