package common.util.sessioncookie;

import java.util.Objects;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.Builder;
import lombok.Getter;

public class CookieUtilVer2 {

	private static final String LOCAL_PROFILE = "local";

	private CookieUtilVer2() {
		super();
	}

	private static class ExceptionMessage {

		public static String isNull(String paramName) {
	        return String.format("'%s' is null", paramName);
	    }

		public static String isNegative(String paramName) {
			return String.format("'%s' is negative", paramName);
		}

	}

	private static final String RESPONSE_IS_NUL = ExceptionMessage.isNull("response");
	private static final String REQUEST_IS_NUL = ExceptionMessage.isNull("request");
	private static final String COOKIE_NAME_IS_NUL = ExceptionMessage.isNull("cookieName");
	private static final String NAME_IS_NUL = ExceptionMessage.isNull("name");
	private static final String VALUE_IS_NUL = ExceptionMessage.isNull("value");

	@Getter
	@Builder
	public static class CookieConfig {
		private String name;
		private String value;

		@Builder.Default
		public int expiry = -1; 	// 기본값: 세션 쿠키

		protected String domain;

		@Builder.Default
		protected String profile = LOCAL_PROFILE;
	}

	/**
	 * Servlet 3.0 쿠키 설정
	 * @param response
	 * @param cookieConfig
	 * <pre>
	 * {@code
	 * CookieConfig config = CookieConfig.builder()
	 * 	.name("sid")
	 * 	.value("abc123")
	 * 	.expiry(3600)
	 * 	.domain("example.com");
	 * 	.profile("local")
	 * 	.build();
	 * }
	 * </pre>
	 */
	public static void addCookie(HttpServletResponse response, CookieConfig cookieConfig) {
		Objects.requireNonNull(response, RESPONSE_IS_NUL);
		Objects.requireNonNull(cookieConfig.name, NAME_IS_NUL);
		if (cookieConfig.name.trim().isEmpty()) {
			throw new IllegalArgumentException(NAME_IS_NUL);
		}

        Objects.requireNonNull(cookieConfig.value, VALUE_IS_NUL);
        if (cookieConfig.value.trim().isEmpty()) {
			throw new IllegalArgumentException(VALUE_IS_NUL);
		}

		if ( cookieConfig.expiry < 0 ) {
			throw new IllegalArgumentException(ExceptionMessage.isNegative("expiry"));
		}


		Cookie cookie = new Cookie(cookieConfig.name, cookieConfig.value);
		cookie.setMaxAge(cookieConfig.expiry);
		cookie.setPath("/");

		if ( (cookieConfig.domain != null) && (!cookieConfig.domain.trim().isEmpty()) ) {
			cookie.setDomain(cookieConfig.domain);
		}

		cookie.setHttpOnly(true);
		cookie.setSecure(!LOCAL_PROFILE.equals(cookieConfig.profile));

		response.addCookie(cookie);
	}

	/**
	 * Servlet 3.0 세션 쿠키 설정
	 * @param response
	 * @param cookieConfig
	 * <pre>
	 * {@code
	 * CookieConfig config = CookieConfig.builder()
	 * 	.name("sid")
	 * 	.value("abc123")
	 * 	.domain("example.com");
	 * 	.profile("local")
	 * 	.build();
	 * }
	 * </pre>
	 */
	public static void addSessionCookie(HttpServletResponse response, CookieConfig cookieConfig) {
		Objects.requireNonNull(response, RESPONSE_IS_NUL);
		Objects.requireNonNull(cookieConfig.name, NAME_IS_NUL);
		if (cookieConfig.name.trim().isEmpty()) {
			throw new IllegalArgumentException(NAME_IS_NUL);
		}

        Objects.requireNonNull(cookieConfig.value, VALUE_IS_NUL);
        if (cookieConfig.value.trim().isEmpty()) {
			throw new IllegalArgumentException(VALUE_IS_NUL);
		}

		Cookie cookie = new Cookie(cookieConfig.name, cookieConfig.value);
		cookie.setPath("/");

		if ( (cookieConfig.domain != null) && (!cookieConfig.domain.trim().isEmpty()) ) {
			cookie.setDomain(cookieConfig.domain);
		}

		cookie.setHttpOnly(true);
		cookie.setSecure(!LOCAL_PROFILE.equals(cookieConfig.profile));

		response.addCookie(cookie);
	}

