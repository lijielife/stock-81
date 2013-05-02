package job.gf.simplejms.util.server.protocol;

import java.util.Arrays;

import job.gf.simplejms.server.protocol.MsgFrame;
import job.gf.simplejms.server.protocol.MsgFrameParser;
import junit.framework.Assert;

import org.junit.Test;

public class MsgFrameParserTest {
	@Test
	public void testReadSimple() {
		byte[] data = new byte[] { MsgFrameParser.FRAME_BYTE, 2, 0, 0, 0, 3, 4,
				8, 9 };

		MsgFrameParser parser = new MsgFrameParser();
		parser.setReadData(data);

		MsgFrame frame = parser.read();
		Assert.assertNotNull(frame);

		byte[] frameData = frame.getData();
		for (int i = 0; i < frameData.length; i++) {
			Assert.assertTrue(frameData[i] == data[5 + i]);
		}
	}

	@Test
	public void testReadSimple2() {
		byte[] data = new byte[] { MsgFrameParser.FRAME_BYTE, 2, 0, 0, 0, 3, 4,
				MsgFrameParser.FRAME_BYTE, 1, 0, 0, 0, 20 };

		MsgFrameParser parser = new MsgFrameParser();
		parser.setReadData(data);

		MsgFrame frame = parser.read();
		Assert.assertNotNull(frame);

		byte[] frameData = frame.getData();
		for (int i = 0; i < frameData.length; i++) {
			Assert.assertTrue(frameData[i] == data[5 + i]);
		}

		frame = parser.read();
		Assert.assertNotNull(frame);

		frameData = frame.getData();
		for (int i = 0; i < frameData.length; i++) {
			Assert.assertTrue(frameData[i] == data[12 + i]);
		}
	}
	
	@Test
	public void testReadSimple3() {
		byte[] data = new byte[] { MsgFrameParser.FRAME_BYTE, 2, 0, 0, 0, 98, 99,
				MsgFrameParser.FRAME_BYTE, 1, 0, 0, 0, 20,MsgFrameParser.FRAME_BYTE,4,0,0,0,1,2,3,4,MsgFrameParser.FRAME_BYTE,5,0,0,0,0,5,5,5};

		MsgFrameParser parser = new MsgFrameParser();
		parser.setReadData(data);

		MsgFrame frame = parser.read();
		Assert.assertNotNull(frame);
		Assert.assertTrue(Arrays.equals(frame.getData(), new byte[]{98,99}));

		frame = parser.read();
		Assert.assertNotNull(frame);
		Assert.assertTrue(Arrays.equals(frame.getData(), new byte[]{20}));
		
		frame = parser.read();
		Assert.assertNotNull(frame);
		Assert.assertTrue(Arrays.equals(frame.getData(), new byte[]{1,2,3,4}));
		
		frame = parser.read();
		Assert.assertNull(frame);
	}
	
	@Test
	public void testReadSimple4() {
		byte[] data1 = new byte[] { 4,1,MsgFrameParser.FRAME_BYTE};
		byte[] data2 = new byte[] { 3,0};
		byte[] data3 = new byte[] { 0,0,1};
		byte[] data4 = new byte[] { 2,3};

		MsgFrameParser parser = new MsgFrameParser();
		parser.setReadData(data1);

		MsgFrame frame = parser.read();
		Assert.assertNull(frame);
		
		parser.setReadData(data2);
		frame = parser.read();
		Assert.assertNull(frame);
		

		parser.setReadData(data3);
		frame = parser.read();
		Assert.assertNull(frame);
		

		parser.setReadData(data4);
		frame = parser.read();
		Assert.assertNotNull(frame);
		
	}
}
