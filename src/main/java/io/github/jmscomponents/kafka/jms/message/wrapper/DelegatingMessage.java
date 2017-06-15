/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.jmscomponents.kafka.jms.message.wrapper;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import java.util.Enumeration;

public abstract class DelegatingMessage<T extends Message> implements Message {
   
   public abstract T delegate();
   
   @Override
   public String getJMSMessageID() throws JMSException
   {
      return delegate().getJMSMessageID();
   }

   @Override
   public void setJMSMessageID(String id) throws JMSException
   {
      delegate().setJMSMessageID(id);
   }

   @Override
   public long getJMSTimestamp() throws JMSException
   {
      return delegate().getJMSTimestamp();
   }

   @Override
   public void setJMSTimestamp(long timestamp) throws JMSException
   {
      delegate().setJMSTimestamp(timestamp);
   }

   @Override
   public byte[] getJMSCorrelationIDAsBytes() throws JMSException
   {
      return delegate().getJMSCorrelationIDAsBytes();
   }

   @Override
   public void setJMSCorrelationIDAsBytes(byte[] correlationID) throws JMSException
   {
      delegate().setJMSCorrelationIDAsBytes(correlationID);
   }

   @Override
   public void setJMSCorrelationID(String correlationID) throws JMSException
   {
      delegate().setJMSCorrelationID(correlationID);
   }

   @Override
   public String getJMSCorrelationID() throws JMSException
   {
      return delegate().getJMSCorrelationID();
   }

   @Override
   public Destination getJMSReplyTo() throws JMSException
   {
      return delegate().getJMSReplyTo();
   }

   @Override
   public void setJMSReplyTo(Destination replyTo) throws JMSException
   {
      delegate().setJMSReplyTo(replyTo);
   }

   @Override
   public Destination getJMSDestination() throws JMSException
   {
      return delegate().getJMSDestination();
   }

   @Override
   public void setJMSDestination(Destination destination) throws JMSException
   {
      delegate().setJMSDestination(destination);
   }

   @Override
   public int getJMSDeliveryMode() throws JMSException
   {
      return delegate().getJMSDeliveryMode();
   }

   @Override
   public void setJMSDeliveryMode(int deliveryMode) throws JMSException
   {
      delegate().setJMSDeliveryMode(deliveryMode);
   }

   @Override
   public boolean getJMSRedelivered() throws JMSException
   {
      return delegate().getJMSRedelivered();
   }

   @Override
   public void setJMSRedelivered(boolean redelivered) throws JMSException
   {
      delegate().setJMSRedelivered(redelivered);
   }

   @Override
   public String getJMSType() throws JMSException
   {
      return delegate().getJMSType();
   }

   @Override
   public void setJMSType(String type) throws JMSException
   {
      delegate().setJMSType(type);
   }

   @Override
   public long getJMSExpiration() throws JMSException
   {
      return delegate().getJMSExpiration();
   }

   @Override
   public void setJMSExpiration(long expiration) throws JMSException
   {
      delegate().setJMSExpiration(expiration);
   }

   @Override
   public int getJMSPriority() throws JMSException
   {
      return delegate().getJMSPriority();
   }

   @Override
   public void setJMSPriority(int priority) throws JMSException
   {
      delegate().setJMSPriority(priority);
   }

   @Override
   public void clearProperties() throws JMSException
   {
      delegate().clearProperties();
   }

   @Override
   public boolean propertyExists(String name) throws JMSException
   {
      return delegate().propertyExists(name);
   }

   @Override
   public boolean getBooleanProperty(String name) throws JMSException
   {
      return delegate().getBooleanProperty(name);
   }

   @Override
   public byte getByteProperty(String name) throws JMSException
   {
      return delegate().getByteProperty(name);
   }

   @Override
   public short getShortProperty(String name) throws JMSException
   {
      return delegate().getShortProperty(name);
   }

   @Override
   public int getIntProperty(String name) throws JMSException
   {
      return delegate().getIntProperty(name);
   }

   @Override
   public long getLongProperty(String name) throws JMSException
   {
      return delegate().getLongProperty(name);
   }

   @Override
   public float getFloatProperty(String name) throws JMSException
   {
      return delegate().getFloatProperty(name);
   }

   @Override
   public double getDoubleProperty(String name) throws JMSException
   {
      return delegate().getDoubleProperty(name);
   }

   @Override
   public String getStringProperty(String name) throws JMSException
   {
      return delegate().getStringProperty(name);
   }

   @Override
   public Object getObjectProperty(String name) throws JMSException
   {
      return delegate().getObjectProperty(name);
   }

   @Override
   public Enumeration getPropertyNames() throws JMSException
   {
      return delegate().getPropertyNames();
   }

   @Override
   public void setBooleanProperty(String name, boolean value) throws JMSException
   {
      delegate().setBooleanProperty(name, value);
   }

   @Override
   public void setByteProperty(String name, byte value) throws JMSException
   {
      delegate().setByteProperty(name, value);
   }

   @Override
   public void setShortProperty(String name, short value) throws JMSException
   {
      delegate().setShortProperty(name, value);
   }

   @Override
   public void setIntProperty(String name, int value) throws JMSException
   {
      delegate().setIntProperty(name, value);
   }

   @Override
   public void setLongProperty(String name, long value) throws JMSException
   {
      delegate().setLongProperty(name, value);
   }

   @Override
   public void setFloatProperty(String name, float value) throws JMSException
   {
      delegate().setFloatProperty(name, value);
   }

   @Override
   public void setDoubleProperty(String name, double value) throws JMSException
   {
      delegate().setDoubleProperty(name, value);
   }

   @Override
   public void setStringProperty(String name, String value) throws JMSException
   {
      delegate().setStringProperty(name, value);
   }

   @Override
   public void setObjectProperty(String name, Object value) throws JMSException
   {
      delegate().setObjectProperty(name, value);
   }

   @Override
   public void acknowledge() throws JMSException
   {
      delegate().acknowledge();
   }

   @Override
   public void clearBody() throws JMSException
   {
      delegate().clearBody();
   }

   @Override
   public long getJMSDeliveryTime() throws JMSException
   {
      return delegate().getJMSDeliveryTime();
   }

   @Override
   public void setJMSDeliveryTime(long deliveryTime) throws JMSException
   {
      delegate().setJMSDeliveryTime(deliveryTime);
   }

   @Override
   public <T> T getBody(Class<T> c) throws JMSException
   {
      return delegate().getBody(c);
   }

   @Override
   public boolean isBodyAssignableTo(Class c) throws JMSException
   {
      return delegate().isBodyAssignableTo(c);
   }
}
