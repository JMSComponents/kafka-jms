package io.github.jmscomponents.kafka.jms;

import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.XAJMSContext;

import io.github.jmscomponents.kafka.jms.exception.UnsupportedOperationException;
import io.github.jmscomponents.kafka.jms.exception.JmsExceptionSupport;
import io.github.jmscomponents.kafka.jms.util.ReferenceCounter;
import io.github.jmscomponents.kafka.jms.util.ReferenceCounterUtil;

public abstract class KafkaConnectionForContextImpl implements KafkaConnectionForContext {

   final Runnable closeRunnable = new Runnable() {
      @Override
      public void run() {
         try {
            close();
         } catch (JMSException e) {
            throw JmsExceptionSupport.convertToRuntimeException(e);
         }
      }
   };

   final ReferenceCounter refCounter = new ReferenceCounterUtil(closeRunnable);

   protected final ThreadAwareContext threadAwareContext = new ThreadAwareContext();

   @Override
   public JMSContext createContext(int sessionMode) {
      KafkaConnectionFactory.validateSessionMode(sessionMode);
      refCounter.increment();

      return new KafkaJMSContext(this, sessionMode, threadAwareContext);
   }

   @Override
   public XAJMSContext createXAContext() {
      throw JmsExceptionSupport.convertToRuntimeException(new UnsupportedOperationException("XA is currently not supported"));
   }

   @Override
   public void closeFromContext() {
      refCounter.decrement();
   }

   protected void incrementRefCounter() {
      refCounter.increment();
   }

   public ThreadAwareContext getThreadAwareContext() {
      return threadAwareContext;
   }
}