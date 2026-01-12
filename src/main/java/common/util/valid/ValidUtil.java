package common.util.valid;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 8. 13. 김대광	SonarLint 지시에 따른 수정 (정규식은 어쩔수가 없구나...)
 * 2025. 5. 18. 김대광	AI가 추천한 Singleton 패턴으로 변경
 * 2025. 5. 27. 김대광	유틸은 Singleton 패턴을 사용하지 않는 것이 좋다는 의견 반영, 제미나이에 의한 일부 코드 개선
 * </pre>
 *
 *
 * @author 김대광
 */
public class ValidUtil {

	private static final Logger logger = LoggerFactory.getLogger(ValidUtil.class);

	private ValidUtil() {
		super();
	}

	private static class ExceptionMessage {

		public static String isNullOrEmpty(String paramName) {
	        return String.format("'%s' is null or empty", paramName);
	    }

	}

	/**
	 * Null, 공백 체크
	 *
	 * <pre>
	 * org.apache.commons.lang3.StringUtils.isBlank 권장
	 * </pre>
	 *
	 * @param str
	 * @return
	 */
	public static boolean isBlank(String str) {
		return (str == null || str.trim().isEmpty());
	}

	/**
	 * 문자열 길이 최소/최대 길이 준수 여부
	 * @param str
	 * @param min
	 * @param max
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static boolean isLengthOver(String str, int min, int max) {
		if ( isBlank(str) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
		}

		if ( min < 0 || max < 0 ) {
			throw new IllegalArgumentException("min or max is less than 0.");
		}

		int strLen = str.length();
		return (strLen < min) || (strLen > max);
	}

	/**
	 * URL 체크
	 * @param str
	 * @return
	 */
	public static boolean isSafeUrl(String urlStr) {
		if ( isBlank(urlStr) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
		}

		try {
			// 절대 URL 검사
			URL url = new URL(urlStr);
			String protocol = url.getProtocol();

			return "http".equalsIgnoreCase(protocol) || "https".equalsIgnoreCase(protocol);

		} catch (MalformedURLException e) {
			logger.error("Not an absolute URL, checking for relative path: {}", e.getMessage());
			// 상대 경로 검사
			return urlStr.equals("/") || urlStr.startsWith("/");
		} catch (Exception e) {
			logger.error("Unexpected error during URL check: {}", e.getMessage());
			return false;
		}
	}

	/**
	 * 형태 체크
	 */
	public static class Type {

		private Type() {
			super();
		}

		/**
		 * 숫자 체크
		 * @param str
		 * @return
		 * @throws IllegalArgumentException
		 */
		public static boolean isNumber(String str) {
			if ( isBlank(str) ) {
				throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
			}

			return str.matches("^\\d+$");
		}

		/**
		 * 영문 체크
		 * @param str
		 * @return
		 * @throws IllegalArgumentException
		 */
		public static boolean isEnglish(String str) {
			if ( isBlank(str) ) {
				throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
			}

			return str.matches("^[a-zA-Z]+$");
		}

		/**
		 * 영문, 공백 체크
		 * @param str
		 * @return
		 * @throws IllegalArgumentException
		 */
		public static boolean isEngBlank(String str) {
			if ( isBlank(str) ) {
				throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
			}

			return str.matches("^[a-zA-Z\\s]+$");
		}

		/**
		 * 영문, 숫자 체크
		 * @param str
		 * @return
		 * @throws IllegalArgumentException
		 */
		public static boolean isEngNum(String str) {
			if ( isBlank(str) ) {
				throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
			}

			return str.matches("^[a-zA-Z0-9]+$");
		}

		/**
		 * 한글 체크
		 * @param str
		 * @return
		 * @throws IllegalArgumentException
		 */
		public static boolean isHangul(String str) {
			if ( isBlank(str) ) {
				throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
			}

			return str.matches("^[가-힣]+$");
		}

		/**
		 * 한글, 공백 체크
		 * @param str
		 * @return
		 * @throws IllegalArgumentException
		 */
		public static boolean isHanBlank(String str) {
			if ( isBlank(str) ) {
				throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
			}

			return str.matches("^[가-힣\\s]+$");
		}

		/**
		 * 한글, 영문 체크
		 * @param str
		 * @return
		 * @throws IllegalArgumentException
		 */
		public static boolean isHanEng(String str) {
			if ( isBlank(str) ) {
				throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
			}

			return str.matches("^[가-힣a-zA-Z]+$");
		}

