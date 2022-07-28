#/bin/bash
. ./config

if [ $# -ne 3 ]; then
  echo usage: create_topic.py topicName replication-factor partitions
  echo eg: create_topic.py test 1 2
else
  if [ $sasl = true ]; then
    $KAFKA_HOME/bin/kafka-topics.sh --bootstrap-server $kafka --create --topic $1 \
      --replication-factor $2 \
      --partitions $3 \
      --command-config $KAFKA_HOME/config/admin_sasl.conf
  else
    $KAFKA_HOME/bin/kafka-topics.sh --bootstrap-server $kafka --create --topic $1 \
      --replication-factor $2 \
      --partitions $3
  fi
fi
