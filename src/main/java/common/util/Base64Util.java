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
 * </pre>
 *
 *
 * @author 김대광
 */
public class Base64Util {

	private final Logger logger = LoggerFactory.getLogger(Base64Util.class);

	private Base64Util() {
		super();
	}

	private static class LazyHolder {
		private static final Base64Util INSTANCE = new Base64Util();
	}

	public static Base64Util getInstance() {
		return LazyHolder.INSTANCE;
	}

	public String encode(String text) {
		if ( StringUtils.isBlank(text) ) {
			throw new NullPointerException("text is null");
		}

		return Base64.getEncoder().encodeToString(text.getBytes());
	}

	public String encode(String text, String charset) {
		if ( StringUtils.isBlank(text) ) {
			throw new NullPointerException("text is null");
		}

		if ( StringUtils.isBlank(charset) ) {
			throw new NullPointerException("charset is null");
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
			throw new NullPointerException("encodedText is null");
		}

		byte[] textBytes = Base64.getDecoder().decode(encodedText.getBytes());
		return new String(textBytes);
	}

	public String decode(String encodedText, String charset) {
		if ( StringUtils.isBlank(encodedText) ) {
			throw new NullPointerException("encodedText is null");
		}

		if ( StringUtils.isBlank(charset) ) {
			throw new NullPointerException("charset is null");
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

