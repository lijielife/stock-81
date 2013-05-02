var assert = require("assert");
var MsgFrame=require("./../../lib/protocol/MsgFrame");

describe('MsgFrame', function(){
	it("writeData",function(){
		var msg = new MsgFrame(3);
		var d=[1,2,3,4,5];
		assert.equal(msg.isOk(),false);
		var start=0;
		var readLen=0;
		readLen=msg.writeData(d,start);
		assert.ok(msg.isOk());
		assert.ok(readLen==3);
		
		msg = new MsgFrame(4);
		var d=[1,2];
		assert.equal(msg.isOk(),false);
		var readLen=0;
		readLen=msg.writeData(d,0);
		assert.ok(msg.isOk()==false);
		assert.ok(readLen==2);
		
		d=[3,4,5];
		readLen=msg.writeData(d,0);
		assert.ok(msg.isOk());
		assert.ok(readLen==2);
	});
});