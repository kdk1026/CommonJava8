package common.util.crypto.rsa;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import common.util.ExceptionMessage;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 6. 1. kdk	최초작성
 * </pre>
 *
 * <pre>
 * 권장되는 Bouncy Castle 라이브러리를 사용하여 RSA 암호화를 수행하는 유틸리티 클래스
 *  - 비대칭(공개 키) 암호화 알고리즘
 *  - 공개 키로 암호화하고 개인 키로 복호화하거나, 개인 키로 서명하고 공개 키로 서명을 검증하는 데 사용
 *  - 주요 특징
 *    - 공개 키(Public Key): 누구나 가질 수 있으며, 데이터 암호화 또는 서명 검증에 사용
 *    - 개인 키(Private Key): 소유자만 가질 수 있으며, 데이터 복호화 또는 서명 생성에 사용
 *    - 용도
 *      - 주로 작은 크기의 데이터(예: 대칭 키)를 암호화하거나, 디지털 서명에 사용
 *      - 대량의 데이터를 직접 RSA로 암호화하는 것은 비효율적
 *      - 대칭 키(예: AES 키)를 RSA로 암호화한 후, 이 대칭 키로 실제 데이터를 암호화하는 하이브리드 암호화 방식을 사용
 * </pre>
 *
 * @author kdk
 */
public class BouncyCastleRsaUtil {

	private BouncyCastleRsaUtil() {
		super();
	}

	private static final Logger logger = LoggerFactory.getLogger(BouncyCastleRsaUtil.class);

	private static final String UTF_8 = StandardCharsets.UTF_8.toString();

	private static final String KEY_IS_NULL = "key must not be null";

	static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
            logger.debug("Bouncy Castle Provider 등록 완료.");
        }
    }

	/**
	 * 일반적인 OAEP만 정의 (필요 시, 다른 알고리즘 추가 가능)
	 */
	public static class Algorithm {
		private Algorithm() {
			super();
		}

		/** 가장 일반적 (권장) */
		public static final String RSA_ECB_OAEP_WITH_SHA256_AND_MGF1_PADDING = "RSA/ECB/OAEPWithSHA256AndMGF1Padding";
	}

	/**
	 * 키를 Base64 문자열로 변환 및 Base64 문자열을 키로 변환
	 */
	public static class Convert {
		private Convert() {
			super();
		}

		/**
		 * 공개키를 Base64 문자열로 변환
		 * @param key
		 * @return
		 */
		public static String convertPublicKeyToString(PublicKey publicKey) {
			Objects.requireNonNull(publicKey, KEY_IS_NULL);

			byte[] keyBytes = publicKey.getEncoded();
			return Base64.getEncoder().encodeToString(keyBytes);
		}

		/**
		 * 개인키를 Base64 문자열로 변환
		 * @param key
		 * @return
		 */
		public static String convertPrivateKeyToString(PrivateKey privateKey) {
			Objects.requireNonNull(privateKey, KEY_IS_NULL);

			byte[] keyBytes = privateKey.getEncoded();
			return Base64.getEncoder().encodeToString(keyBytes);
		}

	    /**
	     * Base64 문자열을 PublicKey로 변환
	     * @param base64PublicKey
	     * @return
	     */
	    public static PublicKey convertStringToPublicKey(String base64PublicKey) {
			if ( StringUtils.isBlank(base64PublicKey) ) {
				throw new IllegalArgumentException(ExceptionMessage.isNull("base64PublicKey"));
			}

	        byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);
	        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
	        KeyFactory keyFactory = null;
	        PublicKey publicKey = null;

			try {
				keyFactory = KeyFactory.getInstance("RSA");
				publicKey = keyFactory.generatePublic(spec);
			} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
				logger.error("", e);
			}

	        return publicKey;
	    }

	    /**
	     * Base64 문자열을 PrivateKey로 변환
	     * @param base64PrivateKey
	     * @return
	     */
	    public static PrivateKey convertStringToPrivateKey(String base64PrivateKey) {
			if ( StringUtils.isBlank(base64PrivateKey) ) {
				throw new IllegalArgumentException(ExceptionMessage.isNull("base64PrivateKey"));
			}

	        byte[] keyBytes = Base64.getDecoder().decode(base64PrivateKey);
	        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
	        KeyFactory keyFactory = null;
	        PrivateKey privateKey = null;

	        try {
	        	keyFactory = KeyFactory.getInstance("RSA");
	        	privateKey = keyFactory.generatePrivate(spec);
	        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
				logger.error("", e);
			}

	        return privateKey;
	    }
	}

    /**
     * <pre>
     * RSA 키 쌍 생성
     *  - keySize: 보통 2048, 3072, 4096 비트 사용 (2048비트 이상 권장)
     * </pre>
     * @param keySize
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     */
    public static KeyPair generateRsaKeyPair(int keySize) throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME);
        keyPairGen.initialize(keySize);
        return keyPairGen.generateKeyPair();
    }

	/**
	 * RSA 암호화
	 * @param algorithm
	 * @param publicKey
	 * @param plainText
	 * @return
	 */
	public static String encrypt(String algorithm, PublicKey publicKey, String plainText) {
		if ( StringUtils.isBlank(algorithm) ) {
			throw new IllegalArgumentException("algorithm must not be blank");
		}

		Objects.requireNonNull(publicKey, KEY_IS_NULL);

		if ( StringUtils.isBlank(plainText) ) {
			throw new IllegalArgumentException("plainText must not be blank");
		}

		String encryptedText = "";

		try {
			Cipher cipher = Cipher.getInstance(algorithm, BouncyCastleProvider.PROVIDER_NAME);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] encryptedMessage = cipher.doFinal(plainText.getBytes(UTF_8));
			encryptedText = Base64.getEncoder().encodeToString(encryptedMessage);

		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
				IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException |
				NoSuchProviderException e) {
			logger.error("", e);
		}

		return encryptedText;
	}

	/**
	 * RSA 복호화
	 * @param encryptedText
	 * @param privateKey
	 * @param padding
	 * @return
	 */
	public static String decrypt(String algorithm, PrivateKey privateKey, String cipherText) {
		if ( StringUtils.isBlank(algorithm) ) {
			throw new IllegalArgumentException("algorithm must not be blank");
		}

		Objects.requireNonNull(privateKey, KEY_IS_NULL);

		if ( StringUtils.isBlank(cipherText) ) {
			throw new IllegalArgumentException("cipherText must not be blank");
		}

		String decryptedText = "";

		try {
			Cipher cipher = Cipher.getInstance(algorithm, BouncyCastleProvider.PROVIDER_NAME);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] decryptedMessage = cipher.doFinal(Base64.getDecoder().decode(cipherText));
			decryptedText = new String(decryptedMessage, UTF_8);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
				IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException |
				NoSuchProviderException e) {
			logger.error("", e);
		}

		return decryptedText;
	}

}
