package common.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 8. 10. 김대광	최초작성
 * </pre>
 *
 *
 * @author 김대광
 */
public class UserAgentUtil {

	private static final String USER_AGENT = "User-Agent";

	/** 외부에서 객체 인스턴스화 불가 */
	private UserAgentUtil() {
		super();
	}

	/**
	 * Singleton 인스턴스 생성
	 *
	 * @return
	 */
	public static UserAgentUtil getInstance() {
		return LazyHolder.INSTANCE;
	}

	/**
	 * LazyHolder Singleton 패턴
	 *
	 * @return
	 */
	private static class LazyHolder {
		private static final UserAgentUtil INSTANCE = new UserAgentUtil();
	}

	/**
	 * 모바일 브라우저 여부 체크
	 * @param request
	 * @return
	 */
	public boolean isMobile(HttpServletRequest request) {
		if ( request == null ) {
			throw new IllegalArgumentException("request is null");
		}

		String sUserAgent = request.getHeader(USER_AGENT);

		boolean isMobile = false;

		if ( sUserAgent.indexOf("Mobi") > -1 ) {
			isMobile = true;
		}

		return isMobile;
	}

	/**
	 * Android, iOS 여부 체크
	 * @param request
	 * @return
	 */
	public String isMobileOs(HttpServletRequest request) {
		if ( request == null ) {
			throw new IllegalArgumentException("request is null");
		}

		String sUserAgent = request.getHeader(USER_AGENT);

		String sPlatForm = "";

		if ( sUserAgent.contains("Android") ) {
			sPlatForm = "Android";
		}

		if ( sUserAgent.contains("iPhone") || sUserAgent.contains("iPad") || sUserAgent.contains("iPod") ) {
			sPlatForm = "iOS";
		}

		return sPlatForm;
	}

	/**
	 * UserAgent 에서 특정 문자열 유무 체크
	 * @param request
	 * @param chkStr
	 * @return
	 */
	public boolean isCheckUserAgent(HttpServletRequest request, String chkStr) {
		if ( request == null ) {
			throw new IllegalArgumentException("request is null");
		}

		if ( StringUtils.isBlank(chkStr) ) {
			throw new IllegalArgumentException("chkStr is null");
		}

		String sUserAgent = request.getHeader(USER_AGENT);

		return sUserAgent.indexOf(chkStr) > -1;
	}

}

