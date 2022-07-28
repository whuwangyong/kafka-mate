#/bin/bash
. ./config

if [ $sasl = true ]; then
  topics=$($KAFKA_HOME/bin/kafka-topics.sh --bootstrap-server $kafka --list --command-config $KAFKA_HOME/config/admin_sasl.conf)
  for topic in $topics; do
    echo delete topic $topic on $kafka
    $KAFKA_HOME/bin/kafka-topics.sh --bootstrap-server $kafka --delete --topic $topic --command-config $KAFKA_HOME/config/admin_sasl.conf
  done
else
  topics=$($KAFKA_HOME/bin/kafka-topics.sh --bootstrap-server $kafka --list)
  for topic in $topics; do
    echo delete topic $topic on $kafka
    $KAFKA_HOME/bin/kafka-topics.sh --bootstrap-server $kafka --delete --topic $topic
  done
fi
