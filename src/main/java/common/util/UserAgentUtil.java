package common.util;

import java.util.Objects;

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

	private static final String USER_AGENT = "User-Agent";
	private static final String REQUEST = "request";

	private UserAgentUtil() {
		super();
	}

	private static class ExceptionMessage {

		public static String isNull(String paramName) {
	        return String.format("'%s' is null", paramName);
	    }

		public static String isNullOrEmpty(String paramName) {
	        return String.format("'%s' is null or empty", paramName);
	    }

	}

	/**
	 * 모바일 브라우저 여부 체크
	 * @param request
	 * @return
	 */
	public static boolean isMobile(HttpServletRequest request) {
		Objects.requireNonNull(request, ExceptionMessage.isNull(REQUEST));

		String sUserAgent = request.getHeader(USER_AGENT);

		return sUserAgent != null && sUserAgent.indexOf("Mobi") > -1;
	}

	/**
	 * Android, iOS 여부 체크
	 * @param request
	 * @return
	 */
	public static String isMobileOs(HttpServletRequest request) {
		Objects.requireNonNull(request, ExceptionMessage.isNull(REQUEST));

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
		Objects.requireNonNull(request, ExceptionMessage.isNull(REQUEST));

		if ( StringUtils.isBlank(chkStr) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("chkStr"));
		}

		String sUserAgent = request.getHeader(USER_AGENT);

		return sUserAgent != null && sUserAgent.indexOf(chkStr) > -1;
	}

	/**
	 * UserAgent에서 브라우저 식별
	 * @param request
	 * @return
	 */
	public static String getBrowser(HttpServletRequest request) {
		Objects.requireNonNull(request, ExceptionMessage.isNull(REQUEST));

		String userAgent = request.getHeader(USER_AGENT);

        if (userAgent == null) {
            return "Unknown";
        }

        userAgent = userAgent.toLowerCase();

        if (userAgent.contains("samsungbrowser")) {
            return "Samsung Internet";
        } else if (userAgent.contains("whale")) {
            return "Whale";
        } else if (userAgent.contains("edge")) {
            return "Microsoft Edge";
        } else if (userAgent.contains("opr") || userAgent.contains("opera")) {
            return "Opera";
        } else if (userAgent.contains("chrome")) {
            return "Chrome";
        } else if (userAgent.contains("firefox")) {
            return "Firefox";
        } else if (userAgent.contains("safari") && !userAgent.contains("chrome")) {
            return "Safari";
        } else if (userAgent.contains("msie") || userAgent.contains("trident")) {
            return "Internet Explorer";
        } else {
            return "Other";
        }
    }

}

