package common.util.map;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
 * 2018. 9.  3. 김대광	최초작성
 * 2021. 8. 13. 김대광	SonarLint 지시에 따른 주저리 주저리
 * </pre>
 */
public class MapUtil {

	private static final Logger logger = LoggerFactory.getLogger(MapUtil.class);

	private MapUtil() {
		super();
	}

	private static class ExceptionMessage {

		public static String isNull(String paramName) {
	        return String.format("'%s' is null", paramName);
	    }

		public static String isNullOrEmpty(String paramName) {
	        return String.format("'%s' is null or empty", paramName);
	    }

	}

	/**
	 * Object를 Map<String, Object> 으로 변환
	 * @param obj
	 * @param map
	 */
	public static Map<String, Object> objectToMapObject(Object obj) {
		Objects.requireNonNull(obj, ExceptionMessage.isNull("obj"));

		Map<String, Object> map = new HashMap<>();

		try {
			Field[] fields = obj.getClass().getDeclaredFields();

			for (int i=0; i<fields.length; i++) {
				fields[i].setAccessible(true);

				if ( Collection.class.isAssignableFrom(fields[i].getType() )) {
					String sArrKey = fields[i].getName();
					@SuppressWarnings("unchecked")
					List<Object> objList = (List<Object>) fields[i].get(obj);

					List<Map<String, Object>> mapList = new ArrayList<>();

					for (Object objArr : objList) {
						Map<String, Object> objMap = objectToMapObject(objArr);
						mapList.add(objMap);
						map.put(sArrKey, mapList);
					}
				} else {
					String sKey = fields[i].getName();
					Object value = fields[i].get(obj);
					map.put(sKey, (value != null) ? value:"");
				}
			}

		} catch (IllegalArgumentException | IllegalAccessException e) {
			logger.error("", e);
		}

		return map;
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
		Objects.requireNonNull(obj, ExceptionMessage.isNull("obj"));

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
		if ( map == null || map.isEmpty() ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("map"));
		}

		if ( StringUtils.isBlank(key) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("key"));
		}

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
		if ( map == null || map.isEmpty() ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("map"));
		}

		if ( keys == null || keys.length == 0 ) {
			throw new IllegalArgumentException(ExceptionMessage.isNull("keys"));
		}

		String key = "";

		for (int i=0; i < keys.length; i++) {
			key = keys[i];

			map.computeIfAbsent(key, k -> "");
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
		if ( map == null || map.isEmpty() ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("map"));
		}

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
		if ( map == null || map.isEmpty() ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("map"));
		}

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
