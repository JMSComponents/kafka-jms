package io.github.jmscomponents.kafka.jms.message;

import java.nio.ByteBuffer;

/**
 * Created by pearcem on 25/04/2017.
 */
public class Test {
    
    public static void main(String... args){
        test(44);
        test(4444444444444444444l);
        test(Integer.toString(44));
        test(true);
        test(Boolean.toString(true));
        test(444444.444d);
        test(Double.toString(444444.444d));

    }
    
    public static void test(Object o){
        TypedHeader th = new TypedHeader("hey", o);
        System.out.println(o.getClass());
        System.out.println(th);
        byte[] b = th.value();
        System.out.println(b.length);

        TypedHeader th1 = new TypedHeader("hey", ByteBuffer.wrap(b));
        System.out.println(th1);

    }
}
