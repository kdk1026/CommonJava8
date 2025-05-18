package common.util.crypto.rsa;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 3. 2. kdk	최초작성
 * </pre>
 *
 *
 * @author kdk
 */
public class RsaCryptoUtil {

	private RsaCryptoUtil() {
		super();
	}

	private static final Logger logger = LoggerFactory.getLogger(RsaCryptoUtil.class);

	private static final int DEFAULT_KEY_SIZE = 2048;

	private static final String CHARSET = StandardCharsets.UTF_8.toString();

	/** 데이터의 길이가 키 길이와 일치해야 함 */
	public static final String RSA_ECB_NOPADDING = "RSA/ECB/NoPadding";

	/** JavaScript 라이브러인 jsencrypt 와 맞출려면 이것을 사용해야 함 (가장 일반적) */
	public static final String RSA_ECB_PKCS1PADDING = "RSA/ECB/PKCS1Padding";

	/** 보안성이 가장 높음 */
	public static final String RSA_ECB_OAEPPADDING = "RSA/ECB/OAEPPadding";

	public static class Generate {
		private Generate() {
			super();
		}

		/**
		 * 공개키, 개인키 가져오기 위한 KeyPair 생성
		 * @return
		 */
		public static KeyPair generateKeyPair() {
			KeyPairGenerator keyPairGen = null;

			try {
				keyPairGen = KeyPairGenerator.getInstance("RSA");
			} catch (NoSuchAlgorithmException e) {
				logger.error("", e);
			}

			if ( keyPairGen != null ) {
				keyPairGen.initialize(DEFAULT_KEY_SIZE);
				return keyPairGen.generateKeyPair();
			} else {
				return null;
			}
		}
	}

	public static class Convert {
		private Convert() {
			super();
		}

		/**
		 * <pre>
		 * Base64 인코딩된 문자열로 공개 키를 반환
		 *  - Front에 공유하여 암호화하기 위한 키
		 * </pre>
		 * @param keyPair
		 * @return
		 */
		public static String getBase64PublicKey(KeyPair keyPair) {
			if ( keyPair == null ) {
				throw new IllegalArgumentException("keyPair is null");
			}

	        return Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
	    }

	    /**
	     * <pre>
	     * Base64 인코딩된 문자열로 개인 키를 반환
	     *  - Server단에 보관하여 복호화하기 위한 키
	     * </pre>
	     * @param keyPair
	     * @return
	     */
	    public static String getBase64PrivateKey(KeyPair keyPair) {
			if ( keyPair == null ) {
				throw new IllegalArgumentException("keyPair is null");
			}

	        return Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
	    }

	    /**
	     * Base64 문자열을 PublicKey로 변환
	     * @param base64PublicKey
	     * @return
	     */
	    public static PublicKey getPublicKeyFromBase64(String base64PublicKey) {
			if ( StringUtils.isBlank(base64PublicKey) ) {
				throw new IllegalArgumentException("base64PublicKey is null");
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
	    public static PrivateKey getPrivateKeyFromBase64(String base64PrivateKey) {
			if ( StringUtils.isBlank(base64PrivateKey) ) {
				throw new IllegalArgumentException("base64PrivateKey is null");
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
	 * RSA 암호화
	 * @param plainText
	 * @param publicKey
	 * @param padding
	 * @return
	 */
	public static String encrypt(String plainText, PublicKey publicKey, String padding) {
		if ( StringUtils.isBlank(plainText) ) {
			throw new IllegalArgumentException("plainText is null");
		}

		if ( publicKey == null ) {
			throw new IllegalArgumentException("publicKey is null");
		}

		if ( StringUtils.isBlank(padding) ) {
			throw new IllegalArgumentException("padding is null");
		}

		String encryptedText = "";

		try {
			Cipher cipher = Cipher.getInstance(padding);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] encryptedMessage = cipher.doFinal(plainText.getBytes(CHARSET));
			encryptedText = Base64.getEncoder().encodeToString(encryptedMessage);
		} catch (Exception e) {
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
	public static String decrypt(String encryptedText, PrivateKey privateKey, String padding) {
		if ( StringUtils.isBlank(encryptedText) ) {
			throw new IllegalArgumentException("encryptedText is null");
		}

		if ( privateKey == null ) {
			throw new IllegalArgumentException("privateKey is null");
		}

		if ( StringUtils.isBlank(padding) ) {
			throw new IllegalArgumentException("padding is null");
		}

		String decryptedText = "";

		try {
			Cipher cipher = Cipher.getInstance(padding);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] decryptedMessage = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
			decryptedText = new String(decryptedMessage, CHARSET);
		} catch (Exception e) {
			logger.error("", e);
		}

		return decryptedText;
	}

}
