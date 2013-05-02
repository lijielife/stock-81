#!/bin/bash
CLASSPATH=./classes

for f in ./libs/*.jar;do CLASSPATH=$CLASSPATH:$f;done;

echo $CLASSPATH

java -cp $CLASSPATH job.gf.simplejms.server.TagSubscribeServer

