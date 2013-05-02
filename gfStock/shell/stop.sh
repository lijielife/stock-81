if [ -f 'pid' ];then
PID=`cat pid`
if kill -9 $PID;then
echo "kill sucess"
else
echo "kill fail"
fi
rm -f 'pid'
fi
