package common.util;

import javax.servlet.http.HttpServletRequest;

public class RequestIpUtil {
	
	private RequestIpUtil() {
		super();
	}

	public static String getIpAdd(HttpServletRequest request) {
		
		String sIpAddr = request.getHeader("X-Forwarded-For");
		
		if (sIpAddr == null) {
			sIpAddr = request.getHeader("Proxy-Client-IP");
		}
		
		if (sIpAddr == null) {
			sIpAddr = request.getHeader("WL-Proxy-Client-IP");
		}
		
		if (sIpAddr == null) {
			sIpAddr = request.getHeader("HTTP_CLIENT_IP");
		}
		
		if (sIpAddr == null) {
			sIpAddr = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		
		if (sIpAddr == null) {
			sIpAddr = request.getRemoteAddr();
		}
		
		return sIpAddr;
	}
	
}
