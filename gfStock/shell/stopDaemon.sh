if [ -f 'daemonpid' ];then
PID=`cat daemonpid`
if kill -9 $PID;then
echo "kill sucess"
else
echo "kill fail"
fi
rm -f 'daemonpid'
fi
