package com.will.simple.kafka.kafka.definitive.guide.ch3;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

public class ProducerV1Test {

    @Test
    public void test() {
        Producer producer = ProducerV1.createProducer();
//        System.out.println(producer);

        ProducerRecord producerRecord = ProducerRecordV1.createProducerRecord();

        producer.send(producerRecord);
    }

    @Test
    public void test2() throws ExecutionException, InterruptedException {
        Producer producer = ProducerV1.createProducer();
//        System.out.println(producer);

        ProducerRecord producerRecord = ProducerRecordV1.createProducerRecord();

        Object o = producer.send(producerRecord).get();
        System.out.println(o);
    }

    @Test
    public void test3() throws ExecutionException, InterruptedException {
        Producer producer = ProducerV1.createProducer();
//        System.out.println(producer);

        ProducerRecord producerRecord = ProducerRecordV1.createProducerRecord();

        Object o = producer.send(producerRecord, new ProducerCallbackV1());
        System.out.println(o);
        Thread.sleep(50000);
    }
}