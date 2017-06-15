/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.jmscomponents.kafka.jms.message;

import java.util.HashMap;
import java.util.Set;

import io.github.jmscomponents.kafka.jms.exception.PropertyConversionException;

/**
 * Property Value Conversion.
 * <p>
 * This implementation follows section 3.5.4 of the <i>Java Message Service</i> specification
 * (Version 1.1 April 12, 2002).
 * <p>
 */
public class TypedProperties extends HashMap<String, Object> {

   public void putBooleanProperty(final String key, final boolean value) {
      doPutValue(key, value);
   }

   public void putByteProperty(final String key, final byte value) {
      doPutValue(key, value);
   }

   public void putBytesProperty(final String key, final byte[] value) {
      doPutValue(key, value);
   }

   public void putShortProperty(final String key, final short value) {
      doPutValue(key, value);
   }

   public void putIntProperty(final String key, final int value) {
      doPutValue(key, value);
   }

   public void putLongProperty(final String key, final long value) {
      doPutValue(key, value);
   }

   public void putFloatProperty(final String key, final float value) {
      doPutValue(key, value);
   }

   public void putDoubleProperty(final String key, final double value) {
      doPutValue(key, value);
   }

   public void putStringProperty(final String key, final String value) {
      doPutValue(key, value);
   }

   public void putNullValue(final String key) {
      doPutValue(key, null);
   }

   public void putCharProperty(final String key, final char value) {
      doPutValue(key, value);
   }

   public void putTypedProperties(final TypedProperties otherProps) {
      if (otherProps == null) {
         return;
      }
      putAll(otherProps);
   }

   public Object getProperty(final String key) {
      return doGetProperty(key);
   }

   public Boolean getBooleanProperty(final String key) throws PropertyConversionException {
      Object value = doGetProperty(key);
      if (value == null) {
         return Boolean.valueOf(null);
      } else if (value instanceof Boolean) {
         return (Boolean) value;
      } else if (value instanceof String) {
         return Boolean.valueOf(((String) value).toString());
      }
      throw new PropertyConversionException("Invalid conversion: " + key);
   }

   public Byte getByteProperty(final String key) throws PropertyConversionException {
      Object value = doGetProperty(key);
      if (value == null) {
         return Byte.valueOf(null);
      } else if (value instanceof Byte) {
         return (Byte) value;
      } else if (value instanceof String) {
         return Byte.parseByte(((String) value).toString());
      }
      throw new PropertyConversionException("Invalid conversion: " + key);
   }

   public Character getCharProperty(final String key) throws PropertyConversionException {
      Object value = doGetProperty(key);
      if (value == null) {
         throw new NullPointerException("Invalid conversion: " + key);
      }

      if (value instanceof Character) {
         return ((Character) value);
      }
      throw new PropertyConversionException("Invalid conversion: " + key);
   }

   public byte[] getBytesProperty(final String key) throws PropertyConversionException {
      Object value = doGetProperty(key);
      if (value == null) {
         return null;
      } else if (value instanceof byte[]) {
         return (byte[]) value;
      }
      throw new PropertyConversionException("Invalid conversion: " + key);
   }

   public Double getDoubleProperty(final String key) throws PropertyConversionException {
      Object value = doGetProperty(key);
      if (value == null) {
         return Double.valueOf(null);
      } else if (value instanceof Float) {
         return ((Float) value).doubleValue();
      } else if (value instanceof Double) {
         return (Double) value;
      } else if (value instanceof String) {
         return Double.parseDouble(((String) value).toString());
      }
      throw new PropertyConversionException("Invalid conversion: " + key);
   }

   public Integer getIntProperty(final String key) throws PropertyConversionException {
      Object value = doGetProperty(key);
      if (value == null) {
         return Integer.valueOf(null);
      } else if (value instanceof Integer) {
         return (Integer) value;
      } else if (value instanceof Byte) {
         return ((Byte) value).intValue();
      } else if (value instanceof Short) {
         return ((Short) value).intValue();
      } else if (value instanceof String) {
         return Integer.parseInt(((String) value).toString());
      }
      throw new PropertyConversionException("Invalid conversion: " + key);
   }

