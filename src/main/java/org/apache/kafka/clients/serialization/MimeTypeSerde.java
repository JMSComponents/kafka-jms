package org.apache.kafka.clients.serialization;

import javax.activation.MimeType;
import javax.jms.Message;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

/**
 * Created by pearcem on 24/04/2017.
 */
public class MimeTypeSerde<T extends Message> implements Deserializer<T>, Serializer<T> {

    private final Serializer<T> serializer;
    private final Deserializer<T> deserializer;
    private final MimeType mimeType;

    
    public MimeTypeSerde(MimeType mimeType, Serializer<T> serializer, Deserializer<T> deserializer){
        this.mimeType = mimeType;
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        serializer.configure(configs, isKey);
        deserializer.configure(configs, isKey);
    }

    @Override
    public byte[] serialize(String topic, T data) {
        return serialize(topic, data);
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        return deserialize(topic, data);
    }
    
    public MimeType getMimeType(){
        return mimeType;
    }

    @Override
    public void close() {
        serializer.close();
        deserializer.close();
    }
}
