#/bin/bash
. ./config

if [ $# -eq 0 ]; then
  echo 'need one argument: topic'
else
  topic=$1
  if [ $sasl = true ]; then
    $KAFKA_HOME/bin/kafka-topics.sh --bootstrap-server $kafka --describe --topic $topic --command-config $KAFKA_HOME/config/admin_sasl.conf
  else
    $KAFKA_HOME/bin/kafka-topics.sh --bootstrap-server $kafka --describe --topic $topic
  fi
fi
