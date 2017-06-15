package org.apache.kafka.clients.jms.message;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.apache.kafka.clients.serialization.Headers;

/**
 * Created by pearcem on 26/04/2017.
 */
public class KafkaTextMessage extends AbstractKafkaMessage<String> implements TextMessage {
    
    @Override
    public void setText(String string) throws JMSException {
        body = string;
    }

    @Override
    public String getText() throws JMSException {
        return body;
    }
}
