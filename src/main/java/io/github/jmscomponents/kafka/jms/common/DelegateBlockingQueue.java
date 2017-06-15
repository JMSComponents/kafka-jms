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

package io.github.jmscomponents.kafka.jms.common;

import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;


public abstract class DelegateBlockingQueue<E> implements BlockingQueue<E> {
   
   public abstract BlockingQueue<E> delegate();
   
   public boolean add(E o)
   {
      return delegate().add(o);
   }

   public boolean offer(E o)
   {
      return delegate().offer(o);
   }

   public void put(E o) throws InterruptedException
   {
      delegate().put(o);
   }

   public boolean offer(E o, long timeout, TimeUnit unit) throws InterruptedException
   {
      return delegate().offer(o, timeout, unit);
   }

   @Override
   public E take() throws InterruptedException
   {
      return delegate().take();
   }

   @Override
   public E poll(long timeout, TimeUnit unit) throws InterruptedException
   {
      return delegate().poll(timeout, unit);
   }

   @Override
   public int remainingCapacity()
   {
      return delegate().remainingCapacity();
   }

   @Override
   public boolean remove(Object o)
   {
      return delegate().remove(o);
   }

   @Override
   public boolean contains(Object o)
   {
      return delegate().contains(o);
   }

   public int drainTo(Collection c)
   {
      return delegate().drainTo(c);
   }

   public int drainTo(Collection c, int maxElements)
   {
      return delegate().drainTo(c, maxElements);
   }

   @Override
   public E remove()
   {
      return delegate().remove();
   }

   @Override
   public E poll()
   {
      return delegate().poll();
   }

   @Override
   public E element()
   {
      return delegate().element();
   }

   @Override
   public E peek()
   {
      return delegate().peek();
   }

   @Override
   public int size()
   {
      return delegate().size();
   }

   @Override
   public boolean isEmpty()
   {
      return delegate().isEmpty();
   }

   @Override
   public Iterator iterator()
   {
      return delegate().iterator();
   }

   @Override
   public Object[] toArray()
   {
      return delegate().toArray();
   }

   public Object[] toArray(Object[] a)
   {
      return delegate().toArray(a);
   }

   public boolean containsAll(Collection c)
   {
      return delegate().containsAll(c);
   }

   public boolean addAll(Collection c)
   {
      return delegate().addAll(c);
   }

   public boolean removeAll(Collection c)
   {
      return delegate().removeAll(c);
   }

   public boolean removeIf(Predicate filter)
   {
      return delegate().removeIf(filter);
   }

   public boolean retainAll(Collection c)
   {
      return delegate().retainAll(c);
   }

   @Override
   public void clear()
   {
      delegate().clear();
   }

   @Override
   public boolean equals(Object o)
   {
      return delegate().equals(o);
   }

   @Override
   public int hashCode()
   {
      return delegate().hashCode();
   }

   @Override
   public Spliterator spliterator()
   {
      return delegate().spliterator();
   }

   @Override
   public Stream stream()
   {
      return delegate().stream();
   }

   @Override
   public Stream parallelStream()
   {
      return delegate().parallelStream();
   }

   public void forEach(Consumer action)
   {
      delegate().forEach(action);
   }

}
