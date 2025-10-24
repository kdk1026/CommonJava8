package common.util.bytes;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 8. 14. 김대광	SonarLint 지시에 따른 수정 및 주저리 (Complexity 어쩔 수 없다)
 * </pre>
 *
 *
 * @author 김대광
 */
public class ByteStringUtils {

	private ByteStringUtils() {
		super();
	}

	private static class ExceptionMessage {

		public static String isNullOrEmpty(String paramName) {
	        return String.format("'%s' is null or empty", paramName);
	    }

		public static String isNegative(String paramName) {
			return String.format("'%s' is negative", paramName);
		}

	}

	private static final Logger logger = LoggerFactory.getLogger(ByteStringUtils.class);

	/**
	 * @since 1.7
	 */
	public static final String UTF_8 = StandardCharsets.UTF_8.toString();
	public static final String EUC_KR = Charset.forName("EUC-KR").toString();

	private static final String ORE_DATA = "bOrgData";
	private static final String CHARSET_NAME = "charsetName";

	/**
	 * <pre>
	 * 문자열의 Bytes 길이 구하기
	 * </pre>
	 * @param str
	 * @param charsetName
	 * @return
	 */
	public static int getByteLength(String str) {
		return str.getBytes().length;
	}

	/**
	 * <pre>
	 * 문자열의 Bytes 길이 구하기
	 *   - 해당 CharacterSet 인코딩
	 * </pre>
	 * @param str
	 * @param charsetName
	 * @return
	 */
	public static int getByteLength(String str, String charsetName) {
		if ( StringUtils.isBlank(str) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
		}

		if ( StringUtils.isBlank(charsetName) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(CHARSET_NAME));
		}

		int nLen = 0;

		try {
			nLen = str.getBytes(charsetName).length;

		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		}

		return nLen;
	}

	/**
	 * 문자열 바이트 최대 준수 여부
	 * @param str
	 * @param maxByte
	 * @return
	 */
	public static boolean isByteOver(String str, int maxByte) {
		if ( StringUtils.isBlank(str) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
		}

		if ( maxByte < 0 ) {
			throw new IllegalArgumentException(ExceptionMessage.isNegative("maxByte"));
		}

		boolean resFlag = false;

		try {
			byte strByte = (byte) str.getBytes(UTF_8).length;
			resFlag = strByte > maxByte;

		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		}

		return resFlag;
	}

	/**
	 * 문자열 바이트 (해당 케릭터셋) 최대 준수 여부
	 * @param str
	 * @param maxByte
	 * @param charsetName
	 * @return
	 */
	public static boolean isByteOver(String str, int maxByte, String charsetName) {
		if ( StringUtils.isBlank(str) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
		}

		if ( maxByte < 0 ) {
			throw new IllegalArgumentException(ExceptionMessage.isNegative("maxByte"));
		}

		if ( StringUtils.isBlank(charsetName) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(CHARSET_NAME));
		}

		boolean resFlag = false;

		try {
			byte strByte = (byte) str.getBytes(charsetName).length;
			resFlag = strByte > maxByte;

		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		}

		return resFlag;
	}

	/**
	 * euc-kr 바이트 어레이를 utf-8 문자열로 변환
	 * @param bOrgData
	 * @return
	 */
	public static String eucKrToUtf8String(byte[] bOrgData) {
		if ( bOrgData == null || bOrgData.length == 0 ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(ORE_DATA));
		}

		String sRes = "";

		try {
			String str = new String(bOrgData, EUC_KR);
			byte[] bData = str.getBytes(UTF_8);
			sRes = new String(bData, UTF_8);

		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		}

		return sRes;
	}

	/**
	 * utf-8 바이트 어레이를 euc-kr 문자열로 변환
	 * @param bOrgData
	 * @return
	 */
	public static String utf8ToEucKrString(byte[] bOrgData) {
		if ( bOrgData == null || bOrgData.length == 0 ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(ORE_DATA));
		}

		String sRes = "";

		try {
			String str = new String(bOrgData, UTF_8);
			byte[] bData = str.getBytes(EUC_KR);
			sRes = new String(bData, EUC_KR);

		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		}

		return sRes;
	}

	/**
	 * euc-kr 바이트 어레이를 utf-8 바이트 어레이로 변환
	 * @param bOrgData
	 * @return
	 */
	public static byte[] eucKrToUtf8(byte[] bOrgData) {
		if ( bOrgData == null || bOrgData.length == 0 ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(ORE_DATA));
		}

		byte[] bData = null;

		try {
			String str = new String(bOrgData, EUC_KR);
			bData = str.getBytes(UTF_8);

		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		}

		return bData;
	}

	/**
	 * utf-8 바이트 어레이를 euc-kr 바이트 어에이로 변환
	 * @param bOrgData
	 * @return
	 */
	public static byte[] utf8ToEucKr(byte[] bOrgData) {
		if ( bOrgData == null || bOrgData.length == 0 ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(ORE_DATA));
		}

		byte[] bData = null;

		try {
			String str = new String(bOrgData, UTF_8);
			bData = str.getBytes(EUC_KR);

		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		}

		return bData;
	}

	/**
	 * byte 단위로 문자열 자르기
	 * @param str
	 * @param offset
	 * @param length
	 * @param charsetName
	 * @return
	 */
	public static String substrString(String str, int offset, int length, String charsetName) {
		if ( StringUtils.isBlank(str) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
		}

		if ( offset < 0 ) {
			throw new IllegalArgumentException(ExceptionMessage.isNegative("offset"));
		}

		if ( length < 0 ) {
			throw new IllegalArgumentException(ExceptionMessage.isNegative("length"));
		}

		if ( StringUtils.isBlank(charsetName) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(CHARSET_NAME));
		}

		String sRes = "";

		try {
			byte[] bytes = str.getBytes(charsetName);

			byte[] value = new byte[length];

			if (bytes.length < offset + length) {
				return "";
			}

			for(int i = 0; i < length; i++){
				value[i] = bytes[offset + i];
			}

			sRes = new String(value, charsetName).trim();

		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		}

		return sRes;
	}

}
