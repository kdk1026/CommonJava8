package common.util.bytes;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ByteUtils {

	private ByteUtils() {
		super();
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ByteUtils.class);
	
	/**
	 * Object의 Bytes 길이 구하기
	 * @param obj
	 * @return
	 */
	public static int getByteLength(Object obj, String sEncoding) {
		int nByteLen = 0;
		try {
			Field[] fields = obj.getClass().getDeclaredFields();
			for (Field f : fields) {
				if (f.get(obj) != null) {
					nByteLen += f.get(obj).toString().getBytes(sEncoding).length;
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return nByteLen;
	}
	
	/**
	 * Map의 Bytes 길이 구하기 
	 * @param map
	 * @param sEncoding
	 * @return
	 */
	public static int getByteLength(Map<String, Object> map, String sEncoding) {
		int nByteLen = 0;
		try {
			Iterator<String> it = map.keySet().iterator();
			String key = "";
			while (it.hasNext()) {
				key = it.next();
				nByteLen += (String.valueOf(map.get(key))).getBytes(sEncoding).length;
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return nByteLen;
	}
	
	// TODO: 추후 정리 <아래 URL 참고>
	// https://github.com/okjsp/okjsp-android/blob/master/okjsp-android/src/com/tistory/iiixzu/common/ByteUtils.java
	
}
