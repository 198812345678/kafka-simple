### 4. DESIGN
#### 4.2 Persistence
##### Don't fear the filesystem!
* kafka使用文件作为存储
* 基于kafka数据量比较大的应用场景，使用内存有两个缺点，1.数据对象占用内存大；2.GC过程复杂，比较耗时
* 操作系统实现的文件系统在磁盘和内核之间也有缓存，并且更加可靠
* 因此kafka选择使用文件log方式持久化，数据会利用文件系统在内核的缓存机制
##### Constant Time Suffices
* 消息系统中数据持久化的数据结构有可能是使用B数，访问B树的时间复杂度理论上是常量，但是磁盘操作在一定维度上控制并行，开销很大
* 将磁盘操作和缓存操作结合，这样的性能是随数据量的增长呈线性增长的
* log方式持久化数据直观点就是，只对文件进行追加和读取（TODO 顺序读取？），读写并行，并且可以水平扩展
* 这样kafka的数据容量就可以水平扩展，持久化的数据更多
#### 4.3 Efficiency
* 如果少量应用就能使基础设施成为瓶颈，那么很小的变化就可能造成问题
* 提高基础设施的效率能保证应用负载过重之前基础设施没有问题，这在集群部署方式中非常重要，因为集群上部署着几十上百的应用，相互影响很危险
* 除了磁盘还有两个影响性能的因素：1.频繁的小数据量I/O操作，2.大量的数据拷贝
* 为了避免频繁的小数据量I/O操作，kafka将消息打包，一组消息平摊一次I/O操作的开销，server端一次追加一组数据到log，consumer一次fetch一组数据
* 打包消息的方式将速度提升几个数量级，更多的顺序磁盘操作，连续的内存块等，尽量将数据的随机访问转化为顺序访问
* 低负载场景下数据拷贝问题不大，高负载场景影响比较大，kafka中的数据在producer，broker，consumer之间是以二进制形式流转，流转的时候不需要转换(可以直接利用零拷贝机制传输)
* broker维护的二进制数据就是一个文件，文件里的数据可以利用领拷贝机制减少传输开销
* 普通网络传输文件数据的方式是，1.数据从磁盘拷贝到内核的pagecache，2.应用从内核把数据拷贝到用户空间的buffer，3.应用把数据写道内核的socket buffer，4.操作系统把数据从socket buffer拷贝到网卡的buffer
* 上述流程需要4次数据拷贝，2次系统调用，但是sendfile机制直接把数据从pagecache拷贝到网卡buffer
* 假设多数场景是多个consumer从同一个topic消费数据，利用上述的零拷贝机制，数据读取一次缓存在pagecache，避免了内存中存储数据，也不用在内核和用户空间之间拷贝数据，约等于不用拷贝数据，只是网络开销
* 在消费者比较多的场景，上述机制基本不用从磁盘读取数据
##### End-to-end Batch Compression
* 网络之间传输压缩后的数据能节省带宽，压缩算法对重复内容压缩效果明显，多个消息中大部分字段名是重复的，因此批量压缩消息比单个消息压缩效果好
#### 4.4 The Producer
##### Load balancing
* producer可以把数据发送到partition对应的leader节点，要做到这个，可以通过任意节点获取哪些节点存活、每个partition对应leader节点的metadata
* producer client可以控制消息发送到哪个partition，可以随机，可以根据指定key做hash，client也可以自定义分配方法
##### Asynchronous send
* 为了实现批量发送producer会把数据暂存在内存，暂存的最大消息数和最长等待时间可以配置，这样既增加了每次发送的数据量又控制了I/O的大小，可以平衡延时和吞吐
#### 4.5 The Consumer
* consumer向对应partition的leader节点发fetch请求，consumer指定offset，broker返回从这个位置开始的若干条数据，consumer可以倒带重新消费消息
##### Push vs. pull
* 在push消息还是pull消息设计上kafka沿用了传统消息系统的方式，producer push到broker，consumer从broker pull消息
* 消费者主动pull和broker向消费者push，两者各有利弊
* push的方式无法兼顾消费能力不同的消费者，如果生产能力过剩消费者会过载，比如突然的垃圾流量
* pull的方式消费者可以根据自身的情况消费慢一点或者追赶消费，可以通过某种协议让consumer反馈自身已经过载，但是很难准确控制让消费者既不过载又能充分利用消费者的资源
* pull的方式有助于批量消费消息，如果是push的方式，必须选择单个推送或者在不知道消费能力(不知道consumer是否空闲)的情况下暂存一批数据后推送，为了降低延时，单个消息发送的话比较浪费，pull方式在有消费能力的时候主动拉取所有未消费的消息（可以配置上限），去掉了不必要的延时
* 当无数据可消费时，pull方式可能会无限循环快速拉取，kafka中可以配置一个时间，当无数据时等待一段时间直到超时，降低拉取的频率，也可以设置等待达到一定数据量
* 还有一种方式，producer写本地log，consumer通过broker从produer拉取数据，这种方式不适用有大量应用作为producer的场景，在成千上万应用的磁盘上存储数据很难保证高可靠，运维也很困难
##### Consumer Position
* 记录哪些数据被成功消费，也是影响性能的一个点
* 很多消息系统在broker上记录哪些消息被成功消费的元数据，当消息发送出去后，有的broker直接在本地记录，有的需要消费者的ACK再记录，对于单机来说这是很直观的做法，因为单机场景数据存储不可扩展，当消息被消费掉后即可删掉，减小数据占用空间
* 为了避免丢消息，很多消息系统需要消费者回执ACK，但是这种方案有几个问题，1.消费成功但是没有ACK可能造成重复消费，2.broker要记录消息的两个状态，发送成功和消费成功，3.一直没有收到ACK的消息怎么办
* kafka中partition的消息只会被group中的一个consumer消费，而且是顺序消费，这样用一个offset就可以记录哪些消息被成功消费，还可以定期核查这个offset，以极低的成本实现ACK
* 倒带重新消费也是一大特性（TODO 怎么重新拉取消息？）
##### Offline Data Load
* 数据存储的可扩展性使得数据存储更多，可以满足定期消费的场景，比如定期将大批量数据load到离线系统中
#### 4.6 Message Delivery Semantics
* 从producer端到consumer端，保证消息投递成功有几种类型，1.最多投递一次（可能丢消息），2.至少一次（不会丢但会重复投递），3.有且仅有一次
* kafka中消息重要committed成功就不会丢，committed成功是指数据写入到任何一个存储对应partition消息的broker
* 0.11.0.0版本之前提交失败重复提交会重复写commit log，0.11.0.0之后有幂等处理，每个producer分配一个ID，每个消息分配一个sequence number
* 0.11.0.0之后还提供事务提交功能，即提交到多个partition的消息同时成功同时失败
* 并不是所有场景都需要非常强的保证，有的场景producer需要等到提交成功，可能会等上10ms；有的场景完全可以异步；有的场景只要leader节点写入成功即可
* 从consumer的角度看，所有副本的log和offset都相同，如果一个consumer进程挂掉，由一个新的进程接替这个consumer，那新的进程需要选择一个合适的offset开始处理数据
* consumer先记录偏移再处理消息的话，存在记录偏移成功但是消息处理失败的情况，接管任务的新consumer从记录的偏移开始处理，处理失败的消息就丢掉了，这对应最多投递一次
* consumer先处理消息后记录偏移的话，接管任务的新consumer开始接到的消息可能是重复消息，这对应至少一次，需要幂等
* 有且仅有一次是这样的场景，当consumer是另一个topic的producer时，可以将该consumer消费的偏移记录在一个topic，这个consumer向偏移数据topic和另一个topic投递数据放在同一个事务中，那么消费偏移和转发投递同时成同时失败（也要看事务的隔离级别），失败的不会投递给消费者
* 事务的隔离级别也有read_uncommitted，read_committed
* producer和consumer都有事务机制 TODO
#### 4.7 Replication
* 为了容灾kafka以partition维度做备份
* 有些其他消息系统的副本机制有些缺点：副本节点流量小，带宽受限制，配置复杂；kafka默认备份不用配置，不需要副本可以把replication factor设为1
* replication factor等于备份+leader节点的数量
* 读写请求都会发到leader节点
* follower上的log跟leader节点一致，但有可能落后
* follower像普通消费者一样从leader节点消费消息，然后维护自己的log，这样可以利用kafka批量发送消息的机制同步
* 节点存活有两个条件，1.保持和ZK的会话，2.与leader节点的同步没有落后太远
* 满足上面两个条件的节点被维护在leader的"in sync"列表，当节点卡住或者down机或者落后太多会从"in sync"列表中删除
* committed是指partition的所有副本都维护好了自己的log，kafka保证committed的消息不会丢失，producer可以选择等待消息committed或者不等
* producer可以要求检查ACK（0，1，all），当all时可以配置一个收到回复的副本的最小个数，只要大于这个数字，producer就可以不等committed认为发送成功
#####  Replicated Logs: Quorums, ISRs, and State Machines (Oh my!)
* replicated log是这样的模型：多个节点就数据的顺序保持一致。最简单的实现方式是由一个leader决定数据的顺序，其他follower只要从leader按顺序复制数据
> 线性访问磁盘比随机访问内存快
> 如果使用缓存，需要