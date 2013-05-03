package job.gf.simplejms.server.protocol;

import job.gf.simplejms.util.ConvertUtil;

public class MsgFrameParser {
	MsgFrame currentFrame;
	
	static final int LEN_LEN=4;
	byte[] LenReadBuffer=new byte[LEN_LEN];
	int lenReadNum=0;
	boolean isReadLen=false;
	
	public static final byte FRAME_BYTE=0X31;
	public static byte[] FRAME_BEGIN= new byte[]{FRAME_BYTE};
	
	byte[] readData=null;
	int start=0;
	
	public void setReadData(byte[] data){
		readData=data;
		start=0;
	}
	
	public MsgFrame read(){
		if(readData==null || readData.length==0 || readData.length<=start) return null;
		
		//new frame
		if(currentFrame==null){
			if(!isReadLen){
				//find begin
				for(;start<readData.length;start++){
					//one byte is zero
					if(readData[start]==FRAME_BYTE){
						isReadLen=true;
						lenReadNum=0;
						start++;
						break;
					}
				}
			}
			
			if(isReadLen){
				//read rest len
				//find begin
				for(;start<readData.length && lenReadNum<LEN_LEN;start++){
					//one byte is zero
					LenReadBuffer[lenReadNum]=readData[start];
					lenReadNum++;
				}
			}
			
			//len
			//read not header
			if(lenReadNum!=LEN_LEN){
				return null;
			}
			
			
			int len = ConvertUtil.byteToInt(LenReadBuffer);
			isReadLen=false;
			if(len<=0){
				return MsgFrame.ZERO_MSGFRAME;
			}
			
			currentFrame=new MsgFrame(len);
		}
		
		int restLen = currentFrame.getRestLen();
		if(restLen>0){
			int readLen=currentFrame.writeData(readData, start);
			start+=readLen;
		}
		
		if(currentFrame.isOk()){
			MsgFrame temp = currentFrame;
			currentFrame=null;
			isReadLen=false;
			return temp;
		}
		
		return null;
	}

	
	public void clear(){
		currentFrame=null;
		isReadLen=false;
	}
	
}
