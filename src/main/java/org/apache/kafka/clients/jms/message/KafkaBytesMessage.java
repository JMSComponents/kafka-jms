package org.apache.kafka.clients.jms.message;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.TextMessage;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;

import org.apache.kafka.clients.serialization.Headers;

/**
 * Created by pearcem on 26/04/2017.
 */
public class KafkaBytesMessage extends AbstractKafkaMessage<ArrayList<Byte>> implements BytesMessage {

    public KafkaBytesMessage(Headers headers) {
        super(headers);
        body = new ArrayList<>();
    }

    public KafkaBytesMessage() {
        super();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        
    }


    @Override
    public long getBodyLength() throws JMSException {
        return body.size();
    }

    @Override
    public boolean readBoolean() throws JMSException {
        return false;
    }

    @Override
    public byte readByte() throws JMSException {
        return 0;
    }

    @Override
    public int readUnsignedByte() throws JMSException {
        return 0;
    }

    @Override
    public short readShort() throws JMSException {
        return 0;
    }

    @Override
    public int readUnsignedShort() throws JMSException {
        return 0;
    }

    @Override
    public char readChar() throws JMSException {
        return 0;
    }

    @Override
    public int readInt() throws JMSException {
        return 0;
    }

    @Override
    public long readLong() throws JMSException {
        return 0;
    }

    @Override
    public float readFloat() throws JMSException {
        return 0;
    }

    @Override
    public double readDouble() throws JMSException {
        return 0;
    }

    @Override
    public String readUTF() throws JMSException {
        return null;
    }

    @Override
    public int readBytes(byte[] value) throws JMSException {
        return 0;
    }

    @Override
    public int readBytes(byte[] value, int length) throws JMSException {
        return 0;
    }

    @Override
    public void writeBoolean(boolean value) throws JMSException {

    }

    @Override
    public void writeByte(byte value) throws JMSException {

    }

    @Override
    public void writeShort(short value) throws JMSException {

    }

    @Override
    public void writeChar(char value) throws JMSException {

    }

    @Override
    public void writeInt(int value) throws JMSException {

    }

    @Override
    public void writeLong(long value) throws JMSException {

    }

    @Override
    public void writeFloat(float value) throws JMSException {

    }

    @Override
    public void writeDouble(double value) throws JMSException {

    }

    @Override
    public void writeUTF(String value) throws JMSException {

    }

    @Override
    public void writeBytes(byte[] value) throws JMSException {

    }

    @Override
    public void writeBytes(byte[] value, int offset, int length) throws JMSException {

    }

    @Override
    public void writeObject(Object value) throws JMSException {

    }

    @Override
    public void reset() throws JMSException {

    }
}
