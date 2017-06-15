package io.github.jmscomponents.kafka.jms.util;

public interface ReferenceCounter {

   int increment();

   int decrement();
}