package job.gf.simplejms.server;

import job.gf.simplejms.util.ConvertUtil;

public class TagSubscribeUtil {
	// 4字节
	public static int byteToMsgType(byte[] bs) {
		// 四位长度
		int d = ConvertUtil.byteToInt(bs);

		return d;
	}

	// 4字节
	public static byte[] msgTypeToByte(int n) {
		// 四位长度
		byte[] bs = ConvertUtil.intToByte(n);

		return bs;
	}

	// 4字节
	public static int byteToMsgTag(byte[] bs) {
		// 四位长度
		int d = ConvertUtil.byteToInt(bs);

		return d;
	}

	// 4字节
	public static byte[] msgTagToByte(int n) {
		// 四位长度
		byte[] bs = ConvertUtil.intToByte(n);

		return bs;
	}

}
