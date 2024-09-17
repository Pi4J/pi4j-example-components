#!/bin/bash
cd /home/pi/deploy/$1
pkill java
DISPLAY=:0 XAUTHORITY=/home/pi/.Xauthority java -XX:+UseZGC -Xmx1G -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5005 -jar $1.jar
exit 0
