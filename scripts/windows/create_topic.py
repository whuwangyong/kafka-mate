import os
import sys
import config

if __name__ == "__main__":
    if len(sys.argv) != 4:
        print('usage: create_topic.py topicName replication-factor partitions')
        print('eg: create_topic.py test 1 2')
        sys.exit(1)
    cmd = config.kafka_home + "\\bin\\windows\\kafka-topics.bat --bootstrap-server " + config.kafka_server \
          + " --create --topic " + sys.argv[1] \
          + " --replication-factor " + sys.argv[2] \
          + " --partitions " + sys.argv[3]
    if config.sasl == "true":
        os.system(cmd + config.cmd_cfg_str)
    else:
        os.system(cmd)
