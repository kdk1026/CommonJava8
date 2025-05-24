package common.util.crypto.aes;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import common.util.ExceptionMessage;

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

	private AesCryptoUtilV2() {
		super();
	}

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

	/**
	 * AES 암호화
	 * @param plainText
	 * @param key
	 * @param iv
	 * @param padding
	 * @return
	 */
	public static String encrypt(String plainText, String key, String iv, String padding) {
		if ( StringUtils.isBlank(plainText) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNull("plainText"));
		}

		if ( StringUtils.isBlank(key) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNull("key"));
		}

		if ( key.length() != 16 && key.length() != 24 && key.length() != 32 ) {
			throw new IllegalArgumentException(ExceptionMessage.isNull("key"));
		}

		if ( StringUtils.isBlank(padding) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNull("padding"));
		}

		String encryptedText = "";

		try {
			SecretKey secretKey = new SecretKeySpec(key.getBytes(CHARSET), "AES");

			Cipher cipher = Cipher.getInstance(padding);

			if ( padding.indexOf("CBC") > -1 ) {
				// CBC의 경우, IvParameterSpec 생략 가능
				if ( iv != null ) {
					IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(CHARSET));
					cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
				} else {
					cipher.init(Cipher.ENCRYPT_MODE, secretKey);
				}
			} else {
				// ECB의 경우, IvParameterSpec 사용 불가
				cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			}

			byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(CHARSET));
			encryptedText = Base64.getEncoder().encodeToString(encryptedBytes);
		} catch (Exception e) {
			logger.error("", e);
		}

		return encryptedText;
	}

	/**
	 * AES 복호화
	 * @param encryptedText
	 * @param key
	 * @param iv
	 * @param padding
	 * @return
	 */
	public static String decrypt(String encryptedText, String key, String iv, String padding) {
		if ( StringUtils.isBlank(encryptedText) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNull("encryptedText"));
		}

		if ( StringUtils.isBlank(key) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNull("key"));
		}

		if ( key.length() != 16 && key.length() != 24 && key.length() != 32 ) {
			throw new IllegalArgumentException(ExceptionMessage.isNull("key"));
		}

		if ( StringUtils.isBlank(padding) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNull("padding"));
		}

		String decryptedText = "";

		try {
			SecretKey secretKey = new SecretKeySpec(key.getBytes(CHARSET), "AES");

			Cipher cipher = Cipher.getInstance(padding);

			if ( padding.indexOf("CBC") > -1 ) {
				// CBC의 경우, IvParameterSpec 생략 가능
				if ( iv != null ) {
					IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(CHARSET));
					cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
				} else {
					cipher.init(Cipher.DECRYPT_MODE, secretKey);
				}

			} else {
				// ECB의 경우, IvParameterSpec 사용 불가
				cipher.init(Cipher.DECRYPT_MODE, secretKey);
			}

			byte[] decryptedBytes = Base64.getDecoder().decode(encryptedText);
			decryptedText = new String(cipher.doFinal(decryptedBytes), CHARSET);
		} catch (Exception e) {
			logger.error("", e);
		}

		return decryptedText;
	}

}
