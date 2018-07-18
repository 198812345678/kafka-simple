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