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
import java.util.Properties;

import io.github.jmscomponents.kafka.amqp.jms.JmsKafkaConnectionFactory;
import io.github.jmscomponents.kafka.jms.KafkaConnectionFactory;
import org.apache.kafka.streams.integration.utils.EmbeddedKafkaCluster;
import org.junit.After;
import org.junit.Before;


public class BaseKafkaJMSIT {

    static String TOPIC_NAME = "topic";
    static String QUEUE_NAME = "queue";
    
    EmbeddedKafkaCluster embeddedKafkaCluster;
    KafkaConnectionFactory connectionFactory;

    @Before
    public void before() throws IOException, InterruptedException, JMSException {
        Properties properties = new Properties();
        properties.setProperty("zookeeper.session.timeout.ms", "10000");
        properties.setProperty("zookeeper.sync.time.ms", "5000");
        properties.setProperty("fetch.max.wait.ms", "1000");
        properties.setProperty("session.timeout.ms", "50000");
        embeddedKafkaCluster = new EmbeddedKafkaCluster(1, properties);
        embeddedKafkaCluster.start();
        embeddedKafkaCluster.createTopic(TOPIC_NAME);
        embeddedKafkaCluster.createTopic(QUEUE_NAME);
        Thread.sleep(10000);
        connectionFactory = new JmsKafkaConnectionFactory(embeddedKafkaCluster.bootstrapServers());
        connectionFactory.setProperties(properties);

    }
    
    @After
    public void after() throws InterruptedException {
        embeddedKafkaCluster.stop();
        Thread.sleep(10000);
    }

}
