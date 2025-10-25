package common.util.crypto.aes;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import common.util.crypto.EncryptResult;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 8. 13. 김대광	SonarLint 지시에 따른 수정 (SecureRandom 추가로... 이 버전에 맞춰놓은 Node.js 유틸도 수정해야 돼... 아... 귀찮아 죽겠다..)
 * 2021. 8. 15. 김대광	SecureRandom 관련 메소드 추가해서 주저리 주저리 하고, 암/복호화 시에는 걷어냄
 * </pre>
 *
 * <pre>
 * 블록암호 알고리즘 기능 중 대중적인 AES 제공
 * - 복호화가 가능한 양방향 암호화
 *   (예: 주민등록번호, 신용카드번호, 계좌번호, 여권번호, 운전면허번호 등)
 * - 일정한 블록 크기로 나누어,
 *   각 블록을 송·수신자간에 공유한 비밀키를 사용하여 암호화하는 방식
 *
 * - 암호화 키
 *   > 128비트(16자), 192비트(24자), 256비트(32자)
 *   > 192비트, 256비트 사용 조건 - Java 8부터는 Default로 지원하는 듯
 *     1. Java Cryptography Extension (JCE) 다운로드
 *     2. local_policy.jar, US_export_policy.jar 파일을 다음 경로에 덮어쓰기
 *        → $JAVA_HOME/jre/lib/security
 * - 패딩
 *   > NoPadding 인 경우, 평문 길이가 암호화 키와 동일해야 함
 *
 * - Base64
 * 	 > java 8
 * </pre>
 *
 * @author 김대광
 */
public class AesCryptoUtil {

	private static final Logger logger = LoggerFactory.getLogger(AesCryptoUtil.class);

	/**
	 * @since 1.7
	 */
	private static final String CHARSET = StandardCharsets.UTF_8.toString();

	public static final String AES_CBC_NOPADDING ="AES/CBC/NoPadding";
	public static final String AES_CBC_PKCS5PADDING ="AES/CBC/PKCS5Padding";
	public static final String AES_ECB_NOPADDING ="AES/ECB/NoPadding";
	public static final String AES_ECB_PKCS5PADDING ="AES/ECB/PKCS5Padding";

	private AesCryptoUtil() {
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

	/**
	 * AES 암호화
	 * @param sKey
	 * @param sIv - null or empty or 16바이트 문자열
	 * @param sPadding
	 * @param strPlainText
	 * @return
	 */
	public static EncryptResult aesEncrypt(String sKey, String sIv, String sPadding, String strPlainText) {
		if ( StringUtils.isBlank(sKey) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("sKey"));
		}

		if ( StringUtils.isBlank(sPadding) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("sPadding"));
		}

		if ( StringUtils.isBlank(strPlainText) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("strPlainText"));
		}

		if ( sKey.length() != 16 && sKey.length() != 24 && sKey.length() != 32 ) {
			throw new IllegalArgumentException(ExceptionMessage.isNegative("key"));
		}

		String strEncryptText = "";
		String generatedIvString = null;

		try {
			SecretKey secretKey = new SecretKeySpec(sKey.getBytes(CHARSET), "AES");

			String padding = sPadding;
			Cipher cipher = Cipher.getInstance(padding);

			if ( padding.indexOf("CBC") > -1 ) {
    			byte[] ivBytes = null;

    			if ( StringUtils.isBlank(sIv) ) {
	    			SecureRandom secureRandom = new SecureRandom();
	    			ivBytes = new byte[16];
	    			secureRandom.nextBytes(ivBytes);
    			} else {
    				ivBytes = sIv.getBytes(CHARSET);
    			}

				// CBC의 경우, IvParameterSpec 생략 가능
				cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(ivBytes));

				generatedIvString = Base64.getEncoder().encodeToString(ivBytes);
			} else {
				// ECB의 경우, IvParameterSpec 사용 불가
				cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			}

			byte[] textBytes = cipher.doFinal(strPlainText.getBytes(CHARSET));
			strEncryptText = Base64.getEncoder().encodeToString(textBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException |
                UnsupportedEncodingException | IllegalArgumentException e) {
        	logger.error("", e);
        }

		return new EncryptResult(strEncryptText, generatedIvString);
	}

	/**
	 * AES 복호화
	 * @param sKey
	 * @param sIv CBC인 경우 필수
	 * @param isBase64Iv 암호화 시, iv 인자 없이 암호화 한 경우 true
	 * @param sPadding
	 * @param strEncryptText
	 * @return
	 */
	public static String aesDecrypt(String sKey, String sIv, boolean isBase64Iv, String sPadding, String strEncryptText) {
		if ( StringUtils.isBlank(sKey) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("sKey"));
		}

		if ( StringUtils.isBlank(sPadding) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("sPadding"));
		}

		if ( StringUtils.isBlank(strEncryptText) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("strEncryptText"));
		}

		if ( sKey.length() != 16 && sKey.length() != 24 && sKey.length() != 32 ) {
			throw new IllegalArgumentException(ExceptionMessage.isNegative("key"));
		}

		String strDecryptText = "";

		try {
			SecretKey secretKey = new SecretKeySpec(sKey.getBytes(CHARSET), "AES");

			String padding = sPadding;
			Cipher cipher = Cipher.getInstance(padding);

			if ( padding.indexOf("CBC") > -1 ) {
				if ( StringUtils.isBlank(sIv) ) {
					throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("sIv"));
				}

				byte[] ivBytes = null;
				if ( isBase64Iv ) {
					ivBytes = Base64.getDecoder().decode(sIv);
				} else {
					ivBytes = sIv.getBytes(CHARSET);
				}

				// CBC의 경우, IvParameterSpec 생략 가능
				cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(ivBytes));
			} else {
				// ECB의 경우, IvParameterSpec 사용 불가
				cipher.init(Cipher.DECRYPT_MODE, secretKey);
			}

			byte[] textBytes = Base64.getDecoder().decode(strEncryptText);
			strDecryptText = new String(cipher.doFinal(textBytes), CHARSET);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException |
                UnsupportedEncodingException | IllegalArgumentException e) {
        	logger.error("", e);
        }
		return strDecryptText;
	}

}
