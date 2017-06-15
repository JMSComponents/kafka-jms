package io.github.jmscomponents.kafka.jms.message.wrapper;

import javax.jms.JMSException;
import javax.jms.TextMessage;

/**
 * Created by pearcem on 30/03/2017.
 */
public class TextMessageWrapper extends MessageWrapper<TextMessage> implements TextMessage {
   
   public TextMessageWrapper(TextMessage textMessage, AcknowledgeCallback acknowledgeCallback){
      super(textMessage, acknowledgeCallback);
   }
   
   @Override
   public void setText(String string) throws JMSException
   {
      delegate().setText(string);
   }

   @Override
   public String getText() throws JMSException
   {
      return delegate().getText();
   }
   
}
