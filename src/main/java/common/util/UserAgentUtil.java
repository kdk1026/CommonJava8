package common.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 8. 10. 김대광	최초작성
 * 2025. 5. 18. 김대광	AI가 추천한 Singleton 패턴으로 변경
 * 2025. 5. 27. 김대광	유틸은 Singleton 패턴을 사용하지 않는 것이 좋다는 의견 반영
 * </pre>
 *
 *
 * @author 김대광
 */
public class UserAgentUtil {

	private UserAgentUtil() {
		super();
	}

	private static final String USER_AGENT = "User-Agent";

	/**
	 * 모바일 브라우저 여부 체크
	 * @param request
	 * @return
	 */
	public static boolean isMobile(HttpServletRequest request) {
		if ( request == null ) {
			throw new IllegalArgumentException("request is null");
		}

		String sUserAgent = request.getHeader(USER_AGENT);

		return sUserAgent != null && sUserAgent.indexOf("Mobi") > -1;
	}

	/**
	 * Android, iOS 여부 체크
	 * @param request
	 * @return
	 */
	public static String isMobileOs(HttpServletRequest request) {
		if ( request == null ) {
			throw new IllegalArgumentException("request is null");
		}

		String sUserAgent = request.getHeader(USER_AGENT);

		if ( sUserAgent == null ) {
            return "";
        }

		if ( sUserAgent.contains("Android") ) {
			return "Android";
		}

		if ( sUserAgent.contains("iPhone") || sUserAgent.contains("iPad") || sUserAgent.contains("iPod") ) {
			return "iOS";
		}

		return "";
	}

	/**
	 * UserAgent 에서 특정 문자열 유무 체크
	 * @param request
	 * @param chkStr
	 * @return
	 */
	public static boolean isCheckUserAgent(HttpServletRequest request, String chkStr) {
		if ( request == null ) {
			throw new IllegalArgumentException("request is null");
		}

		if ( StringUtils.isBlank(chkStr) ) {
			throw new IllegalArgumentException("chkStr is null");
		}

		String sUserAgent = request.getHeader(USER_AGENT);

		return sUserAgent != null && sUserAgent.indexOf(chkStr) > -1;
	}

}

