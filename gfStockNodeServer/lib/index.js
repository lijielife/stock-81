var wsHttpServer=require("./wsHttpServer");
var jmsClient = require("./jmsClient");
var MsgProcessor = require("./msgProcessor");

var processor=new MsgProcessor(jmsClient,wsHttpServer);
wsHttpServer.setMsgProcessor(processor);
jmsClient.setMsgProcessor(processor);
//server
wsHttpServer.start();

//client
jmsClient.start();