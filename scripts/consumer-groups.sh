#!/bin/bash
. config

if [ $# -eq 1 ]; then
  "$KAFKA_HOME"/bin/kafka-consumer-groups.sh --bootstrap-server "$BOOTSTRAP_SERVER" --group "$1" --describe
else
  "$KAFKA_HOME"/bin/kafka-consumer-groups.sh --bootstrap-server "$BOOTSTRAP_SERVER" --list
  echo "USAGE: $0 [consumer-group-name]"
fi
