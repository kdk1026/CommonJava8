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

	private static final Logger logger = LoggerFactory.getLogger(EncodeUtil.class);

	/**
	 * Base64 인코딩
	 * @param binaryData
	 * @return
	 * @since 1.8
	 */
	public static String encodeBase64(byte[] binaryData) {
		if ( binaryData == null || binaryData.length == 0 ) {
			throw new IllegalArgumentException("binaryData is null");
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
			throw new IllegalArgumentException("base64Data is null");
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
			throw new IllegalArgumentException("sPlain is null");
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
			throw new IllegalArgumentException("sPlain is null");
		}

		if ( StringUtils.isBlank(sCharsetName) ) {
			throw new IllegalArgumentException("sCharsetName is null");
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
			throw new IllegalArgumentException("sEncodedData is null");
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
			throw new IllegalArgumentException("sEncodedData is null");
		}

		if ( StringUtils.isBlank(sCharsetName) ) {
			throw new IllegalArgumentException("sCharsetName is null");
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
