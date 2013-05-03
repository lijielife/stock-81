#!/bin/bash
if [ -f 'daemonpid' ];then
sh stopDaemon.sh;
fi


CLASSPATH=./classes

for f in ./libs/*.jar;do CLASSPATH=$CLASSPATH:$f;done;

echo $CLASSPATH

java -cp $CLASSPATH job.gf.stock.Runner&
echo $! > daemonpid

