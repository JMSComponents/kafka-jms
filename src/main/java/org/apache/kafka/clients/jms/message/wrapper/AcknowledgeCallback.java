package org.apache.kafka.clients.jms.message.wrapper;

import javax.jms.JMSException;


public interface AcknowledgeCallback {
   
   void acknowledge() throws JMSException;
   
}