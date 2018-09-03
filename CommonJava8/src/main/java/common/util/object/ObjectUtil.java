package common.util.object;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
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
public class ObjectUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(ObjectUtil.class);
	
	private ObjectUtil() {
		super();
	}
	
	/**
	 * @Description
	 * <pre>
	 * Object의 Field가 Blank인지 체크 
	 * </pre>
	 * @param obj
	 * @param fieldName
	 * @return
	 * <pre>
	 * -----------------------------------
	 * 개정이력
	 * 2018. 9. 3. 김대광	최초작성
	 * </pre>
	 */
	public static boolean isBlank(Object obj, String fieldName) {
		Field field = FieldUtils.getField(obj.getClass(), fieldName, true);
		
		String str = "";
		try {
			str = String.valueOf(field.get(obj));
			
		} catch (IllegalArgumentException | IllegalAccessException e) {
			logger.error("", e);
		}
		
		return StringUtils.isBlank(str);
	}
	
	/**
	 * @Description
	 * <pre>
	 * Object의 Field명 추출 
	 * </pre>
	 * @param obj
	 * @return
	 * <pre>
	 * -----------------------------------
	 * 개정이력
	 * 2018. 9. 3. 김대광	최초작성
	 * </pre>
	 */
	public static List<String> getFieldNames(Object obj) {
		List<String> list = new ArrayList<>();
		
		List<Field> fields = FieldUtils.getAllFieldsList(obj.getClass());
		for (Field f : fields) {
			list.add(f.getName());
		}
		
		return list;
	}

	/**
	 * @Description
	 * <pre>
	 * 요청 파라미터를 Object로 변환
	 * </pre>
	 * @param request
	 * @param obj
	 * <pre>
	 * -----------------------------------
	 * 개정이력
	 * 2018. 9. 3. 김대광	최초작성
	 * </pre>
	 */
	public static void paramToObject(HttpServletRequest request, Object obj) {
		try {
			BeanUtils.populate(obj, request.getParameterMap());
			
		} catch (IllegalAccessException | InvocationTargetException e) {
			logger.error("", e);
		}
	}

	/**
	 * @Description
	 * <pre>
	 * Map을 Object로 변환
	 * </pre>
	 * @param obj
	 * <pre>
	 * -----------------------------------
	 * 개정이력
	 * 2018. 9. 3. 김대광	최초작성
	 * </pre>
	 */
	public static void mapToObject(Map<String, Object> map, Object obj) {
		try {
			BeanUtils.populate(obj, map);
			
		} catch (IllegalAccessException | InvocationTargetException e) {
			logger.error("", e);
		}
	}
	
	/**
	 * @Description
	 * <pre>
	 * Map을 구조체로 변환
	 * </pre>
	 * @param map
	 * @param obj
	 * <pre>
	 * -----------------------------------
	 * 개정이력
	 * 2018. 9. 3. 김대광	최초작성
	 * </pre>
	 */
	public static void mapToStruct(Map<String, Object> map, Object obj) {
		String key = "";
		String name = "";
		Field[] fields = null;
		Class<?> cls = null;
		
		Iterator<String> it = map.keySet().iterator();
		while ( it.hasNext() ) {
			key = it.next();

			try {
				fields = obj.getClass().getDeclaredFields();
				cls = obj.getClass();

				for (Field f : fields) {
					name = f.getName();
					
					if ( key.equals(name) ) {
						cls.getField(name).set(obj, (map.get(key) != null) ? map.get(key) : "");
					}
				}
				
			} catch (Exception e) {
				logger.error("", e);
			}
		}
	}
	
}
