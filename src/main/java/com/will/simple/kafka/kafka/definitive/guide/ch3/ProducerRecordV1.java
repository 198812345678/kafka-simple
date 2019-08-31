package com.will.simple.kafka.kafka.definitive.guide.ch3;

import org.apache.kafka.clients.producer.ProducerRecord;

public class ProducerRecordV1 {

    public static ProducerRecord createProducerRecord() {
        ProducerRecord<String, String> record =
                new ProducerRecord<>("testTopic", "key", "testMsg");
        return record;
    }
}
