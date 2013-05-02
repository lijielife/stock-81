var Log = require("./../vendor/visionmediaLog"),
url = require("url"),
path = require("path"),
fs = require("fs"),
BSON = require('buffalo')//var object = BSON.parse(buffer)  var buffer = BSON.serialize(object)

var log = new Log("msgProcessor");

/***manager client ***/
function ClientManager(){
	this.keyClientMap={};
	this.tagClientMap={};
}
ClientManager.prototype.addClient=function(client){
	this.keyClientMap[client.key]=client;
}
ClientManager.prototype.removeClient=function(client){
	var key = client.key;
	var tags= client.tags;
	for(var i=0;i<tags.length;i++){
		var arr=this.tagClientMap[tags[i]];
		if(arr){
			for(var j=0;j<arr.length;j++){
				if(arr[j]==key){
					arr.splice(j,1);
					break;
				}
			}
		}
	}
	
	delete this.keyClientMap[client.key];
}
ClientManager.prototype.addTag=function(client,tag){
	var key = client.key;
	
	var tags=client.tags;
	for(var i=0;i<tags.length;i++){
		if(tags[i]==tag){
			return;
		}
	}
	
	tags.push(tag);
	var arr=this.tagClientMap[tag];
	if(!arr){
		arr=[];
		this.tagClientMap[tag]=arr;
	}
	
	//add to tagClientMap
	arr.push(key);
}

ClientManager.prototype.removeTag=function(client,tag){
	var key = client.key;
	
	var tags=client.tags;
	var find=false;
	for(var i=0;i<tags.length;i++){
		if(tags[i]==tag){
			find=true;
			tags.splice(i,1);
		}
	}
	
	if(!find) return;
	
	var arr=this.tagClientMap[tag];
	if(arr){
		for(var i=0;i<arr.length;i++){
			if(arr[i]==key){
				arr.splice(i,1);
			}
		}
	}
}


ClientManager.prototype.getTagClient=function(tag){
	var arr=this.tagClientMap[tag];
	if(arr){
		var clients=[];
		for(var i=0;i<arr.length;i++){
			clients.push(this.keyClientMap[arr[i]]);		
		}
		return clients;
	}
	
	return [];
}

ClientManager.prototype.getAllClient=function(){
	var arr=[];
	for(var key in this.keyClientMap){
		arr.push(this.keyClientMap[key]);
	}
	return arr;
}

/*** data save 
	data={x,y,index};
***/
function StockData(){
	this.data={};
	this.lastCleanTime=(new Date()).getTime();
}

StockData.prototype.cleanData=function(time){
	log.info("clean StockData");
	for(var key in this.data){
		var obj = this.data[key];
		
		var arr=obj.arr;
		if(arr.length==0) continue;
		var index=0;
		for(index=0;index<arr.length && arr[index].x<time;index++){
		}
		
		
		arr.splice(0,index);
	}
}

StockData.prototype.addTagData=function(tag,data){
	var nowTime = new Date().getTime();
	if(nowTime-this.lastCleanTime>3600000){
		//one hour clean 
		this.cleanData(nowTime-3600000);
		this.lastCleanTime=nowTime;
	}

	var obj=this.data[tag];
	if(!obj){
		obj={"index":0,"arr":[]};
		this.data[tag]=obj;
	}
	
	if(obj.index>data['index']){
		obj.arr.splice(0,obj.arr.length);
	}
	obj.index=data['index'];
	obj.arr.push(data);
}
StockData.prototype.getTagData=function(tag){
	var obj=this.data[tag];
	if(obj) return obj.arr;
	
	return [];
}

function MsgProcessor(jmsClient,wsHttpServer){
	this.jmsClient=jmsClient;
	this.wsHttpServer=wsHttpServer;
	this.clientManager=new ClientManager();
	this.stockData=new StockData();
}

function Point(x,y,index){
	this.x=x;
	this.y=y;
	this.index=index;
}

