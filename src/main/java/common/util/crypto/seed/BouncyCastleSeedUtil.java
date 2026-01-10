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
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

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

	private static final Logger logger = LoggerFactory.getLogger(BouncyCastleSeedUtil.class);

	private static final String UTF_8 = StandardCharsets.UTF_8.toString();

	private BouncyCastleSeedUtil() {
		super();
	}

	static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
            logger.debug("Bouncy Castle Provider 등록 완료.");
        }
    }

	private static class ExceptionMessage {

		public static String isNull(String paramName) {
	        return String.format("'%s' is null", paramName);
	    }

		public static String isNullOrEmpty(String paramName) {
	        return String.format("'%s' is null or empty", paramName);
	    }

	}

	/**
	 * 일반적인 CBC, ECB만 정의 (필요 시, 다른 알고리즘 추가 가능)
	 */
	public static class Algorithm {
		private Algorithm() {
			super();
		}

		/** 표준이며 보안성이 우수함 (권장) */
		public static final String SEED_CBC_PKCS5PADDING = "SEED/CBC/PKCS7Padding";

		/** 높은 보안성이 필요할 때 쓰지만 구현이 까다로움 (주의) */
		public static final String AES_GCM_NOPADDING = "SEED/GCM/NoPadding";
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
		Objects.requireNonNull(key, ExceptionMessage.isNull("key"));

		byte[] keyBytes = key.getEncoded();
		return Base64.getEncoder().encodeToString(keyBytes);
	}

	/**
	 * Base64 문자열을 SecretKey로 변환
	 * @param base64KeyString
	 * @param algorithm
	 * @return
	 */
	private static SecretKey convertStringToKey(String base64KeyString) {
		byte[] keyBytes = Base64.getDecoder().decode(base64KeyString);
		return new SecretKeySpec(keyBytes, "SEED");
	}

    /**
     * SEED 암호화
     * @param algorithm
     * @param base64KeyString
     * @param plainText
     * @return
     */
    public static EncryptResult encrypt(String algorithm, String base64KeyString, String plainText) {
		if ( StringUtils.isBlank(algorithm) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("algorithm"));
		}

		if ( StringUtils.isBlank(base64KeyString) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("base64KeyString"));
		}

		if ( StringUtils.isBlank(plainText) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("plainText"));
		}

    	String encryptedText = "";
    	String generatedIvString = null;

    	try {
    		Cipher cipher = Cipher.getInstance(algorithm, BouncyCastleProvider.PROVIDER_NAME);
    		SecretKey key = convertStringToKey(base64KeyString);

    		if ( algorithm.contains("CBC") ) {
    			SecureRandom secureRandom = new SecureRandom();
    			byte[] ivBytes = new byte[16];
    			secureRandom.nextBytes(ivBytes);

    			cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(ivBytes));

    			generatedIvString = Base64.getEncoder().encodeToString(ivBytes);
    		} else if ( algorithm.contains("GCM") ) {
    			SecureRandom secureRandom = new SecureRandom();
    			byte[] ivBytes = new byte[12];
    			secureRandom.nextBytes(ivBytes);

    			GCMParameterSpec spec = new GCMParameterSpec(128, ivBytes);
    			cipher.init(Cipher.ENCRYPT_MODE, key, spec);

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
     * @param base64KeyString
     * @param base64IvString
     * @param cipherText
     * @return
     */
    public static String decrypt(String algorithm, String base64KeyString, String base64IvString, String cipherText) {
		if ( StringUtils.isBlank(algorithm) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("algorithm"));
		}

    	if ( StringUtils.isBlank(base64KeyString) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("base64KeyString"));
		}

      	if ( StringUtils.isBlank(base64IvString) ) {
    		throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("base64IvString"));
    	}

    	if ( StringUtils.isBlank(cipherText) ) {
    		throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("cipherText"));
    	}

		String decryptedText = "";
		try {
			Cipher cipher = Cipher.getInstance(algorithm, BouncyCastleProvider.PROVIDER_NAME);
			SecretKey key = convertStringToKey(base64KeyString);

			if ( algorithm.contains("CBC") ) {
				byte[] ivBytes = Base64.getDecoder().decode(base64IvString);

				cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(ivBytes));
			} else if ( algorithm.contains("GCM") ) {
				byte[] ivBytes = Base64.getDecoder().decode(base64IvString);

				GCMParameterSpec spec = new GCMParameterSpec(128, ivBytes);
				cipher.init(Cipher.DECRYPT_MODE, key, spec);
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
