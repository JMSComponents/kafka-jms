package io.github.jmscomponents.kafka.jms.message.wrapper;

import javax.jms.JMSException;


public interface AcknowledgeCallback {
   
   void acknowledge() throws JMSException;
   
}