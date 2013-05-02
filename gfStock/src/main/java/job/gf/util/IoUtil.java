package job.gf.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IoUtil {
	static final String DEFAULT_CODE="utf8";
	
	public static List<String> lines(InputStream in){
		return lines(in,DEFAULT_CODE);
	}
	
	public static List<String> lines(InputStream in,String code){
		InputStreamReader reader=null;
		try {
			reader = new InputStreamReader(in,code);
		} catch (UnsupportedEncodingException e1) {
			closeSilent(in);
			return null;
		}
		
		BufferedReader bufReader = new BufferedReader(reader);
		
		List<String> ret=new ArrayList<String>();
		String line=null;
		try {
			while((line=bufReader.readLine())!=null){
				ret.add(line);
			}
		} catch (IOException e) {
			return null;
		}finally{
			closeSilent(bufReader);
			closeSilent(reader);
			closeSilent(in);
		}
		
		return ret;
	}
	
	public static void closeSilent(Reader reader){
		if(reader!=null){
			try {
				reader.close();
			} catch (IOException e) {
			}
		}
	}
	
	public static void closeSilent(InputStream reader){
		if(reader!=null){
			try {
				reader.close();
			} catch (IOException e) {
			}
		}
	}
	
	public static Map<String,String> loadProp(InputStream in){
		if(in==null) return null;
		
		List<String> lines = lines(in);
		if(lines!=null){
			Map<String,String> vs = new HashMap<String,String>(8);
			for(String s:lines){
				s=s.trim();
				if(s.startsWith("#")) continue;
				
				String params[]=s.split("=", 2);
				if(params.length!=2){
					continue;
				}
				
				String key = params[0].trim();
				String value = params[1].trim();
				
				vs.put(key, value);
			}
			
			return vs;
		}
		
		return null;
	}
}
