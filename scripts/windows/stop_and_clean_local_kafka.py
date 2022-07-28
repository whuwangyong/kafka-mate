import os
import shutil
import config

print("stopping kafka...")
print(os.popen(config.kafka_home + "\\bin\\windows\\kafka-server-stop.bat").read())

print("stopping zk...")
print(os.popen(config.kafka_home + "\\bin\\windows\\zookeeper-server-stop.bat").read())

print("deleting log files...")
if os.path.exists("/tmp"):
    shutil.rmtree("/tmp")
