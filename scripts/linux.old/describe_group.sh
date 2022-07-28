#/bin/bash
. ./config

if [ $# -eq 0 ]; then
  echo 'need one argument: consumer group'
  echo "the groups on $kafka:"
  if [ $sasl = true ]; then
    $KAFKA_HOME/bin/kafka-consumer-groups.sh --bootstrap-server $kafka --list \
      --command-config $KAFKA_HOME/config/admin_sasl.conf
  else
    $KAFKA_HOME/bin/kafka-consumer-groups.sh --bootstrap-server $kafka --list
  fi
else
  group=$1
  if [ $sasl = true ]; then
    $KAFKA_HOME/bin/kafka-consumer-groups.sh --bootstrap-server $kafka --describe --group $group \
      --command-config $KAFKA_HOME/config/admin_sasl.conf
  else
    $KAFKA_HOME/bin/kafka-consumer-groups.sh --bootstrap-server $kafka --describe --group $group
  fi
fi
