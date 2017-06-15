package io.github.jmscomponents.kafka.jms.exception;

/**
 * A PropertyConversionException is thrown by {@code javax.jms.Message} methods when a
 * property can not be converted to the expected type.
 */
public final class PropertyConversionException extends RuntimeException {

   private static final long serialVersionUID = -3010008708334904332L;

   public PropertyConversionException(final String message) {
      super(message);
   }
}