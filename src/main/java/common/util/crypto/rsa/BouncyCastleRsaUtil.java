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

	private static final Logger logger = LoggerFactory.getLogger(BouncyCastleRsaUtil.class);

	private static final String UTF_8 = StandardCharsets.UTF_8.toString();

	private BouncyCastleRsaUtil() {
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

	private static class Algorithm {
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
		 * @param publicKey
		 * @return
		 */
		public static String convertPublicKeyToString(PublicKey publicKey) {
			Objects.requireNonNull(publicKey, ExceptionMessage.isNull("publicKey"));

			byte[] keyBytes = publicKey.getEncoded();
			return Base64.getEncoder().encodeToString(keyBytes);
		}

		/**
		 * 개인키를 Base64 문자열로 변환
		 * @param privateKey
		 * @return
		 */
		public static String convertPrivateKeyToString(PrivateKey privateKey) {
			Objects.requireNonNull(privateKey, ExceptionMessage.isNull("privateKey"));

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
				throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("base64PublicKey"));
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
				throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("base64PrivateKey"));
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
    	Objects.requireNonNull(keySize, ExceptionMessage.isNull("keySize"));

		if (keySize != 2048 || keySize != 3072 || keySize != 4096) {
			throw new IllegalArgumentException("keySize must be one of 2048, 3072, or 4096");
    	}

        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME);
        keyPairGen.initialize(keySize);
        return keyPairGen.generateKeyPair();
    }

	/**
	 * RSA 암호화
	 * @param publicKey
	 * @param plainText
	 * @return
	 */
	public static String encrypt(PublicKey publicKey, String plainText) {
		Objects.requireNonNull(publicKey, ExceptionMessage.isNull("publicKey"));

		if ( StringUtils.isBlank(plainText) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("plainText"));
		}

		String encryptedText = "";

		try {
			Cipher cipher = Cipher.getInstance(Algorithm.RSA_ECB_OAEP_WITH_SHA256_AND_MGF1_PADDING, BouncyCastleProvider.PROVIDER_NAME);
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
	 * @param privateKey
	 * @param cipherText
	 * @return
	 */
	public static String decrypt(PrivateKey privateKey, String cipherText) {
		Objects.requireNonNull(privateKey, ExceptionMessage.isNull("privateKey"));

		if ( StringUtils.isBlank(cipherText) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("cipherText"));
		}

		String decryptedText = "";

		try {
			Cipher cipher = Cipher.getInstance(Algorithm.RSA_ECB_OAEP_WITH_SHA256_AND_MGF1_PADDING, BouncyCastleProvider.PROVIDER_NAME);
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
