#/bin/bash
. ./config

if [ $# -eq 0 ]; then
  echo 'need one argument: topic'
else
  topic=$1
  if [ $sasl = true ]; then
    $KAFKA_HOME/bin/kafka-console-consumer.sh --bootstrap-server $kafka --topic $topic \
      --from-beginning \
      --isolation-level read_committed \
      --consumer.config $KAFKA_HOME/config/consumer.properties
  else
    $KAFKA_HOME/bin/kafka-console-consumer.sh --bootstrap-server $kafka --topic $topic \
      --from-beginning \
      --isolation-level read_committed
  fi
fi
