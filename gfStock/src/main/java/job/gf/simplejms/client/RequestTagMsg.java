package job.gf.simplejms.client;


public class RequestTagMsg{
	public static final int TYPE_PUBLIC=1;  //public tag
	public static final int TYPE_SUBSCRIBE=2; //subscribe tag
	public static final int TYPE_UNSUBSCRIBE=3; //subscribe tag
	
	
	private int type;
	private int tag;
	private byte data[];
	
	public RequestTagMsg(){
		
	}
	
	public boolean isTypePublic(){
		return type==TYPE_PUBLIC;
	}
	public boolean isTypeSubscribe(){
		return type==TYPE_SUBSCRIBE;
	}
	
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}

	public int getType() {
		return type;
	}

	public int getTag() {
		return tag;
	}

	public int getDataLen(){
		return data==null?0:data.length;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}
	
	
	
	

}
