package job.gf.simplejms.server.protocol;

public class MsgFrame extends Frame{
	public static MsgFrame ZERO_MSGFRAME=new MsgFrame(0);
	
	private int writeLen=0;
	
	public MsgFrame(int totalLen){
		super(totalLen);
	}


	public int getWriteLen() {
		return writeLen;
	}
	
	public int getRestLen() {
		return totalLen-writeLen;
	}
	
	public int writeData(byte[] d,int start){
		if(d==null || d.length==0 || isOk()) return 0;
		
		int len = d.length-start;
		int restLen = getRestLen();
		len = Math.min(len, restLen);
		if(len==0) return 0;
		
		System.arraycopy(d, start, data, writeLen, len);
		writeLen+=len;
		return len;
	}


	public int writeData(byte[] d){
		return writeData(d,0);
	}
	
	public boolean isOk(){
		return writeLen==totalLen;
	}
}
