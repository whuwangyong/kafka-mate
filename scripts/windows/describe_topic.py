import os
import sys
import config

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print('usage: describe_topic.py topicName')
        sys.exit(1)
    cmd = config.kafka_home + "\\bin\\windows\\kafka-topics.bat --bootstrap-server " + config.kafka_server \
          + " --describe --topic " + sys.argv[1]
    if config.sasl == "true":
        os.system(cmd + config.cmd_cfg_str)
    else:
        os.system(cmd)
