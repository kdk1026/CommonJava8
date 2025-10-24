package common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Base64;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncodeUtil {

	private EncodeUtil() {
		super();
	}

	private static class ExceptionMessage {

		public static String isNullOrEmpty(String paramName) {
	        return String.format("'%s' is null or empty", paramName);
	    }

	}

	private static final Logger logger = LoggerFactory.getLogger(EncodeUtil.class);

	/**
	 * Base64 인코딩
	 * @param binaryData
	 * @return
	 * @since 1.8
	 */
	public static String encodeBase64(byte[] binaryData) {
		if ( binaryData == null || binaryData.length == 0 ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("binaryData"));
		}

		return Base64.getEncoder().encodeToString(binaryData);
	}

	/**
	 * Base64 디코딩
	 * @param binaryData
	 * @return
	 * @since 1.8
	 */
	public static byte[] decodeBase64(String base64Data) {
		if ( StringUtils.isBlank(base64Data) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("base64Data"));
		}

		return Base64.getDecoder().decode(base64Data);
	}

	/**
	 * <pre>
	 * URL 인코딩
	 *   - commons.codec
	 *     > URLCodec urlCodec = new URLCodec();
	 *     > urlCodec.encode(plain)
	 * </pre>
	 * @param binaryData
	 * @return
	 */
	public static String urlEncode(String sPlain) {
		if ( StringUtils.isBlank(sPlain) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("sPlain"));
		}

		String sRes = "";
		try {
			sRes = URLEncoder.encode(sPlain, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		}
		return sRes;
	}

	/**
	 * <pre>
	 * URL 인코딩
	 *   - commons.codec
	 *     > URLCodec urlCodec = new URLCodec();
	 *     > urlCodec.encode(plain, charset)
	 * </pre>
	 * @param binaryData
	 * @return
	 */
	public static String urlEncode(String sPlain, String sCharsetName) {
		if ( StringUtils.isBlank(sPlain) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("sPlain"));
		}

		if ( StringUtils.isBlank(sCharsetName) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("sCharsetName"));
		}

		String sRes = "";
		try {
			sRes = URLEncoder.encode(sPlain, sCharsetName);
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		}
		return sRes;
	}

	/**
	 * <pre>
	 * URL 디코딩
	 *   - commons.codec
	 *     > URLCodec urlCodec = new URLCodec();
	 *     > urlCodec.decode(plain)
	 * </pre>
	 * @param binaryData
	 * @return
	 */
	public static String urlDecode(String sEncodedData) {
		if ( StringUtils.isBlank(sEncodedData) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("sEncodedData"));
		}

		String sRes = "";
		try {
			sRes = URLDecoder.decode(sEncodedData, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		}
		return sRes;
	}

	/**
	 * <pre>
	 * URL 디코딩
	 *   - commons.codec
	 *     > URLCodec urlCodec = new URLCodec();
	 *     > urlCodec.decode(plain, charset)
	 * </pre>
	 * @param binaryData
	 * @return
	 */
	public static String urlDecode(String sEncodedData, String sCharsetName) {
		if ( StringUtils.isBlank(sEncodedData) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("sEncodedData"));
		}

		if ( StringUtils.isBlank(sCharsetName) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("sCharsetName"));
		}

		String sRes = "";
		try {
			sRes = URLDecoder.decode(sEncodedData, sCharsetName);
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		}
		return sRes;
	}

}
