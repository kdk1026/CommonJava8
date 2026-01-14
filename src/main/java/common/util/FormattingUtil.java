package common.util;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 8. 13. 김대광	JavaDoc 작성 (SonarLint 지시에 따른 수정 : replaceAll -> replace , 정규식 관련은 어쩔 수가 없다...)
 * 			마음같아서는 FormattingUtil, MaskingUtil 분리하고 싶은데... 지금은 너무 귀찮구나...
 * 2026. 1. 13. 김대광 MaskingUtil이 있으므로 마스킹 관련 제거
 * </pre>
 *
 *
 * @author 김대광
 */
public class FormattingUtil {

	private static final String FORMAT_HYPHEN = "$1-$2-$3";
	private static final String FORMAT_NOT_HYPHEN = "$1$2$3";

	private static final String FORMAT_BIZ_HYPHEN = "$1$2-$3";
	private static final String FORMAT_BIZ_NOT_HYPHEN = "$1$2$3";

	private FormattingUtil() {
		super();
	}

	private static class ExceptionMessage {

		public static String isNullOrEmpty(String paramName) {
	        return String.format("'%s' is null or empty", paramName);
	    }

	}

	/**
	 * 전화번호 포맷
	 *
	 * <pre>
	 * 휴대폰 번호
	 * 일반 전화번호
	 * 070 인터넷 전화(VoIP)
	 * 080 수신자 부담 전화
	 * 030, 050 평생번호 및 안심번호
	 * 15xx, 16xx, 18xx 등 전국 대표번호
	 * </pre>
	 * @param str
	 * @param isHyphen
	 * @return
	 */
	public static String makePhoneNum(String str, boolean isHyphen) {
		if ( StringUtils.isBlank(str) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
		}

		String result = makeCellPhoneNum(str, isHyphen);

		if ( StringUtils.isBlank(result) ) {
			result = makeBasicPhoneNum(str, isHyphen);
		}

		if ( StringUtils.isBlank(result) ) {
			result = makeInternetPhoneNum(str, isHyphen);
		}

		if ( StringUtils.isBlank(result) ) {
			result = makeTollFreePhoneNum(str, isHyphen);
		}

		if ( StringUtils.isBlank(result) ) {
			result = makeVirtualPhoneNum(str, isHyphen);
		}

		if ( StringUtils.isBlank(result) ) {
			result = makeBusinessPhoneNum(str, isHyphen);
		}

		return result;
	}

	/**
	 * <pre>
	 * 일반 전환번호 포맷
	 *   - 0x(x)-xxx(x)-xxxx
	 * </pre>
	 *
	 * @param str
	 * @param isHyphen
	 * @return
	 */
	public static String makeBasicPhoneNum(String str, boolean isHyphen) {
		if ( StringUtils.isBlank(str) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
		}

		String pattern = "^(02|03[1-3]|04[1-4]|05[1-5]|06[1-4])-?(\\d{3,4})-?(\\d{4})$";
		if (!str.matches(pattern)) {
			return null;
		}
		return str.replaceAll(pattern, (isHyphen) ? FORMAT_HYPHEN : FORMAT_NOT_HYPHEN);
	}

	/**
	 * <pre>
	 * 070 인터넷 전화(VoIP) 포맷
	 *   - 070-xxx(x)-xxxx
	 * </pre>
	 *
	 * @param str
	 * @param isHyphen
	 * @return
	 */
	public static String makeInternetPhoneNum(String str, boolean isHyphen) {
		if ( StringUtils.isBlank(str) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
		}

		String pattern = "^070-?(\\d{3,4})-?(\\d{4})$";
		if (!str.matches(pattern)) {
			return null;
		}
		return str.replaceAll(pattern, (isHyphen) ? FORMAT_HYPHEN : FORMAT_NOT_HYPHEN);
	}

	/**
	 * <pre>
	 * 080 수신자 부담 전화 포맷
	 *   - 080-xxx(x)-xxxx
	 * </pre>
	 *
	 * @param str
	 * @param isHyphen
	 * @return
	 */
	public static String makeTollFreePhoneNum(String str, boolean isHyphen) {
		if ( StringUtils.isBlank(str) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
		}

		String pattern = "^080-?(\\d{3,4})-?(\\d{4})$";
		if (!str.matches(pattern)) {
			return null;
		}
		return str.replaceAll(pattern, (isHyphen) ? FORMAT_HYPHEN : FORMAT_NOT_HYPHEN);
	}

	/**
	 * <pre>
	 * 030, 050 평생번호 및 안심번호 포맷
	 *   - 030|050-xxx(x)-xxxx
	 * </pre>
	 *
	 * @param str
	 * @param isHyphen
	 * @return
	 */
	public static String makeVirtualPhoneNum(String str, boolean isHyphen) {
		if ( StringUtils.isBlank(str) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
		}

		String pattern = "^(030|050\\d)-?(\\d{3,4})-?(\\d{4})$";
		if (!str.matches(pattern)) {
			return null;
		}
		return str.replaceAll(pattern, (isHyphen) ? FORMAT_HYPHEN : FORMAT_NOT_HYPHEN);
	}

