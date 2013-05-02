var net = require('net');
var clientSetting=require("./../cfg/jmsClientSetting");
var convertUtil=require("./util/convertUtil");
var Log = require("./../vendor/visionmediaLog");
var MsgFrameParser = require("./protocol/MsgFrameParser");

var log = new Log("jmsClient");

var msgServer={};
msgServer.host=clientSetting.msgServer.host||"127.0.0.1";
msgServer.port=clientSetting.msgServer.port||8888;
msgServer.reTryTime=clientSetting.msgServer.reTryTime||5000;
msgServer.tags=clientSetting.msgServer.tags||[];

msgServer.hasConnect=false;
msgServer.isConnecting=false;
msgServer.client=new net.Socket();
msgServer.msgFrameParser=new MsgFrameParser();

msgServer.sendBuf=[];

log.info("msgServer host=%s,port=%s",msgServer.host,msgServer.port);

msgServer.client.on('data',onClientData);
msgServer.client.on('close',onClientClose);
msgServer.client.on('error',onClientError);
msgServer.client.on('end',onClientEnd);
msgServer.client.on('connect',onClientConnect);

//
msgServer.BEGIN_BYTE=new Buffer([0X31]);

//消息处理
var msgProcessor=null;

function connectClient(){
	if(msgServer.isConnecting){
		return;
	}
	msgServer.isConnecting=true;
	log.info("connect to msgServer with host=%s,port=%s",msgServer.host,msgServer.port);
	
	msgServer.client.connect( msgServer.port,msgServer.host);
}

function onClientConnect(ev){
	log.info("connect to msgServer sucess");
	msgServer.hasConnect=true;
	msgServer.msgFrameParser.clear();
	
	//sub scribe
	var tags = msgServer.tags;
	for(var i=0;i<tags.length;i++){
		subscribeTag(tags[i]);
	}
	
	sendBufData();
}

function onClientData(ev){
	//log.debug("rev Data");
	msgServer.msgFrameParser.setReadData(ev);
	
	var frame=null;
	while((frame=msgServer.msgFrameParser.read())!=null){
		parseFrame(frame);
	}
}

function onClientError(ev){
	log.debug("error");
	log.error("onClientError %s",ev);
	msgServer.client.destroy();
}
function onClientEnd(ev){
	log.debug("end by server");
	msgServer.client.destroy();
}
function onClientClose(){
	log.error("client close");
	msgServer.hasConnect=false;
	msgServer.isConnecting=false;
	
	if(msgServer.reTryTime>0){
		log.info("try to connect in %s s",msgServer.reTryTime/1000);
		setTimeout(connectClient,msgServer.reTryTime);
	}
}

function parseFrame(frame){
	var data = frame.getData();
	var tagBuf = data.slice(0,4);
	var tag = convertUtil.binaryToInt(tagBuf);
	var frameData=null;
	if(data.length>4){
		frameData=data.slice(4);
	}
	
	if(msgProcessor!=null){
		msgProcessor.processJmsClientMsg(tag,frameData);
	}
}

function sendBufData(){
	//buffer
	if(!msgServer.hasConnect){
		return;
	}

	var item=null;
	while((item=msgServer.sendBuf.shift(0))!=null){
		msgServer.client.write(msgServer.BEGIN_BYTE);
		var len = item.length;
		var bs = convertUtil.intToBuffer(len);
		msgServer.client.write(bs);
		msgServer.client.write(item);
	}
}

function sendData(buf){
	msgServer.sendBuf.push(buf);
	
	sendBufData();
}

var TAGMSG_TYPE_PUBLIC=1;
var TAGMSG_TYPE_SUBSCRIBE=2;
var TAGMSG_TYPE_UNSUBSCRIBE=3;
function subscribeTag(tag){
	log.info("subscribe tag=%s",tag);
	var buf = new Buffer(8);
	
	var bs = convertUtil.intToBuffer(TAGMSG_TYPE_SUBSCRIBE);
	bs.copy(buf);
	
	bs = convertUtil.intToBuffer(tag);
	bs.copy(buf,4);
	
	sendData(buf);
}
function unSubscribeTag(tag){
	log.info("unsubscribe tag=%s",tag);
	var buf = new Buffer(8);
	
	var bs = convertUtil.intToBuffer(TAGMSG_TYPE_UNSUBSCRIBE);
	bs.copy(buf);
	
	bs = convertUtil.intToBuffer(tag);
	bs.copy(buf,4);
	
	sendData(buf);
}
function publicTag(tag,buffer){
	if(!(buffer instanceof Buffer)){
		buffer = new Buffer(buffer);
	}
	var buf = new Buffer(8+buffer.length);
	
	var bs = convertUtil.intToBuffer(TAGMSG_TYPE_PUBLIC);
	bs.copy(buf);
	
	bs = convertUtil.intToBuffer(tag);
	bs.copy(buf,4);
	
	buffer.copy(buf,8);
	
	sendData(buf);
}

function setMsgProcessor(processor){
	msgProcessor=processor;
}

function start(){
	connectClient();
}
function destroy(){
	msgServer.client.destroy();
}

exports.start=start;
exports.destroy=destroy;
exports.setMsgProcessor=setMsgProcessor;
