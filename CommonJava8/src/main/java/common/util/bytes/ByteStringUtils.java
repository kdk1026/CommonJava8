package common.util.bytes;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ByteStringUtils {
	
	private ByteStringUtils() {
		super();
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ByteStringUtils.class);

	/**
	 * @since 1.7
	 */
	public static final String UTF_8 = StandardCharsets.UTF_8.toString();
	public static final String EUC_KR = Charset.forName("EUC-KR").toString();
	
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
	public static boolean isByteOver(String str, byte maxByte) {
		boolean resFlag = false;
		
		try {
			byte strByte = (byte) str.getBytes(UTF_8).length;
			resFlag = strByte > maxByte;
			
		} catch (Exception e) {
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
	public static boolean isByteOver(String str, byte maxByte, String charsetName) {
		boolean resFlag = false;
		
		try {
			byte strByte = (byte) str.getBytes(charsetName).length;
			resFlag = strByte > maxByte;
			
		} catch (Exception e) {
			logger.error("", e);
		}
		
		return resFlag;
	}
	
	/**
	 * 해당 케릭터셋으로 변환
	 * @param bData
	 * @param charsetName
	 * @return
	 */
	public static byte[] toByteEncoding(byte[] bData, String charsetName) {
		String sData = "";
		byte[] bResData = null;
		
		try {
			sData = new String(bData, charsetName);
			bResData = sData.getBytes(charsetName); 
			
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		}
		
		return bResData;
	}

	/**
	 * byte 단위로 문자열 자르기
	 * @param sData
	 * @param nStartByte
	 * @param nCutByte
	 * @param isUtf8
	 * @return
	 */
	public static String getByteStrSubstring(String sData, int nStartByte, int nCutByte, boolean isUtf8) {
		int nBeginIndex = -1;
		int nEndIndex = -1;
		int nChkByte = 0;
		int nEndByte = nStartByte + nCutByte;
		
		for (int i = 0; i < sData.length(); i++) {
			String ch = Character.toString(sData.charAt(i+1));
			
			if ( isUtf8 ) {
				nChkByte += ch.getBytes().length >= 2 ? 3 : 1;
			} else {
				nChkByte += ch.getBytes().length >= 2 ? 2 : 1;
			}
			
			if ( nBeginIndex == -1 && nChkByte >= nStartByte ) {
				nBeginIndex = i;
			}
			
			if ( nEndIndex == -1 && nChkByte >= nEndByte ) {
				nEndIndex = (nChkByte > nEndByte) ? i : i+2;
				break;
			}
		}

		return sData.substring(nBeginIndex, nEndIndex);
	}
	
}
