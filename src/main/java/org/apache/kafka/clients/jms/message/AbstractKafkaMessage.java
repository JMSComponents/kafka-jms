package org.apache.kafka.clients.jms.message;

import static org.apache.kafka.clients.jms.message.JmsMessagePropertySupport.checkValidObject;
import static org.apache.kafka.clients.jms.message.JmsMessagePropertySupport.convertPropertyTo;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageFormatException;
import javax.jms.Queue;
import javax.jms.Topic;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.kafka.clients.serialization.Headers;

/**
 * Created by pearcem on 25/04/2017.
 */
public abstract class AbstractKafkaMessage<T> implements Message {

    public static final String JMS_DESTINATION = "JMSDestination";
    public static final String JMS_REPLYTO = "JMSReplyTo";
    public static final String JMS_TYPE = "JMSType";
    public static final String JMS_DELIVERY_MODE = "JMSDeliveryMode";
    public static final String JMS_PRIORITY = "JMSPriority";
    public static final String JMS_MESSAGEID = "JMSMessageID";
    public static final String JMS_TIMESTAMP = "JMSTimestamp";
    public static final String JMS_CORRELATIONID = "JMSCorrelationID";
    public static final String JMS_EXPIRATION = "JMSExpiration";
    public static final String JMS_REDELIVERED = "JMSRedelivered";
    public static final String JMS_DELIVERYTIME = "JMSDeliveryTime";
    
    public static final List<String> JMS_HEADERS = Arrays.asList(
        JMS_DESTINATION, 
        JMS_REPLYTO,
        JMS_TYPE,
        JMS_DELIVERY_MODE,
        JMS_PRIORITY,
        JMS_MESSAGEID,
        JMS_TIMESTAMP,
        JMS_CORRELATIONID,
        JMS_EXPIRATION,
        JMS_REDELIVERED,
        JMS_DELIVERY_MODE
    );

    public static final String JMSX_GROUPID = "JMSXGroupID";
    public static final String JMSX_GROUPSEQ = "JMSXGroupSeq";
    public static final String JMSX_DELIVERY_COUNT = "JMSXDeliveryCount";
    public static final String JMSX_USERID = "JMSXUserID";

    private final Map<String, Object> properties = new HashMap<>();
    
    protected T body;
    
    public AbstractKafkaMessage(){
    }
    
    @Override
    public String getJMSMessageID() throws JMSException {
        return getStringProperty(JMS_MESSAGEID);
    }

    @Override
    public void setJMSMessageID(String s) throws JMSException {
        setStringProperty(JMS_MESSAGEID, s);
    }

    @Override
    public long getJMSTimestamp() throws JMSException {
        return getLongProperty(JMS_TIMESTAMP);
    }

    @Override
    public void setJMSTimestamp(long l) throws JMSException {
        setLongProperty(JMS_TIMESTAMP, l);
    }

