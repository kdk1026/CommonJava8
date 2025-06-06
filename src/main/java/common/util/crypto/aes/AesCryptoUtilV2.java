package common.util.crypto.aes;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import common.util.ExceptionMessage;
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
	 * @param padding
	 * @return
	 */
	public static EncryptResult encrypt(String plainText, String key, String padding) {
		Objects.requireNonNull(plainText, "plainText must not be null");
		Objects.requireNonNull(key, "key must not be null");
		Objects.requireNonNull(padding, "padding must not be null");

		if ( key.length() != 16 && key.length() != 24 && key.length() != 32 ) {
			throw new IllegalArgumentException(ExceptionMessage.isNull("key"));
		}

		String encryptedText = "";
		String generatedIvString = null;

		try {
			SecretKey secretKey = new SecretKeySpec(key.getBytes(CHARSET), "AES");

			Cipher cipher = Cipher.getInstance(padding);

			if ( padding.indexOf("CBC") > -1 ) {
				SecureRandom secureRandom = new SecureRandom();
                byte[] ivBytes = new byte[16];
                secureRandom.nextBytes(ivBytes);

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
	 * @param iv
	 * @param padding
	 * @return
	 */
	public static String decrypt(String encryptedText, String key, String iv, String padding) {
		Objects.requireNonNull(encryptedText, "encryptedText must not be null");
		Objects.requireNonNull(key, "key must not be null");
		Objects.requireNonNull(iv, "iv must not be null for CBC mode");
		Objects.requireNonNull(padding, "padding must not be null");

		if ( key.length() != 16 && key.length() != 24 && key.length() != 32 ) {
			throw new IllegalArgumentException(ExceptionMessage.isNull("key"));
		}

		String decryptedText = "";

		try {
			SecretKey secretKey = new SecretKeySpec(key.getBytes(CHARSET), "AES");

			Cipher cipher = Cipher.getInstance(padding);

			if ( padding.indexOf("CBC") > -1 ) {
				Objects.requireNonNull(iv, "iv for CBC mode");

				byte[] ivBytes = Base64.getDecoder().decode(iv);

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
