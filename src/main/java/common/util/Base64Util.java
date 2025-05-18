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
 * </pre>
 *
 *
 * @author 김대광
 */
public class Base64Util {

	private final Logger logger = LoggerFactory.getLogger(Base64Util.class);

	private static Base64Util instance;

	private Base64Util() {
		super();
	}

	public static synchronized Base64Util getInstance() {
		if (instance == null) {
			instance = new Base64Util();
		}

		return instance;
	}

	public String encode(String text) {
		if ( StringUtils.isBlank(text) ) {
			throw new IllegalArgumentException("text is null");
		}

		return Base64.getEncoder().encodeToString(text.getBytes());
	}

	public String encode(String text, String charset) {
		if ( StringUtils.isBlank(text) ) {
			throw new IllegalArgumentException("text is null");
		}

		if ( StringUtils.isBlank(charset) ) {
			throw new IllegalArgumentException("charset is null");
		}

		try {
			return Base64.getEncoder().encodeToString(text.getBytes(charset));
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
			return null;
		}
	}

	public String decode(String encodedText) {
		if ( StringUtils.isBlank(encodedText) ) {
			throw new IllegalArgumentException("encodedText is null");
		}

		byte[] textBytes = Base64.getDecoder().decode(encodedText.getBytes());
		return new String(textBytes);
	}

	public String decode(String encodedText, String charset) {
		if ( StringUtils.isBlank(encodedText) ) {
			throw new IllegalArgumentException("encodedText is null");
		}

		if ( StringUtils.isBlank(charset) ) {
			throw new IllegalArgumentException("charset is null");
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

