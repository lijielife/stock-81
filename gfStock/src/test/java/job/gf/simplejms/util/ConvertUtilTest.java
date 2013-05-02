package job.gf.simplejms.util;

import junit.framework.Assert;

import org.junit.Test;

public class ConvertUtilTest {
	@Test
    public void testByteToInt() {
		byte[] bs = new byte[0];
		int n = ConvertUtil.byteToInt(bs);
		Assert.assertTrue(n==0);
        
    }
}
