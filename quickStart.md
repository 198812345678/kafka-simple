# quickStart

启动zk
./zookeeper-server-start.sh ../config/zookeeper.properties
启动broker
./kafka-server-start.sh ../config/server.properties

> kafka-server启动报java.lang.OutOfMemoryError
>> kafka-server-start.sh中KAFKA_HEAP_OPTS="-Xmx2G -Xms512M"
>> tmp/kafka-logs/__consumer_offset-# 占用1G+空间, 启动一个consumer后出现
>> __consumer_offsets是自动创建的topic，相关配置offsets.topic.num.partitions和offsets.topic.replication.factor
>> __consumer_offsets：offset manager用来管理consumer的offset


WARNING: Due to limitations in metric names, topics with a period ('.') or underscore ('_') could collide. To avoid issues it is best to use either, but not both.

> Step 7: Use Kafka Connect to import/export data
>> 在上一节启动3个broker的基础上，connect-test这个topic创建在broker-1上了
>> 当broker-1停掉后，connect-test的describe中leader=-1 （TODO）
>> 没有实现connector的效果(TODO)




先停掉zk，其中一个broker停不掉了，重启zk，再停掉这个broker