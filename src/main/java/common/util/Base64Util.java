package common.util;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2024. 8. 13. 김대광	최초작성
 * 2025. 5. 18. 김대광	AI가 추천한 Singleton 패턴으로 변경
 * 2025. 5. 27. 김대광	유틸은 Singleton 패턴을 사용하지 않는 것이 좋다는 의견 반영
 * </pre>
 *
 *
 * @author 김대광
 */
public class Base64Util {

	private static final Logger logger = LoggerFactory.getLogger(Base64Util.class);

	private Base64Util() {
		super();
	}

	private static class ExceptionMessage {

		public static String isNullOrEmpty(String paramName) {
	        return String.format("'%s' is null or empty", paramName);
	    }

	}

	public static String encode(String text) {
		if ( StringUtils.isBlank(text) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("text"));
		}

		return Base64.getEncoder().encodeToString(text.getBytes());
	}

	public static String encode(String text, String charset) {
		if ( StringUtils.isBlank(text) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("text"));
		}

		if ( StringUtils.isBlank(charset) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("charset"));
		}

		try {
			return Base64.getEncoder().encodeToString(text.getBytes(charset));
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
			return null;
		}
	}

	public static String decode(String encodedText) {
		if ( StringUtils.isBlank(encodedText) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("encodedText"));
		}

		byte[] textBytes = Base64.getDecoder().decode(encodedText.getBytes());
		return new String(textBytes);
	}

	public static String decode(String encodedText, String charset) {
		if ( StringUtils.isBlank(encodedText) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("encodedText"));
		}

		if ( StringUtils.isBlank(charset) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("charset"));
		}

		try {
			byte[] textBytes = Base64.getDecoder().decode(encodedText.getBytes(charset));
			return new String(textBytes);
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
			return null;
		}
	}

}

