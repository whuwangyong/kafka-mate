#!/bin/bash
. config

topics=`bash list-topics.sh`

for topic in $topics; do
  res=$("$KAFKA_HOME"/bin/kafka-topics.sh --bootstrap-server "$BOOTSTRAP_SERVER" --delete --topic "$topic")
done

