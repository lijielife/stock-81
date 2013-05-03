package job.gf.simplejms.server;

import job.gf.simplejms.server.protocol.Frame;

public class TagMsg implements IMsg{
	public static final int TYPE_PUBLIC=1;  //public tag
	public static final int TYPE_SUBSCRIBE=2; //subscribe tag
	public static final int TYPE_UNSUBSCRIBE=3; //subscribe tag
	
	
	private int type;
	private int tag;
	private byte data[];
	private long time=0;
	private boolean isOk=false;
	
	public TagMsg(Frame frame){
		time=System.currentTimeMillis();
		
		if(frame.getTotalLen()<8){
			return;
		}
		
		byte[] buf = new byte[4];
		byte[] frameData = frame.getData();
		System.arraycopy(frameData, 0, buf, 0, 4);
		type= TagSubscribeUtil.byteToMsgType(buf);
		
		System.arraycopy(frameData, 4, buf, 0, 4);
		tag= TagSubscribeUtil.byteToMsgTag(buf);
		
		int len = frame.getTotalLen()-8;
		if(len>0){
			data=new byte[len];
			System.arraycopy(frame.getData(), 8, data, 0, len);
		}
		isOk=true;
	}
	
	public boolean isOk(){
		return isOk;
	}
	
	public boolean isTypePublic(){
		return type==TYPE_PUBLIC;
	}
	public boolean isTypeSubscribe(){
		return type==TYPE_SUBSCRIBE;
	}
	public boolean isTypeUnSubscribe(){
		return type==TYPE_UNSUBSCRIBE;
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

	public int getType() {
		return type;
	}

	public int getTag() {
		return tag;
	}

	public int getDataLen(){
		return data.length;
	}
	
	

}
