package common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

 /**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2026. 1. 13. 김대광	최초작성
 * </pre>
 *
 * <pre>
 * - Base64 인코딩/디코딩
 * - UTL 인코딩/디코딩
 * </pre>
 *
 * @author 김대광
 */
public class EncodeUtil {

	private static final Logger logger = LoggerFactory.getLogger(EncodeUtil.class);

	private static final String UTF_8 = StandardCharsets.UTF_8.toString();

	private static final String CHARSET = "charset";
	private static final String ENCODED_TEXT = "encodedText";

	private EncodeUtil() {
		super();
	}

	private static class ExceptionMessage {

		public static String isNullOrEmpty(String paramName) {
	        return String.format("'%s' is null or empty", paramName);
	    }

	}

	/**
	 * Base64 인코딩
	 * @param text
	 * @return
	 * @since 1.8
	 */
	public static String encodeBase64(String text) {
		if ( StringUtils.isBlank(text) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("text"));
		}

		return Base64.getEncoder().encodeToString(text.getBytes());
	}

	/**
	 * Base64 인코딩
	 * @param text
	 * @param charset
	 * @return
	 * @since 1.8
	 */
	public static String encodeBase64(String text, String charset) {
		if ( StringUtils.isBlank(text) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("text"));
		}

		if ( StringUtils.isBlank(charset) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(CHARSET));
		}

		try {
			return Base64.getEncoder().encodeToString(text.getBytes(charset));
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
			return null;
		}
	}

	/**
	 * Base64 디코딩
	 * @param encodedText
	 * @return
	 * @since 1.8
	 */
	public static String decodeBase64(String encodedText) {
		if ( StringUtils.isBlank(encodedText) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(ENCODED_TEXT));
		}

		byte[] textBytes = Base64.getDecoder().decode(encodedText.getBytes());
		return new String(textBytes);
	}

	/**
	 * Base64 디코딩
	 * @param encodedText
	 * @param charset
	 * @return
	 * @since 1.8
	 */
	public static String decodeBase64(String encodedText, String charset) {
		if ( StringUtils.isBlank(encodedText) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(ENCODED_TEXT));
		}

		if ( StringUtils.isBlank(charset) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(CHARSET));
		}

		try {
			byte[] textBytes = Base64.getDecoder().decode(encodedText.getBytes(charset));
			return new String(textBytes);
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
			return null;
		}
	}

	/**
	 * <pre>
	 * URL 인코딩
	 *  - 현대 웹 환경은 UTF-8이 표준
	 *  - 국내 레거시 공공기관 사이트나 특정 윈도우 기반 시스템은 EUC-KR 또는 CP949 사용하는 경우 있음
	 *
	 * <pre>
	 * commons.codec
	 * {@code
	 * URLCodec urlCodec = new URLCodec();
	 * urlCodec.encode(text)
	 * }
	 * @param text
	 * @return
	 */
	public static String urlEncode(String text) {
		if ( StringUtils.isBlank(text) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("text"));
		}

		try {
			return URLEncoder.encode(text, UTF_8);
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
			return null;
		}
	}

	/**
	 * <pre>
	 * URL 인코딩
	 *  - 현대 웹 환경은 UTF-8이 표준
	 *  - 국내 레거시 공공기관 사이트나 특정 윈도우 기반 시스템은 EUC-KR 또는 CP949 사용하는 경우 있음
	 *
	 * <pre>
	 * commons.codec
	 * {@code
	 * URLCodec urlCodec = new URLCodec();
	 * urlCodec.encode(text, charset)
	 * }
	 * @param text
	 * @param charset
	 * @return
	 */
	public static String urlEncode(String text, String charset) {
		if ( StringUtils.isBlank(text) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("text"));
		}

		if ( StringUtils.isBlank(charset) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(CHARSET));
		}

		try {
			return URLEncoder.encode(text, charset);
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
			return null;
		}
	}

	/**
	 * <pre>
	 * URL 디코딩
	 *  - 현대 웹 환경은 UTF-8이 표준
	 *  - 국내 레거시 공공기관 사이트나 특정 윈도우 기반 시스템은 EUC-KR 또는 CP949 사용하는 경우 있음
	 *
	 * <pre>
	 * commons.codec
	 * {@code
	 * URLCodec urlCodec = new URLCodec();
	 * urlCodec.decode(encodedText)
	 * }
	 * @param encodedText
	 * @return
	 */
	public static String urlDecode(String encodedText) {
		if ( StringUtils.isBlank(encodedText) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(ENCODED_TEXT));
		}

		try {
			return URLDecoder.decode(encodedText, UTF_8);
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
			return null;
		}
	}

	/**
	 * <pre>
	 * URL 디코딩
	 *  - 현대 웹 환경은 UTF-8이 표준
	 *  - 국내 레거시 공공기관 사이트나 특정 윈도우 기반 시스템은 EUC-KR 또는 CP949 사용하는 경우 있음
	 *
	 * <pre>
	 * commons.codec
	 * {@code
	 * URLCodec urlCodec = new URLCodec();
	 * urlCodec.decode(encodedText, charset)
	 * }
	 * @param encodedText
	 * @param charset
	 * @return
	 */
	public static String urlDecode(String encodedText, String charset) {
		if ( StringUtils.isBlank(encodedText) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(ENCODED_TEXT));
		}

		if ( StringUtils.isBlank(charset) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(CHARSET));
		}

		try {
			return URLDecoder.decode(encodedText, charset);
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
			return null;
		}
	}

}
