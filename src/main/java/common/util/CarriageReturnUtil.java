package common.util;

 /**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 8. 11. 김대광	최초작성
 * </pre>
 *
 *
 * @author 김대광
 */
public class CarriageReturnUtil {

	private CarriageReturnUtil() {
		super();
	}

	private static final String BR_TAG = "<br />";

	/**
	 * 캐리지 리턴 문자열을 줄바꿈 태그로 변환
	 * @param content
	 * @return
	 */
	public static String changeBrTag(String content) {
		String osName = System.getProperty("os.name");

		double dOsVersion = Double.parseDouble(System.getProperty("os.version"));
		int osVersion = (int) dOsVersion;

		if ( osName.contains("win") ) {
			return content.replace("\r\n", BR_TAG);
		} else if ( osName.contains("mac") && osVersion < 9 ) {
			return content.replace("\r", BR_TAG);
		} else {
			return content.replace("\n", BR_TAG);
		}
	}

}
