package common.util.crypto.seed;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import common.util.crypto.EncryptResult;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 6. 1. kdk	최초작성
 * </pre>
 *
 * <pre>
 * 권장되는 Bouncy Castle 라이브러리를 사용하여 SEED 암호화를 수행
 *  - 대한민국의 국가 표준 암호화 알고리즘
 *  - AES보다는 느리지만, Triple DES보다는 빠른 성능
 *  - 128비트의 키 길이
 * </pre>
 *
 * @author kdk
 */
public class BouncyCastleSeedUtil {

	private BouncyCastleSeedUtil() {
		super();
	}

	private static final Logger logger = LoggerFactory.getLogger(BouncyCastleSeedUtil.class);

	private static final String UTF_8 = StandardCharsets.UTF_8.toString();

	private static final String KEY_IS_NULL = "key must not be null";

	static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
            logger.debug("Bouncy Castle Provider 등록 완료.");
        }
    }

	/**
	 * 일반적인 CBC, ECB만 정의 (필요 시, 다른 알고리즘 추가 가능)
	 */
	public static class Algorithm {
		private Algorithm() {
			super();
		}

		/** 암호화 하려는 평문의 길이가 16바이트의 배수여야 함 */
		public static final String SEED_CBC_NOPADDING = "SEED/CBC/NoPadding";

		/** 가장 일반적 (권장) */
		public static final String SEED_CBC_PKCS5PADDING = "SEED/CBC/PKCS5Padding";

		/** 권장하지 않음 */
		public static final String SEED_ECB_NOPADDING = "SEED/ECB/NoPadding";

		/** 권장하지 않음 */
		public static final String SEED_ECB_PKCS5PADDING = "SEED/ECB/PKCS5Padding";
	}

	/**
	 * SEED 키 생성 (128비트)
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 */
	public static SecretKey generateSeedKey() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyGenerator keyGen = KeyGenerator.getInstance("SEED", BouncyCastleProvider.PROVIDER_NAME);
        return keyGen.generateKey();
    }

	/**
	 * 키를 Base64 문자열로 변환
	 * @param key
	 * @return
	 */
	public static String convertKeyToString(SecretKey key) {
		Objects.requireNonNull(key, KEY_IS_NULL);

		byte[] keyBytes = key.getEncoded();
		return Base64.getEncoder().encodeToString(keyBytes);
	}

    /**
     * SEED 암호화
     * @param algorithm
     * @param key
     * @param plainText
     * @return
     */
    public static EncryptResult encrypt(String algorithm, SecretKey key, String plainText) {
		if ( StringUtils.isBlank(algorithm) ) {
			throw new IllegalArgumentException("algorithm must not be blank");
		}

		Objects.requireNonNull(key, KEY_IS_NULL);

		if ( StringUtils.isBlank(plainText) ) {
			throw new IllegalArgumentException("plainText must not be blank");
		}

    	String encryptedText = "";
    	String generatedIvString = null;

    	try {
    		Cipher cipher = Cipher.getInstance(algorithm, BouncyCastleProvider.PROVIDER_NAME);

    		if ( algorithm.indexOf("ECB") > -1 ) {
    			cipher.init(Cipher.ENCRYPT_MODE, key);
    		} else {
    			SecureRandom secureRandom = new SecureRandom();
                byte[] ivBytes = new byte[16];
                secureRandom.nextBytes(ivBytes);

    			cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(ivBytes));

    			generatedIvString = Base64.getEncoder().encodeToString(ivBytes);
    		}

    		byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(UTF_8));
			encryptedText = Base64.getEncoder().encodeToString(encryptedBytes);

    	} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException |
                IllegalArgumentException | NoSuchProviderException | UnsupportedEncodingException e) {
        	logger.error("SEED 암호화 중 오류 발생: {}", e.getMessage(), e);
		}

        return new EncryptResult(encryptedText, generatedIvString);
    }

    /**
     * SEED 복호화
     * @param algorithm
     * @param key
     * @param ivStr
     * @param cipherText
     * @return
     */
    public static String decrypt(String algorithm, SecretKey key, String ivStr, String cipherText) {
    	if ( StringUtils.isBlank(algorithm) ) {
			throw new IllegalArgumentException("algorithm must not be blank");
		}

		Objects.requireNonNull(key, KEY_IS_NULL);

		Objects.requireNonNull(cipherText, "cipherText must not be null");

		String decryptedText = "";
		try {
			Cipher cipher = Cipher.getInstance(algorithm, BouncyCastleProvider.PROVIDER_NAME);

			if ( algorithm.indexOf("ECB") > -1 ) {
				cipher.init(Cipher.DECRYPT_MODE, key);
			} else {
				if ( StringUtils.isBlank(ivStr) ) {
					throw new IllegalArgumentException("iv must not be blank");
				}

				byte[] ivBytes = Base64.getDecoder().decode(ivStr);
				cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(ivBytes));
			}

			byte[] decryptedBytes = Base64.getDecoder().decode(cipherText);
			decryptedText = new String(cipher.doFinal(decryptedBytes), UTF_8);

		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException |
                IllegalArgumentException | NoSuchProviderException | UnsupportedEncodingException e) {
			logger.error("SEED 복호화 중 오류 발생: {}", e.getMessage(), e);
		}

		return decryptedText;
    }

}
