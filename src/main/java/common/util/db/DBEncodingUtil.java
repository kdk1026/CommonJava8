package common.util.db;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBEncodingUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(DBEncodingUtil.class);
	
	private DBEncodingUtil() {
		super();
	}
	
	/**
	 * @since 1.7
	 */
	private static final String ISO_8859_1 = StandardCharsets.ISO_8859_1.toString();
	
	private static final String MS949 = Charset.forName("ms949").toString();
	
	/**
	 * @since 1.7
	 */
	private static final String UTF_8 = StandardCharsets.UTF_8.toString();

	public static class US7ASCII {
		private US7ASCII() {
			super();
		}
		
		/**
		 * 해당 key의 값이 null이면 빈 값으로 처리
		 */
		public static class ReadColumn {
			private ReadColumn() {
				super();
			}
			
			public static String readHangeul(String val) {
				String sVal = "";
				try {
					sVal = new String(val.getBytes(ISO_8859_1), MS949);
				} catch (UnsupportedEncodingException e) {
					logger.error("", e);
				}
				return sVal;
			}
			
			public static void readHangeul(Map<Object, Object> map, String ... keys) {
				String key = "";
				String val = "";
				
				for (int i=0; i < keys.length; i++) {
					key = keys[i];
					
					if (map.containsKey(key)) {
						val = readHangeul( String.valueOf(map.get(key)) );
						map.put(key, val);
					} else {
						map.put(key, "");
					}
				}
			}
			
			public static void readHangeul(List<Map<Object, Object>> list, String ... keys) {
				for (Map<Object, Object> map : list) {
					readHangeul(map, keys);
				}
			}
		}
		
		/**
		 * 해당 key의 값이 null이면 빈 값으로 처리
		 */
		public static class ScalaSubQuery {
			private ScalaSubQuery() {
				super();
			}
			
			public static String readHangeul(String val) {
				String sVal = "";
				try {
					sVal = new String(val.getBytes(ISO_8859_1), UTF_8);
				} catch (UnsupportedEncodingException e) {
					logger.error("", e);
				}
				return sVal;
			}
			
			public static void readHangeul(Map<Object, Object> map, String ... keys) {
				String key = "";
				String val = "";
				
				for (int i=0; i < keys.length; i++) {
					key = keys[i];
					
					if (map.containsKey(key)) {
						val = readHangeul( String.valueOf(map.get(key)) );
						map.put(key, val);
					} else {
						map.put(key, "");
					}
				}
			}
			
			public static void readHangeul(List<Map<Object, Object>> list, String ... keys) {
				for (Map<Object, Object> map : list) {
					readHangeul(map, keys);
				}
			}
		}

		public static void writeHangeul(Map<Object, Object> map, String ... keys) {
			String key = "";
			String val1 = "";
			String val2 = "";
			
			for (int i=0; i < keys.length; i++) {
				key = keys[i];
				
				if (map.containsKey(key)) {
					val1 = String.valueOf(map.get(key));
					
					try {
						val2 = new String(val1.getBytes(MS949), ISO_8859_1);
						
					} catch (UnsupportedEncodingException e) {
						logger.error("", e);
					}

					map.put(key, val2);
				}
			}
		}
	}
	
}
