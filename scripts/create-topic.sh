#!/bin/bash
. config

if [ $# -eq 3 ]; then
  "$KAFKA_HOME"/bin/kafka-topics.sh --bootstrap-server "$BOOTSTRAP_SERVER" --create --topic "$1" --partitions "$2" --replication-factor "$3"
else
  echo "USAGE: $0 topic-name partitions replication-factor"
fi
