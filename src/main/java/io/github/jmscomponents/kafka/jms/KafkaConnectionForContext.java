package io.github.jmscomponents.kafka.jms;

import javax.jms.JMSContext;
import javax.jms.XAJMSContext;

/**
 * Interface created to support reference counting all contexts using it.
 * <p>
 * Necessary to support {@code JMSContext.close()} conditions.
 *
 * @see JMSContext
 */
public interface KafkaConnectionForContext extends javax.jms.Connection {

   JMSContext createContext(int sessionMode);

   XAJMSContext createXAContext();

   void closeFromContext();
}
