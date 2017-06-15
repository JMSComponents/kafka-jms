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
package io.github.jmscomponents.kafka.jms;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;
import java.io.Serializable;

/**
 * Interface that a Provider should implement to provide a Provider
 * Specific Message implementation that optimizes the exchange of
 * message properties and payload between the JMS Message API and the
 * underlying Provider Message implementations.
 */
public interface MessageFactory {

    /**
     * Creates an instance of a basic Message object.  The provider may
     * either create the Message with the default generic internal message
     * implementation or create a Provider specific instance that optimizes
     * the access and marshaling of the message.
     *
     * @return a newly created and initialized Message instance.
     *
     * @throws JMSException if the provider cannot create the message for some reason.
     */
    Message createMessage() throws JMSException;

    /**
     * Creates an instance of a basic TextMessage object.  The provider may
     * either create the Message with the default generic internal message
     * implementation or create a Provider specific instance that optimizes
     * the access and marshaling of the message.
     *
     * @param payload
     *        The value to initially assign to the Message body, or null if empty to start.
     *
     * @return a newly created and initialized TextMessage instance.
     *
     * @throws JMSException if the provider cannot create the message for some reason.
     */
    TextMessage createTextMessage(String payload) throws JMSException;

    /**
     * Creates an instance of a basic TextMessage object.  The provider may
     * either create the Message with the default generic internal message
     * implementation or create a Provider specific instance that optimizes
     * the access and marshaling of the message.
     *
     * @return a newly created and initialized TextMessage instance.
     *
     * @throws JMSException if the provider cannot create the message for some reason.
     */
    TextMessage createTextMessage() throws JMSException;

    /**
     * Creates an instance of a basic BytesMessage object.  The provider may
     * either create the Message with the default generic internal message
     * implementation or create a Provider specific instance that optimizes
     * the access and marshaling of the message.
     *
     * @return a newly created and initialized TextMessage instance.
     *
     * @throws JMSException if the provider cannot create the message for some reason.
     */
    BytesMessage createBytesMessage() throws JMSException;

    /**
     * Creates an instance of a basic MapMessage object.  The provider may
     * either create the Message with the default generic internal message
     * implementation or create a Provider specific instance that optimizes
     * the access and marshaling of the message.
     *
     * @return a newly created and initialized TextMessage instance.
     *
     * @throws JMSException if the provider cannot create the message for some reason.
     */
    MapMessage createMapMessage() throws JMSException;

    /**
     * Creates an instance of a basic StreamMessage object.  The provider may
     * either create the Message with the default generic internal message
     * implementation or create a Provider specific instance that optimizes
     * the access and marshaling of the message.
     *
     * @return a newly created and initialized TextMessage instance.
     *
     * @throws JMSException if the provider cannot create the message for some reason.
     */
    StreamMessage createStreamMessage() throws JMSException;

    /**
     * Creates an instance of a basic ObjectMessage object.  The provider may
     * either create the Message with the default generic internal message
     * implementation or create a Provider specific instance that optimizes
     * the access and marshaling of the message.
     *
     * @param payload
     *        The value to initially assign to the Message body, or null if empty to start.
     *
     * @return a newly created and initialized ObjectMessage instance.
     *
     * @throws JMSException if the provider cannot create the message for some reason.
     */
    ObjectMessage createObjectMessage(Serializable payload) throws JMSException;

    /**
     * Creates an instance of a basic ObjectMessage object.  The provider may
     * either create the Message with the default generic internal message
     * implementation or create a Provider specific instance that optimizes
     * the access and marshaling of the message.
     *
     * @return a newly created and initialized ObjectMessage instance.
     *
     * @throws JMSException if the provider cannot create the message for some reason.
     */
    ObjectMessage createObjectMessage() throws JMSException;

}
