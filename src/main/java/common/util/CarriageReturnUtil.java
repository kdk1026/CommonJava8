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

	private static final String BR_TAG = "<br />";

	private CarriageReturnUtil() {
		super();
	}

	/**
	 * 캐리지 리턴 문자열을 줄바꿈 태그로 변환
	 *
	 * <pre>
	 * - \r\n = Windows
	 * - \r = 구형 Mac OS (9 이하)
	 * - \n = Unix/Linux/최신 Mac
	 * </pre>
	 * @param content
	 * @return
	 */
	public static String changeBrTag(String content) {
		if ( content == null || content.trim().isEmpty() ) {
			return null;
		}

	    return content.replace("\r\n", BR_TAG).replace("\r", BR_TAG).replace("\n", BR_TAG);
	}

}
