package common.util;

import javax.servlet.http.HttpServletRequest;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2020. 9. 18. 김대광	최초작성
 * 2021. 8. 13. 김대광	SonarLint 지시에 따른 수정 (Complexity는 어쩔 수 없단다)
 * </pre>
 *
 *
 * @author 김대광
 */
public class RequestUtil {

	private RequestUtil() {
		super();
	}

	/**
	 * IP 주소 가져오기
	 * @param request
	 * @return
	 */
	public static String getRequestIpAddress(HttpServletRequest request) {
	    String[] sHeaders = {
	    		"X-Forwarded-For",
	    		"Proxy-Client-IP",
	    		"WL-Proxy-Client-IP",
	    		"HTTP_CLIENT_IP",
	    		"HTTP_X_FORWARDED_FOR",
	    		"X-Real-IP",
	    		"X-RealIP",
	    		"REMOTE_ADDR"
	    };

	    for ( String header : sHeaders ) {
	    	String sIp = request.getHeader(header);

	    	if ( sIp != null && sIp.length() != 0 && !"unknown".equalsIgnoreCase(sIp) ) {
	    		return sIp;
	    	}
	    }

	    return request.getRemoteAddr();
	}

	/**
	 * 도메인 가져오기 (포트 있는 경우, 포트 포함)
	 * @param request
	 * @return
	 */
	public static String getRequestDomain(HttpServletRequest request) {
		String sReqUrl = request.getRequestURL().toString();
		String sServletPath = request.getServletPath();
		return sReqUrl.replace(sServletPath, "");
	}

}
