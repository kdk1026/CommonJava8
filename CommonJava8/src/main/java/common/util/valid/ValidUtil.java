package common.util.valid;

public class ValidUtil {

	private ValidUtil() {
		super();
	}
	
	/**
	 * Null, 공백 체크
	 * @param str
	 * @return
	 */
	public static boolean isBlank(String str) {
		return (str == null || str.replace("/ /gi", "") == "");
	}
	
	/**
	 * 문자열 길이 최소/최대 길이 준수 여부
	 * @param str
	 * @param min
	 * @param max
	 * @return
	 */
	public static boolean isLengthOver(String str, int min, int max) {
		int strLen = str.length();
		return (strLen < min) || (strLen > max);
	}
	
	/**
	 * 숫자 체크
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str) {
		return str.matches("^[0-9]+$");
	}

	/**
	 * 영문 체크
	 * @param str
	 * @return
	 */
	public static boolean isEnglish(String str) {
		return str.matches("^[a-zA-Z]+$");
	}

	/**
	 * 영문, 공백 체크
	 * @param str
	 * @return
	 */
	public static boolean isEngBlank(String str) {
		return str.matches("^[a-zA-Z\\s]+$");
	}

	/**
	 * 영문, 숫자 체크
	 * @param str
	 * @return
	 */
	public static boolean isEngNum(String str) {
		return str.matches("^[a-zA-Z0-9]+$");
	}

	/**
	 * 한글 체크
	 * @param str
	 * @return
	 */
	public static boolean isHangul(String str) {
		return str.matches("^[가-힣]+$");
	}

	/**
	 * 한글, 공백 체크
	 * @param str
	 * @return
	 */
	public static boolean isHanBlank(String str) {
		return str.matches("^[가-힣\\s]+$");
	}

	/**
	 * 한글, 영문 체크
	 * @param str
	 * @return
	 */
	public static boolean isHanEng(String str) {
		return str.matches("^[가-힣a-zA-Z]+$");
	}

	/**
	 * 특수문자 체크
	 * @param str
	 * @return
	 */
	public static boolean isSpecial(String str) {
		return str.matches("^.*[\\W|~!@#[$]%^&*\\(\\)-[_]+[|]<>?:\\{\\}].*+$");
	}

	/**
	 * 이메일 형식 체크
	 * @param strVal
	 * @return
	 */
	public static boolean isEmail(String str) {
		return str.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})+$");
	}

	/**
	 * 전화번호 형식 체크
	 * @param strVal
	 * @return
	 */
	public static boolean isPhoneNum(String str) {
		return str.matches("^(0[2|3[1|2|3]|4[1|2|3|4]|5[1|2|3|4|5]|6[1|2|3|4]])-?(\\d{3,4})-?(\\d{4})+$");
	}

	/**
	 * 휴대폰 번호 형식 체크
	 * @param strVal
	 * @return
	 */
	public static boolean isCellPhoneNum(String str) {
		return str.matches("^(01[016789])-?(\\d{3,4})-?(\\d{4})+$");
	}

	/**
	 * 사업자 등록번호 형식 체크
	 * @param strVal
	 * @return
	 */
	public static boolean isCompanyRegNum(String str) {
		return str.matches("^[(\\d{3})-?(\\d{2})-?(\\d{5})+$]");
	}

	/**
	 * IPv4 형식 체크
	 * @param strVal
	 * @return
	 */
	public static boolean isIPv4(String str) {
		return str.matches("^(1[0-9]{2}|2[0-5][0-5]|[0-9]{1,2})(\\.(1[0-9]{2}|2[0-5][0-5]|[0-9]{1,2})){3}+$");
	}
	
	/**
	 * YYYYMMDD 형식 체크
	 * @param str
	 * @return
	 */
	public static boolean isYYYYMMDD(String str) {
		return str.matches("^[0-9]{4}(0[1-9]|1[012])(0[1-9]|1[0-9]|2[0-9]|3[01])+$");
	}
	
	/**
	 * HHmmss 형식 체크
	 * @param str
	 * @return
	 */
	public static boolean isHHmmss(String str) {
		return str.matches("^((0[1-9])|1[0-9]|2[0-4])([0-5][0-9])([0-5][0-9])+$");
	}
	
	/**
	 * Y/N 형식 체크
	 * @param str
	 * @return
	 */
	public static boolean isYN(String str) {
		return str.matches("^[Y|N]+$");
	}
	
	/**
	 * <pre>
	 * 아이디 형식 체크
	 *  1. 첫 글자 영문
	 *  2. 7자 이상 30자 이내
	 * </pre>
	 * @param str
	 * @return
	 */
	public static boolean isId(String str) {
		return str.matches("^[a-zA-z](?=.*[a-zA-Z])[a-zA-Z0-9]{6,29}$");
	}
	
	/**
	 * <pre>
	 * 비밀번호 형식 체크
	 *  1. 첫 글자 영문
	 *  2. 첫 글자 이후 영문, 숫자, 특수문자 조합
	 *    2.1. 2가지 조합 시, 10자리 이상
	 *    2.2. 3가지 조합 시, 8자리 이상
	 * </pre>
	 * @param str
	 * @return
	 */
	public static boolean isPassword(String str) {
		String sPattern1 = "^[a-zA-z](?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9\\W|~!@#[$]%^&*\\(\\)-[_]+[|]<>?:\\{\\}]{9,}$";
		String sPattern2 = "^[a-zA-z](?=.*[a-zA-Z])(?=.*[0-9])(?=.*[\\W|~!@#[$]%^&*\\(\\)-[_]+[|]<>?:\\{\\}])[a-zA-Z0-9\\W|~!@#[$]%^&*\\(\\)-[_]+[|]<>?:\\{\\}]{7,}$";
		
		return str.matches(sPattern1) || str.matches(sPattern2);
	}
	
}
