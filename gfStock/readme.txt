行情生生器与消息队列服务器的
1、项目结构
---src                            代码
   ---main                        系统代码
      ---java
      	 ---job.gf.simplejms      消息队列服务器代码
      	 ---job.gf.stock          行情生成器代码
      ---resources                配置文件
   ---test                        测试代码
---build                          编译和测试临时文件
---libs                           第三方jar库
---shell                          部署时用到shell
---target                         编译后的,部署目录
---build.xml                      ant 的配置文件
---readme.txt                     帮助

2、安装环境(下边的命令都是在ubuntu下进行的)
    a、java的编译环境
    	sudo apt-get install openjdk-6-jdk
    b、ant运行环境
    	sudo apt-get install ant
    	(参考：http://ant.apache.org/)
    
3、配置与编译
	src/main/resources/jmsServer.cnf
	消息队列服务器，默认监听端口：8992，可以在上面文件修改；
	
	src/main/resources/jmsClient.cnf
	消息队列服务器client，默认监听端口：8992，可以在上面文件修改；
	
	src/main/resources/stocks.cnf
	股市初始化数据;
	
	src/main/resources/simplelogger.properties
	日志配置;
	
	测试：ant test
	编译：ant
	
4、运行(运行消息队列服务器和行情生成器,两者同一个程序)
	编译完成后，cd 入target目录，里面就是编译好的所有文件
	
	运行消息队列服务器和行情生成器(两者同一个程序)
	＃startAll.sh
	查看日志
	#tail -f app.log 
	
	其它shell意思
	#startAllDaemon.sh 以daemon形式运行，不能交互
	
5、交互命令参考
	help 
	显示帮助命令   
	quit    
	退出
	broadcast msg  
	发浏览器发送“公告信息”，msg是字符串
	addStock name price total [generator]
	添加股票，name名字；price是价格，单位元；total是发行数量；generator是行情生成器生成数据
	的规律，默认是random，随机生成；crazy，剧烈波动，上升和下跌；ten，波动在10%内。

6、分开运行(分别运行消息队列服务器和行情生成器)
	单独启动消息队列服务器
	#startJmsServer.sh 
	单独启运行情生成器
	#startStockGenerator.sh 
	查看日志
	#tail -f app.log 