package job.gf.simplejms.util;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.bson.BasicBSONEncoder;
import org.bson.BasicBSONObject;
import org.junit.Test;

public class OneTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BasicBSONObject bson  = new BasicBSONObject();
		bson.append("name", 123);
		
		BasicBSONEncoder encoder = new BasicBSONEncoder();
		byte[] bs = encoder.encode(bson);
		StringBuilder sb = new StringBuilder();
		for(byte b:bs){
			sb.append(0xff&b).append(",");
		}
		
		System.out.println(sb.toString());
	}
	
	@Test
	public void testByteToInt() {
        
    }

}
