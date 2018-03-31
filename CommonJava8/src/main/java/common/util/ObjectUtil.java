package common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectUtil {
	
	private ObjectUtil() {
		super();
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ObjectUtil.class);

	public static boolean isBlank(Object obj, String fieldName) {
		String str = null;
		try {
			Field field = obj.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			Object value = field.get(obj);
			str = (String) value;
		} catch (Exception e) {
			logger.error("", e);
		}
		return (str == null) || (str.trim().length() == 0);
	}

	/**
	 * 요청 파라미터를 해당 Object에 설정
	 * @param request
	 * @param obj
	 * @return
	 */
	public static Object setReqParamToObject(HttpServletRequest request, Object obj) {
		String sKey = "";
		String sMethodStr = "";
		Enumeration<?> params = request.getParameterNames();

		while( params.hasMoreElements() ) {
			sKey = (String) params.nextElement();
			sMethodStr = "set" + sKey.substring(0,1).toUpperCase() + sKey.substring(1);

			try {
				Method[] methods = obj.getClass().getDeclaredMethods();
				for (int i=0; i<=methods.length-1; i++) {
					if ( sMethodStr.equals(methods[i].getName()) ) {
						methods[i].invoke(obj, request.getParameter(sKey));
					}
				}
			} catch (Exception e) {
				logger.error("", e);
			}
		}
		return obj;
	}

	/**
	 * 해당 Object를 Map으로 변환
	 * @param obj
	 * @param map
	 * @throws Exception
	 */
	public static Map<String, Object> convertObjectToMap(Object obj) {
		Map<String, Object> commandMap = new HashMap<>();

		try {
			Field[] fields = obj.getClass().getDeclaredFields();
			for (int i=0; i<fields.length; i++) {
				fields[i].setAccessible(true);
				String key = fields[i].getName();
				Object value = fields[i].get(obj);
				
				if ( !key.equals("serialVersionUID") ) {
					commandMap.put(key, (value != null) ? value:"");
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return commandMap;
	}

}