   public Long getLongProperty(final String key) throws PropertyConversionException {
      Object value = doGetProperty(key);
      if (value == null) {
         return Long.valueOf(null);
      } else if (value instanceof Long) {
         return (Long) value;
      } else if (value instanceof Byte) {
         return ((Byte) value).longValue();
      } else if (value instanceof Short) {
         return ((Short) value).longValue();
      } else if (value instanceof Integer) {
         return ((Integer) value).longValue();
      } else if (value instanceof String) {
         return Long.parseLong(((String) value).toString());
      }
      throw new PropertyConversionException("Invalid conversion: " + key);
   }

   public Short getShortProperty(final String key) throws PropertyConversionException {
      Object value = doGetProperty(key);
      if (value == null) {
         return Short.valueOf(null);
      } else if (value instanceof Byte) {
         return ((Byte) value).shortValue();
      } else if (value instanceof Short) {
         return (Short) value;
      } else if (value instanceof String) {
         return Short.parseShort(((String) value).toString());
      }
      throw new PropertyConversionException("Invalid conversion: " + key);
   }

   public Float getFloatProperty(final String key) throws PropertyConversionException {
      Object value = doGetProperty(key);
      if (value == null)
         return Float.valueOf(null);
      if (value instanceof Float) {
         return ((Float) value);
      }
      if (value instanceof String) {
         return Float.parseFloat(((String) value).toString());
      }
      throw new PropertyConversionException("Invalid conversion: " + key);
   }

   public String getStringProperty(final String key) throws PropertyConversionException {
      Object value = doGetProperty(key);

      if (value == null) {
         return null;
      }

      if (value instanceof String) {
         return (String) value;
      } else if (value instanceof Boolean) {
         return value.toString();
      } else if (value instanceof Character) {
         return value.toString();
      } else if (value instanceof Byte) {
         return value.toString();
      } else if (value instanceof Short) {
         return value.toString();
      } else if (value instanceof Integer) {
         return value.toString();
      } else if (value instanceof Long) {
         return value.toString();
      } else if (value instanceof Float) {
         return value.toString();
      } else if (value instanceof Double) {
         return value.toString();
      }
      throw new PropertyConversionException("Invalid conversion: " + key);
   }

   public Object removeProperty(final String key) {
      return doRemoveProperty(key);
   }

   public boolean containsProperty(final String key) {
     return containsKey(key);
   }

   public Set<String> getPropertyNames() {
      return keySet();
   }

   private synchronized void doPutValue(final String key, final Object value) {
      put(key, value);
   }

   private synchronized Object doRemoveProperty(final String key) {
      return remove(key);
   }

   private synchronized Object doGetProperty(final Object key) {
      return get(key);
   }
   
   /**
    * Helper for MapMessage#setObjectProperty(String, Object)
    *
    * @param key        The String key
    * @param value      The Object value
    * @param properties The typed properties
    */
   public static void setObjectProperty(final String key, final Object value, final TypedProperties properties) {
      if (value == null) {
         properties.putNullValue(key);
      } else if (value instanceof Boolean) {
         properties.putBooleanProperty(key, (Boolean) value);
      } else if (value instanceof Byte) {
         properties.putByteProperty(key, (Byte) value);
      } else if (value instanceof Character) {
         properties.putCharProperty(key, (Character) value);
      } else if (value instanceof Short) {
         properties.putShortProperty(key, (Short) value);
      } else if (value instanceof Integer) {
         properties.putIntProperty(key, (Integer) value);
      } else if (value instanceof Long) {
         properties.putLongProperty(key, (Long) value);
      } else if (value instanceof Float) {
         properties.putFloatProperty(key, (Float) value);
      } else if (value instanceof Double) {
         properties.putDoubleProperty(key, (Double) value);
      } else if (value instanceof String) {
         properties.putStringProperty(key, (String) value);
      } else if (value instanceof byte[]) {
         properties.putBytesProperty(key, (byte[]) value);
      } else {
         throw new PropertyConversionException(value.getClass() + " is not a valid property type");
      }
   }
}
