package common.util;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 8. 13. 김대광	JavaDoc 작성 (SonarLint 지시에 따른 수정 : replaceAll -> replace , 정규식 관련은 어쩔 수가 없다...)
 * 			마음같아서는 FormattingUtil, MaskingUtil 분리하고 싶은데... 지금은 너무 귀찮구나...
 * </pre>
 *
 *
 * @author 김대광
 */
public class FormattingUtil {

	private FormattingUtil() {
		super();
	}

	private static final String FORMAT_HYPHEN = "$1-$2-$3";
	private static final String FORMAT_NOT_HYPHEN = "$1$2$3";

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
			throw new IllegalArgumentException("str is null");
		}

		String sPattern = "^(0[2|3[1|2|3]|4[1|2|3|4]|5[1|2|3|4|5]|6[1|2|3|4]])-?(\\d{3,4})-?(\\d{4})+$";
		if (!str.matches(sPattern)) {
			return null;
		}
		return str.replaceAll(sPattern, (isHyphen) ? FORMAT_HYPHEN : FORMAT_NOT_HYPHEN);
	}

	/**
	 * <pre>
	 * 전환번호 마스킹 포맷
	 *   - 0x(x)-***(*)-xxxx
	 * </pre>
	 *
	 * @param str
	 * @param isHyphen
	 * @return
	 */
	public static String makePhoneNumberMasking(String str, boolean isHyphen) {
		if ( StringUtils.isBlank(str) ) {
			throw new IllegalArgumentException("str is null");
		}

		Pattern pattern = Pattern
				.compile("^(0[2|3[1|2|3]|4[1|2|3|4]|5[1|2|3|4|5]|6[1|2|3|4]])-?(\\d{3,4})-?(\\d{4})+$");

		Matcher matcher = pattern.matcher(str);
		if (!matcher.find()) {
			return null;
		}

		String sTarget = matcher.group(2);
		char[] c = new char[sTarget.length()];
		Arrays.fill(c, '*');

		StringBuilder sb = new StringBuilder();
		sb.append(matcher.group(1)).append(String.valueOf(c)).append(matcher.group(3));

		String sPhoneNum = sb.toString();
		String sMaskingPattern = "^(0[2|3[1|2|3]|4[1|2|3|4]|5[1|2|3|4|5]|6[1|2|3|4]])-?([*]{3,4})-?(\\d{4})+$";

		if (!sPhoneNum.matches(sMaskingPattern)) {
			return null;
		}

		return sPhoneNum.replaceAll(sMaskingPattern, (isHyphen) ? FORMAT_HYPHEN : FORMAT_NOT_HYPHEN);
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
			throw new IllegalArgumentException("str is null");
		}

		String sPattern = "^(01[016789])-?(\\d{3,4})-?(\\d{4})$";
		if (!str.matches(sPattern)) {
			return null;
		}
		return str.replaceAll(sPattern, (isHyphen) ? FORMAT_HYPHEN : FORMAT_NOT_HYPHEN);
	}

	/**
	 * <pre>
	 * 휴대폰 번호 마스킹 포맷
	 *   - 01x-***(*)-xxxx
	 * </pre>
	 *
	 * @param str
	 * @param isHyphen
	 * @return
	 */
	public static String makeCellPhoneNumberMasking(String str, boolean isHyphen) {
		if ( StringUtils.isBlank(str) ) {
			throw new IllegalArgumentException("str is null");
		}

		Pattern pattern = Pattern.compile("^(01[016789])-?(\\d{3,4})-?(\\d{4})$");

		Matcher matcher = pattern.matcher(str);
		if (!matcher.find()) {
			return null;
		}

		String sTarget = matcher.group(2);
		char[] c = new char[sTarget.length()];
		Arrays.fill(c, '*');

		StringBuilder sb = new StringBuilder();
		sb.append(matcher.group(1)).append(String.valueOf(c)).append(matcher.group(3));

		String sCellPhoneNum = sb.toString();
		String sMaskingPattern = "^(01[016789])-?([*]{3,4})-?(\\d{4})+$";

		if (!sCellPhoneNum.matches(sMaskingPattern)) {
			return null;
		}

		return sCellPhoneNum.replaceAll(sMaskingPattern, (isHyphen) ? FORMAT_HYPHEN : FORMAT_NOT_HYPHEN);
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
			throw new IllegalArgumentException("str is null");
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
			throw new IllegalArgumentException("str is null");
		}

		String sPattern = "^([0-9]{4})[- / .]?(0[1-9]|1[012])[- / .]?(0[1-9]|1[0-9]|2[0-9]|3[01])+$";
		if (!str.matches(sPattern)) {
			return null;
		}
		return str.replaceAll(sPattern, (isHyphen) ? FORMAT_HYPHEN : FORMAT_NOT_HYPHEN);
	}

	/**
	 * <pre>
	 * 생년월일 마스킹 포맷
	 * 	- ****-**-**, YY**-**-**
	 * </pre>
	 *
	 * @param str
	 * @param isShowHundred
	 *  @param isHyphen
	 * @return
	 */
	public static String makeBirthdayMasking(String str, boolean isShowHundred, boolean isHyphen) {
		if ( StringUtils.isBlank(str) ) {
			throw new IllegalArgumentException("str is null");
		}

		Pattern pattern = Pattern
				.compile("^([0-9]{2})([0-9]{2})[- / .]?(0[1-9]|1[012])[- / .]?(0[1-9]|1[0-9]|2[0-9]|3[01])+$");

		Matcher matcher = pattern.matcher(str);
		if (!matcher.find()) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		for (int i = 1; i <= matcher.groupCount(); i++) {
			String sTarget = matcher.group(i);

			char[] c = new char[sTarget.length()];
			Arrays.fill(c, '*');

			if (isShowHundred && i == 1) {
				sb.append(sTarget);
			} else {
				sb.append(String.valueOf(c));
			}
		}

		String sPattern = "^(.{4})(.{2})(.{2})$";
		return sb.toString().replaceAll(sPattern, (isHyphen) ? FORMAT_HYPHEN : FORMAT_NOT_HYPHEN);
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
			throw new IllegalArgumentException("str is null");
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
	 * 이름 마스킹 포맷
	 *   - O*, O*O, O**O
	 * </pre>
	 *
	 * @param str
	 * @return
	 */
	public static String makeNameMasking(String str) {
		if ( StringUtils.isBlank(str) ) {
			throw new IllegalArgumentException("str is null");
		}

		int nLen = str.length();
		String sPattern = "";
		switch (nLen) {
		case 2:
			sPattern = "^(.)(.+)$";
			break;

		case 4:
			sPattern = "^(.)(.+)(.)(.)$";
			break;

		default:
			sPattern = "^(.)(.+)(.)$";
			break;
		}

		Pattern pattern = Pattern.compile(sPattern);

		Matcher matcher = pattern.matcher(str);
		if (!matcher.find()) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		for (int i = 1; i <= matcher.groupCount(); i++) {
			String sTarget = matcher.group(i);
			boolean isTrue = false;

			if (nLen == 4) {
				isTrue = (i == 2 || i == 3);
			} else {
				isTrue = (i == 2);
			}

			if (isTrue) {
				char[] c = new char[sTarget.length()];
				Arrays.fill(c, '*');

				sb.append(String.valueOf(c));
			} else {
				sb.append(sTarget);
			}
		}

		return sb.toString();
	}

	/**
	 * <pre>
	 * IPv4 마스킹 포맷
	 *   - xxx.xxx.***.xxx
	 * </pre>
	 *
	 * @param str
	 * @return
	 */
	public static String makeIpv4AddrMasking(String str) {
		if ( StringUtils.isBlank(str) ) {
			throw new IllegalArgumentException("str is null");
		}

		StringBuilder sb = new StringBuilder();

		String[] sAddr = str.split("\\.");

		int i = 1;
		for (String s : sAddr) {
			if (i == 3) {
				char[] c = new char[s.length()];
				Arrays.fill(c, '*');

				sb.append(String.valueOf(c)).append(".");
			} else {
				if (i == sAddr.length) {
					sb.append(s);
				} else {
					sb.append(s).append(".");
				}
			}

			i++;
		}

		return sb.toString();
	}

	/**
	 * <pre>
	 * IPv6 마스킹 포맷
	 *   - xxx::xxx:xxx:***:xxx
	 * </pre>
	 *
	 * @param str
	 * @return
	 */
	public static String makeIpv6AddrMasking(String str) {
		if ( StringUtils.isBlank(str) ) {
			throw new IllegalArgumentException("str is null");
		}

		StringBuilder sb = new StringBuilder();

		String[] sAddr = str.split(":");

		int i = 1;
		for (String s : sAddr) {
			if (i < 2) {
				sb.append(s);
			} else if (i == 5) {
				char[] c = new char[s.length()];
				Arrays.fill(c, '*');

				sb.append(String.valueOf(c)).append(":");
			} else {
				if (i == 2) {
					sb.append(":");
				} else if (i == sAddr.length) {
					sb.append(s);
				} else {
					sb.append(s).append(":");
				}
			}

			i++;
		}

		return sb.toString();
	}

	/**
	 * 비밀번호 마스킹
	 *
	 * @param str
	 * @return
	 */
	public static String passwordMasking(String str) {
		if ( StringUtils.isBlank(str) ) {
			throw new IllegalArgumentException("str is null");
		}

		char[] c = new char[str.length()];
		Arrays.fill(c, '*');
		return String.valueOf(c);
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
			throw new IllegalArgumentException("str is null");
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

	/**
	 * <pre>
	 * 카드번호 마스킹 포맷
	 *   - (16자리) ####-##**-****-####
	 *   - (15자리) ####-##****-**###
	 * </pre>
	 *
	 * @param str
	 * @param isHyphen
	 * @return
	 */
	public static String makeCardNoMasking(String str, boolean isHyphen) {
		if ( StringUtils.isBlank(str) ) {
			throw new IllegalArgumentException("str is null");
		}

		String sCardNo = "";
		StringBuilder sb = new StringBuilder();

		Pattern pattern = null;
		Matcher matcher = null;
		String sMaskingPattern = "";
		String sFormat = "";

		String sTarget = "";
		char[] c1 = null;
		char[] c2 = null;

		switch (str.length()) {
		case 16:
			pattern = Pattern.compile("^(\\d{4})-?(\\d{2})(\\d{2})-?(\\d{4})-?(\\d{4})$");

			matcher = pattern.matcher(str);
			if (!matcher.find()) {
				return null;
			}

			sTarget = matcher.group(3);
			c1 = new char[sTarget.length()];
			Arrays.fill(c1, '*');

			sTarget = matcher.group(4);
			c2 = new char[sTarget.length()];
			Arrays.fill(c2, '*');

			sb.append(matcher.group(1)).append(matcher.group(2));
			sb.append(String.valueOf(c1)).append(String.valueOf(c2)).append(matcher.group(5));

			sCardNo = sb.toString();
			sMaskingPattern = "(\\d{4})-?(\\d{2})([*]{2})-?([*]{4})-?(\\d{4})+$";

			sFormat = (isHyphen) ? "$1-$2$3-$4-$5" : "$1$2$3$4$5";

			break;

		case 15:
			pattern = Pattern.compile("^(\\d{4})-?(\\d{2})(\\d{4})-?(\\d{2})(\\d{3})$");

			matcher = pattern.matcher(str);
			if (!matcher.find()) {
				return null;
			}

			sTarget = matcher.group(3);
			c1 = new char[sTarget.length()];
			Arrays.fill(c1, '*');

			sTarget = matcher.group(4);
			c2 = new char[sTarget.length()];
			Arrays.fill(c2, '*');

			sb.append(matcher.group(1)).append(matcher.group(2));
			sb.append(String.valueOf(c1)).append(String.valueOf(c2)).append(matcher.group(5));

			sCardNo = sb.toString();
			sMaskingPattern = "(\\d{4})-?(\\d{2})([*]{4})-?([*]{2})(\\d{3})+$";

			sFormat = (isHyphen) ? "$1-$2$3-$4$5" : "$1$2$3$4$5";

			break;

		default:
			break;
		}

		if ( !sCardNo.matches(sMaskingPattern) ) {
			return null;
		}

		return sCardNo.replaceAll(sMaskingPattern, sFormat);
	}

}
