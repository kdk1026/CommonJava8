package common.util.crypto;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 3. 13. kdk	최초작성
 * </pre>
 * <pre>
 * 특별한 경우가 아니면 사용 비권장
 *  - AES 보다 보안에 취약함
 *
 * 키의 길이
 *  - 192비트(24자)
 * </pre>
 * @author kdk
 */
public class TripleDesUtil {

	private TripleDesUtil() {
		super();
	}

	private static final Logger logger = LoggerFactory.getLogger(TripleDesUtil.class);

	private static final String CHARSET = StandardCharsets.UTF_8.toString();

	public static final String DESede_ECB_PKCS5PADDING ="DESede/ECB/PKCS5Padding";

	/**
	 * Triple DES 암호화
	 * @param plainText
	 * @param key
	 * @return
	 */
	public static String encrypt(String plainText, String key) {
		if ( StringUtils.isBlank(plainText) ) {
			throw new IllegalArgumentException("plainText is null");
		}

		if ( StringUtils.isBlank(key) ) {
			throw new IllegalArgumentException("key is null");
		}

		if ( key.length() < 24 ) {
			throw new IllegalArgumentException("key length is less than 24");
		}

		String encryptedText = "";

		try {
			byte[] keyBytes = new byte[24];
			byte[] mkeyBytes = key.getBytes(CHARSET);
			System.arraycopy(mkeyBytes, 0, keyBytes, 0, Math.min(mkeyBytes.length, keyBytes.length));

			SecretKey secretKey = new SecretKeySpec(keyBytes, "DESede");

			Cipher cipher = Cipher.getInstance(DESede_ECB_PKCS5PADDING);

			cipher.init(Cipher.ENCRYPT_MODE, secretKey);

			byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(CHARSET));

			encryptedText = Base64.getEncoder().encodeToString(encryptedBytes);
		} catch (Exception e) {
			logger.error("", e);
		}

		return encryptedText;
	}

	/**
	 * Triple DES 복호화
	 * @param encryptedText
	 * @param key
	 * @return
	 */
	public static String decrypt(String encryptedText, String key) {
		if ( StringUtils.isBlank(encryptedText) ) {
			throw new IllegalArgumentException("encryptedText is null");
		}

		if ( StringUtils.isBlank(key) ) {
			throw new IllegalArgumentException("key is null");
		}

		if ( key.length() < 24 ) {
			throw new IllegalArgumentException("key length is less than 24");
		}

		String decryptedText = "";

		try {
			byte[] keyBytes = new byte[24];
			byte[] mkeyBytes = key.getBytes(CHARSET);
			System.arraycopy(mkeyBytes, 0, keyBytes, 0, Math.min(mkeyBytes.length, keyBytes.length));

			SecretKey secretKey = new SecretKeySpec(keyBytes, "DESede");

			Cipher cipher = Cipher.getInstance(DESede_ECB_PKCS5PADDING);

			cipher.init(Cipher.DECRYPT_MODE, secretKey);

			byte[] decryptedBytes = Base64.getDecoder().decode(encryptedText);
			decryptedText = new String(cipher.doFinal(decryptedBytes), CHARSET);
		} catch (Exception e) {
			logger.error("", e);
		}

		return decryptedText;
	}

}
