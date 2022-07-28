#/bin/bash
. ./config

cd $KAFKA_HOME

function start() {
  rm -rf /tmp/zk.log
  rm -rf /tmp/kafka*.log
  echo "start zookeeper and kafka..."
  #bin/zookeeper-server-start.sh config/zookeeper.properties >/tmp/zk.log &
  $ZK_HOME/bin/zkServer.sh start-foreground >/tmp/zk.log &
  while [ $(grep "zookeeper.snapshotSizeFactor" /tmp/zk.log | wc -l) -ne 1 ]; do
    echo "waiting zookeeper start..."
    sleep 1
  done
  bin/kafka-server-start.sh config/server.properties >/tmp/kafka0.log &
  bin/kafka-server-start.sh config/server-1.properties >/tmp/kafka1.log &
  bin/kafka-server-start.sh config/server-2.properties >/tmp/kafka2.log &
  while [ $(pgrep -f "kafka.Kafka" | wc -l) -ne 3 ]; do
    echo "waiting kafka start..."
    sleep 1
  done
}

start
echo "$(date +"%F %T") done"
