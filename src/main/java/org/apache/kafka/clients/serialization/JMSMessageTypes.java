package org.apache.kafka.clients.serialization;

import javax.jms.BytesMessage;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.common.serialization.Serializer;

/**
 * Created by pearcem on 24/04/2017.
 */
public class JMSMessageTypes {
    
    
    List<Serializer<Message>> messageSerializers = new ArrayList<>();

    List<Serializer<TextMessage>> textMessage = new ArrayList<>();
    
    List<Serializer<BytesMessage>> byteMessage = new ArrayList<>();

    List<Serializer<MapMessage>> mapMessage = new ArrayList<>();

    List<Serializer<ObjectMessage>> objectMessage = new ArrayList<>();

    List<Serializer<StreamMessage>> streamMessage = new ArrayList<>();
    
    
    


}
