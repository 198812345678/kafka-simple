# Use cases

> Messageing
>> 

> Website Activity Tracking
>> * 以实时发布订阅流的方式，构建用户活跃度跟踪
>> * 浏览、搜索等行为，每一种行为发布到自己的一个topic
>> * 这些流可以被其他应用订阅，real-time processing, real-time monitoring，导入Hadoop或离线数据仓库进行离线处理、报表
>> * 这种应用需要很大容量

> Metrics
>> 类似监控的意思吧（TODO)

> Log Aggregation
>> * 生成日志摘要，可以实现低延迟（TODO 摘要类似加密中的那个摘要，可以还原出完整信息）
>> * 以消息形式，可以实现多数据源
>> * 比其他日志处理系统性能好，持久化更可靠（备份机制），端到端延迟低

> Stream Processing
>> * kafka的流处理可以从一个topic消费原生数据，然后进行聚合、填充、转化等，流转到另一个新的topic
>> * 比如，推荐文章，从RSS爬下原生文章，publish到"articles" topic， 然后文章内容经过标准化publish到新的topic，下游再把处理过的文章推荐给用户


> Event Sourcing
>> * 这种应用是把状态变迁以时间序列的形式记录log，kafka存储大容量日志的能力有利于这一类应用

> Commit Log
>> * 分布式系统的commit-log，在节点之间同步数据，或者down掉的节点重新启动重新载入数据