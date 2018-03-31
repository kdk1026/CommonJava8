package common.util.map;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapUtil {
	
	private MapUtil() {
		super();
	}
	
	private static final Logger logger = LoggerFactory.getLogger(MapUtil.class);

	public static boolean isBlank(Map<String, Object> dataMap, String key) {
		String str = (String) dataMap.get(key);
		return (str == null) || (str.trim().length() == 0);
	}

	/**
	 * 요청 파라미터를 Map에 설정
	 * @param request
	 * @return
	 */
	public static Map<String, Object> setReqParamToMap(HttpServletRequest request) {
		Map<String, Object> commandMap = new HashMap<>();
		Enumeration<?> paramEnum = request.getParameterNames();

		while (paramEnum.hasMoreElements()) {
			String key = (String) paramEnum.nextElement();
			Object[] values = request.getParameterValues(key);
			commandMap.put(key, (values.length > 1) ? values:values[0]);
		}
		return commandMap;
	}

	/**
	 * 요청 파라미터를 Map에 설정 후, 값이 null인 Key에 기본 값 설정
	 * @param request
	 * @param commandMap
	 * @param nameArray
	 */
	public static Map<String, Object> setReqParamToDefaultValMap(HttpServletRequest request,
			String[] nameArray, String str) {
		Map<String, Object> commandMap = setReqParamToMap(request);

		for (String key : nameArray) {
			Object value = request.getParameter(key);
			commandMap.put(key, (value != null) ? value:str);
		}
		return commandMap;
	}

	/**
	 * 해당 Map을 해당 Object로 변환
	 * @param map
	 * @param obj
	 * @return
	 */
	public static Object convertMapToObject(Map<String, Object> map, Object obj) {
		String key = "";
		String setMethodStr = "set";
		String methodStr = "";

		Iterator<String> it = map.keySet().iterator();
		while ( it.hasNext() ) {
			key = it.next();
			methodStr = setMethodStr+key.substring(0,1).toUpperCase()+key.substring(1);
			try {
				Method[] methods = obj.getClass().getDeclaredMethods();
				for (int i=0; i<methods.length; i++) {
					if ( methodStr.equals(methods[i].getName()) ) {
						methods[i].invoke(obj, map.get(key));
					}
				}
			} catch (Exception e) {
				logger.error("convertMapToObject Exception", e);
			}
		}
		return obj;
	}
	
	/**
	 * 해당 Map을 해당 Struct로 변환
	 * @param map
	 * @param obj
	 * @return
	 */
	public static Object convertMapToStruct(Map<String, Object> map, Object obj) {
		String key = "";

		Iterator<String> it = map.keySet().iterator();
		while ( it.hasNext() ) {
			key = it.next();
			
			try {
				Field[] fields = obj.getClass().getDeclaredFields();
				String name = "";
				Class<?> cls = obj.getClass();
				
				for (int i=0; i<fields.length; i++) {
					name = fields[i].getName();
					
					if ( key.equals(name) ) {
						cls.getField(name).set(obj, map.get(key));
					}
				}
			} catch (Exception e) {
				logger.error("", e);
			}
		}
		return obj;
	}

	/**
	 * 해당 Object를 해당 Map에 변환하여 추가
	 * @param obj
	 * @param commandMap
	 * @return
	 */
	public static Map<String, Object> addObjectToMap(Object obj, Map<String, Object> commandMap) {
		try {
			Field[] fields = obj.getClass().getDeclaredFields();
			for (int i=0; i<fields.length; i++) {
				fields[i].setAccessible(true);
				String key = fields[i].getName();
				Object value = fields[i].get(obj);
				commandMap.put(key, (value != null) ? value:"");
			}
		} catch (Exception e) {
			logger.error("addObjectToMap Exception", e);
		}
		return commandMap;
	}
}
