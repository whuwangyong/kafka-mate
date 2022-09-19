#!/bin/bash
. config

echo "kill kafka and zookeeper..."
pkill -f "kafka.Kafka" -9
pkill -f org.apache.zookeeper.server.quorum.QuorumPeerMain -9

echo "rm -rf /tmp/kafka-logs /tmp/zookeeper"
rm -rf /tmp/kafka-logs /tmp/zookeeper

echo "start..."
./start.sh
