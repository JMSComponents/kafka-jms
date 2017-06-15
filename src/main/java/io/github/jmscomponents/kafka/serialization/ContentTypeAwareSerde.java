package io.github.jmscomponents.kafka.serialization;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

/**
 * Created by pearcem on 24/04/2017.
 */
public class ContentTypeAwareSerde implements ExtendedSerializer<Message>, ExtendedDeserializer<Message>  {


    public static final String CONTENT_TYPE_HEADER = "content-type";

    public static final String JMS_MSG_TYPE_HEADER = "x-opt-jms-msg-type";

    /**
     * Value mapping for JMS_MSG_TYPE which indicates the message is a generic JMS Message
     * which has no body.
     */
    public static final byte JMS_MESSAGE = 0;

    /**
     * Value mapping for JMS_MSG_TYPE which indicates the message is a JMS ObjectMessage
     * which has an Object value serialized in its message body.
     */
    public static final byte JMS_OBJECT_MESSAGE = 1;

    /**
     * Value mapping for JMS_MSG_TYPE which indicates the message is a JMS MapMessage
     * which has an Map instance serialized in its message body.
     */
    public static final byte JMS_MAP_MESSAGE = 2;

    /**
     * Value mapping for JMS_MSG_TYPE which indicates the message is a JMS BytesMessage
     * which has a body that consists of raw bytes.
     */
    public static final byte JMS_BYTES_MESSAGE = 3;

    /**
     * Value mapping for JMS_MSG_TYPE which indicates the message is a JMS StreamMessage
     * which has a body that is a structured collection of primitives values.
     */
    public static final byte JMS_STREAM_MESSAGE = 4;

    /**
     * Value mapping for JMS_MSG_TYPE which indicates the message is a JMS TextMessage
     * which has a body that contains a UTF-8 encoded String.
     */
    public static final byte JMS_TEXT_MESSAGE = 5;


