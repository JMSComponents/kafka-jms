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

import javax.jms.BytesMessage;
import javax.jms.JMSException;

public class BytesMessageWrapper extends MessageWrapper<BytesMessage> implements BytesMessage
{
   
   public BytesMessageWrapper(BytesMessage bytesMessage, AcknowledgeCallback acknowledgeCallback){
      super(bytesMessage, acknowledgeCallback);
   }

   @Override
   public long getBodyLength() throws JMSException
   {
      return delegate().getBodyLength();
   }

   @Override
   public boolean readBoolean() throws JMSException
   {
      return delegate().readBoolean();
   }

   @Override
   public byte readByte() throws JMSException
   {
      return delegate().readByte();
   }

   @Override
   public int readUnsignedByte() throws JMSException
   {
      return delegate().readUnsignedByte();
   }

   @Override
   public short readShort() throws JMSException
   {
      return delegate().readShort();
   }

   @Override
   public int readUnsignedShort() throws JMSException
   {
      return delegate().readUnsignedShort();
   }

   @Override
   public char readChar() throws JMSException
   {
      return delegate().readChar();
   }

   @Override
   public int readInt() throws JMSException
   {
      return delegate().readInt();
   }

   @Override
   public long readLong() throws JMSException
   {
      return delegate().readLong();
   }

   @Override
   public float readFloat() throws JMSException
   {
      return delegate().readFloat();
   }

   @Override
   public double readDouble() throws JMSException
   {
      return delegate().readDouble();
   }

   @Override
   public String readUTF() throws JMSException
   {
      return delegate().readUTF();
   }

   @Override
   public int readBytes(byte[] value) throws JMSException
   {
      return delegate().readBytes(value);
   }

   @Override
   public int readBytes(byte[] value, int length) throws JMSException
   {
      return delegate().readBytes(value, length);
   }

   @Override
   public void writeBoolean(boolean value) throws JMSException
   {
      delegate().writeBoolean(value);
   }

   @Override
   public void writeByte(byte value) throws JMSException
   {
      delegate().writeByte(value);
   }

   @Override
   public void writeShort(short value) throws JMSException
   {
      delegate().writeShort(value);
   }

   @Override
   public void writeChar(char value) throws JMSException
   {
      delegate().writeChar(value);
   }

   @Override
   public void writeInt(int value) throws JMSException
   {
      delegate().writeInt(value);
   }

   @Override
   public void writeLong(long value) throws JMSException
   {
      delegate().writeLong(value);
   }

   @Override
   public void writeFloat(float value) throws JMSException
   {
      delegate().writeFloat(value);
   }

   @Override
   public void writeDouble(double value) throws JMSException
   {
      delegate().writeDouble(value);
   }

   @Override
   public void writeUTF(String value) throws JMSException
   {
      delegate().writeUTF(value);
   }

   @Override
   public void writeBytes(byte[] value) throws JMSException
   {
      delegate().writeBytes(value);
   }

   @Override
   public void writeBytes(byte[] value, int offset, int length) throws JMSException
   {
      delegate().writeBytes(value, offset, length);
   }

   @Override
   public void writeObject(Object value) throws JMSException
   {
      delegate().writeObject(value);
   }

   @Override
   public void reset() throws JMSException
   {
      delegate().reset();
   }
   
}
