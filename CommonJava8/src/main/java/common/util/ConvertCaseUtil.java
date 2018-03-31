package common.util;

public class ConvertCaseUtil {
	
	private ConvertCaseUtil() {
		super();
	}

	/**
	 * <pre>
	 * 첫 단어를 소문자로 시작하고, 나머지는 소문자로 표기
	 * 띄어쓰기 및 '_' 다음 단어를 대문자로 구분
	 * </pre>
	 * @param str
	 * @return
	 */
	public static String camelCase(String str) {
		StringBuilder sb = new StringBuilder();

		boolean isNext = false;
		int nLen = str.length();
		char chCurrent;

		for (int i=0; i < nLen; i++) {
			chCurrent = str.charAt(i);
			if ( (chCurrent == ' ') ||  (chCurrent == '_') ) {
				isNext = true;
			} else {
				char chTemp;
				if (isNext) {
					chTemp = Character.toUpperCase(chCurrent);
					sb.append(chTemp);
					isNext = false;
				} else {
					chTemp = Character.toLowerCase(chCurrent);
					sb.append(chTemp);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * <pre>
	 * 첫 단어를 소문자로 시작하고, 나머지는 소문자로 표기
	 * 해당 문자 다음 단어를 대문자로 구분
	 * </pre>
	 * @param chKey
	 * @param str
	 * @return
	 */
	public static String camelCase(char chKey, String str) {
		StringBuilder sb = new StringBuilder();

		boolean isUnderScore = (str.indexOf(chKey) < 0);
		if (isUnderScore) {
			return str;
		}

		boolean isNext = false;
		int nLen = str.length();
		char chCurrent;

		for (int i=0; i < nLen; i++) {
			chCurrent = str.charAt(i);
			if (chCurrent == chKey) {
				isNext = true;
			} else {
				char chTemp;
				if (isNext) {
					chTemp = Character.toUpperCase(chCurrent);
					sb.append(chTemp);
					isNext = false;
				} else {
					chTemp = Character.toLowerCase(chCurrent);
					sb.append(chTemp);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * <pre>
	 * 첫 단어를 대문자로 시작하고, 나머지는 소문자로 표기
	 * 띄어쓰기 및 '_' 다음 단어를 대문자로 구분
	 * </pre>
	 * @param str
	 * @return
	 */
	public static String pascalCase(String str) {
		StringBuilder sb = new StringBuilder();

		String sCamelCase = camelCase(str);
		char chFirst = sCamelCase.charAt(0);

		sb.append(Character.toTitleCase(chFirst));
		sb.append(sCamelCase.substring(1));

		return sb.toString();
	}

}
