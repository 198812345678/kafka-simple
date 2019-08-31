#### Constructing a Kafka Producer
* 可能开始是一个线程一个producer，想要提高吞吐则可以启动多个线程使用这个producer，当增加线程数不能再提高吞吐后，就该增加producer了
* @see com.will.simple.kafka.kafka.definitive.guide.ch3.ProducerV1.createProducer

#### Sending a Message to Kafka
* 只管发，有没有发出去不管
* @see com.will.simple.kafka.kafka.definitive.guide.ch3.ProducerV1Test.test
* 同步发送，得到broker的反馈
* @see com.will.simple.kafka.kafka.definitive.guide.ch3.ProducerV1Test.test2
* 异步发送，有callback