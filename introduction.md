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

> Topics and Logs
>> * 每一个topic，整个kafka集群维护多个分区的log
>> * 每一个分区中的record陆续append到分区中，record有序并且顺序不可变
>> * 每一个分区就是一个a structured commit log
>> * 分区中的record分配一个序列ID，叫做offset，是某一个分区中record的唯一标识
>> * 所有record会被持久化一段时间，这个时间可配置
>> * kafka的性能跟data size成常量关系，所以数据保存较长时间没有关系
>> * consumer持有的元数据是offset
>> * 一般情况consumer按照读取的record线性向前移动offset，但是consumer也可以按照任意顺序消费record
>> * consumer很轻量级，一个consumer加入或者离开对集群或者其他consumer没有影响，比如可以使用命令行tail任意topic中的内容，不会影响到已有consumer
>> * 日志分区的目的，log的规模可以超过单台server的容量，一个独立的分区必须适配他所在的server，但是一个topic可以有多个分区所以可以处理任意大的数据量
>> * 日志分区的目的，每一个分区可以作为一个并行的单元

> Distribution
>> * 集群中的server以共享的方式处理partition的数据和请求，可以配置partition在若干个server上冗余用来容错
>> * 每一个partition有一个server作为leader，多个其他server作为follower，也可以没有follower
>> * partition的读写请求都由leader处理，follower被动地复制leader
>> * leader挂了从follower中的一个自动成为新的leader
>> * 每一个server是一些partition的leader，同时这个server还是其余partition的follower，这样就实现了集群负载均衡


> Geo-Replication
>> * MirrorMaker 是一个工具，可以在集群间同步数据
>> * TODO


> Producers
>> * producer负责决定record投递到topic的哪个partition，可以轮询，也可以根据key匹配


> Consumers
>> * topic下一个record投递到每个group中的一个consumer实例
>> * 如果所有consumer属于同一个group，record均衡投递到这些consumer
>> * 如果每一个consumer都属于不同的group，相当于向这些consumer广播record
>> * partition数量/consumer实例数量，用这种方式为partition分配consumer
>> * group中有新的实例加入的话会从其他成员那里接管部分partition，group中有实例down掉会把partition分配给其他成员
>> * record的顺序是partition维度的，全局有序可以用只设置一个partition的方式实现，但是这样group中就只有一个consumer会接收record，其他consumer空闲

> Multi-tenancy
>> * TODO

> Guarantees
>> * 对于同一个partition，同一个producer发送的两个record是有序的
>> * consumer看到record的顺序是record在log中存储的顺序
>> * 如果topic配置了replication factor N，那么可以容忍N-1个server挂掉，不丢失record


> Kafka as a Messaging System
>> * 传统消息系统区分两种模式，queuing 和 publish-subscribe
>> * kafka引入group的概念同时具备这两种模式的特点，不需要二选一
>> * 保证顺序方面，传统消息系统只能通过exclusive consumer的方式实现有序，这丢失了并行处理能力
>> * kafka的有序也是以单一消费方式实现，但是单一消费是partition维度，多个partition并行就实现了并行处理的能力

> Kafka as a Storage System
>> * data写入kafka是指数据写入磁盘并备份到容错节点（TODO 感觉理解不对，producer什么时候才会收到ACK？）

> Kafka for Stream Processing
>> * compute aggregations off of streams or join streams together
>> * 有益于这些场景：处理无序数据，代码修改后重新处理，performing stateful computations（TODO）

> Putting the Pieces Together
>> * kafka既可以处理实时数据又可以处理历史数据
>> * 可以用作低延迟的管道，也可以依赖其可靠的存储能力投递关键数据，然后结合线下系统只处理某时间段的数据，或者允许应用下线一段时间进行维护