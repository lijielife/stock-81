package job.gf.simplejms.client;

import job.gf.simplejms.server.IMsg;
import job.gf.simplejms.server.TagSubscribeUtil;
import job.gf.simplejms.server.protocol.Frame;

public class ClientTagMsg implements IMsg{
	private int tag;
	private byte data[];
	private boolean isOk=false;
	private long time;
	
	public ClientTagMsg(Frame frame){
		time=System.currentTimeMillis();
		
		if(frame.getTotalLen()<4){
			return;
		}
		
		byte[] buf = new byte[4];
		byte[] frameData = frame.getData();
		
		System.arraycopy(frameData, 0, buf, 0, 4);
		tag= TagSubscribeUtil.byteToMsgTag(buf);
		
		int len = frame.getTotalLen()-4;
		if(len>0){
			data=new byte[len];
			System.arraycopy(frameData, 4, data, 0, len);
		}
		isOk=true;
	}
	
	public boolean isOk(){
		return isOk;
	}
	
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}

	public long getTime() {
		return time;
	}

	public int getTag() {
		return tag;
	}

	public int getDataLen(){
		return data.length;
	}
	
	

}
