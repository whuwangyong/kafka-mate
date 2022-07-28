import os
import sys
import config

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print('usage: consume_from.py topicName')
        sys.exit(1)
    cmd = config.kafka_home + "\\bin\\windows\\kafka-console-consumer.bat --bootstrap-server " + config.kafka_server \
          + " --topic " + sys.argv[1] \
          + " --from-beginning --isolation-level read_committed "
    if config.sasl == "true":
        os.system(cmd + config.consumer_cfg_str)
    else:
        os.system(cmd)
