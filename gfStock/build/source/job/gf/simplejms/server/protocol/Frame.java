package job.gf.simplejms.server.protocol;

public class Frame {
	protected int totalLen = 0;
	protected byte data[];
	
	public Frame(int len){
		totalLen=len;
		data=new byte[len];
	}

	public int getTotalLen() {
		return totalLen;
	}

	public byte[] getData() {
		return data;
	}

}
