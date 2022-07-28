import os
import sys
import config

if __name__ == "__main__":
    print('usage: describe_group.py [groupName]')
    cmd = config.kafka_home + "\\bin\\windows\\kafka-consumer-groups.bat --bootstrap-server " + config.kafka_server
    if len(sys.argv) == 2:
        cmd = cmd + " --describe --group " + sys.argv[1]
    else:
        cmd = cmd + " --list"

    if config.sasl == "true":
        cmd = cmd + config.cmd_cfg_str
    os.system(cmd)
