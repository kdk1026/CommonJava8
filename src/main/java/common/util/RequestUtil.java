package common.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger logger = LoggerFactory.getLogger(RequestUtil.class);

	private static final String REQUEST_IS_NULL = "request cannot be null";

	/**
	 * IP 주소 가져오기
	 * @param request
	 * @return
	 */
	public static String getRequestIpAddress(HttpServletRequest request) {
		Objects.requireNonNull(request, REQUEST_IS_NULL);

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

	    	if ( sIp != null && !sIp.isEmpty() && !"unknown".equalsIgnoreCase(sIp) ) {
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
		Objects.requireNonNull(request, REQUEST_IS_NULL);

	    String scheme = request.getScheme();
	    String domain = request.getServerName();
	    int port = request.getServerPort();
	    String contextPath = request.getContextPath();

	    boolean isDefaultPort = (scheme.equals("http") && port == 80) || (scheme.equals("https") && port == 443);

	    String baseUrl = isDefaultPort
	        ? scheme + "://" + domain
	        : scheme + "://" + domain + ":" + port;

	    return baseUrl + contextPath;
	}

	/**
	 * 기본 도메인 가져오기 (포트 미포함)
	 * @param request
	 * @return
	 */
	public static String getBaseDomain(HttpServletRequest request) {
		Objects.requireNonNull(request, REQUEST_IS_NULL);

		String reqUrl = request.getRequestURL().toString();
		URI uri;

		try {
			uri = new URI(reqUrl);
			return uri.getScheme() + "://" + uri.getAuthority();
		} catch (URISyntaxException e) {
			logger.error("", e);
			return null;
		}
	}

	/**
	 * 브라우저 User-Agent 가져오기
	 * @param request
	 * @return
	 */
	public static String getBrowserInfo(HttpServletRequest request) {
		Objects.requireNonNull(request, REQUEST_IS_NULL);

		String userAgent = request.getHeader("User-Agent");
        return userAgent != null ? userAgent : "User-Agent 정보 없음";
	}

}
