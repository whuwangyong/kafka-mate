import os

kafka_home = "E:\\kafka\\kafka_2.13-2.6.0"
zk_home = "E:\\kafka\\apache-zookeeper-3.7.0-bin"
kafka_server = "127.0.0.1:9092"
sasl = "false"

if sasl == "true":
    os.environ["KAFKA_OPTS"] = "-Djava.security.auth.login.config=" + kafka_home + "\\config\\kafka_jaas.conf"
    cmd_cfg_str = " --command-config " + kafka_home + "\\config\\admin_sasl.conf"
    consumer_cfg_str = " --consumer.config " + kafka_home + "\\config\\consumer.properties"
    producer_cfg_str = " --producer.config " + kafka_home + "\\config\\producer.properties"
