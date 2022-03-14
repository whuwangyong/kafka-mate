#!/bin/bash
. config

if [ $# -eq 0 ]; then
  echo "USAGE: $0 topic-name1, topic-name2, ..."
else
  for topic in "$@"; do
    res=$("$KAFKA_HOME"/bin/kafka-topics.sh --bootstrap-server "$BOOTSTRAP_SERVER" --delete --topic "$topic")
    if [ "$res" == "" ]; then
      echo "$topic" deleted
    fi
  done
fi
