package common.util.sessioncookie;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

/**
 * <pre>
 * 세션 처리 유틸 클래스
 *  - 타임아웃 적용 우선 순위
 *    1. setMaxInactiveInterval (단위: 초)
 *    2. web.xml (단위: 분)
 *    3. WAS 설정
 *       ex) Tomcat경로/conf/web.xml (단위: 분, default: 30분)
 * </pre>
 * @since 2018. 12. 24.
 * @author 김대광
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2018. 12. 24. 김대광	최초작성
 * </pre>
 */
public class SessionUtils {

	protected SessionUtils() {
		super();
	}

	private static class ExceptionMessage {

		public static String isNull(String paramName) {
	        return String.format("'%s' is null", paramName);
	    }

		public static String isNullOrEmpty(String paramName) {
	        return String.format("'%s' is null or empty", paramName);
	    }

		public static String isNegative(String paramName) {
			return String.format("'%s' is negative", paramName);
		}

	}

	private static final String REQUEST_IS_NUL = ExceptionMessage.isNull("request");
	private static final String OBJ_IS_NUL = ExceptionMessage.isNull("obj");

	/**
	 * 로그인 정보 처리 내부 클래스
	 * @since 2018. 12. 24.
	 * @author 김대광
	 * <pre>
	 * -----------------------------------
	 * 개정이력
	 * 2018. 12. 24. 김대광	최초작성
	 * </pre>
	 */
	public static class LoginInfo {

		protected LoginInfo() {
			super();
		}

		public static final String SESSION_KEY = "__userInfo__";
		public static final int INACTIVE_INTERVAL = 60*20;

		/**
		 * 로그인 정보를 세션에 저장
		 * @param request
		 * @param obj
		 */
		public static void setAttribute(HttpServletRequest request, Object obj) {
			Objects.requireNonNull(request, REQUEST_IS_NUL);
			Objects.requireNonNull(obj, OBJ_IS_NUL);

			HttpSession session = request.getSession();

			session.setAttribute(SESSION_KEY, obj);
	        session.setMaxInactiveInterval(INACTIVE_INTERVAL);
		}

		/**
		 * 로그인 정보를 세션에 저장
		 * @param request
		 * @param sKey
		 * @param obj
		 * @param nSecond
		 */
		public static void setAttribute(HttpServletRequest request, Object obj, int nSecond) {
			Objects.requireNonNull(request, REQUEST_IS_NUL);
			Objects.requireNonNull(obj, OBJ_IS_NUL);

			if ( nSecond < 1 ) {
				throw new IllegalArgumentException(ExceptionMessage.isNegative("nSecond"));
			}

			HttpSession session = request.getSession();

			session.setAttribute(SESSION_KEY, obj);
			session.setMaxInactiveInterval(nSecond);
		}

		/**
		 * 로그인 정보를 세션에서 가져오기
		 * @return
		 */
		public static Object getSession(HttpServletRequest request) {
			Objects.requireNonNull(request, REQUEST_IS_NUL);

			HttpSession session = request.getSession(false);
			return (session == null ? null : session.getAttribute(SESSION_KEY));
		}
	}

	/**
	 * 세션에 저장
	 * @param request
	 * @param sKey
	 * @param obj
	 */
	public static void setAttribute(HttpServletRequest request, String sKey, Object obj) {
		Objects.requireNonNull(request, REQUEST_IS_NUL);

		if ( StringUtils.isBlank(sKey) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("sKey"));
		}

		Objects.requireNonNull(obj, OBJ_IS_NUL);

		HttpSession session = request.getSession();

		session.setAttribute(sKey, obj);
	}

	/**
	 * 세션에 저장
	 * @param request
	 * @param sKey
	 * @param obj
	 * @param nSecond
	 */
	public static void setAttribute(HttpServletRequest request, String sKey, Object obj, int nSecond) {
		Objects.requireNonNull(request, REQUEST_IS_NUL);

		if ( StringUtils.isBlank(sKey) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("sKey"));
		}

		Objects.requireNonNull(obj, OBJ_IS_NUL);

		if ( nSecond < 1 ) {
			throw new IllegalArgumentException(ExceptionMessage.isNegative("nSecond"));
		}

		HttpSession session = request.getSession();

		session.setAttribute(sKey, obj);
		session.setMaxInactiveInterval(nSecond);
	}

	/**
	 * 세션에서 가져오기
	 * @param request
	 * @param sKey
	 * @return
	 */
	public static Object getAttribute(HttpServletRequest request, String sKey) {
		Objects.requireNonNull(request, REQUEST_IS_NUL);

		if ( StringUtils.isBlank(sKey) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("sKey"));
		}

		HttpSession session = request.getSession(false);
		return (session == null ? null : session.getAttribute(sKey));
	}

}