    private MimeTypeSerde<Void> messageSerde;
    private MimeTypeSerde<byte[]> bytesMessageSerde;
    private MimeTypeSerde<String> textMessageSerde;
    private MimeTypeSerde<Map> mapMessageSerde;
    private MimeTypeSerde<List> streamMessageSerde;
    private MimeTypeSerde<Object> objectMessageSerde;


    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        messageSerde.configure(configs, isKey);
        bytesMessageSerde.configure(configs, isKey);
        textMessageSerde.configure(configs, isKey);
        mapMessageSerde.configure(configs, isKey);
        streamMessageSerde.configure(configs, isKey);
        objectMessageSerde.configure(configs, isKey);
    }

    @Override
    public byte[] serialize(String topic, Message data) {
        return messageSerde.serializer().serialize(topic, data);
    }

    @Override
    public Message deserialize(String topic, byte[] data) {
        return messageSerde.deserializer().deserialize(topic, data);
    }



    @Override
    public byte[] serialize(String topic, Headers headers, Message data) throws JMSException {

        byte[] bytes;
        if (data instanceof BytesMessage) {
            headers.add(JMS_MSG_TYPE_HEADER, new byte[]{JMS_BYTES_MESSAGE});
            ((BytesMessage) data).reset();
            byte[] body = new byte[(int)((BytesMessage) data).getBodyLength()];
            ((BytesMessage) data).readBytes(body);
            bytes = serialize(bytesMessageSerde, topic, headers, body);
        } else if (data instanceof MapMessage) {
            headers.add(JMS_MSG_TYPE_HEADER, new byte[]{JMS_MAP_MESSAGE});
            Enumeration names = ((MapMessage) data).getMapNames();
            Map map = new HashMap();
            while(names.hasMoreElements()){
                String name = (String)names.nextElement();
                map.put(name, ((MapMessage) data).getObject(name);
            }
            bytes = serialize(mapMessageSerde, topic, headers, map);
        } else if (data instanceof ObjectMessage) {
            headers.add(JMS_MSG_TYPE_HEADER, new byte[]{JMS_OBJECT_MESSAGE});
            Serializable serializable = ((ObjectMessage) data).getObject()
            bytes = serialize(objectMessageSerde, topic, headers, serializable);
        } else if (data instanceof StreamMessage) {
            headers.add(JMS_MSG_TYPE_HEADER, new byte[]{JMS_STREAM_MESSAGE});
            ((StreamMessage) data).reset();
            Object obj = null;
            List list = new ArrayList();
            while ((obj = ((StreamMessage) data).readObject() != null) {
                list.add(obj);
            }
            bytes = serialize(streamMessageSerde, topic, headers, (StreamMessage) data);
        } else if (data instanceof TextMessage) {
            headers.add(JMS_MSG_TYPE_HEADER, new byte[]{JMS_TEXT_MESSAGE});
            bytes = serialize(textMessageSerde, topic, headers, (TextMessage) ((TextMessage) data).getText());
        } else {
            headers.add(JMS_MSG_TYPE_HEADER, new byte[]{JMS_MESSAGE});
            bytes = serialize(messageSerde, topic, headers, data);
        }
        return bytes;
    }

    public <T> byte[] serialize(MimeTypeSerde<T> serde, String topic, Headers headers, T data) {
        headers.add(CONTENT_TYPE_HEADER, serde.mimeType().toString().getBytes(Charset.forName("UTF-8")));
        return serde.serializer().serialize(topic, data);
    }




    @Override
    public Message deserialize(String topic, Headers headers, byte[] data) {
        Header jmsMsgTypeHeader = headers.lastHeader(JMS_MSG_TYPE_HEADER);
        Message message;
        if (jmsMsgTypeHeader != null){
            byte jmsMsgType = jmsMsgTypeHeader.value()[0];
            switch (jmsMsgType) {
                case JMS_MESSAGE:
                    message = deserialize(messageSerde, topic, headers, data);
                    break;
                case JMS_BYTES_MESSAGE:
                    message = deserialize(bytesMessageSerde, topic, headers, data);
                    break;
                case JMS_TEXT_MESSAGE:
                    message = deserialize(textMessageSerde, topic, headers, data);
                    break;
                case JMS_MAP_MESSAGE:
                    message = deserialize(mapMessageSerde, topic, headers, data);
                    break;
                case JMS_STREAM_MESSAGE:
                    message = deserialize(streamMessageSerde, topic, headers, data);
                    break;
                case JMS_OBJECT_MESSAGE:
                    message = deserialize(objectMessageSerde, topic, headers, data);
                    break;
                default:
                    throw new SerializationException("Invalid JMS Message Type annotation value found in message: " + jmsMsgType);
            }
        } else {
            //Content type based resolution (failback https://www.oasis-open.org/committees/download.php/56418/amqp-bindmap-jms-v1.0-wd06.pdf)
            Header contentTypeHeader = headers.lastHeader(CONTENT_TYPE_HEADER);
            String contentType = contentTypeHeader == null ? null : new String(contentTypeHeader.value(), Charset.forName("UTF-8"));

            if (contentType == null) {
                message = deserialize(bytesMessageSerde, topic, headers, data);
            } else {
                try {
                    if (messageSerde.match(contentType)) {
                        message = deserialize(messageSerde, topic, headers, data);
                    } else if (textMessageSerde.match(contentType)) {
                        message = deserialize(textMessageSerde, topic, headers, data);
                    } else if (streamMessageSerde.match(contentType)) {
                        message = deserialize(streamMessageSerde, topic, headers, data);
                    } else if (objectMessageSerde.match(contentType)) {
                        message = deserialize(objectMessageSerde, topic, headers, data);
                    } else if (mapMessageSerde.match(contentType)) {
                        message = deserialize(mapMessageSerde, topic, headers, data);
                    } else {
                        message = deserialize(bytesMessageSerde, topic, headers, data);
                    }
                } catch (MimeTypeParseException e) {
                    throw new SerializationException(e.getMessage(), e);
                }
            }
        }
        return message;
    }

    public <T> T deserialize(MimeTypeSerde<T> serde, String topic, Headers headers, byte[] data) {
        return serde.deserializer().deserialize(topic, data);
    }


    public static void copyProperties(Message source, Headers headers) throws JMSException {
        target.setJMSMessageID(source.getJMSMessageID());
        target.setJMSCorrelationID(source.getJMSCorrelationID());
        target.setJMSReplyTo(transformDestination(source.getJMSReplyTo()));
        target.setJMSDestination(transformDestination(source.getJMSDestination()));
        target.setJMSDeliveryMode(source.getJMSDeliveryMode());
        target.setJMSDeliveryTime(getForeignMessageDeliveryTime(source));
        target.setJMSRedelivered(source.getJMSRedelivered());
        target.setJMSType(source.getJMSType());
        target.setJMSExpiration(source.getJMSExpiration());
        target.setJMSPriority(source.getJMSPriority());
        target.setJMSTimestamp(source.getJMSTimestamp());

        Enumeration<?> propertyNames = source.getPropertyNames();

        while (propertyNames.hasMoreElements()) {
            String name = propertyNames.nextElement().toString();
            Object obj = source.getObjectProperty(name);
            target.setObjectProperty(name, obj);
        }
    }



    @Override
    public void close() {
        messageSerde.close();
        bytesMessageSerde.close();
        textMessageSerde.close();
        mapMessageSerde.close();
        streamMessageSerde.close();
        objectMessageSerde.close();
    }

    public class MimeTypeSerde<T> implements Serde<T> {

        private final Serde<T> serde;
        private final MimeType mimeType;

        public MimeTypeSerde(Serde<T> serde, MimeType mimeType){
            this.serde = serde;
            this.mimeType = mimeType;
        }

        @Override
        public void configure(Map<String, ?> configs, boolean isKey) {
            serde.configure(configs, isKey);
        }

        @Override
        public void close() {
            serde.close();
        }

        @Override
        public Serializer<T> serializer() {
            return serde.serializer();
        }

        @Override
        public Deserializer<T> deserializer() {
            return serde.deserializer();
        }

        public MimeType mimeType(){
            return mimeType;
        }

        public boolean match(String rawdata) throws MimeTypeParseException {
            return mimeType.match(rawdata);
        }
    }

}
