#/bin/bash
. ./config

if [ $# -lt 1 ]; then
  echo 'give the topics to delete, multi topics split by blank'
else
  for topic in $*; do
    echo delete topic $topic on $kafka
    if [ $sasl = true ]; then
      $KAFKA_HOME/bin/kafka-topics.sh --bootstrap-server $kafka --delete --topic $topic --command-config $KAFKA_HOME/config/admin_sasl.conf
    else
      $KAFKA_HOME/bin/kafka-topics.sh --bootstrap-server $kafka --delete --topic $topic
    fi
  done
fi
