#### Broker Configuration
* log.dirs可以配置多个路径，同一个partition的数据只在一个路径下，新partition的数据会选择一个partition数量最少的路径
* num.recovery.threads.per.data.dir配置数据恢复时（），处理日志分片的线程数量，是每一个log.dirs的线程数量