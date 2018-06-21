# introduction

> A streaming platform has three key capabilities
>> * 像消息队列或者消息系统一样，能够发布或者订阅streams of records
>> * 以 fault-tolerant durable 的方式存储streams of records
>> * 当streams of records出现时能够处理它

> Kafka is generally used for two broad classes of applications:
>> * 构建实时streaming data pipelines ，在系统间可靠地传输数据
>> * 构建对streams of data进行实时转换/相应的流式应用

> First a few concepts:
>> * kafka可以以一个或多个server组建集群，这些server可以跨多个数据源（不是多种吧）
>> * streams of records 被存储在topics下
>> * 每个record包含 a key, a value, and a timestamp

> Kafka has four core APIs:
>> * Producer API : publish a stream of records to one or more Kafka topics.
>> * Consumer API : 订阅多个topic，处理stream of records
>> * Streams API : act as a stream processor, 从多个topic消费 input stream，并向多个topic生产output stream, 就是把 input streams转换成output streams
>> * Connector API : TODO 

client和server之间使用TCP通信

-------------------
