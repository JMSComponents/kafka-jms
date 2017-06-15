package io.github.jmscomponents.kafka.jms.util;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

public class ReferenceCounterUtil implements ReferenceCounter {

   private final Runnable runnable;

   /**
    * If executor is null the runnable will be called within the same thread, otherwise the executor will be used
    */
   private final Executor executor;

   private final AtomicInteger uses = new AtomicInteger(0);

   public ReferenceCounterUtil(Runnable runnable) {
      this(runnable, null);
   }

   public ReferenceCounterUtil(Runnable runnable, Executor executor) {
      this.runnable = runnable;
      this.executor = executor;
   }

   @Override
   public int increment() {
      return uses.incrementAndGet();
   }

   @Override
   public int decrement() {
      int value = uses.decrementAndGet();
      if (value == 0) {
         if (executor != null) {
            executor.execute(runnable);
         } else {
            runnable.run();
         }
      }

      return value;
   }
}