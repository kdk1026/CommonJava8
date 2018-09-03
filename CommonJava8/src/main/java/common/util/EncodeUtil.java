package common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncodeUtil {
	
	private EncodeUtil() {
		super();
	}
	
	private static final Logger logger = LoggerFactory.getLogger(EncodeUtil.class);

	/**
	 * <pre>
	 * Base64 인코딩
	 *   - java 8
	 * </pre>
	 * @param binaryData
	 * @return
	 */
	public static String encodeBase64(byte[] binaryData) {
		return Base64.getEncoder().encodeToString(binaryData);
	}

	/**
	 * <pre>
	 * Base64 디코딩
	 *   - java 8
	 * </pre>
	 * @param binaryData
	 * @return
	 */
	public static byte[] decodeBase64(String base64Data) {
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
		String sRes = "";
		try {
			sRes = URLDecoder.decode(sEncodedData, sCharsetName);
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		}
		return sRes;
	}

}
