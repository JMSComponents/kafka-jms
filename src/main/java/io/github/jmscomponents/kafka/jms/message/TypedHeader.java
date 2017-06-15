/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.jmscomponents.kafka.jms.message;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import io.github.jmscomponents.kafka.serialization.Header;

public class TypedHeader implements Header {

    public static final int SIZE_INT = 4;

    public static final int SIZE_BOOLEAN = 1;

    public static final int SIZE_LONG = 8;

    public static final int SIZE_BYTE = 1;

    public static final int SIZE_SHORT = 2;

    public static final int SIZE_DOUBLE = 8;

    public static final int SIZE_FLOAT = 4;

    public static final int SIZE_CHAR = 2;
    
    public static final byte TRUE = 1;

    public static final byte FALSE = 0;
    
    public static final byte BOOLEAN = 0;

    public static final byte BYTE = 1;
    
    public static final byte SHORT = 2;

    public static final byte INT = 3;

    public static final byte LONG = 4;

    public static final byte FLOAT = 5;

    public static final byte DOUBLE = 6;

    public static final byte CHAR = 7;
    
    public static final byte STRING = 8;
    
    public static final byte[] MAGIC = new byte[]{0,'J','M','S','E','D'}; 

    
    private final Header header;
    
    public TypedHeader(Header header) {
        this.header = header;
    }

    public TypedHeader(String key, ByteBuffer byteBuffer) {
        this(new RecordHeader(key, byteBuffer));
    }

    public TypedHeader(String key, Object value) {
        this(new RecordHeader(key, value == null ? null : toByteBuffer(value)));
    }
    
    public byte[] value(){
        return header.value();
    }

    public String key(){
        return header.key();
    }
    
    public Object valueObject(){
        byte[] value = header.value();
        return value == null ? null : toObject(ByteBuffer.wrap(value));
    }
    
    public static ByteBuffer toByteBuffer(Object value){
        if (value == null) {
            return null;
        } else if (value instanceof Boolean) {
            return ByteBuffer.allocate(MAGIC.length + SIZE_BYTE + SIZE_BOOLEAN).put(MAGIC).put(BOOLEAN).put(value == Boolean.TRUE ? TRUE : FALSE);
        } else if (value instanceof Byte) {
            return ByteBuffer.allocate(MAGIC.length + SIZE_BYTE + SIZE_BYTE).put(MAGIC).put(BYTE).put(((Byte) value));
        } else if (value instanceof Character) {
            return ByteBuffer.allocate(MAGIC.length + SIZE_BYTE + SIZE_CHAR).put(MAGIC).put(CHAR).putChar(((Character) value));
        } else if (value instanceof Short) {
            return ByteBuffer.allocate(MAGIC.length + SIZE_BYTE + SIZE_SHORT).put(MAGIC).put(SHORT).putShort(((Short) value));
        } else if (value instanceof Integer) {
            return ByteBuffer.allocate(MAGIC.length + SIZE_BYTE + SIZE_INT).put(MAGIC).put(INT).putInt(((Integer) value));
        } else if (value instanceof Long) {
            return ByteBuffer.allocate(MAGIC.length + SIZE_BYTE + SIZE_LONG).put(MAGIC).put(LONG).putLong(((Long) value));
        } else if (value instanceof Float) {
            return ByteBuffer.allocate(MAGIC.length + SIZE_BYTE + SIZE_FLOAT).put(MAGIC).put(FLOAT).putFloat(((Float) value));
        } else if (value instanceof Double) {
            return ByteBuffer.allocate(MAGIC.length + SIZE_BYTE + SIZE_DOUBLE).put(MAGIC).put(DOUBLE).putDouble(((Double) value));
        } else if (value instanceof String) {
            return ByteBuffer.allocate(MAGIC.length + SIZE_BYTE + ((String) value).length()).put(MAGIC).put(STRING).put(((String) value).getBytes(StandardCharsets.UTF_8));
        } else if (value instanceof byte[]) {
            return ByteBuffer.allocate(((byte[]) value).length).put((byte[])value);
        } else {
            throw new IllegalArgumentException(value.getClass() + " is not a valid property type");
        }
    }

    
    public static Object toObject(ByteBuffer byteBuffer){
        if (byteBuffer == null) return null;
        if (byteBuffer.limit() > 6 &&
            byteBuffer.get(0) == MAGIC[0] &&
            byteBuffer.get(1) == MAGIC[1] &&
            byteBuffer.get(2) == MAGIC[2] &&
            byteBuffer.get(3) == MAGIC[3] &&
            byteBuffer.get(4) == MAGIC[4] &&
            byteBuffer.get(5) == MAGIC[5]) {
            byteBuffer.position(6);
            System.out.println(new String(MAGIC, StandardCharsets.UTF_8));
        } else {
            return readRemaining(byteBuffer);
        }
        byte type = byteBuffer.get();
        switch (type) {
            case BOOLEAN:
                return byteBuffer.get() == TRUE ? Boolean.TRUE : Boolean.FALSE;
            case BYTE:
                return byteBuffer.get();
            case CHAR:
                return byteBuffer.getChar();
            case DOUBLE:
                return byteBuffer.getDouble();
            case FLOAT:
                return byteBuffer.getFloat();
            case LONG:
                return byteBuffer.getLong();
            case INT:
                return byteBuffer.getInt();
            case SHORT:
                return byteBuffer.getShort();
            case STRING:
                return new String(readRemaining(byteBuffer), StandardCharsets.UTF_8);
            default:
                throw new IllegalStateException("Invalid property type: " + type);
        }
    }
    
    private static byte[] readRemaining(ByteBuffer byteBuffer){
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);
        return bytes;
    }
    
    
    
    @Override
    public String toString() {
        return "TypedHeader(key = " + header.key() + ", value = " + valueObject() + ")";
    }

}