MsgProcessor.prototype.processJmsClientMsg=function (tag,buffer){
	//log.debug("processJmsClientMsg tag=%s",tag);
	
	if(tag==20){
		//notice
		var clients = this.clientManager.getAllClient();
		brocastMsg(clients,buffer);
	}else if(tag=="30"){
		//stock
		var object=null;
		try{
			object = BSON.parse(buffer);
			
			//log.debug("stock object=%s",JSON.stringify(object));
			var p = new Point(object['priceTime'],object['price'],object['index']);
			var stock=object['code'];
			this.stockData.addTagData(stock,p);
		}catch(e){
			log.error("error when processJmsClientMsg tag=%s,%s",tag,e);
		}	
		
		//
		var code = object['code'];
		var clients = this.clientManager.getTagClient(code);
		brocastMsg(clients,buffer);
	}else if(tag=="40"){
		//market
		var object=null;
		try{
			object = BSON.parse(buffer);
			
			var p = new Point(object['publicTime'],object['shanghaiIndex'],object['index']);
			var stock="market";
			this.stockData.addTagData(stock,p);
			//log.debug("stock object=%s",JSON.stringify(object));
		}catch(e){
			log.error("error when processJmsClientMsg tag=%s,%s",tag,e);
		}	
		
		//
		var clients = this.clientManager.getTagClient("market");
		brocastMsg(clients,buffer);
	}
}

function brocastMsg(clients,buffer){
	if(clients.length>0){
		for(var i=0;i<clients.length;i++){
			var client = clients[i];
		
			client.connection.sendBytes(buffer);
		}
	}
}

MsgProcessor.prototype.processWsHttpServerMsg=function (client,action,buffer){
	log.debug("processWsHttpServerMsg");
	var key = client.key;
	
	if(action=="accept"){
		//new client
		this.clientManager.addClient(client);
		return;
	}else if(action=="close"){
		this.clientManager.removeClient(client);
		return;
	}else if(action=="message"){
		//msg
		var obj=null;
		try{
			obj=BSON.parse(buffer.binaryData);
		}catch(e){
			log.error("can not parse buffer to json");
			return;
		}
		
		//{"msgId":"addTag"|"removeTag",tag:""}
		try{
			if(obj['msgId']=="addTag"){
				this.clientManager.addTag(client,obj['tag']);
				
				//send history
				var arr=this.stockData.getTagData(obj['tag']);
				if(arr.length>0){
					var msg={"msgId":"history","data":arr};
					var buffer = BSON.serialize(msg);
					client.connection.sendBytes(buffer);
				}
				
				
			}else if(obj['msgId']=="removeTag"){
				this.clientManager.removeTag(client,obj['tag']);
			}
		}catch(e){
			log.error("error when process object=%",JSON.stringify(obj));
			return;
		}
	}
}

/*** http ***/
var contentTypesByExtension = {
	    '.html': "text/html",
	    '.css':  "text/css",
	    '.js':   "text/javascript"
};
MsgProcessor.prototype.processHttpServerMsg=function (request,response){
	log.debug("processHttpServerMsg");
	
	var uri = url.parse(request.url).pathname;
    var rootDir = path.resolve(__dirname,"./../");
    var filename = path.join(rootDir, this.wsHttpServer.getPublicDir());
    filename = path.join(filename, uri);

  	path.exists(filename, function(exists) {
	    if(!exists) {
	      response.writeHead(404, {"Content-Type": "text/plain"});
	      response.write("404 Not Found\n");
	      response.end();
	      return;
	    }

    	if (fs.statSync(filename).isDirectory()) filename += '/index.html';

	    fs.readFile(filename, "binary", function(err, file) {
		      if(err) {        
		        response.writeHead(500, {"Content-Type": "text/plain"});
		        response.write(err + "\n");
		        response.end();
		        return;
		      }
		
		      var headers = {};
		      var contentType = contentTypesByExtension[path.extname(filename)];
		      if (contentType) headers["Content-Type"] = contentType;
		      response.writeHead(200, headers);
		      response.write(file, "binary");
		      response.end();
	    });
	 });
}

exports = module.exports=MsgProcessor;