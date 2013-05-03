webSocket服务器与http服务器
(前端已经做了测试：可以运行在chrome的v15,v16,v26等版本，应该v14以上的版本都支持)

1、项目结构
---cfg                     配置文件
---lib                     系统的支撑和业务逻辑处理
---log                     日志输出目录
---node_modules            第三方库
   ---buffalo              bson的序列化与反序列化
   ---websocket            node的websocket封装
---public                  提供网页的资源文件
   ---css
   ---img
   ---js
   ---stock
---test                     单元测试
---vendor                   小型的工具库
---index.js                 系统入口

2、安装环境(下边的命令都是在ubuntu下进行的)
    a、node的运行环境(version>=v0.6.19)
    	sudo apt-get install python-software-properties
		sudo add-apt-repository ppa:chris-lea/node.js
		sudo apt-get update
		sudo apt-get install nodejs
    b、npm包管理
    	sudo apt-get install npm
    
3、配置
	cfg/jmsClientSetting.js
	消息队列服务器的host和端口；
	
	cfg/wsHttpServerSetting.js
	webSocket与http服务器的地址和端口
	
	cfg/setting.js
	日志等配置
4、测试
	安装mocha
	#npm install -g mocha
	#ln -s /usr/bin/nodejs /usr/bin/node
	
	运行测试
	#mocha
	
5、运行
	#nodejs index.js
	
        让nodejs在后台运行
        #nohup nodejs index.js &
6、默认访问地址
	http://127.0.0.1:8899
	(不是本机访问,上面的127.0.0.1替换成ip)
