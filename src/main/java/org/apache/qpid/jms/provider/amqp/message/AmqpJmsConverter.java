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
package org.apache.qpid.jms.provider.amqp.message;

import static org.apache.qpid.jms.provider.amqp.message.AmqpMessageSupport.JMS_BYTES_MESSAGE;
import static org.apache.qpid.jms.provider.amqp.message.AmqpMessageSupport.JMS_MAP_MESSAGE;
import static org.apache.qpid.jms.provider.amqp.message.AmqpMessageSupport.JMS_MESSAGE;
import static org.apache.qpid.jms.provider.amqp.message.AmqpMessageSupport.JMS_MSG_TYPE;
import static org.apache.qpid.jms.provider.amqp.message.AmqpMessageSupport.JMS_OBJECT_MESSAGE;
import static org.apache.qpid.jms.provider.amqp.message.AmqpMessageSupport.JMS_STREAM_MESSAGE;
import static org.apache.qpid.jms.provider.amqp.message.AmqpMessageSupport.JMS_TEXT_MESSAGE;
import static org.apache.qpid.jms.provider.amqp.message.AmqpMessageSupport.OCTET_STREAM_CONTENT_TYPE;
import static org.apache.qpid.jms.provider.amqp.message.AmqpMessageSupport.SERIALIZED_JAVA_OBJECT_CONTENT_TYPE;
import static org.apache.qpid.jms.provider.amqp.message.AmqpMessageSupport.isContentType;

import javax.jms.JMSException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.qpid.jms.JmsTopic;
import org.apache.qpid.jms.message.JmsBytesMessage;
import org.apache.qpid.jms.message.JmsMessage;
import org.apache.qpid.jms.message.facade.JmsMessageFacade;
import org.apache.qpid.jms.meta.JmsConnectionId;
import org.apache.qpid.jms.meta.JmsConnectionInfo;
import org.apache.qpid.jms.provider.amqp.AmqpConnection;
import org.apache.qpid.jms.provider.amqp.AmqpProvider;
import org.apache.qpid.jms.util.ContentTypeSupport;
import org.apache.qpid.jms.util.InvalidContentTypeException;
import org.apache.qpid.proton.amqp.Binary;
import org.apache.qpid.proton.amqp.Symbol;
import org.apache.qpid.proton.amqp.messaging.AmqpSequence;
import org.apache.qpid.proton.amqp.messaging.AmqpValue;
import org.apache.qpid.proton.amqp.messaging.ApplicationProperties;
import org.apache.qpid.proton.amqp.messaging.Data;
import org.apache.qpid.proton.amqp.messaging.DeliveryAnnotations;
import org.apache.qpid.proton.amqp.messaging.Footer;
import org.apache.qpid.proton.amqp.messaging.Header;
import org.apache.qpid.proton.amqp.messaging.MessageAnnotations;
import org.apache.qpid.proton.amqp.messaging.Properties;
import org.apache.qpid.proton.amqp.messaging.Section;
import org.apache.qpid.proton.codec.AMQPDefinedTypes;
import org.apache.qpid.proton.codec.DecoderImpl;
import org.apache.qpid.proton.codec.EncoderImpl;
import org.apache.qpid.proton.codec.WritableBuffer;
import org.apache.qpid.proton.message.Message;

/**
 * AMQP Codec class used to hide the details of encode / decode
 */
public final class AmqpJmsConverter
{

    /**
     * Given a Message instance, encode the Message to the wire level representation
     * of that Message.
     *
     * @param message
     *      the Message that is converted to AMQP equivalent
     *
     * @return a buffer containing the wire level representation of the input Message.
     */
    public static Message toAmqpMessage(javax.jms.Message message) throws JMSException
    {

        JmsMessage jmsMessage;
        if (JmsMessage.class.isAssignableFrom(message.getClass())){
            jmsMessage = (JmsMessage) message;
        } else {
            jmsMessage = JmsMessageTransformation.transformMessage(message);
        }
        if (jmsMessage instanceof JmsBytesMessage){
            ((JmsBytesMessage) jmsMessage).reset();
        }
        
        JmsMessageFacade jmsMessageFacade = jmsMessage.getFacade();
        if (AmqpJmsMessageFacade.class.isAssignableFrom(jmsMessageFacade.getClass())){
            AmqpJmsMessageFacade amqpJmsMessageFacade = (AmqpJmsMessageFacade) jmsMessageFacade;
            Message amqpMessage = Message.Factory.create();
            amqpMessage.setHeader(amqpJmsMessageFacade.getHeader());
            amqpMessage.setDeliveryAnnotations(amqpJmsMessageFacade.getDeliveryAnnotations());
            amqpMessage.setMessageAnnotations(amqpJmsMessageFacade.getMessageAnnotations());
            amqpMessage.setProperties(amqpJmsMessageFacade.getProperties());
            amqpMessage.setApplicationProperties(amqpJmsMessageFacade.getApplicationProperties());
            amqpMessage.setBody(amqpJmsMessageFacade.getBody());
            amqpMessage.setFooter(amqpJmsMessageFacade.getFooter());
            if (amqpJmsMessageFacade.getDestination() != null)
                amqpMessage.setAddress(amqpJmsMessageFacade.getDestination().getName());
            return amqpMessage;
        }

        throw new JMSException("Could not create a JMS message from incoming message");

    }

