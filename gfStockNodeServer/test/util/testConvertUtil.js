var assert = require("assert");
var convertUtil=require("./../../lib/util/convertUtil");

describe('convertUtil', function(){
	it("int<->Byte",function(){
		var num=41;
		var bs=convertUtil.intToBinary(num);
		var vNum=convertUtil.binaryToInt(bs);
		assert.equal(num,vNum);
		
		num=300;
		bs=convertUtil.intToBinary(num);
		vNum=convertUtil.binaryToInt(bs);
		assert.equal(num,vNum);
		
		num=10000;
		bs=convertUtil.intToBinary(num);
		vNum=convertUtil.binaryToInt(bs);
		assert.equal(num,vNum);
		
		num=700;
		bs=convertUtil.intToBinary(num);
		vNum=convertUtil.binaryToInt(bs);
		assert.equal(num,vNum);
	});
})