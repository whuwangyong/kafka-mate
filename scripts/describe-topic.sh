#!/bin/bash
. config

if [ $# -eq 1 ]; then
  "$KAFKA_HOME"/bin/kafka-topics.sh --bootstrap-server "$BOOTSTRAP_SERVER" --describe --topic "$1"
else
  echo "USAGE: $0 topic-name"
fi
