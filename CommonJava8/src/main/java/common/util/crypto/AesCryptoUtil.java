package common.util.crypto;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * 블록암호 알고리즘 기능 중 대중적인 AES 제공
 * - 복호화가 가능한 양방향 암호화
 *   (예: 주민등록번호, 신용카드번호, 계좌번호, 여권번호, 운전면허번호 등)
 * - 일정한 블록 크기로 나누어, 
 *   각 블록을 송·수신자간에 공유한 비밀키를 사용하여 암호화하는 방식
 *   
 * - 암호화 키
 *   > 128비트(16자), 192비트(24자), 256비트(32자)
 *   > 192비트, 256비트 사용 조건
 *     1. Java Cryptography Extension (JCE) 다운로드
 *     2. local_policy.jar, US_export_policy.jar 파일을 다음 경로에 덮어쓰기
 *        → $JAVA_HOME/jre/lib/security
 * - 패딩
 *   > NoPadding 인 경우, 평문 길이가 암호화 키와 동일해야 함
 *   
 * - Base64
 * 	 > java 8
 * </pre>
 */
public class AesCryptoUtil {
	
	private AesCryptoUtil() {
		super();
	}

	private static final Logger logger = LoggerFactory.getLogger(AesCryptoUtil.class);
	
	private static final byte[] IV_BYTES = new byte[16];
	/**
	 * @since 1.7
	 */
	private static final String CHARSET = StandardCharsets.UTF_8.toString();
	
	public static final String AES_CBC_NOPADDING ="AES/CBC/NoPadding";
	public static final String AES_CBC_PKCS5PADDING ="AES/CBC/PKCS5Padding";
	public static final String AES_ECB_NOPADDING ="AES/ECB/NoPadding";
	public static final String AES_ECB_PKCS5PADDING ="AES/ECB/PKCS5Padding";
	
	public static String aesEncrypt(String sKey, String sPadding, String strPlainText) {
		String strEncryptText = "";
		try {
			SecretKey secretKey = new SecretKeySpec(sKey.getBytes(CHARSET), "AES");
			
			String padding = sPadding;
			Cipher cipher = Cipher.getInstance(padding);

			if ( padding.indexOf("CBC") > -1 ) {
				// CBC의 경우, IvParameterSpec 생략 가능
				cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(IV_BYTES));
				
			} else {
				// ECB의 경우, IvParameterSpec 사용 불가
				cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			}
			
			byte[] textBytes = cipher.doFinal(strPlainText.getBytes(CHARSET));
			strEncryptText = Base64.getEncoder().encodeToString(textBytes);
		} catch (Exception e) {
			logger.error("", e);
		}
		return strEncryptText;
	}

	public static String aesDecrypt(String sKey, String sPadding, String strEncryptText) {
		String strDecryptText = "";
		try {
			SecretKey secretKey = new SecretKeySpec(sKey.getBytes(CHARSET), "AES");
			
			String padding = sPadding;
			Cipher cipher = Cipher.getInstance(padding);

			if ( padding.indexOf("CBC") > -1 ) {
				// CBC의 경우, IvParameterSpec 생략 가능
				cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(IV_BYTES));
				
			} else {
				// ECB의 경우, IvParameterSpec 사용 불가
				cipher.init(Cipher.DECRYPT_MODE, secretKey);
			}			

			byte[] textBytes = Base64.getDecoder().decode(strEncryptText);
			strDecryptText = new String(cipher.doFinal(textBytes), CHARSET);
		} catch (Exception e) {
			logger.error("", e);
		}
		return strDecryptText;
	}
}
