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
package io.github.jmscomponents.kafka.jms;


import javax.jms.JMSException;

import io.github.jmscomponents.kafka.jms.util.Preconditions;

public abstract class Destination implements javax.jms.Destination, Comparable<Destination> {
   final String name;

   public Destination(String name) throws JMSException {
      Preconditions.checkNotNull(name, "destination name cannot be null");
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public int compareTo(Destination that) {
      return this.getName().compareTo(that.getName());
   }

   public int hashCode() {
      return getName().hashCode();
   }

   public boolean equals(Object obj) {
      return obj instanceof Destination ? this.getName().equals(((Destination) obj).getName()) : false;
   }

   public String toString() {
      return getClass().getSimpleName() + ":" + getName();
   }
}
