import config
import subprocess
import time

print("starting zk...")
cmd = config.zk_home + "\\bin\\zkServer.cmd"
subprocess.Popen(cmd)

time.sleep(2)

print("starting kafka...")
cmd = config.kafka_home + "\\bin\\windows\\kafka-server-start.bat " + config.kafka_home + "\\config\\server.properties"
subprocess.Popen(cmd)
time.sleep(2)

print("done! %s" % time.ctime())