	/**
	 * <pre>
	 * 15xx, 16xx, 18xx 등 전국 대표번호 포맷
	 *   - 15|16|18xx-xxxx
	 * </pre>
	 *
	 * @param str
	 * @param isHyphen
	 * @return
	 */
	public static String makeBusinessPhoneNum(String str, boolean isHyphen) {
		if ( StringUtils.isBlank(str) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
		}

		String pattern = "^(15|16|18)(\\d{2})-?(\\d{4})$";
		if (!str.matches(pattern)) {
			return null;
		}
		return str.replaceAll(pattern, (isHyphen) ? FORMAT_BIZ_HYPHEN : FORMAT_BIZ_NOT_HYPHEN);
	}

	/**
	 * <pre>
	 * 휴대폰 번호 포맷
	 *   - 01x-xxx(x)-xxxx
	 * </pre>
	 *
	 * @param str
	 * @param isHyphen
	 * @return
	 */
	public static String makeCellPhoneNum(String str, boolean isHyphen) {
		if ( StringUtils.isBlank(str) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
		}

		String pattern = "^(01[016789])-?(\\d{3,4})-?(\\d{4})$";
		if (!str.matches(pattern)) {
			return null;
		}
		return str.replaceAll(pattern, (isHyphen) ? FORMAT_HYPHEN : FORMAT_NOT_HYPHEN);
	}

	/**
	 * <pre>
	 * 사업자 등록번호 포맷
	 *   - xxx-xx-xxxxx
	 * </pre>
	 *
	 * @param sBusinessRegNum
	 * @param isHyphen
	 * @return
	 */
	public static String makeBusinessRegNum(String str, boolean isHyphen) {
		if ( StringUtils.isBlank(str) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
		}

		String pattern = "^(\\d{3})-?(\\d{2})-?(\\d{5})$";
		if (!str.matches(pattern)) {
			return null;
		}
		return str.replaceAll(pattern, (isHyphen) ? FORMAT_HYPHEN : FORMAT_NOT_HYPHEN);
	}

	/**
	 * <pre>
	 * 날짜 포맷
	 *   - YYYY-MM-DD
	 * </pre>
	 *
	 * @param str
	 * @param isHyphen
	 * @return
	 */
	public static String makeYYYYMMDD(String str, boolean isHyphen) {
		if ( StringUtils.isBlank(str) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
		}

		final String datePattern = "^(\\d{4})[\\-/. ]?(\\d{2})[\\-/. ]?(\\d{2})$";

		Pattern pattern = Pattern.compile(datePattern);
        Matcher matcher = pattern.matcher(str);

        if ( !matcher.matches() ) {
        	return null;
        }

        String year = matcher.group(1);
        String month = matcher.group(2);
        String day = matcher.group(3);

        if (isHyphen) {
            return String.format("%s-%s-%s", year, month, day);
        } else {
            return String.format("%s%s%s", year, month, day);
        }
	}

	/**
	 * <pre>
	 * 카드번호 포맷
	 *   - (16자리) ####-####-####-####
	 *   - (15자리) ####-######-#####
	 * </pre>
	 *
	 * @param str
	 * @param isHyphen
	 * @return
	 */
	public static String makeCardNo(String str, boolean isHyphen) {
		if ( StringUtils.isBlank(str) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
		}

		String pattern = "";
		String format = "";

		switch (str.length()) {
		case 16:
			pattern = "^(\\d{4})-?(\\d{4})-?(\\d{4})-?(\\d{4})$";
			format = (isHyphen) ? "$1-$2-$3-$4" : "$1$2$3$4";
			break;

		case 15:
			pattern = "^(\\d{4})-?(\\d{6})-?(\\d{5})$";
			format = (isHyphen) ? FORMAT_HYPHEN : FORMAT_NOT_HYPHEN;
			break;

		default:
			break;
		}

		if (!str.matches(pattern)) {
			return null;
		}

		return str.replaceAll(pattern, format);
	}

	/**
	 * <pre>
	 * 수치를 금액 표현으로 변환
	 *   - #,###
	 * </pre>
	 *
	 * @param num
	 * @return
	 */
	public static String convertMoneyFormat(int num) {
		DecimalFormat df = new DecimalFormat("#,###");
		return df.format(num);
	}

	/**
	 * 숫자금액 문자열을 한글 금액 표현으로 변환
	 *
	 * @param str
	 * @return
	 */
	public static String convertMoneyHangul(String str) {
		if ( StringUtils.isBlank(str) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
		}

		String moneyStr = str.replace(",", "");
		if (!moneyStr.matches("^\\d+$")) {
			return "";
		}

		String[] units = {"", "일", "이", "삼", "사", "오", "육", "칠", "팔", "구"};
	    String[] smallUnits = {"", "십", "백", "천"};
	    String[] largeUnits = {"", "만", "억", "조", "경"};

	    StringBuilder result = new StringBuilder();
	    int length = moneyStr.length();

		for (int i = 0; i < length; i++) {
			int digit = moneyStr.charAt(i) - '0';
			int reversePos = length - 1 - i;

			if (digit > 0) {
				if (!(digit == 1 && reversePos % 4 != 0)) {
					result.append(units[digit]);
				}
				result.append(smallUnits[reversePos % 4]);
			}

			if (reversePos % 4 == 0) {
				int end = i + 1;
				int start = Math.max(0, end - 4);
				long currentSectionValue = Long.parseLong(moneyStr.substring(start, end));

				if (currentSectionValue > 0) {
					result.append(largeUnits[reversePos / 4]);
				}
			}
		}

		if (result.length() > 0) {
			result.append("원");
		}

		return result.toString();
	}

}
