#/bin/bash
. ./config

if [ $sasl = true ]; then
  $KAFKA_HOME/bin/kafka-topics.sh --bootstrap-server $kafka --list --command-config $KAFKA_HOME/config/admin_sasl.conf
else
  $KAFKA_HOME/bin/kafka-topics.sh --bootstrap-server $kafka --list
fi