		/**
		 * 문자열에 특수문자(알파벳, 숫자, 언더스코어(_), 공백을 제외한 문자)가 포함되어 있는지 체크
		 * @param str
		 * @return
		 * @throws IllegalArgumentException
		 */
		public static boolean isSpecial(String str) {
			if ( isBlank(str) ) {
				throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
			}

	        return Pattern.compile("[^\\w\\s]").matcher(str).find();
		}

		/**
		 * 공백 체크
		 * @param str
		 * @return
		 */
		public static boolean isSpace(String str) {
			if ( isBlank(str) ) {
				throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
			}

			return Pattern.compile("[\\s]").matcher(str).find();
		}

		/**
		 * 한글을 제외한 문자로만 이루어져 있는지 체크
		 * @param str
		 * @return
		 */
		public static boolean isNotHangul(String str) {
			if ( isBlank(str) ) {
				throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
			}

			return str.matches("[^가-힣]+");
		}
	}

	/**
	 * 형식 체크
	 */
	public static class Format {

		private Format() {
			super();
		}

	    /**
	     * <pre>
	     * 이메일 형식 체크 (일반적인 유효성 검사)
	     * 이메일 주소는 로컬 파트와 도메인 파트로 구성됩니다.
	     * 로컬 파트는 영문자, 숫자, 일부 특수문자(. _ % + -)를 허용하며 점(.)으로 시작/끝나거나 연속될 수 없습니다.
	     * 도메인 파트는 영문자, 숫자, 하이픈(-)을 허용하며 점(.)으로 시작/끝나거나 연속될 수 없습니다.
	     * 최상위 도메인(TLD)은 최소 두 글자 이상의 영문자로 구성됩니다.
	     * </pre>
	     *
	     * @param str
	     * @return
	     * @throws IllegalArgumentException
	     */
		public static boolean isEmail(String str) {
			if ( isBlank(str) ) {
				throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
			}

			String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

			return str.matches(emailRegex);
		}

		/**
		 * 전화번호 형식 체크 (휴대폰 번호 제외)
		 * @param strVal
		 * @return
		 * @throws IllegalArgumentException
		 */
		public static boolean isPhoneNum(String str) {
			if ( isBlank(str) ) {
				throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
			}

			String phoneRegex = "^(02|03[1-3]|04[1-4]|05[1-5]|06[1-4])-?(\\d{3,4})-?(\\d{4})|^(070|050[2-7])-?(\\d{4})-?(\\d{4})|^(15|16|18)\\d{2}-?(\\d{4})$";

			return str.matches(phoneRegex);
		}

		/**
		 * 휴대폰 번호 형식 체크
		 * @param strVal
		 * @return
		 * @throws IllegalArgumentException
		 */
		public static boolean isCellPhoneNum(String str) {
			if ( isBlank(str) ) {
				throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
			}

			String cellPhoneRegex = "^010-?\\d{4}-?\\d{4}$";

			return str.matches(cellPhoneRegex);
		}

	    /**
	     * 사업자 등록번호 형식 체크 (대한민국 3-2-5 또는 10자리 숫자 형식)
	     * 하이픈(-) 유무에 관계없이 유효성을 검사합니다.
	     * 예: "123-45-67890", "1234567890" 모두 유효합니다.
	     *
	     * @param str
	     * @return
	     * @throws IllegalArgumentException
	     */
		public static boolean isCompanyRegNum(String str) {
			if ( isBlank(str) ) {
				throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
			}

			String companyRegNumRegex = "^\\d{3}-?\\d{2}-?\\d{5}$";

			return str.matches(companyRegNumRegex);
		}

		/**
		 * IPv4 형식 체크
		 * @param strVal
		 * @return
		 * @throws IllegalArgumentException
		 */
		public static boolean isIPv4(String str) {
			if ( isBlank(str) ) {
				throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
			}

			// 0부터 255까지의 숫자를 나타내는 정규식 패턴
		    // (?:...): 논캡처링 그룹. 성능에 약간의 이점을 줄 수 있습니다.
		    final String IPV4_OCTET_REGEX = "(?:25[0-5]|2[0-4][\\d]|1[\\d]{2}|[1-9][\\d]|[\\d])";

	        String ipv4Regex = "^" + IPV4_OCTET_REGEX + "\\."
	                + IPV4_OCTET_REGEX + "\\."
	                + IPV4_OCTET_REGEX + "\\."
	                + IPV4_OCTET_REGEX + "$";

	        return str.matches(ipv4Regex);
		}