	/**
	 * cookieName 인자 값을 가지는 쿠키 가져오기
	 * @param request
	 * @param cookieName
	 * @return
	 */
	public static Cookie getCookie(HttpServletRequest request, String cookieName) {
		Objects.requireNonNull(request, REQUEST_IS_NUL);
		Objects.requireNonNull(cookieName, COOKIE_NAME_IS_NUL);
		if (cookieName.trim().isEmpty()) {
			throw new IllegalArgumentException(COOKIE_NAME_IS_NUL);
		}

		Cookie cookie = null;
		Cookie[] cookies = request.getCookies();

		if ( cookies != null ) {
			for (Cookie c : cookies) {
				if ( cookieName.equals(c.getName()) ) {
					cookie = c;
					break;
				}
			}
		}

		return cookie;
	}

	/**
	 * cookieName 인자 값을 가지는 쿠키의 값 가져오기
	 * @param request
	 * @param cookieName
	 * @return
	 */
	public static String getCookieValue(HttpServletRequest request, String cookieName) {
		Objects.requireNonNull(request, REQUEST_IS_NUL);
		Objects.requireNonNull(cookieName, COOKIE_NAME_IS_NUL);
		if (cookieName.trim().isEmpty()) {
			throw new IllegalArgumentException(COOKIE_NAME_IS_NUL);
		}

		Cookie cookie = getCookie(request, cookieName);
		return (cookie != null) ? cookie.getValue() : "";
	}

	/**
	 * 모든 쿠키 제거
	 * @param request
	 * @param response
	 * @param profile
	 */
	public static void removeCookies(HttpServletRequest request, HttpServletResponse response, String profile) {
		Objects.requireNonNull(request, REQUEST_IS_NUL);
		Objects.requireNonNull(response, RESPONSE_IS_NUL);

		Cookie[] cookies = request.getCookies();

		if (cookies != null && cookies.length > 0) {
			for (int i=0; i < cookies.length; i++) {
				cookies[i].setPath("/");
				cookies[i].setMaxAge(0);

				cookies[i].setDomain(cookies[i].getDomain());

				cookies[i].setHttpOnly(true);
				cookies[i].setSecure(!LOCAL_PROFILE.equals(profile));

				response.addCookie(cookies[i]);
			}
		}
	}

	/**
	 * 특정 쿠키 제거
	 * @param request
	 * @param response
	 * @param cookieConfig
	 * <pre>
	 * {@code
	 * CookieConfig config = CookieConfig.builder()
	 * 	.name("sid")
	 * 	.domain("example.com");
	 * 	.profile("local")
	 * 	.build();
	 * }
	 * </pre>
	 */
	public static void removeCookie(HttpServletResponse response, CookieConfig cookieConfig) {
		Objects.requireNonNull(response, RESPONSE_IS_NUL);
		Objects.requireNonNull(cookieConfig.name, NAME_IS_NUL);
		if (cookieConfig.name.trim().isEmpty()) {
			throw new IllegalArgumentException(NAME_IS_NUL);
		}

		Cookie cookie = new Cookie(cookieConfig.name, null);
		cookie.setPath("/");
		cookie.setMaxAge(0);

		if ( (cookieConfig.domain != null) && (!cookieConfig.domain.trim().isEmpty()) ) {
			cookie.setDomain(cookieConfig.domain);
		}

		cookie.setHttpOnly(true);
		cookie.setSecure(!LOCAL_PROFILE.equals(cookieConfig.profile));

		response.addCookie(cookie);
	}

	/**
	 * 쿠키 유무 확인
	 * @param request
	 * @param cookieName
	 */
	public static boolean isExist(HttpServletRequest request, String cookieName) {
		Objects.requireNonNull(request, REQUEST_IS_NUL);
		Objects.requireNonNull(cookieName, COOKIE_NAME_IS_NUL);
		if (cookieName.trim().isEmpty()) {
			throw new IllegalArgumentException(COOKIE_NAME_IS_NUL);
		}

		String cookieValue = getCookieValue(request, cookieName);
		return !"".equals(cookieValue);
	}

	/**
	 * 쿠키 유효기간 가져오기
	 * @param request
	 * @param cookieName
	 */
	public static int getCookieMaxAge(HttpServletRequest request, String cookieName) {
		Objects.requireNonNull(request, REQUEST_IS_NUL);
		Objects.requireNonNull(cookieName, COOKIE_NAME_IS_NUL);
		if (cookieName.trim().isEmpty()) {
			throw new IllegalArgumentException(COOKIE_NAME_IS_NUL);
		}

		Cookie cookie = getCookie(request, cookieName);
		return (cookie != null) ? cookie.getMaxAge() : 0;
	}

}
