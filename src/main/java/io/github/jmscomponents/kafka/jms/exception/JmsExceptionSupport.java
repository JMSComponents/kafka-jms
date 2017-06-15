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

package io.github.jmscomponents.kafka.jms.exception;


import java.lang.*;

/**
 * Exception support class.
 *
 * Factory class for creating JMSException instances based on String messages or by
 * wrapping other non-JMS exception.
 *
 * @since 1.0
 */
public final class JmsExceptionSupport {

   private JmsExceptionSupport() {}
   
   public static JMSKafkaException toJMSException(Exception source) {
      JMSKafkaException result = null;

      if (source instanceof java.lang.IllegalArgumentException) {
         result = new IllegalArgumentException(source.getMessage(), source);
      } else if (source instanceof java.util.concurrent.ExecutionException) {
         result = new ExecutionException(source.getMessage(), source);
      } else if (source instanceof java.io.IOException) {
         result = new IOException(source.getMessage(), source);
      } else if (source instanceof java.lang.InterruptedException) {
         result = new InterruptedException(source.getMessage(), source);
      } else {
         result = new JMSKafkaException(source.getMessage(), source);
      }
      return result;
   }
}
