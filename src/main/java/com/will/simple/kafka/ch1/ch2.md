#### Broker Configuration
* log.dirs可以配置多个路径，同一个partition的数据只在一个路径下，新partition的数据会选择一个partition数量最少的路径
* num.recovery.threads.per.data.dir配置数据恢复时（），处理日志分片的线程数量，是每一个log.dirs的线程数量

#### Hardware Selection
* 磁盘吞吐影响producer的性能（因为broker本地日志记录完成才是commit完成），磁盘容量影响kafka集群节点的规模，内存影响consumer的性能（consumer拉取消息可以是从cache中），进出流量的不平衡（一个生成者对应多个消费者）、备份等使得网络方面评估比较复杂

#### Kafka in the Cloud

#### Kafka Clusters
* How Many Brokers: 考虑总数据量（包括备份容灾所存储的数据）和每个节点的容量，考虑每个节点的吞吐和内存
* Broker Configuratio：相同的zk，不同的borkerid
* Operating System Tuning：Virtual Memory，Disk， Networking

#### Production Concerns
* Garbage Collector Options：调整垃圾回收策略
* Datacenter Layout

### Operating System Tuning
#### Virtual Memory
* 关于Linux swap：https://www.jianshu.com/p/73847b688728
* https://www.cnblogs.com/yinzhengjie/p/9994207.html
* 当物理内存不足时会通过虚拟内存做换进换出避免oom，但是这种操作会影响到Kafka各种操作的性能；再者当有数据被交换出物理内存后，如果Kafka要处理这个数据就相当于内存中没有这个数据，kafka的性能非常依赖内存缓存，这种情况下说明内存已经不足，而使用虚拟内存并不能发挥kafaka的特性
* 但是为了避免oom让系统挂掉，又不能把swap完全关掉，所以操作系统层面配置成尽量少的使用虚拟内存
* kafka相当依赖磁盘IO的性能，使用的都是高速磁盘, 而且log是优先写在磁盘，所以内存中的脏页相对少，vm.dirty_background_ratio参数可以设置小一点。但是不能设置0，因为那样会促使内核频繁地刷新页面，从而降低内核为底层设备的磁盘写入提供缓冲的能力。
* 配合dirty_background_ratio将vm.dirty_ratio设置大一点，可以降低同步刷脏页的次数，但是风险是造成脏页过多、同步刷脏页的阻塞时间较长。这种场景建议开启备份，防止丢消息
* 这些参数的调整要实时观察kafka运行过程的变化，从而选定合理配置

#### Disk
* 磁盘上使用的文件系统选择

#### Networking
* 可以调整每个网络包接收、发送的buffer，和同时建立的网络链接数量
