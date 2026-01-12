package common.util.useragent;

import org.apache.commons.lang3.StringUtils;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2026. 1. 12. 김대광	최초작성
 * </pre>
 *
 *
 * @author 김대광
 */
public class UserAgentUtil {

	private UserAgentUtil() {
		super();
	}

	private static class ExceptionMessage {

		public static String isNullOrEmpty(String paramName) {
	        return String.format("'%s' is null or empty", paramName);
	    }

	}

	/**
	 * UserAgent 에서 특정 문자열 유무 체크
	 * @param request
	 * @param chkStr
	 * @return
	 */
	public static boolean isCheckUserAgent(String uaString, String chkStr) {
		if ( StringUtils.isBlank(uaString) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("uaString"));
		}

		if ( StringUtils.isBlank(chkStr) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("chkStr"));
		}

		return uaString.indexOf(chkStr) > -1;
	}

}
