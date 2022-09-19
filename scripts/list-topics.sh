#!/bin/bash
. config

"$KAFKA_HOME"/bin/kafka-topics.sh --bootstrap-server "$BOOTSTRAP_SERVER" --list