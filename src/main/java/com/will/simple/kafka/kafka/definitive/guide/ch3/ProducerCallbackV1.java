package com.will.simple.kafka.kafka.definitive.guide.ch3;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;

public class ProducerCallbackV1 implements Callback {


    @Override
    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
        if (e != null) {
            e.printStackTrace();
        }
        System.out.println(recordMetadata);
    }
}