		/**
		 * YYYYMMDD 형식 체크
		 *
		 * <pre>
		 * 윤년이나 월별 일수를 모두 고려하여 정확하게 날짜를 파싱하고 검증
		 * </pre>
		 *
		 * @param str
		 * @return
		 * @throws IllegalArgumentException
		 */
		public static boolean isYYYYMMDD(String str) {
			if ( isBlank(str) ) {
				throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
			}

			if ( !str.matches("^(\\d{8}|\\d{4}-\\d{2}-\\d{2})$") ) {
				return false;
			}

			try {
				if ( str.contains("-") ) {
					LocalDate.parse(str, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
					return true;
				} else {
					LocalDate.parse(str, DateTimeFormatter.ofPattern("yyyyMMdd"));
					return true;
				}
			} catch (DateTimeException e) {
				return false;
			}
		}

		/**
		 * HHmm 형식 체크
		 *
		 * <pre>
		 * HH:mm 형식 체크
		 * str.matches("^([01]\\d|2[0-3]):[0-5]\\d$")
		 * </pre>
		 *
		 * @param str
		 * @return
		 * @throws IllegalArgumentException
		 */
		public static boolean isHHmm(String str) {
			if ( isBlank(str) ) {
				throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
			}

			return str.matches("^([01]\\d|2[0-3])[0-5]\\d$");
		}

		/**
		 * HHmmss 형식 체크
		 *
		 * <pre>
		 * HH:mm:ss 형식 체크
		 * str.matches("^(?:[01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d$")
		 * </pre>
		 * @param str
		 * @return
		 * @throws IllegalArgumentException
		 */
		public static boolean isHHmmss(String str) {
			if ( isBlank(str) ) {
				throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
			}

			return str.matches("^(?:[01]\\d|2[0-3])[0-5]\\d[0-5]\\d$");
		}

		/**
		 * Y/N 형식 체크
		 * @param str
		 * @return
		 * @throws IllegalArgumentException
		 */
		public static boolean isYN(String str) {
			if ( isBlank(str) ) {
				throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
			}

			return str.matches("^[YN]$");
		}
	}

	/**
	 * 아이디, 비번 체크
	 */
	public static class Account {

		private Account() {
			super();
		}

		/**
		 * <pre>
		 * 아이디 형식 체크
		 *  1. 첫 글자 영문
		 *  2. 7자 이상 30자 이내
		 * </pre>
		 * @param str
		 * @return
		 * @throws IllegalArgumentException
		 */
		public static boolean isId(String str) {
			if ( isBlank(str) ) {
				throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
			}

			return str.matches("^[a-zA-Z][a-zA-Z0-9]{6,29}$");
		}

		/**
		 * <pre>
		 * 비밀번호 형식 체크
		 *  1. 첫 글자 영문
		 *  2. 첫 글자 이후 영문, 숫자, 특수문자 조합
		 *   - 영문, 숫자, 특수문자 중 2가지 조합: 최소 10자 이상
		 *   - 영문, 숫자, 특수문자 중 3가지 조합: 최소 8자 이상
		 *   - 1가지 이하 조합은 유효하지 않음.
		 * </pre>
		 * @param str
		 * @return
		 * @throws IllegalArgumentException
		 */
		public static boolean isPassword(String str) {
			if ( isBlank(str) ) {
				throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
			}

			// 1. 첫 글자 영문 확인 및 허용 문자 검증
			if (!str.matches("^[a-zA-Z][a-zA-Z\\d\\W]*$")) {
	            return false;
	        }

			// 2. 조합 개수 확인
			boolean hasLetter = Pattern.compile("[a-zA-Z]").matcher(str).find();	// 영문 포함 여부
			boolean hasDigit = Pattern.compile("\\d").matcher(str).find();			// 숫자 포함 여부

			// 특수문자 확인: 영문, 숫자, 언더스코어(`_`)를 제외한 문자가 있는지 확인
			boolean hasSpecialChar = Pattern.compile("[^a-zA-Z\\d\\s]").matcher(str).find();

			int combinationCount = 0;
	        if (hasLetter) combinationCount++;
	        if (hasDigit) combinationCount++;
	        if (hasSpecialChar) combinationCount++;

	        // 3. 길이 조건 최종 확인
	        if (combinationCount == 2) {
	            return str.length() >= 10;
	        } else if (combinationCount >= 3) {
	            return str.length() >= 8;
	        } else {
	            return false;
	        }
		}
	}

}
