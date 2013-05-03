package job.gf.simplejms.util.server.protocol;

import job.gf.simplejms.server.protocol.MsgFrame;
import junit.framework.Assert;

import org.junit.Test;

public class MsgFrameTest {
	@Test
    public void testWriteData() {
		byte[] data = new byte[4];
		
		MsgFrame frame = new MsgFrame(4);
		int writeLen = frame.writeData(data,2);
		Assert.assertFalse(frame.isOk());
		Assert.assertTrue(frame.getWriteLen()==data.length-2);
		Assert.assertTrue(writeLen==2);
		
		writeLen=frame.writeData(data,2);
		Assert.assertTrue(frame.isOk());
		Assert.assertTrue(writeLen==2);
		
		data=new byte[100];
		frame=new MsgFrame(10);
		writeLen=frame.writeData(data);
		Assert.assertTrue(frame.isOk());
		Assert.assertTrue(writeLen==10);
		
		data=new byte[]{0,8,100,8,4,7,9};
		frame=new MsgFrame(5);
		writeLen=frame.writeData(data);
		Assert.assertTrue(frame.isOk());
		Assert.assertTrue(writeLen==5);
		
		byte[] frameData = frame.getData();
		for(int i=0;i<frameData.length;i++){
			Assert.assertTrue(frameData[i]==data[i]);
		}
		
		frame=new MsgFrame(4);
		writeLen=frame.writeData(data,2);
		Assert.assertTrue(frame.isOk());
		Assert.assertTrue(writeLen==4);
		
		frameData = frame.getData();
		for(int i=0;i<frameData.length;i++){
			Assert.assertTrue(frameData[i]==data[i+2]);
		}
        
    }
}
