package io.github.jmscomponents.kafka.jms.message;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import io.github.jmscomponents.kafka.serialization.Headers;

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
