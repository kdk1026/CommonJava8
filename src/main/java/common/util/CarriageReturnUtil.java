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
		if ( content == null || "".equals(content.trim()) ) {
			return null;
		}

		String newContent = content.replace("\r\n", BR_TAG); // Windows
	    newContent = newContent.replace("\r", BR_TAG);       // 구형 Mac OS
	    newContent = newContent.replace("\n", BR_TAG);       // Unix/Linux/최신 Mac

	    return newContent;
	}

}
