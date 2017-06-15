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

/**
 * Created by pearcem on 31/03/2017.
 */
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