    @Override
    public byte[] getJMSCorrelationIDAsBytes() throws JMSException {
        return getJMSCorrelationID().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void setJMSCorrelationIDAsBytes(byte[] bytes) throws JMSException {
        setJMSCorrelationID(new String(bytes, StandardCharsets.UTF_8));
    }

    @Override
    public void setJMSCorrelationID(String s) throws JMSException {
        setStringProperty(JMS_CORRELATIONID, s);
    }

    @Override
    public String getJMSCorrelationID() throws JMSException {
        return getStringProperty(JMS_CORRELATIONID);
    }

    @Override
    public Destination getJMSReplyTo() throws JMSException {
        String destinationName = getStringProperty(JMS_REPLYTO);
        Destination destination = null;
        if (destinationName != null){
            destination = new org.apache.kafka.clients.jms.Topic(destinationName);
        }
        return destination;
    }

    @Override
    public void setJMSReplyTo(Destination destination) throws JMSException {
        if (destination instanceof Queue) {
            setStringProperty(JMS_REPLYTO, ((Queue) destination).getQueueName());
        } else if (destination instanceof Topic){
            setStringProperty(JMS_REPLYTO, ((Topic) destination).getTopicName());
        }
    }

    @Override
    public Destination getJMSDestination() throws JMSException {
        String destinationName = getStringProperty(JMS_DESTINATION);
        Destination destination = null;
        if (destinationName != null){
            destination = new org.apache.kafka.clients.jms.Topic(destinationName);
        }
        return destination;
        
    }

    @Override
    public void setJMSDestination(Destination destination) throws JMSException {
        if (destination instanceof Queue) {
            setStringProperty(JMS_DESTINATION, ((Queue) destination).getQueueName());
        } else if (destination instanceof Topic){
            setStringProperty(JMS_DESTINATION, ((Topic) destination).getTopicName());
        }
    }

    @Override
    public int getJMSDeliveryMode() throws JMSException {
        return getIntProperty(JMS_DELIVERY_MODE);
    }

    @Override
    public void setJMSDeliveryMode(int i) throws JMSException {
        setIntProperty(JMS_DELIVERY_MODE, i);
    }

    @Override
    public boolean getJMSRedelivered() throws JMSException {
        return getBooleanProperty(JMS_REDELIVERED);
    }

    @Override
    public void setJMSRedelivered(boolean b) throws JMSException {
        setBooleanProperty(JMS_REDELIVERED, b);
    }

    @Override
    public String getJMSType() throws JMSException {
        return getStringProperty(JMS_TYPE);
    }

    @Override
    public void setJMSType(String s) throws JMSException {
        setStringProperty(JMS_TYPE, s);
    }

    @Override
    public long getJMSExpiration() throws JMSException {
        return getLongProperty(JMS_EXPIRATION);
    }

    @Override
    public void setJMSExpiration(long l) throws JMSException {
        setLongProperty(JMS_EXPIRATION, l);
    }

    @Override
    public int getJMSPriority() throws JMSException {
        return getIntProperty(JMS_PRIORITY);
    }

    @Override
    public void setJMSPriority(int i) throws JMSException {
        setIntProperty(JMS_PRIORITY, i);
    }

    @Override
    public void clearProperties() throws JMSException {
        properties.clear();
    }

    @Override
    public boolean propertyExists(String s) throws JMSException {
        return properties.containsKey(s);
    }

    @Override
    public Enumeration getPropertyNames() throws JMSException {
        Set<String> propertyNames = properties.keySet()
                                           .stream()
                                           .filter(name -> !JMS_HEADERS.contains(name))
                                           .collect(Collectors.toSet());
        return Collections.enumeration(propertyNames);
    }

    @Override
    public void setObjectProperty(String name, Object value) throws JMSException {
        checkValidObject(value);
        properties.put(name, value);
    }

    @Override
    public Object getObjectProperty(String name) throws JMSException {
        return properties.get(name);
    }

    @Override
    public boolean getBooleanProperty(String name) throws JMSException {
        return convertPropertyTo(name, getObjectProperty(name), Boolean.class);
    }

    @Override
    public byte getByteProperty(String name) throws JMSException {
        return convertPropertyTo(name, getObjectProperty(name), Byte.class);
    }

    @Override
    public short getShortProperty(String name) throws JMSException {
        return convertPropertyTo(name, getObjectProperty(name), Short.class);
    }

    @Override
    public int getIntProperty(String name) throws JMSException {
        return convertPropertyTo(name, getObjectProperty(name), Integer.class);
    }

    @Override
    public long getLongProperty(String name) throws JMSException {
        return convertPropertyTo(name, getObjectProperty(name), Long.class);
    }

    @Override
    public float getFloatProperty(String name) throws JMSException {
        return convertPropertyTo(name, getObjectProperty(name), Float.class);
    }

    @Override
    public double getDoubleProperty(String name) throws JMSException {
        return convertPropertyTo(name, getObjectProperty(name), Double.class);
    }

    @Override
    public String getStringProperty(String name) throws JMSException {
        return convertPropertyTo(name, getObjectProperty(name), String.class);
    }

    @Override
    public void setBooleanProperty(String name, boolean value) throws JMSException {
        setObjectProperty(name, Boolean.valueOf(value));
    }

    @Override
    public void setByteProperty(String name, byte value) throws JMSException {
        setObjectProperty(name, Byte.valueOf(value));
    }

    @Override
    public void setShortProperty(String name, short value) throws JMSException {
        setObjectProperty(name, Short.valueOf(value));
    }

    @Override
    public void setIntProperty(String name, int value) throws JMSException {
        setObjectProperty(name, Integer.valueOf(value));
    }

    @Override
    public void setLongProperty(String name, long value) throws JMSException {
        setObjectProperty(name, Long.valueOf(value));
    }

    @Override
    public void setFloatProperty(String name, float value) throws JMSException {
        setObjectProperty(name, new Float(value));
    }

    @Override
    public void setDoubleProperty(String name, double value) throws JMSException {
        setObjectProperty(name, new Double(value));
    }

    @Override
    public void setStringProperty(String name, String value) throws JMSException {
        setObjectProperty(name, value);
    }


    @Override
    public void acknowledge() throws JMSException {
        //NO-OP.
    }

    @Override
    public void clearBody() throws JMSException {
        body = null;
    }

    @Override
    public long getJMSDeliveryTime() throws JMSException {
        return getLongProperty(JMS_DELIVERYTIME);
    }

    @Override
    public void setJMSDeliveryTime(long deliveryTime) throws JMSException {
        setLongProperty(JMS_DELIVERYTIME, deliveryTime);
    }

    @Override
    public <T> T getBody(Class<T> c) throws JMSException {
        if (isBodyAssignableTo(c)){
            return (T) body;
        } else {
            throw new MessageFormatException("Message body cannot be read as type: " + c);
        }
    }

    @Override
    public boolean isBodyAssignableTo(Class c) throws JMSException {
        if (body == null) {
            return true;
        } else {
          return c.isAssignableFrom(body.getClass());  
        }
    }
}
