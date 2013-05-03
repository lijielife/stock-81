#!/bin/bash
if [ -f 'pid' ];then
sh stop.sh;
fi


CLASSPATH=./classes

for f in ./libs/*.jar;do CLASSPATH=$CLASSPATH:$f;done;

echo $CLASSPATH

java -cp $CLASSPATH job.gf.stock.Runner
echo $! > pid

