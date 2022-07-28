import os
import config


def list_topics():
    cmd = config.kafka_home + "\\bin\\windows\\kafka-topics.bat --bootstrap-server " + config.kafka_server \
          + " --list "
    global topics
    if config.sasl == "true":
        topics = os.popen(cmd + config.cmd_cfg_str).read()
    else:
        topics = os.popen(cmd).read()
    return str(topics).splitlines()


if __name__ == "__main__":
    for i in list_topics():
        print(i)
