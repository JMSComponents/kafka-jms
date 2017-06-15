package org.apache.kafka.clients.serialization;

import java.nio.BufferOverflowException;
import java.util.Arrays;
import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;
import org.apache.qpid.proton.message.Message;

public class AmqpMessageSerializer implements Serializer<Message>
{

      private int BUFFER_SIZE = 4096;

      public void configure(Map<String, ?> map, boolean b)
      {
         
      }

      public byte[] serialize(String s, Message message)
      {
         byte[] encodedMessage = new byte[BUFFER_SIZE];
         while(true) {
            try {
               int encoded = message.encode(encodedMessage, 0, encodedMessage.length);
               return Arrays.copyOfRange(encodedMessage, 0, encoded);
            } catch (BufferOverflowException var5) {
               encodedMessage = new byte[encodedMessage.length * 2];
            }
         }
      }

      public void close()
      {

      }
   }