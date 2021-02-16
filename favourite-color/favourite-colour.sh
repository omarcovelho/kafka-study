kafka-topics.sh --create --bootstrap-server localhost:9092 \
  --replication-factor 1 --partitions 1 --topic favourite-color-input

kafka-topics.sh --create --bootstrap-server localhost:9092 \
  --replication-factor 1 --partitions 1 --topic user-keys-and-colors \
  --config cleanup.policy=compact

kafka-topics.sh --create --bootstrap-server localhost:9092 \
  --replication-factor 1 --partitions 1 --topic favourite-color-output \
  --config cleanup.policy=compact

kafka-console-consumer.sh --bootstrap-server localhost:9092 \
  --topic favourite-color-output \
  --from-beginning \
  --formatter kafka.tools.DefaultMessageFormatter \
  --property print.key=true \
  --property print.value=true \
  --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
  --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer

kafka-console-producer.sh --broker-list localhost:9092 --topic favourite-color-input