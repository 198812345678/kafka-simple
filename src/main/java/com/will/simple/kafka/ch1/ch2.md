#### Broker Configuration
* log.dirs可以配置多个路径，同一个partition的数据只在一个路径下，新partition的数据会选择一个partition数量最少的路径
* num.recovery.threads.per.data.dir配置数据恢复时（），处理日志分片的线程数量，是每一个log.dirs的线程数量

#### Hardware Selection
* 磁盘吞吐影响producer的性能（因为broker本地日志记录完成才是commit完成），磁盘容量影响kafka集群节点的规模，内存影响consumer的性能（consumer拉取消息可以是从cache中），进出流量的不平衡（一个生成者对应多个消费者）、备份等使得网络方面评估比较复杂

### Operating System Tuning
#### Virtual Memory
* 关于Linux swap：https://www.jianshu.com/p/73847b688728
* 当物理内存不足时会通过虚拟内存做换进换出避免oom，但是这种操作会影响到Kafka各种操作的性能；再者当有数据被交换出物理内存后，如果Kafka要处理这个数据就相当于内存中没有这个数据，kafka的性能非常依赖内存缓存，这种情况下说明内存已经不足，而使用虚拟内存并不能发挥kafaka的特性
* 但是为了避免oom让系统挂掉，又不能把swap完全关掉，所以操作系统层面配置成尽量少的使用虚拟内存

