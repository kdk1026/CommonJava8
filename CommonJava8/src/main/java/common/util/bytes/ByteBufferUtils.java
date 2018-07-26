package common.util.bytes;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ByteBufferUtils {
	
	private ByteBufferUtils() {
		super();
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ByteBufferUtils.class);
	
	public static ByteBuffer toByteBufferString(String str, String sEncoding) {
		byte[] b = null;
		
		try {
			b = str.getBytes(sEncoding);
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		}
		
		if (b == null) {
			return null;
		}
		
		return ByteBuffer.wrap(b);
	}
	
	public static ByteBuffer toByteBufferString(List<String> list, String sEncoding) {
		int nByteLen = 0;
		
		for (String s : list) {
			try {
				nByteLen += s.getBytes(sEncoding).length;
			} catch (UnsupportedEncodingException e) {
				logger.error("", e);
			}
		}
		
		ByteBuffer buffer = ByteBuffer.allocate(nByteLen);
		
		for (String s : list) {
			try {
				buffer.put(s.getBytes(sEncoding));
			} catch (UnsupportedEncodingException e) {
				logger.error("", e);
			}
		}
		
		buffer.flip();
		return buffer;
	}

	public static ByteBuffer toByteBufferObject(Object obj, String sEncoding) {
		int nByteLen = 0;
		
		try {
			Field[] fields = obj.getClass().getDeclaredFields();
			for (Field f : fields) {
				nByteLen += f.get(obj).toString().getBytes(sEncoding).length;
			}
			
		} catch (Exception e) {
			logger.error("", e);
		}
		
		ByteBuffer buffer = ByteBuffer.allocate(nByteLen);

		try {
			Field[] fields = obj.getClass().getDeclaredFields();
			for (Field f : fields) {
				buffer.put(f.get(obj).toString().getBytes(sEncoding));
			}
			
		} catch (Exception e) {
			logger.error("", e);
		}
		
		buffer.flip();
		return buffer;
	}
	
	public static ByteBuffer toByteBufferObject(List<Object> list, String sEncoding) {
		int nByteLen = 0;
		Field[] fields = null;
		
		for (Object obj : list) {
			try {
				fields = obj.getClass().getDeclaredFields();
				for (Field f : fields) {
					nByteLen += f.get(obj).toString().getBytes(sEncoding).length;
				}
			} catch (Exception e) {
				logger.error("", e);
			}
		}
		
		ByteBuffer buffer = ByteBuffer.allocate(nByteLen);

		for (Object obj : list) {
			try {
				fields = obj.getClass().getDeclaredFields();
				for (Field f : fields) {
					buffer.put(f.get(obj).toString().getBytes(sEncoding));
				}
			} catch (Exception e) {
				logger.error("", e);
			}
		}
		
		buffer.flip();
		return buffer;
	}
	
	public static ByteBuffer toByteBufferMap(Map<String, Object> map, String sEncoding) {
		int nByteLen = 0;
		String sKey = "";
		Iterator<String> it = map.keySet().iterator();
		
		while (it.hasNext()) {
			sKey = it.next();
			nByteLen += String.valueOf(map.get(sKey)).getBytes().length;
		}
		
		ByteBuffer buffer = ByteBuffer.allocate(nByteLen);
		
		while (it.hasNext()) {
			sKey = it.next();
			try {
				buffer.put(String.valueOf(map.get(sKey)).getBytes(sEncoding));
			} catch (UnsupportedEncodingException e) {
				logger.error("", e);
			}
		}
		
		buffer.flip();
		return buffer;
	}
	
	public static ByteBuffer toByteBufferMap(List<Map<String, Object>> list, String sEncoding) {
		int nByteLen = 0;
		String sKey = "";
		Iterator<String> it = null;
		
		for (Map<String, Object> map : list) {
			it = map.keySet().iterator();
			while (it.hasNext()) {
				sKey = it.next();
				nByteLen += String.valueOf(map.get(sKey)).getBytes().length;
			}
		}
		
		ByteBuffer buffer = ByteBuffer.allocate(nByteLen);
		
		for (Map<String, Object> map : list) {
			it = map.keySet().iterator();
			while (it.hasNext()) {
				sKey = it.next();
				try {
					buffer.put(String.valueOf(map.get(sKey)).getBytes(sEncoding));
				} catch (UnsupportedEncodingException e) {
					logger.error("", e);
				}
			}
		}
		
		buffer.flip();
		return buffer;
	}
	
}
