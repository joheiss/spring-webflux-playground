# Netstat command to monitor the network connections
netstat -an| grep -w 127.0.0.1.7070

# To watch
watch 'netstat -an| grep -w 127.0.0.1.7070'