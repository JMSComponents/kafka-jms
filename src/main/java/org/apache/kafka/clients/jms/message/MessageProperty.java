package org.apache.kafka.clients.jms.message;

import javax.jms.MessageFormatException;

/**
 * Created by pearcem on 25/04/2017.
 */
public class MessageProperty {



    
    //----- Property Type Validation Methods ---------------------------------//

    public static void checkValidObject(Object value) throws MessageFormatException {
        boolean valid = value instanceof Boolean ||
                        value instanceof Byte ||
                        value instanceof Short ||
                        value instanceof Integer ||
                        value instanceof Long ||
                        value instanceof Float ||
                        value instanceof Double ||
                        value instanceof Character ||
                        value instanceof String ||
                        value == null;

        if (!valid) {
            throw new MessageFormatException("Only objectified primitive objects and String types are allowed but was: " + value + " type: " + value.getClass());
        }
    }
}