    /**
     * Create a new JmsMessage and underlying JmsMessageFacade that represents the proper
     * message type for the incoming AMQP message.
     *
     * @param message
     *      the Message that is converted to JMS equivalent
     *      
     * @return a JmsMessage instance decoded from the message bytes.
     *
     * @throws JMSException if an error occurs while creating the message objects.
     */
    public static javax.jms.Message toJmsMessage(Message message) throws JMSException {
        
        // First we try the easy way, if the annotation is there we don't have to work hard.
        AmqpJmsMessageFacade result = createFromMsgAnnotation(message.getMessageAnnotations());
        if (result == null) {
            // Next, match specific section structures and content types
            result = createWithoutAnnotation(message.getBody(), message.getProperties());
        }

        if (result != null) {
            result.initialize(new AmqpConnection(new AmqpProvider(null, null), new JmsConnectionInfo(new JmsConnectionId("blank")), null));
            result.setHeader(message.getHeader());
            result.setDeliveryAnnotations(message.getDeliveryAnnotations());
            result.setMessageAnnotations(message.getMessageAnnotations());
            result.setProperties(message.getProperties());
            result.setApplicationProperties(message.getApplicationProperties());
            result.setBody(message.getBody());
            result.setFooter(message.getFooter());
            result.setDestination(new JmsTopic(message.getAddress()));
            return result.asJmsMessage();
        }

        throw new JMSException("Could not create a JMS message from incoming message");
    }

    private static AmqpJmsMessageFacade createFromMsgAnnotation(MessageAnnotations messageAnnotations) throws JMSException {
        Object annotation = AmqpMessageSupport.getMessageAnnotation(JMS_MSG_TYPE, messageAnnotations);
        if (annotation != null) {
            switch ((byte) annotation) {
                case JMS_MESSAGE:
                    return new AmqpJmsMessageFacade();
                case JMS_BYTES_MESSAGE:
                    return new AmqpJmsBytesMessageFacade();
                case JMS_TEXT_MESSAGE:
                    return new AmqpJmsTextMessageFacade(StandardCharsets.UTF_8);
                case JMS_MAP_MESSAGE:
                    return new AmqpJmsMapMessageFacade();
                case JMS_STREAM_MESSAGE:
                    return new AmqpJmsStreamMessageFacade();
                case JMS_OBJECT_MESSAGE:
                    return new AmqpJmsObjectMessageFacade();
                default:
                    throw new JMSException("Invalid JMS Message Type annotation value found in message: " + annotation);
            }
        }

        return null;
    }

    private static AmqpJmsMessageFacade createWithoutAnnotation(Section body, Properties properties) {
        Symbol messageContentType = properties != null ? properties.getContentType() : null;

        if (body == null) {
            if (isContentType(SERIALIZED_JAVA_OBJECT_CONTENT_TYPE, messageContentType)) {
                return new AmqpJmsObjectMessageFacade();
            } else if (isContentType(OCTET_STREAM_CONTENT_TYPE, messageContentType) || isContentType(null, messageContentType)) {
                return new AmqpJmsBytesMessageFacade();
            } else {
                Charset charset = getCharsetForTextualContent(messageContentType);
                if (charset != null) {
                    return new AmqpJmsTextMessageFacade(charset);
                } else {
                    return new AmqpJmsMessageFacade();
                }
            }
        } else if (body instanceof Data) {
            if (isContentType(OCTET_STREAM_CONTENT_TYPE, messageContentType) || isContentType(null, messageContentType)) {
                return new AmqpJmsBytesMessageFacade();
            } else if (isContentType(SERIALIZED_JAVA_OBJECT_CONTENT_TYPE, messageContentType)) {
                return new AmqpJmsObjectMessageFacade();
            } else {
                Charset charset = getCharsetForTextualContent(messageContentType);
                if (charset != null) {
                    return new AmqpJmsTextMessageFacade(charset);
                } else {
                    return new AmqpJmsBytesMessageFacade();
                }
            }
        } else if (body instanceof AmqpValue) {
            Object value = ((AmqpValue) body).getValue();

            if (value == null || value instanceof String) {
                return new AmqpJmsTextMessageFacade(StandardCharsets.UTF_8);
            } else if (value instanceof Binary) {
                return new AmqpJmsBytesMessageFacade();
            } else {
                return new AmqpJmsObjectMessageFacade();
            }
        } else if (body instanceof AmqpSequence) {
            return new AmqpJmsObjectMessageFacade();
        }

        return null;
    }

    private static Charset getCharsetForTextualContent(Symbol messageContentType) {
        if (messageContentType != null) {
            try {
                return ContentTypeSupport.parseContentTypeForTextualCharset(messageContentType.toString());
            } catch (InvalidContentTypeException e) {
            }
        }

        return null;
    }
}
