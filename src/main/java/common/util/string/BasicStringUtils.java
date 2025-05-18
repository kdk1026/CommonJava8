package common.util.string;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 8. 13. 김대광	SonarLint 지시에 따른 수정
 * </pre>
 *
 * commons.lang 라이브러리 사용 권장
 * @author 김대광
 */
public class BasicStringUtils {

	private BasicStringUtils() {
		super();
	}

	/**
	 * Null, 공백 체크
	 * @param str
	 * @return
	 */
	public static boolean isBlank(final String str) {
		return (str == null) || (str.trim().isEmpty());
	}

    /**
     * null인 경우 기본 문자열로 대체
     * @param str
     * @param defaultStr
     * @return
     */
    public static String defaultString(final String str, final String defaultStr) {
        return isBlank(str) ? defaultStr : str;
    }

	/**
	 * 해당 문자 포함 여부 체크
	 * @param str
	 * @param validChars
	 * @return
	 */
	public static boolean isContins(final String str, final String validChars) {
		if ( isBlank(str) ) {
			throw new IllegalArgumentException("str");
		}

		if ( isBlank(validChars) ) {
			throw new IllegalArgumentException("validChars");
		}

		return str.indexOf(validChars) > -1;
	}

	/**
	 * 좌측에 자리수 만큼 대체 문자 채우기
	 * @param str
	 * @param size
	 * @param ch
	 * @return
	 */
	public static String leftPad(final String str, final int size, char ch) {
		if ( isBlank(str) ) {
			throw new IllegalArgumentException("str");
		}

		if ( size <= 0 ) {
			throw new IllegalArgumentException("size");
		}

		if ( ch == 0 ) {
			throw new IllegalArgumentException("ch");
		}

		return (size > str.length()) ? leftPad(ch + str, size, ch) : str;
	}

	/**
	 * 우측에 자리수 만큼 대체 문자 채우기
	 * @param str
	 * @param size
	 * @param ch
	 * @return
	 */
	public static String rightPad(final String str, final int size, char ch) {
		if ( isBlank(str) ) {
			throw new IllegalArgumentException("str");
		}

		if ( size <= 0 ) {
			throw new IllegalArgumentException("size");
		}

		if ( ch == 0 ) {
			throw new IllegalArgumentException("ch");
		}

		return (size > str.length()) ? rightPad(str + ch, size, ch) : str;
	}

	/**
	 * String To Hex
	 * @param str
	 * @return
	 */
	public static String encodeHex(final String str) {
		if ( isBlank(str) ) {
			throw new IllegalArgumentException("str");
		}

		char[] chars = str.toCharArray();
		StringBuilder sb = new StringBuilder();

	    for (int i = 0; i < chars.length; i++) {
	    	sb.append(Integer.toHexString(chars[i]));
	    }
	    return sb.toString();
	}

	/**
	 * Hex To String
	 * @param str
	 * @return
	 */
	public static String decodeHex(final String str) {
		if ( isBlank(str) ) {
			throw new IllegalArgumentException("str");
		}

		StringBuilder sb = new StringBuilder();
		String s = "";

		for (int i = 0; i < str.length(); i++) {
			s = str.substring(i, i + 2);
			sb.append((char) Integer.parseInt(s, 16));
		}
		return sb.toString();
	}

}
