#/bin/bash

function stop() {
  echo "stop kafka..."
  for pid in $(pgrep -f "kafka.Kafka"); do kill -9 $pid; done
  echo "stop zookeeper..."
  for pid in $(pgrep -f org.apache.zookeeper.server.quorum.QuorumPeerMain); do kill -9 $pid; done
}

stop
echo "$(date +"%F %T") done"
