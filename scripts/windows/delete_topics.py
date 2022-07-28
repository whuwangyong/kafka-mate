import os
import sys
import config


def delete_topic(topic):
    print('delete topic:', topic)
    cmd = config.kafka_home + "\\bin\\windows\\kafka-topics.bat --bootstrap-server " + config.kafka_server \
          + " --delete --topic " + topic
    if config.sasl == "true":
        os.system(cmd + config.cmd_cfg_str)
    else:
        os.system(cmd)


if __name__ == "__main__":
    if len(sys.argv) == 1:
        print('usage: delete_topics.py topicName1 topicName2 ...')
        sys.exit(1)
    for i in range(1, len(sys.argv)):
        delete_topic(sys.argv[i])
