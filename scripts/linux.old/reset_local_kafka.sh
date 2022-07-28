#/bin/bash
. ./config

cd $KAFKA_HOME

function restart() {
  echo "kill zookeeper and kafka..."
  for pid in $(pgrep -f "kafka.Kafka"); do kill -9 $pid; done
  for pid in $(pgrep -f org.apache.zookeeper.server.quorum.QuorumPeerMain); do kill -9 $pid; done
  echo "clear all zookeeper and kafka data..."
  rm -rf /tmp/kafka-logs* /tmp/zookeeper

  echo "restart zookeeper and kafka..."
  #bin/zookeeper-server-start.sh config/zookeeper.properties >/tmp/zk.log &
  $ZK_HOME/bin/zkServer.sh start-foreground >/tmp/zk.log &
  while [ $(grep "zookeeper.snapshotSizeFactor" /tmp/zk.log | wc -l) -ne 1 ]; do
    echo "waiting zookeeper start..."
    sleep 1
  done
  #bin/kafka-server-start.sh config/server.properties --override auto.create.topics.enable=false >/tmp/kafka0.log &
  bin/kafka-server-start.sh config/server.properties >/tmp/kafka0.log &
  bin/kafka-server-start.sh config/server-1.properties >/tmp/kafka1.log &
  bin/kafka-server-start.sh config/server-2.properties >/tmp/kafka2.log &
  while [ $(pgrep -f "kafka.Kafka" | wc -l) -ne 3 ]; do
    echo "waiting kafka start..."
    sleep 1
  done
}

restart
echo "$(date +"%F %T") done"
