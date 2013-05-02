var assert = require("assert");
var MsgFrameParser=require("./../../lib/protocol/MsgFrameParser");

describe('MsgFrameParser', function(){
	it("read",function(){
		var parser = new MsgFrameParser();
		var data=[0x31,1,0,0,0];
		parser.setReadData(data);
		var f=parser.read(data);
		assert.ok(f==null);
		
		parser.setReadData([2]);
		f=parser.read(data);
		assert.ok(f!=null);
		assert.ok(f.getData()[0]=2);
		
		data=[0x31,2,0,0,0,3,4];
		parser.setReadData(data);
		f=parser.read(data);
		var data = f.getData();
		assert.ok(data!=null);
		assert.ok(data[0]==3);
		assert.ok(data[1]==4);
	});
});