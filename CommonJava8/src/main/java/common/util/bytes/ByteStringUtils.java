package common.util.bytes;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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
	 * <pre>
	 * 문자열의 해당 위치까지 문자의 영/한 여부 체크
	 *   - 0:영문, 1:한글 첫번째 Byte, 2:한글 두번째 Byte
	 * </pre>
	 * @param sData
	 * @param nChkPos
	 * @return
	 */
	public static int checkByteEucKr(String sData, int nChkPos) {
		int nChkByte = 0;
		byte[] bByte = null;
		int nByteLen = 0;
		
		try {
			bByte = sData.getBytes(EUC_KR);
			nByteLen = bByte.length;
			
			int i;
			for (i=0; i < nChkPos; i++) {
				if(nChkPos > nByteLen) {
					break;
				}
				
				char c = (char) bByte[i];
				if (c > 127) {
					nChkByte = 1;
					i++;
				} else {
					nChkByte = 0;
				}
			}
			
			if ( nChkByte == 1 && (i-1) == (nChkPos-1) ) {
				nChkByte = 2;
			}
			
		} catch (Exception e) {
			logger.error("", e);
		}
		
		return nChkByte;
	}
	
	/**
	 * byte 단위로 문자열 자르기
	 *   - subStrByteBuffer 보다 다소 성능 ↓ 
	 * </pre>
	 * @param sData
	 * @param nStart
	 * @param nLen
	 * @return
	 */
	public static String subStrByteEucKr(String sData, int nStart, int nLen) {
		byte[] bByte = null;
		int nByteLen = 0;
		int nChkByte = 0;
		int nStartIdx = 0;
		int nEndIdx = 0;
		String sRes = "";
		
		try {
			bByte = sData.getBytes(EUC_KR);
			nByteLen = bByte.length;
			
			nEndIdx = nStart + nLen;
			if (nEndIdx >= nByteLen || nLen == 0) {
				nEndIdx = nByteLen;
			}
			
			nChkByte = checkByteEucKr(sData, nStart);
			if (nChkByte == 2) {
				nStartIdx = nStartIdx + 1;
			}
			
			nChkByte = checkByteEucKr(sData, nEndIdx);
			if (nChkByte == 1) {
				nEndIdx = nEndIdx - 1;
			}
			
			sRes = new String(bByte, nStartIdx, nEndIdx, EUC_KR);
			
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		}
		
		return sRes;
	}
	
	/**
	 * <pre>
	 * byte 단위로 문자열 자르기
	 *   - subStrByte 보다 다소 성능 ↑
	 * </pre>
	 * @param sData
	 * @param nStart
	 * @param nLen
	 * @return
	 */
	public static String subStrByteBufferEucKr(String sData, int nStart, int nLen) {
		final String sEncoding = EUC_KR;
		
		int nByteLen = 0;
		int nChkByte = 0;
		int nStartIdx = 0;
		int nEndIdx = 0;
		String sRes = "";
		
		try {
			Charset charset =  Charset.forName(sEncoding);
			CharsetEncoder encoder = charset.newEncoder();
			ByteBuffer bBuf = null;
			CharBuffer cBuf = null;
			
			bBuf = encoder.encode(CharBuffer.wrap(sData));
			
			nByteLen = bBuf.limit();
			
			nEndIdx = nStart + nLen;
			if (nEndIdx >= nByteLen || nLen == 0) {
				nEndIdx = nByteLen;
			}
			
			nChkByte = checkByteEucKr(sData, nStart);
			if (nChkByte == 2) {
				nStartIdx = nStartIdx + 1;
			}
			
			nChkByte = checkByteEucKr(sData, nEndIdx);
			if (nChkByte == 1) {
				nEndIdx = nEndIdx - 1;
			}
			
			bBuf.position(nStartIdx);
			bBuf.limit(nEndIdx);
			cBuf = charset.decode(bBuf);
			
			sRes = cBuf.toString();
			
		} catch (Exception e) {
			logger.error("", e);
		}
		
		return sRes;
	}
	
	/**
	 * byte 단위로 문자열 잘라서 List로 반환
	 * @param sData
	 * @param nLimit
	 * @param charsetName
	 * @since 1.7
	 * @return
	 */
	public static List<String> subStrByteBuffer(String sData, int nLimit, String charsetName) {
		List<String> resList = new ArrayList<>();
		String str = "";
		ByteBuffer buf = null;
		
		try {
			buf = ByteBuffer.wrap(sData.getBytes(charsetName));
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		}
		
		if (buf != null) {
			int nLen = buf.limit();
			int nPos = 0;
			byte[] bResData = null;
			
			while (nPos < nLen) {
				buf.position(nPos);
				buf.limit(buf.position() + nLimit);
				bResData = new byte[buf.remaining()];
				buf.get(bResData);
				
				try {
					str = new String(bResData, charsetName).trim();
				} catch (UnsupportedEncodingException e) {
					logger.error("", e);
				}
				resList.add(str);
				
				nPos = nPos + nLimit;
			}
		}
		
		return resList;
	}
	
}
