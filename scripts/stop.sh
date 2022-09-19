#!/bin/bash
. config

res=$("$KAFKA_HOME"/bin/kafka-server-stop.sh)
if [ "$res" == "No kafka server to stop" ]; then
  echo "$res"
else
  while [ "$(pgrep -f "kafka.Kafka" | wc -l)" -gt 0 ]; do
    sleep 0.5
    echo "kafka stopping..."
  done
  echo "kafka stopped"
fi

res=$("$KAFKA_HOME"/bin/zookeeper-server-stop.sh)
if [ "$res" == "No zookeeper server to stop" ]; then
  echo "$res"
else
  while [ "$(pgrep -f org.apache.zookeeper.server.quorum.QuorumPeerMain | wc -l)" -gt 0 ]; do
    sleep 0.5
    echo "zookeeper stopping..."
  done
  echo "zookeeper stopped"
fi

# remove log files because start.sh uses them to check booting status
rm -f "$ZK_LOG"
rm -f "$KAFKA_LOG"