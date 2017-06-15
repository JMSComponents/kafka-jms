package org.apache.kafka.clients.jms.message.wrapper;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import java.io.Serializable;

/**
 * Created by pearcem on 30/03/2017.
 */
public class ObjectMessageWrapper extends MessageWrapper<ObjectMessage> implements ObjectMessage {
   
   public ObjectMessageWrapper(ObjectMessage objectMessage, AcknowledgeCallback acknowledgeCallback){
      super(objectMessage, acknowledgeCallback);
   }


   @Override
   public void setObject(Serializable object) throws JMSException
   {
      delegate().setObject(object);
   }

   @Override
   public Serializable getObject() throws JMSException
   {
      return delegate().getObject();
   }
}
