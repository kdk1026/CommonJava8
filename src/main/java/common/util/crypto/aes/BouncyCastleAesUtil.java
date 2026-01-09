package common.util.crypto.aes;

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
 * 권장되는 Bouncy Castle 라이브러리를 사용하여 AES 암호화를 수행
 *  - 가장 강력하게 권장되는 암호화 알고리즘
 *  - 128비트(16바이트)의 블록 크기를 사용
 *  - 전 세계적으로 가장 널리 사용되는 대칭 키 암호화 표준
 *  - 128비트, 192비트, 256비트의 키 길이
 * </pre>
 *
 * @author kdk
 */
public class BouncyCastleAesUtil {

	private static final Logger logger = LoggerFactory.getLogger(BouncyCastleAesUtil.class);

	private static final String UTF_8 = StandardCharsets.UTF_8.toString();

	private BouncyCastleAesUtil() {
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
	 * 레거시 시스템 위한 CBC/PKCS5Padding와 GCM만
	 */
	public static class Algorithm {
		private Algorithm() {
			super();
		}

		/** 과거 권장, 비권장 : JavaScript 라이브러인 CryptoJS 와 맞출려면 이것을 사용해야 함 */
		public static final String AES_CBC_PKCS5PADDING = "AES/CBC/PKCS5Padding";

		/** 강력 권장 : JavaScript는 내장 보안 API인 Web Crypto API 이용하여 구현 */
		public static final String AES_GCM_NOPADDING = "AES/GCM/NoPadding";
	}

	/**
	 * AES 키 생성 (128, 192, 256 비트 가능)
	 * @param keySize
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 */
	public static SecretKey generateAesKey(int keySize) throws NoSuchAlgorithmException, NoSuchProviderException {
		Objects.requireNonNull(keySize, ExceptionMessage.isNull("keySize"));

		if ( keySize != 128 && keySize != 192 && keySize != 256 ) {
			throw new IllegalArgumentException("keySize must be 128, 192, or 256 bits");
		}

        KeyGenerator keyGen = KeyGenerator.getInstance("AES", BouncyCastleProvider.PROVIDER_NAME);
        keyGen.init(keySize);
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
		return new javax.crypto.spec.SecretKeySpec(keyBytes, "AES");
	}

    /**
     * AES 암호화
     * @param algorithm
     * @param base64KeyString
     * @param ivStr - null | empty | CBC : 16바이트 문자열 | GCM : 12바이트 문자열
     * @param plainText
     * @return
     */
    public static EncryptResult encrypt(String algorithm, String base64KeyString, String ivStr, String plainText) {
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

    		if ( algorithm.contains("CBC") ){
    			byte[] ivBytes = null;

    			if ( StringUtils.isBlank(ivStr) ) {
	    			SecureRandom secureRandom = new SecureRandom();
	    			ivBytes = new byte[16];
	    			secureRandom.nextBytes(ivBytes);
    			} else {
    				ivBytes = ivStr.getBytes(UTF_8);
    			}

    			cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(ivBytes));

    			generatedIvString = Base64.getEncoder().encodeToString(ivBytes);
    		} else if ( algorithm.contains("GCM") ) {
    			byte[] ivBytes = null;

    			if ( StringUtils.isBlank(ivStr) ) {
	    			SecureRandom secureRandom = new SecureRandom();
	    			ivBytes = new byte[12];
	    			secureRandom.nextBytes(ivBytes);
    			} else {
    				ivBytes = ivStr.getBytes(UTF_8);
    			}

    			GCMParameterSpec spec = new GCMParameterSpec(128, ivBytes);
    			cipher.init(Cipher.ENCRYPT_MODE, key, spec);
    		}

    		byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(UTF_8));
			encryptedText = Base64.getEncoder().encodeToString(encryptedBytes);

    	} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException |
                IllegalArgumentException | NoSuchProviderException | UnsupportedEncodingException e) {
        	logger.error("AES 암호화 중 오류 발생: {}", e.getMessage(), e);
		}

        return new EncryptResult(encryptedText, generatedIvString);
    }

    /**
     * AES 복호화
     * @param algorithm
     * @param base64KeyString
     * @param ivStr
     * @param isBase64Iv 암호화 시, iv 인자 없이 암호화 한 경우 true
     * @param cipherText
     * @return
     */
    public static String decrypt(String algorithm, String base64KeyString, String ivStr, boolean isBase64Iv, String cipherText) {
		if ( StringUtils.isBlank(algorithm) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("algorithm"));
		}

    	if ( StringUtils.isBlank(base64KeyString) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("base64KeyString"));
		}

    	if ( StringUtils.isBlank(ivStr) ) {
    		throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("ivStr"));
    	}

    	if ( StringUtils.isBlank(cipherText) ) {
    		throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("cipherText"));
    	}

		String decryptedText = "";
		try {
			Cipher cipher = Cipher.getInstance(algorithm, BouncyCastleProvider.PROVIDER_NAME);
			SecretKey key = convertStringToKey(base64KeyString);

			if ( algorithm.contains("CBC") ){
				if ( StringUtils.isBlank(ivStr) ) {
					throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("ivStr"));
				}

				byte[] ivBytes = null;
				if ( isBase64Iv ) {
					ivBytes = Base64.getDecoder().decode(ivStr);
				} else {
					ivBytes = ivStr.getBytes(UTF_8);
				}

				cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(ivBytes));
			} else if ( algorithm.contains("GCM") ) {
				if ( StringUtils.isBlank(ivStr) ) {
					throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("ivStr"));
				}

				byte[] ivBytes = null;
				if ( isBase64Iv ) {
					ivBytes = Base64.getDecoder().decode(ivStr);
				} else {
					ivBytes = ivStr.getBytes(UTF_8);
				}

				GCMParameterSpec spec = new GCMParameterSpec(128, ivBytes);
				cipher.init(Cipher.DECRYPT_MODE, key, spec);
			}

			byte[] decryptedBytes = Base64.getDecoder().decode(cipherText);
			decryptedText = new String(cipher.doFinal(decryptedBytes), UTF_8);

		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException |
                IllegalArgumentException | NoSuchProviderException | UnsupportedEncodingException e) {
			logger.error("AES 복호화 중 오류 발생: {}", e.getMessage(), e);
		}

		return decryptedText;
    }

}
