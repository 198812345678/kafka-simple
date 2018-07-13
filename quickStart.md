# quickStart

> kafka-server启动报java.lang.OutOfMemoryError
>> kafka-server-start.sh中KAFKA_HEAP_OPTS="-Xmx2G -Xms512M"
>> tmp/kafka-logs/__consumer_offset-# 占用1G+空间
>> __consumer_offset是自动创建的topic，相关配置offsets.topic.num.partitions和offsets.topic.replication.factor