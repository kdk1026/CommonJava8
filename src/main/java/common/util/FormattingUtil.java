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

	private FormattingUtil() {
		super();
	}

	private static class ExceptionMessage {

		public static String isNullOrEmpty(String paramName) {
	        return String.format("'%s' is null or empty", paramName);
	    }

	}

	/**
	 * <pre>
	 * 전환번호 포맷
	 *   - 0x(x)-xxx(x)-xxxx
	 * </pre>
	 *
	 * @param str
	 * @param isHyphen
	 * @return
	 */
	public static String makePhoneNumber(String str, boolean isHyphen) {
		if ( StringUtils.isBlank(str) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
		}

		String sPattern = "^(02|03[1-3]|04[1-4]|05[1-5]|06[1-4])-?(\\d{3,4})-?(\\d{4})$";
		if (!str.matches(sPattern)) {
			return null;
		}
		return str.replaceAll(sPattern, (isHyphen) ? FORMAT_HYPHEN : FORMAT_NOT_HYPHEN);
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
	public static String makeCellPhoneNumber(String str, boolean isHyphen) {
		if ( StringUtils.isBlank(str) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
		}

		String sPattern = "^(01[016789])-?(\\d{3,4})-?(\\d{4})$";
		if (!str.matches(sPattern)) {
			return null;
		}
		return str.replaceAll(sPattern, (isHyphen) ? FORMAT_HYPHEN : FORMAT_NOT_HYPHEN);
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

		String sPattern = "^(\\d{3})-?(\\d{2})-?(\\d{5})$";
		if (!str.matches(sPattern)) {
			return null;
		}
		return str.replaceAll(sPattern, (isHyphen) ? FORMAT_HYPHEN : FORMAT_NOT_HYPHEN);
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

		String sMoney = str.replace(",", "");

		String[] asHanNum1 = { "", "일", "이", "삼", "사", "오", "육", "칠", "팔", "구" };
		String[] asHanNum2 = { "", "십", "백", "천" };
		String[] asHanNum3 = { "", "만", "억", "조" };

		StringBuilder sb = new StringBuilder();

		int nCnt = 0;
		int nLen = sMoney.length();
		for (int i = 0; i < nLen; i++) {
			int n = sMoney.charAt(i);
			sb.append(asHanNum1[n]);

			int nTemp = (nLen - 1 - i);
			if (n > 0) {
				sb.append(asHanNum2[nTemp % 4]);
			} else {
				nCnt++;
			}

			// 4의 배수
			if (((nTemp % 4) == 0) && nCnt != 4) {
				// 만억조
				sb.append(asHanNum3[nTemp / 4]);
			}
			nCnt = 0;
		}
		sb.append("원");
		return sb.toString();
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

		String sPattern = "";
		String sFormat = "";

		switch (str.length()) {
		case 16:
			sPattern = "^(\\d{4})-?(\\d{4})-?(\\d{4})-?(\\d{4})$";
			sFormat = (isHyphen) ? "$1-$2-$3-$4" : "$1$2$3$4";
			break;

		case 15:
			sPattern = "^(\\d{4})-?(\\d{6})-?(\\d{5})$";
			sFormat = (isHyphen) ? FORMAT_HYPHEN : FORMAT_NOT_HYPHEN;
			break;

		default:
			break;
		}

		if (!str.matches(sPattern)) {
			return null;
		}

		return str.replaceAll(sPattern, sFormat);
	}

}
