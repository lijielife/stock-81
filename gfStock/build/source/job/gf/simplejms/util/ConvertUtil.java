package job.gf.simplejms.util;

public class ConvertUtil {
	public static int byteToInt(byte[] res){
		if(res==null || res.length==0) return 0;
		
		int targets=0;
		if(res.length>0){
			targets=res[0]&0xff | targets;
		}
		if(res.length>1){
			targets=(res[1] << 8) & 0xff00 | targets;
		}
		if(res.length>2){
			targets=(res[2] << 16) & 0xff0000 | targets;
		}
		if(res.length>3){
			targets=(res[3] << 24) & 0xff000000 | targets;
		}
		
		return targets;
	}
	
	public static byte[] intToByte(int res){
		byte[] targets = new byte[4];

		targets[0] = (byte) (res & 0xff);// 最低位 
		targets[1] = (byte) ((res >> 8) & 0xff);// 次低位 
		targets[2] = (byte) ((res >> 16) & 0xff);// 次高位 
		targets[3] = (byte) (res >>> 24);// 最高位,无符号右移。 
		return targets; 
	}
}
