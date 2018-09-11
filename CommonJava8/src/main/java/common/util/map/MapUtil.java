/**
 * 
 */
package common.util.map;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 2018. 9. 3.
 * @author 김대광
 * @Description	: Commons lang, beanutils Standard 
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2018. 9. 3. 김대광	최초작성
 * </pre>
 */
public class MapUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(MapUtil.class);

	private MapUtil() {
		super();
	}

	/**
	 * @Description
	 * <pre>
	 * Object를 Map<String, String> 으로 변환
	 * </pre>
	 * @param obj
	 * @return
	 * @since 1.7
	 * <pre>
	 * -----------------------------------
	 * 개정이력
	 * 2018. 9. 3. 김대광	최초작성
	 * </pre>
	 */
	public static Map<String, String> objectToMap(Object obj) {
		Map<String, String> map = null;
		
		try {
			map = BeanUtils.describe(obj);
			map.remove("class");
			
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			logger.error("", e);
		}
		
		return map;
	}
	
	/**
	 * @Description
	 * <pre>
	 * Map의 Key가 Blank인지 체크
	 * </pre>
	 * @param map
	 * @param key
	 * @return
	 * <pre>
	 * -----------------------------------
	 * 개정이력
	 * 2018. 9. 3. 김대광	최초작성
	 * </pre>
	 */
	public static boolean isBlank(Map<String, Object> map, String key) {
		if ( map.get(key) == null ) {
			return true;
		} else {
			return StringUtils.isBlank( String.valueOf(map.get(key)) );
		}
	}
	
	/**
	 * @Description
	 * <pre>
	 * Map에 Key가 없으면 Blank 처리
	 * </pre>
	 * @param map
	 * @param keys
	 * <pre>
	 * -----------------------------------
	 * 개정이력
	 * 2018. 9. 3. 김대광	최초작성
	 * </pre>
	 */
	public static void notContainsKeyToBlank(Map<String, Object> map, String ... keys) {
		String key = "";
		
		for (int i=0; i < keys.length; i++) {
			key = keys[i];
		
			if ( !map.containsKey(key) ) {
				map.put(key, "");
			}
		}
	}
	
	/**
	 * @Description
	 * <pre>
	 * Null을 Blank 처리
	 * </pre>
	 * @param map
	 * @param keys
	 * @since 1.8
	 * <pre>
	 * -----------------------------------
	 * 개정이력
	 * 2018. 9. 3. 김대광	최초작성
	 * </pre>
	 */
	public static void nullToBlank(Map<String, Object> map) {
		String key = "";
		
		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			key = it.next();
			
			map.computeIfAbsent(key, k -> "");
		}
	}
	
	/**
	 * @Description
	 * <pre>
	 * Space를 Blank 처리
	 * </pre>
	 * @param map
	 * <pre>
	 * -----------------------------------
	 * 개정이력
	 * 2018. 9. 3. 김대광	최초작성
	 * </pre>
	 */
	public static void spaceToBlank(Map<String, Object> map) {
		String key = "";
		
		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			key = it.next();
			
			if (" ".equals( String.valueOf(map.get(key)) )) {
				map.put(key, "");
			}
		}
	}
	
}
