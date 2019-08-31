#### Broker Configuration
* log.dirs可以配置多个路径，同一个partition的数据只在一个路径下，新partition的数据会选择一个partition数量最少的路径
* num.recovery.threads.per.data.dir配置数据恢复时（启动、关停、失败恢复），处理日志分片的线程数量，是每一个log.dirs的线程数量
* auto.create.topics.enable有三种情况会自动创建topic：有producer写数据、有consumer读数据、有其它client获取元数据。无法校验这个topic是否真的合法需要创建
* num.partitions一般情况只能增加不能减少
* log.retention.ms和log.retention.hours、log.retention.minutes都是一样的功能，配置多个时，较小时间单位的配置生效。这个时间是根据文件关闭的时间（也就是最后一条消息的时间）计算的，如果发生在broker之间迁移，时间会不准
* log.retention.bytes是partition纬度的配置，整个topic的要考虑到多个patition，如果同时配置了过期时间和大小，两者是或到关系，先满足哪个都会触发数据过期
* log.segment.bytes当partition的log.segment达到一定的大小就会关闭文件，然后新建一个log.segment，上面的过期机制也是针对log.segment。如果流量很小log.segment.bytes配置很大，那过很久文件才关闭，才开始计算过期时间，数据会保留很长时间。根据时间fetching offsets的时候，会找到最后修改时间晚于指定时间的log.segment，这个前面的log.segment是早于指定时间的，就会返回这个开始offset（什么场景需要？）
* log.segment.ms和log.segment.bytes是或到关系。应该注意多个partition的当前log.segment同时到达时间close的影响
* message.max.bytes单个消息的大小限制（压缩后），配置时要考虑网络、磁盘的影响，还有consumer能拉取的大小

