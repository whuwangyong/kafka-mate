#!/bin/bash
. config

if [ $# -eq 1 ]; then
  "$KAFKA_HOME"/bin/kafka-console-producer.sh --bootstrap-server "$BOOTSTRAP_SERVER" --topic "$1"
else
  echo "USAGE: $0 topic-name"
fi
