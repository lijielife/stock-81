var MsgFrame=require("./MsgFrame");
var convertUtil=require("./../util/convertUtil");

var MsgFrameParser=exports = module.exports=function (){
	var currentFrame;
	
	var LenReadBuffer=[0,0,0,0];
	var lenReadNum=0;
	var isReadLen=false;
	
	var FRAME_BYTE=0X31;
	var FRAME_BEGIN= [FRAME_BYTE];
	var LEN_LEN=4;
	
	var readData=null;
	var start=0;
	
	this.setReadData=function(data){
		readData=data;
		start=0;
	}
	
	this.read=function(){
		if(readData==null || readData.length==0 || readData.length<=start) return null;
		
		//new frame
		if(currentFrame==null){
			if(!isReadLen){
				//find begin
				for(;start<readData.length;start++){
					//one byte is zero
					if(readData[start]==FRAME_BYTE){
						isReadLen=true;
						lenReadNum=0;
						start++;
						break;
					}
				}
			}
			
			if(isReadLen){
				//read rest len
				//find begin
				for(;start<readData.length && lenReadNum<LEN_LEN;start++){
					//one byte is zero
					LenReadBuffer[lenReadNum]=readData[start];
					lenReadNum++;
				}
			}
			
			//len
			//read not header
			if(lenReadNum!=LEN_LEN){
				return null;
			}
			
			
			var len = convertUtil.binaryToInt(LenReadBuffer);
			isReadLen=false;
			if(len<=0){
				return MsgFrame.ZERO_MSGFRAME;
			}
			
			currentFrame=new MsgFrame(len);
		}
		
		var restLen = currentFrame.getRestLen();
		if(restLen>0){
			var readLen=currentFrame.writeData(readData, start);
			start+=readLen;
		}
		
		if(currentFrame.isOk()){
			var temp = currentFrame;
			currentFrame=null;
			isReadLen=false;
			return temp;
		}
		
		return null;
	}

	
	this.clear=function(){
		currentFrame=null;
		isReadLen=false;
	}
}



