exports = module.exports=function MsgFrame(stotalLen){
	totalLen=stotalLen;
	data=new Buffer(stotalLen);
	writeLen=0;
	
	//isOk
	this.isOk=function(){
		return writeLen==totalLen;
	}
	
	this.getRestLen=function() {
		return totalLen-writeLen;
	}
	
	this.getWriteLen=function(){
		return writeLen;
	}
	
	this.getTotalLen=function(){
		return totalLen;
	}
	
	this.getData=function(){
		return data;
	}
	
	this.writeData=function(d,start){
		if(d==null || d.length==0 || this.isOk()) return 0;
		
		var len = d.length-start;
		var restLen = this.getRestLen();
		len = Math.min(len, restLen);
		if(len==0) return 0;
		
		for(var i=0;i<len;i++){
			data[i+writeLen]=d[start+i];
		}
		writeLen+=len;
		return len;
	}
}


