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

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.util.Enumeration;

public class MapMessageWrapper extends MessageWrapper<MapMessage> implements MapMessage
{
   public MapMessageWrapper(MapMessage mapMessage, AcknowledgeCallback acknowledgeCallback){
      super(mapMessage, acknowledgeCallback);
   }
   
   @Override
   public boolean getBoolean(String name) throws JMSException
   {
      return delegate().getBoolean(name);
   }

   @Override
   public byte getByte(String name) throws JMSException
   {
      return delegate().getByte(name);
   }

   @Override
   public short getShort(String name) throws JMSException
   {
      return delegate().getShort(name);
   }

   @Override
   public char getChar(String name) throws JMSException
   {
      return delegate().getChar(name);
   }

   @Override
   public int getInt(String name) throws JMSException
   {
      return delegate().getInt(name);
   }

   @Override
   public long getLong(String name) throws JMSException
   {
      return delegate().getLong(name);
   }

   @Override
   public float getFloat(String name) throws JMSException
   {
      return delegate().getFloat(name);
   }

   @Override
   public double getDouble(String name) throws JMSException
   {
      return delegate().getDouble(name);
   }

   @Override
   public String getString(String name) throws JMSException
   {
      return delegate().getString(name);
   }

   @Override
   public byte[] getBytes(String name) throws JMSException
   {
      return delegate().getBytes(name);
   }

   @Override
   public Object getObject(String name) throws JMSException
   {
      return delegate().getObject(name);
   }

   @Override
   public Enumeration getMapNames() throws JMSException
   {
      return delegate().getMapNames();
   }

   @Override
   public void setBoolean(String name, boolean value) throws JMSException
   {
      delegate().setBoolean(name, value);
   }

   @Override
   public void setByte(String name, byte value) throws JMSException
   {
      delegate().setByte(name, value);
   }

   @Override
   public void setShort(String name, short value) throws JMSException
   {
      delegate().setShort(name, value);
   }

   @Override
   public void setChar(String name, char value) throws JMSException
   {
      delegate().setChar(name, value);
   }

   @Override
   public void setInt(String name, int value) throws JMSException
   {
      delegate().setInt(name, value);
   }

   @Override
   public void setLong(String name, long value) throws JMSException
   {
      delegate().setLong(name, value);
   }

   @Override
   public void setFloat(String name, float value) throws JMSException
   {
      delegate().setFloat(name, value);
   }

   @Override
   public void setDouble(String name, double value) throws JMSException
   {
      delegate().setDouble(name, value);
   }

   @Override
   public void setString(String name, String value) throws JMSException
   {
      delegate().setString(name, value);
   }

   @Override
   public void setBytes(String name, byte[] value) throws JMSException
   {
      delegate().setBytes(name, value);
   }

   @Override
   public void setBytes(String name, byte[] value, int offset, int length) throws JMSException
   {
      delegate().setBytes(name, value, offset, length);
   }

   @Override
   public void setObject(String name, Object value) throws JMSException
   {
      delegate().setObject(name, value);
   }

   @Override
   public boolean itemExists(String name) throws JMSException
   {
      return delegate().itemExists(name);
   }

   
}
