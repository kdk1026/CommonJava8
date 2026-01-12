package common.util.object;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2026. 1. 12. 김대광	최초작성
 * </pre>
 *
 *
 * @author 김대광
 */
public class ServletObjectUtil {

	private static final Logger logger = LoggerFactory.getLogger(ServletObjectUtil.class);

	private ServletObjectUtil() {
		super();
	}

	private static class ExceptionMessage {

		public static String isNull(String paramName) {
	        return String.format("'%s' is null", paramName);
	    }

	}

	/**
	 * @Description
	 * <pre>
	 * 요청 파라미터를 Object로 변환
	 * </pre>
	 * @param request
	 * @param obj
	 * @since 1.7
	 * <pre>
	 * -----------------------------------
	 * 개정이력
	 * 2018. 9. 3. 김대광	최초작성
	 * </pre>
	 */
	public static void paramToObject(HttpServletRequest request, Object obj) {
		Objects.requireNonNull(request, ExceptionMessage.isNull("request"));
		Objects.requireNonNull(obj, ExceptionMessage.isNull("obj"));

		try {
			BeanUtils.populate(obj, request.getParameterMap());

		} catch (IllegalAccessException | InvocationTargetException e) {
			logger.error("", e);
		}
	}

}
