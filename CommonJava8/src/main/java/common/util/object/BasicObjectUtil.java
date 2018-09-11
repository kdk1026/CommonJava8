package common.util.object;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicObjectUtil {
	
	private BasicObjectUtil() {
		super();
	}
	
	private static final Logger logger = LoggerFactory.getLogger(BasicObjectUtil.class);

	/**
	 * Object의 Field가 Blank인지 체크 
	 * @param obj
	 * @param fieldName
	 * @return
	 */
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
	 * Object의 Field명 추출 
	 * @param obj
	 * @return
	 * @since 1.7
	 */
	public static List<String> getFieldNames(Object obj) {
		List<String> list = new ArrayList<>();
		
		Field[] fields = obj.getClass().getDeclaredFields();
		for (Field f : fields) {
			f.setAccessible(true);
			list.add(f.getName());
		}
		
		return list;
	}

	/**
	 * 해당 Object를 Map으로 변환
	 * @param obj
	 * @param map
	 * @since 1.7
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
	 * Object를 Http Response에 설정
	 * @param obj
	 * @param response
	 * @return
	 */
	public static void setHttpResponse(Object obj, HttpServletResponse response) {
		try {
			Field[] fields = obj.getClass().getDeclaredFields();
			for (int i=0; i<fields.length; i++) {
				fields[i].setAccessible(true);
				String key = fields[i].getName();
				Object value = fields[i].get(obj);

				if ( !key.equals("serialVersionUID") ) {
					response.setHeader(key, String.valueOf(value) );
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

}
