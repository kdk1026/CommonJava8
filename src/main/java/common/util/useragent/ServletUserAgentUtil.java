package common.util.useragent;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * <pre>
 * ServletUserAgentUtil 으로 파일명 변경
 * isCheckUserAgent 만 UserAgentUtil 로 분리
 *
 * getUserAgent 외에는 YauaaParserUtil 권장
 * </pre>
 *
 *
 * @author 김대광
 */
public class ServletUserAgentUtil {

	public static final String USER_AGENT = "User-Agent";
	private static final String REQUEST = "request";
	private static final String MOBILE = "mobile";
	private static final String UNKNOWN = "Unknown";

	private ServletUserAgentUtil() {
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
	 * 요청 헤더에서 User-Agent 가져오기
	 * @param request
	 * @return
	 */
	public static String getUserAgent(HttpServletRequest request) {
		return request.getHeader(USER_AGENT);
	}

	public static class Device {
		private Device() {
		}

		/**
		 * 태블릿 기기 감지
		 * @param request
		 * @return
		 */
		public static boolean isTablet(HttpServletRequest request) {
			Objects.requireNonNull(request, ExceptionMessage.isNull(REQUEST));

			String userAgent = request.getHeader(USER_AGENT);

			if ( userAgent == null || userAgent.isEmpty() ) {
	            return false;
	        } else {
	        	userAgent = userAgent.toLowerCase();
	        }

			return userAgent.contains("ipad") ||
					(userAgent.contains("android") && !userAgent.contains(MOBILE)) ||
					userAgent.contains("tablet");
		}

		/**
		 * 모바일 기기(태블릿 제외) 감지
		 * @param request
		 * @return
		 */
		public static boolean isOnlyMobile(HttpServletRequest request) {
			Objects.requireNonNull(request, ExceptionMessage.isNull(REQUEST));

			String userAgent = request.getHeader(USER_AGENT);

			if ( userAgent == null || userAgent.isEmpty() ) {
	            return false;
	        } else {
	        	userAgent = userAgent.toLowerCase();
	        }

			return userAgent.contains(MOBILE) && !isTablet(request);
		}

		/**
		 * 모바일 기기(태블릿 포함) 감지
		 * @param request
		 * @return
		 */
		public static boolean isMobile(HttpServletRequest request) {
			Objects.requireNonNull(request, ExceptionMessage.isNull(REQUEST));

			String userAgent = request.getHeader(USER_AGENT);

			if ( userAgent == null || userAgent.isEmpty() ) {
	            return false;
	        } else {
	        	userAgent = userAgent.toLowerCase();
	        }

			return userAgent.contains(MOBILE);
		}

		/**
		 * PC(데스크톱) 기기 감지
		 * @param request
		 * @return
		 */
		public static boolean isPC(HttpServletRequest request) {
			Objects.requireNonNull(request, ExceptionMessage.isNull(REQUEST));

			return !isTablet(request) && !isMobile(request);
		}
	}

	public static class Os {
		private Os() {
		}

		/**
		 * 윈도우 여부 체크
		 * @param request
		 * @return
		 */
		public static boolean isWindows(HttpServletRequest request) {
			Objects.requireNonNull(request, ExceptionMessage.isNull(REQUEST));

			String userAgent = request.getHeader(USER_AGENT);

			if ( userAgent == null || userAgent.isEmpty() ) {
	            return false;
	        }

			return userAgent.contains("Windows NT");
		}

		/**
		 * Linux 여부 체크
		 * @param request
		 * @return
		 */
		public static boolean isLinux(HttpServletRequest request) {
			Objects.requireNonNull(request, ExceptionMessage.isNull(REQUEST));

			String userAgent = request.getHeader(USER_AGENT);

			if ( userAgent == null || userAgent.isEmpty() ) {
	            return false;
	        } else {
	        	userAgent = userAgent.toLowerCase();
	        }

			return userAgent.contains("linux");
		}

		/**
		 * Mac 여부 체크
		 * @param request
		 * @return
		 */
		public static boolean isMac(HttpServletRequest request) {
			Objects.requireNonNull(request, ExceptionMessage.isNull(REQUEST));

			String userAgent = request.getHeader(USER_AGENT);

			if ( userAgent == null || userAgent.isEmpty() ) {
	            return false;
	        } else {
	        	userAgent = userAgent.toLowerCase();
	        }

			return userAgent.contains("macintosh") || userAgent.contains("mac os x");
		}

		/**
		 * Android 여부 체크
		 * @param request
		 * @return
		 */
		public static boolean isAndroid(HttpServletRequest request) {
			Objects.requireNonNull(request, ExceptionMessage.isNull(REQUEST));

			String userAgent = request.getHeader(USER_AGENT);

			if ( userAgent == null || userAgent.isEmpty() ) {
	            return false;
	        } else {
	        	userAgent = userAgent.toLowerCase();
	        }

			return userAgent.contains("android");
		}

		/**
		 * iOS 여부 체크
		 * @param request
		 * @return
		 */
		public static boolean isIos(HttpServletRequest request) {
			Objects.requireNonNull(request, ExceptionMessage.isNull(REQUEST));

			String userAgent = request.getHeader(USER_AGENT);

			if ( userAgent == null || userAgent.isEmpty() ) {
				return false;
			} else {
				userAgent = userAgent.toLowerCase();
			}

			return userAgent.contains("iPhone") || userAgent.contains("iPad") || userAgent.contains("iPod");
		}
	}

	/*
	 * yauaa 라이브러리 사용 권장 (User Agent 문자열의 형식은 언제든지 바뀔 수 있음)
	 */
	public static class OsVersion {
		private OsVersion() {
		}

		/**
		 * Windows 버전 가져오기
		 * @param request
		 * @return
		 */
		public static String getWindowsVersion(HttpServletRequest request) {
			Objects.requireNonNull(request, ExceptionMessage.isNull(REQUEST));

			if ( !Os.isWindows(request) ) {
				return UNKNOWN;
			}

			String userAgent = request.getHeader(USER_AGENT);

			if ( userAgent == null || userAgent.isEmpty() ) {
				return UNKNOWN;
			} else {
				userAgent = userAgent.toLowerCase();
			}

			Matcher matcher = Pattern.compile("Windows NT (\\d+\\.\\d+)").matcher(userAgent);
			if ( matcher.find() ) {
				String ntVersion = matcher.group(1);

				switch (ntVersion) {
	                case "5.1":
	                    return "Windows XP";
	                case "6.1":
	                    return "Windows 7";
	                case "6.2":
	                    return "Windows 8";
	                case "6.3":
	                    return "Windows 8.1";
	                case "10.0":
	                    return "Windows 10/11";
	                default:
	                    return "Windows NT " + ntVersion;
				}
			}

			return "Windows (Version Unknown)";
		}

		/**
		 * Mac OS 버전 가져오기
		 *  - mac OS 11부터는 10.15.7 고정
		 * @param request
		 * @return
		 */
		public static String getMacOsVersion(HttpServletRequest request) {
			Objects.requireNonNull(request, ExceptionMessage.isNull(REQUEST));

			if ( !Os.isMac(request) ) {
				return UNKNOWN;
			}

			String userAgent = request.getHeader(USER_AGENT);

			if ( userAgent == null || userAgent.isEmpty() ) {
				return UNKNOWN;
			} else {
				userAgent = userAgent.toLowerCase();
			}

			Matcher matcher = Pattern.compile("Mac OS X\\s*([0-9._]+)").matcher(userAgent);

			if ( matcher.find() ) {
				String version = matcher.group(1).replace('_', '.');

				return "Mac OS " + version;
			}

			return UNKNOWN;
		}

		/**
		 * Android 버전 가져오기
		 * @param request
		 * @return
		 */
		public static String getAndroidVersion(HttpServletRequest request) {
			Objects.requireNonNull(request, ExceptionMessage.isNull(REQUEST));

			if ( !Os.isAndroid(request) ) {
				return UNKNOWN;
			}

			String userAgent = request.getHeader(USER_AGENT);

			if ( userAgent == null || userAgent.isEmpty() ) {
				return UNKNOWN;
			} else {
				userAgent = userAgent.toLowerCase();
			}

			Matcher matcher = Pattern.compile("Android\\s+([0-9.]+)").matcher(userAgent);

			if ( matcher.find() ) {
				String version = matcher.group(1);

				return "Android " + version;
			}

			return UNKNOWN;
		}

		/**
		 * iOS 버전 가져오기
		 * @param request
		 * @return
		 */
		public static String getIosVersion(HttpServletRequest request) {
			Objects.requireNonNull(request, ExceptionMessage.isNull(REQUEST));

			if ( !Os.isIos(request) ) {
				return UNKNOWN;
			}

			String userAgent = request.getHeader(USER_AGENT);

			if ( userAgent == null || userAgent.isEmpty() ) {
				return UNKNOWN;
			} else {
				userAgent = userAgent.toLowerCase();
			}

			Matcher matcher = Pattern.compile("CPU\\s+(?:iPhone|iPad|iPod)\\s+OS\\s+([0-9_]+)\\s+like\\s+Mac\\s+OS\\s+X").matcher(userAgent);

			if ( matcher.find() ) {
				String version = matcher.group(1).replace('_', '.');

				return "iOS " + version;
			}

			return UNKNOWN;
		}
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

		String userAgent = request.getHeader(USER_AGENT);

		return userAgent != null && userAgent.indexOf(chkStr) > -1;
	}

	/**
	 * UserAgent에서 브라우저 식별
	 * @param request
	 * @return
	 */
	public static String getBrowser(HttpServletRequest request) {
		Objects.requireNonNull(request, ExceptionMessage.isNull(REQUEST));

		String userAgent = request.getHeader(USER_AGENT);

		if ( userAgent == null || userAgent.isEmpty() ) {
			return UNKNOWN;
		} else {
			userAgent = userAgent.toLowerCase();
		}

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
