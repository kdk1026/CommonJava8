package common.util.sessioncookie;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionUtils {
	
	private SessionUtils() {
		super();
	}
	
	public static final String LOGIN_SESSION_ID = "__userInfo__";

	/**
	 * 로그인 정보를 세션에 저장
	 * @param request
	 * @param obj
	 */
	public static void setSessionLoginInfo(HttpServletRequest request, Object obj) {
		HttpSession session = request.getSession(false);
		session.invalidate();
		session = request.getSession(true);

		session.setAttribute(LOGIN_SESSION_ID, obj);

		/*
		 * 세션 타임아웃 적용 우선 순위
		 * 	1. [특정 세션] 유효시간 설정 (단위:초)
		 * 	2. [공통 세션] 유효시간 설정 web.xml (단위:분)
		 * 	3. [공통 세션] 유효시간 설정 Tomcat경로/conf/web.xml (단위:분)
		 */
        session.setMaxInactiveInterval(60*20);
	}

	/**
	 * 로그인 정보를 세션에서 가져옴
	 * @return
	 */
	public static Object getSessionLoginInfo(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		return (session == null ? null : session.getAttribute(LOGIN_SESSION_ID));
	}


	public static void setSessionAttribute(HttpServletRequest request, String sKey, Object obj) {
		HttpSession session = request.getSession();

		session.setAttribute(sKey, obj);
	}

	public static void setSessionAttribute(HttpServletRequest request, String sKey, Object obj, int nSecond) {
		HttpSession session = request.getSession();
		session.setAttribute(sKey, obj);

		session.setMaxInactiveInterval(nSecond);
	}

	public static Object getSessionAttribute(HttpServletRequest request, String sKey) {
		HttpSession session = request.getSession(false);
		return (session == null ? null : session.getAttribute(sKey));
	}

}
