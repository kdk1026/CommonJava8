package common.util.sessioncookie;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtilVer2 {
	
	private CookieUtilVer2() {
		super();
	}
	
	/**
	 * Servlet 3.0 쿠키 설정
	 * @param response
	 * @param name
	 * @param value
	 * @param expiry
	 * @param isUseJs
	 * @param isSecure
	 * @param domain
	 */
	public static void addCookie(HttpServletResponse response, String name, String value, int expiry, boolean isSecure, boolean isUseJs, String domain) {
		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(expiry);
		cookie.setPath("/");
		
		cookie.setSecure(isSecure);
		
		if (!isUseJs) {
			cookie.setHttpOnly(true);
		}
		
		if ( (domain != null) && (domain.trim().length() > 0) ) {
			cookie.setDomain(domain);
		}
		
		response.addCookie(cookie);
	}
	
	/**
	 * cookieName 인자 값을 가지는 쿠키 가져오기
	 * @param request
	 * @param cookieName
	 * @return
	 */
	public static Cookie getCookie(HttpServletRequest request, String cookieName) {
		Cookie cookie = null;
		Cookie[] cookies = request.getCookies();

		for (Cookie c : cookies) {
			if ( cookieName.equals(c.getName()) ) {
				cookie = c;
				break;
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
		Cookie cookie = getCookie(request, cookieName);
		return (cookie != null) ? cookie.getValue() : "";
	}

	/**
	 * 모든 쿠키 제거
	 * @param request
	 * @param response
	 */
	public static void removeCookies(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		
		if (cookies != null && cookies.length > 0) {
			for (int i=0; i < cookies.length; i++) {
				cookies[i].setPath("/");
				cookies[i].setMaxAge(0);
				
				response.addCookie(cookies[i]);
			}
		}
	}

	/**
	 * 특정 쿠키 제거
	 * @param request
	 * @param response
	 * @param cookieName
	 */
	public static void removeCookie(HttpServletResponse response, String cookieName) {
		Cookie cookie = new Cookie(cookieName, null);
		cookie.setPath("/");
		cookie.setMaxAge(0);
		
		if ( cookie.getSecure() ) {
			cookie.setSecure(true);
		}
		
		response.addCookie(cookie);
	}
	
	/**
	 * 쿠키 유무 확인
	 * @param request
	 * @param cookieName
	 */
	public static boolean isExist(HttpServletRequest request, String cookieName) {
		String cookieValue = getCookieValue(request, cookieName);
		return !"".equals(cookieValue);
	}
	
	/**
	 * 쿠키 유효기간 가져오기
	 * @param request
	 * @param cookieName
	 */
	public static int getCookieMaxAge(HttpServletRequest request, String cookieName) {
		Cookie cookie = getCookie(request, cookieName);
		return (cookie != null) ? cookie.getMaxAge() : 0; 
	}

}
