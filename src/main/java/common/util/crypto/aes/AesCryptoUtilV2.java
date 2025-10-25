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
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 3. 2. kdk	최초작성
 * </pre>
 *
 * <pre>
 * 키의 길이
 *  - 128비트(16자), 192비트(24자), 256비트(32자)
 *
 * CryptoJS와 호환할려면 iv(16자) 필수
 * </pre>
 *
 * @author kdk
 */
public class AesCryptoUtilV2 {

	private static final Logger logger = LoggerFactory.getLogger(AesCryptoUtilV2.class);

	private static final String CHARSET = StandardCharsets.UTF_8.toString();

	/** */
	public static final String AES_CBC_NOPADDING ="AES/CBC/NoPadding";

	/** JavaScript 라이브러인 CryptoJS 와 맞출려면 이것을 사용해야 함 (가장 일반적) */
	public static final String AES_CBC_PKCS5PADDING ="AES/CBC/PKCS5Padding";

	/** */
	public static final String AES_ECB_NOPADDING ="AES/ECB/NoPadding";

	/** */
	public static final String AES_ECB_PKCS5PADDING ="AES/ECB/PKCS5Padding";

	private AesCryptoUtilV2() {
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
	 * @param plainText
	 * @param key
	 * @param iv - null or empty or 16바이트 문자열
	 * @param padding
	 * @return
	 */
	public static EncryptResult encrypt(String plainText, String key, String iv, String padding) {
		if ( StringUtils.isBlank(plainText) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("plainText"));
		}

		if ( StringUtils.isBlank(key) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("key"));
		}

		if ( StringUtils.isBlank(padding) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("padding"));
		}

		if ( key.length() != 16 && key.length() != 24 && key.length() != 32 ) {
			throw new IllegalArgumentException(ExceptionMessage.isNegative("key"));
		}

		String encryptedText = "";
		String generatedIvString = null;

		try {
			SecretKey secretKey = new SecretKeySpec(key.getBytes(CHARSET), "AES");

			Cipher cipher = Cipher.getInstance(padding);

			if ( padding.indexOf("CBC") > -1 ) {
				byte[] ivBytes = null;

    			if ( StringUtils.isBlank(iv) ) {
	    			SecureRandom secureRandom = new SecureRandom();
	    			ivBytes = new byte[16];
	    			secureRandom.nextBytes(ivBytes);
    			} else {
    				ivBytes = iv.getBytes(CHARSET);
    			}

				// CBC의 경우, IvParameterSpec 생략 가능
                IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
				cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

				generatedIvString = Base64.getEncoder().encodeToString(ivBytes);
			} else {
				// ECB의 경우, IvParameterSpec 사용 불가
				cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			}

			byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(CHARSET));
			encryptedText = Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException |
                UnsupportedEncodingException | IllegalArgumentException e) {
        	logger.error("", e);
        }

		return new EncryptResult(encryptedText, generatedIvString);
	}

	/**
	 * AES 복호화
	 * @param encryptedText
	 * @param key
	 * @param iv CBC인 경우 필수
	 * @param isBase64Iv 암호화 시, iv 인자 없이 암호화 한 경우 true
	 * @param padding
	 * @return
	 */
	public static String decrypt(String encryptedText, String key, String iv, boolean isBase64Iv, String padding) {
		if ( StringUtils.isBlank(encryptedText) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("encryptedText"));
		}

		if ( StringUtils.isBlank(key) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("key"));
		}

		if ( StringUtils.isBlank(iv) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("iv"));
		}

		if ( StringUtils.isBlank(padding) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("padding"));
		}

		if ( key.length() != 16 && key.length() != 24 && key.length() != 32 ) {
			throw new IllegalArgumentException(ExceptionMessage.isNegative("key"));
		}

		String decryptedText = "";

		try {
			SecretKey secretKey = new SecretKeySpec(key.getBytes(CHARSET), "AES");

			Cipher cipher = Cipher.getInstance(padding);

			if ( padding.indexOf("CBC") > -1 ) {
				if ( StringUtils.isBlank(iv) ) {
					throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("iv"));
				}

				byte[] ivBytes = null;
				if ( isBase64Iv ) {
					ivBytes = Base64.getDecoder().decode(iv);
				} else {
					ivBytes = iv.getBytes(CHARSET);
				}

				IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
				cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
			} else {
				// ECB의 경우, IvParameterSpec 사용 불가
				cipher.init(Cipher.DECRYPT_MODE, secretKey);
			}

			byte[] decryptedBytes = Base64.getDecoder().decode(encryptedText);
			decryptedText = new String(cipher.doFinal(decryptedBytes), CHARSET);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException |
                UnsupportedEncodingException | IllegalArgumentException e) {
        	logger.error("", e);
        }

		return decryptedText;
	}

}
