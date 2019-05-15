#### Broker Configuration
* log.dirs可以配置多个路径，同一个partition的数据只在一个路径下，新partition的数据会选择一个partition数量最少的路径
* num.recovery.threads.per.data.dir配置数据恢复时（），处理日志分片的线程数量，是每一个log.dirs的线程数量

#### Hardware Selection
* 磁盘吞吐影响producer的性能（因为broker本地日志记录完成才是commit完成），磁盘容量影响kafka集群节点的规模，内存影响consumer的性能（consumer拉取消息可以是从cache中），进出流量的不平衡（一个生成者对应多个消费者）、备份等使得网络方面评估比较复杂