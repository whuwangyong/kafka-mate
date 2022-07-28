#/bin/bash
. ./config

if [ $# -eq 0 ]; then
  echo 'need one argument: topic'
else
  topic=$1
  if [ $sasl = true ]; then
    $KAFKA_HOME/bin/kafka-console-producer.sh --bootstrap-server $kafka --topic $topic \
      --producer.config $KAFKA_HOME/config/producer.properties
  else
    $KAFKA_HOME/bin/kafka-console-producer.sh --bootstrap-server $kafka --topic $topic
  fi
fi
