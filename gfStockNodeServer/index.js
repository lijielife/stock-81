var wsHttpServer=require("./lib/wsHttpServer");
var jmsClient = require("./lib/jmsClient");
var MsgProcessor = require("./lib/msgProcessor");

var processor=new MsgProcessor(jmsClient,wsHttpServer);
wsHttpServer.setMsgProcessor(processor);
jmsClient.setMsgProcessor(processor);
//server
wsHttpServer.start();

//client
jmsClient.start();