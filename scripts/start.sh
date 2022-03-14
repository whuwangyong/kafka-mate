#!/bin/bash
. config

repeat=5

nohup "$KAFKA_HOME"/bin/zookeeper-server-start.sh "$KAFKA_HOME"/config/zookeeper.properties >"$ZK_LOG" 2>&1 &
# 为什么不用这个判断？因为进程在，不表示zk完全启动好了
# while [ "$(pgrep -f org.apache.zookeeper.server.quorum.QuorumPeerMain | wc -l)" -eq 0 ]
# 下面这个是zk启动完成后的最后一行日志
while [ "$(grep -c 'Using checkIntervalMs=' "$ZK_LOG")" -eq 0 ]; do
  echo "waiting zookeeper starting..."
  sleep 1
  ((repeat--))
  if [ $repeat -eq 0 ]; then
    echo "zookeeper start failed"
    exit 1
  fi
done
echo "zookeeper started"

repeat=5
nohup "$KAFKA_HOME"/bin/kafka-server-start.sh "$KAFKA_HOME"/config/server.properties >"$KAFKA_LOG" 2>&1 &
while [ "$(grep -c 'started (kafka.server.KafkaServer)' "$KAFKA_LOG")" -eq 0 ]; do
  echo "waiting kafka starting..."
  sleep 1
  ((repeat--))
  if [ $repeat -eq 0 ]; then
    echo "kafka start failed"
    exit 1
  fi
done
echo "kafka started"