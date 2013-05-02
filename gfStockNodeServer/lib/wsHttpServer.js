var webSocketServer = require('websocket').server;
var http = require('http');
var Log = require("./../vendor/visionmediaLog");
var MsgFrameParser = require("./protocol/MsgFrameParser");
var defaultSetting=require("./../cfg/wsHttpServerSetting");

var log = new Log("WsHttpServer");

var serverSetting={port:defaultSetting.port||8889,
host:defaultSetting.host||"localhost",
publicDir:defaultSetting.publicDir||"public"};

function Client(key,ip,con){
	this.key=key;
	this.ip=ip;
	this.connection=con;
	this.tags=[];
}
var httpServer=null;
var wsServer=null;

var startOk=false;

var msgProcessor=null;
 
function start(){
	//httpServer
	httpServer=http.createServer(onHttpRequest);
	httpServer.listen(serverSetting.port,serverSetting.host, function() {
		var address = httpServer.address();
	    log.info("listen %s:%s",address.address,address.port);
	});
	
	httpServer.on('error',function(){
		log.info("error to start server");
	});
	
	//wsServer
	wsServer = new webSocketServer({
	    httpServer: httpServer,
	    autoAcceptConnections: false
	});
	wsServer.on('request',onRequest);
}

function onHttpRequest(request,response){
	if(msgProcessor!=null){
		msgProcessor.processHttpServerMsg(request,response);
	}
}

function originIsAllowed(origin) {
  return true;
}

//ws
function onRequest(request){
	log.debug('Connection from origin %s',request.origin);
 
    if (!originIsAllowed(request.origin)) {
      // Make sure we only accept requests from an allowed origin
      request.reject();
      log.info(' Connection from origin ' + request.origin + ' rejected.');
      return;
    }

    var connection = request.accept(null, request.origin);
    var key = new Date().getTime()+"";
    var client=new Client(key,connection.remoteAddress,connection);
    
    if(msgProcessor!=null){
		msgProcessor.processWsHttpServerMsg(client,"accept");
	}
    
    connection.on('message', function(message) {
        if(msgProcessor!=null){
			msgProcessor.processWsHttpServerMsg(client,'message',message);
		}
    });
    connection.on('close', function(reasonCode, description) {
       if(msgProcessor!=null){
			msgProcessor.processWsHttpServerMsg(client,'close',{"code":reasonCode,"desc":description});
		}
    });
}



function destroy(){

}

function setMsgProcessor(processor){
	msgProcessor=processor;
}

function getPublicDir(){
	return serverSetting.publicDir;
}
exports.getPublicDir=getPublicDir;
exports.start=start;
exports.destroy=destroy;
exports.setMsgProcessor=setMsgProcessor;

 