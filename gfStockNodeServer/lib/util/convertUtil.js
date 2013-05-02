//int to binary 4
var intToBinary = function(n){
	var arr=[0,0,0,0];
	arr[0]=n&0xff;
	arr[1]=(n>>8)&0xff;
	arr[2]=(n>>16)&0xff;
	arr[3]=n>>24;
	
	return arr;
};

var intToBuffer = function(n){
	var arr=new Buffer(4);
	arr[0]=n&0xff;
	arr[1]=(n>>8)&0xff;
	arr[2]=(n>>16)&0xff;
	arr[3]=n>>24;
	
	return arr;
};

var binaryToInt=function(res){
	var targets=0;
	if(res.length>0){
		targets=res[0]&0xff| targets;
	}
	if(res.length>1){
		targets=(res[1] << 8) & 0xff00 | targets;
	}
	if(res.length>2){
		targets=(res[2] << 16) & 0xff0000 | targets;
	}
	if(res.length>3){
		targets=(res[3] << 24) & 0xff000000 | targets;
	}
	
	return targets;
}

exports.intToBinary = intToBinary;
exports.intToBuffer = intToBuffer;
exports.binaryToInt = binaryToInt;