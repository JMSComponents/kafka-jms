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

import javax.jms.JMSException;
import javax.jms.Message;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import io.github.jmscomponents.kafka.jms.KafkaSession;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.clients.producer.internals.DefaultPartitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.utils.Utils;

public class MessageGroupPartitioner extends DefaultPartitioner implements Partitioner {
    
    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
        int numPartitions = partitions.size();
        if (value instanceof Message){
            Message message = (Message) value;
            try {
                if (message.propertyExists(KafkaSession.JMSXGROUP_ID)) {
                    String messageGroupId = message.getStringProperty(KafkaSession.JMSXGROUP_ID);
                    return Utils.toPositive(Utils.murmur2(messageGroupId.getBytes(Charset.forName("UTF-8")))) % numPartitions;
                }
            } catch (JMSException jmse){
            }
        }
        return super.partition(topic, key, keyBytes, value, valueBytes, cluster);
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    public void configure(Map<String, ?> configs) {
        super.configure(configs);
    }
}
