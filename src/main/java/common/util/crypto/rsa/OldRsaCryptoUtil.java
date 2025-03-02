package common.util.crypto.rsa;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import common.util.file.NioFileUtil;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 8. 13. 김대광	SonarLint 지시에 따른 주저리 주저리 (제시하는.. Cipher.getInstance("RSA/None/OAEPWithSHA-1AndMGF1Padding");, Cipher.getInstance("RSA/None/OAEPWITHSHA-256ANDMGF1PADDING")
 * 		RSA는 패딩을 전혀 몰라... RSA 자체가 어려움... 이딴거 쓰는데 지금까지... 1번 보기는 했던거 같구나....
 * </pre>
 * 
 * <pre>
 * 공개키 암호화 방식인 RSA 암호화 기능 제공
 *  - 공개키로 암호화하고, 그와 다른 비밀키로만 열 수 있는 암호화 알고리즘
 *  - RSA 기반 웹페이지 암호화 로그인
 *    > http://kwon37xi.egloos.com/4427199
 *    
 *  - Base64
 *    > java 8
 * </pre>
 *
 * @author 김대광
 */
public class OldRsaCryptoUtil {
	
	private OldRsaCryptoUtil() {
		super();
	}
	
	private static final Logger logger = LoggerFactory.getLogger(OldRsaCryptoUtil.class);

	/**
	 * @since 1.7
	 */
	private static final String CHARSET = StandardCharsets.UTF_8.toString();
	
	private static final int DEFAULT_KEY_SIZE = 2048;
	private static final String KEY_FACTORY_ALGORITHM = "RSA";
	private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
	
	private static final String PRIVATE_KEY_SETSSION_NAME = "__rsaPrivateKey__";
	
	public static final String RSA_ECB_NOPADDING ="RSA/ECB/NoPadding";
	public static final String RSA_ECB_PKCS1PADDING ="RSA/ECB/PKCS1Padding";
	public static final String RSA_ECB_OAEPPADDING ="RSA/ECB/OAEPPadding";
	
	public static class Generate {
		private Generate() {
			super();
		}
		
		public static KeyPair generateKeyPair() {
			KeyPair keyPair = null;
			KeyPairGenerator generator;
			try {
				generator = KeyPairGenerator.getInstance(KEY_FACTORY_ALGORITHM);
				generator.initialize(DEFAULT_KEY_SIZE, new SecureRandom());
				keyPair = generator.genKeyPair();
				
			} catch (NoSuchAlgorithmException e) {
				logger.error("", e);
			}
			return keyPair;
		}
		
		public static PublicKey generatePublicKey(KeyPair keyPair) {
			return keyPair.getPublic();
		}
		
		public static PublicKey generatePublicKey(byte[] encodedPublicKey) {
			PublicKey publicKey = null;
			
			try {
				KeyFactory keyFactory = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM);
				publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedPublicKey));
				
			} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
				logger.error("", e);
			}
			
			return publicKey;
		}
		
		public static PrivateKey generatePrivateKey(KeyPair keyPair) {
			return keyPair.getPrivate();
		}
		
		public static PrivateKey generatePrivateKey(byte[] encodedPrivateKey) {
			PrivateKey privateKey = null;
			
			try {
				KeyFactory keyFactory = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM);
				privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedPrivateKey));
				
			} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
				logger.error("", e);
			}
			
			return privateKey;
		}
		
		public static String generatePrivateSign(String plainText, byte[] encodedPrivateKey) {
			String sign = "";
			
			try {
				Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM);
				sig.initSign(generatePrivateKey(encodedPrivateKey));
				sig.update(plainText.getBytes(CHARSET));
				
				byte[] signature = sig.sign();
				sign = Base64.getEncoder().encodeToString(signature);
				
			} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | UnsupportedEncodingException e) {
				logger.error("", e);
			}
			
			return sign;
		}
	}
	
	public static class SaveFile {
		private SaveFile() {
			super();
		}
		
		public static void savePublicKeyInFile(KeyPair keyPair, String destFilePath, String destFileName) {
			File destPath = new File(destFilePath);
			File destFile = new File(destFilePath + NioFileUtil.FOLDER_SEPARATOR + destFileName);
			
			if (!destPath.exists()) {
				destPath.mkdirs();
			}
			
			try (FileOutputStream fos = new FileOutputStream(destFile)) {
				byte[] encodedPublicKey = keyPair.getPublic().getEncoded();
				
				fos.write(encodedPublicKey);
				
			} catch (IOException e) {
				logger.error("", e);
			}
		}
		
		public static void savePrivateKeyInFile(KeyPair keyPair, String destFilePath, String destFileName) {
			File destPath = new File(destFilePath);
			File destFile = new File(destFilePath + NioFileUtil.FOLDER_SEPARATOR + destFileName);
			
			if (!destPath.exists()) {
				destPath.mkdirs();
			}
			
			try ( FileOutputStream fos = new FileOutputStream(destFile) ) {
				byte[] encodedPrivateKey = keyPair.getPrivate().getEncoded();
				
				fos.write(encodedPrivateKey);
				
			} catch (IOException e) {
				logger.error("", e);
			}
		}
		
		public static void savePrivateSigInFile(String sign, String destFilePath, String destFileName) {
			File destPath = new File(destFilePath);
			File destFile = new File(destFilePath + NioFileUtil.FOLDER_SEPARATOR + destFileName);
			
			if ( !destPath.exists() ) {
				destPath.mkdirs();
			}
			
			try ( FileOutputStream fos = new FileOutputStream(destFile) ) {
				byte[] bData = sign.getBytes();
				
				fos.write(bData);
				
			} catch (IOException e) {
				logger.error("", e);
			}
		}
	}
	
	public static class Encrypt {
		private Encrypt() {
			super();
		}
		
		public static String encrypt(String plainText, byte[] encodedPublicKey) {
			String encryptText = "";
			PublicKey publicKey = Generate.generatePublicKey(encodedPublicKey);
			
			try {
				Cipher cipher = Cipher.getInstance(KEY_FACTORY_ALGORITHM);
				cipher.init(Cipher.ENCRYPT_MODE, publicKey);
				
				byte[] bytes = cipher.doFinal(plainText.getBytes(CHARSET));
				encryptText = Base64.getEncoder().encodeToString(bytes);
				
			} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
				logger.error("", e);
			}
			
			return encryptText;
		}
		
		public static String encrypt(String plainText, byte[] encodedPublicKey, String padding) {
			String encryptText = "";
			PublicKey publicKey = Generate.generatePublicKey(encodedPublicKey);
			
			try {
				Cipher cipher = Cipher.getInstance(padding);
				cipher.init(Cipher.ENCRYPT_MODE, publicKey);
				
				byte[] bytes = cipher.doFinal(plainText.getBytes(CHARSET));
				encryptText = Base64.getEncoder().encodeToString(bytes);
				
			} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
				logger.error("", e);
			}
			
			return encryptText;
		}
	}
	
	
	public static class Decrypt {
		private Decrypt() {
			super();
		}
		
		public static String decrypt(String encryptText, byte[] encodedPrivateKey) {
			String decryptText = "";
			PrivateKey privateKey = Generate.generatePrivateKey(encodedPrivateKey);
			byte[] bytes = Base64.getDecoder().decode(encryptText);
			
			try {
				Cipher cipher = Cipher.getInstance(KEY_FACTORY_ALGORITHM);
				cipher.init(Cipher.DECRYPT_MODE, privateKey);
				
				decryptText = new String(cipher.doFinal(bytes), CHARSET);
				
			} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException e) {
				logger.error("", e);
			}
			
			return decryptText;
		}
		
		public static String decrypt(String encryptText, byte[] encodedPrivateKey, String padding) {
			String decryptText = "";
			PrivateKey privateKey = Generate.generatePrivateKey(encodedPrivateKey);
			byte[] bytes = Base64.getDecoder().decode(encryptText);
			
			try {
				Cipher cipher = Cipher.getInstance(padding);
				cipher.init(Cipher.DECRYPT_MODE, privateKey);
				
				decryptText = new String(cipher.doFinal(bytes), CHARSET);
				
			} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException e) {
				logger.error("", e);
			}
			return decryptText;
		}
		
		public static String decryptFromJsbn(String encryptText, byte[] encodedPrivateKey) {
			String decryptText = "";
			PrivateKey privateKey = Generate.generatePrivateKey(encodedPrivateKey);
			byte[] bytes = hexToByteArray(encryptText);
			
			try {
				Cipher cipher = Cipher.getInstance(KEY_FACTORY_ALGORITHM);
				cipher.init(Cipher.DECRYPT_MODE, privateKey);
				
				decryptText = new String(cipher.doFinal(bytes), CHARSET);
				
			} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException e) {
				logger.error("", e);
			}
			
			return decryptText;
		}
		
		/**
		 * 16진 문자열을 byte 배열로 반환
		 * @param hex
		 * @return
		 */
		private static byte[] hexToByteArray(String hex) {
			if (hex == null || hex.length() % 2 != 0) {
				return new byte[]{};
			}

			byte[] bytes = new byte[hex.length() / 2];
			
			for (int i = 0; i < hex.length(); i += 2) {
				byte value = (byte)Integer.parseInt(hex.substring(i, i + 2), 16);
				bytes[i / 2] = value;
			}
			return bytes;
		}
	}
	
	public static boolean verifySignature(String plainText, String sign, PublicKey publicKey) {
		boolean isVerify = false;
		
		try {
			Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM);
			sig.initVerify(publicKey);
			sig.update(plainText.getBytes());
			
			if ( sig.verify(Base64.getDecoder().decode(sign)) ) {
				isVerify = true;
			}
			
		} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
			logger.error("", e);
		}
		
		return isVerify;
	}
	
	public static class Session {
		private Session() {
			super();
		}
		
		public static void setPrivateKeyInSession(HttpSession session, PrivateKey privateKey) {
			session.setAttribute(PRIVATE_KEY_SETSSION_NAME, privateKey);
		}
		
		public static PrivateKey getPrivateKeyInSession(HttpSession session) {
			return (PrivateKey) session.getAttribute(PRIVATE_KEY_SETSSION_NAME);
		}
		
		private static RSAPublicKeySpec getPublicKeySpec(PublicKey publicKey) {
			RSAPublicKeySpec publicKeySpec = null;
			
			try {
				KeyFactory keyFactory = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM);
				publicKeySpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
				
			} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
				logger.error("", e);
			}
			
			return publicKeySpec;
		}
		
		public static void setPublicKeySpecInSession(HttpSession session, PublicKey publicKey) {
			RSAPublicKeySpec publicKeySpec = getPublicKeySpec(publicKey);
			
			if (publicKeySpec != null) {
				String publicKeyModulus = publicKeySpec.getModulus().toString(16);
				String publicKeyExponent = publicKeySpec.getPublicExponent().toString(16);
				
				session.setAttribute("publicKeyModulus", publicKeyModulus);
				session.setAttribute("publicKeyExponent", publicKeyExponent);
			}
		}
	}
	
	/*
	public static void main(String[] args) {
		String plainText = "admin!@34";
		
		String sDestFilePath = new StringBuilder().append("C:").append(NioFileUtil.FOLDER_SEPARATOR).append("test").append(NioFileUtil.FOLDER_SEPARATOR).append("rsa").toString();
		String sPublicKeyFileName = "public.key";
		String sPrivateKeyFileName = "private.key";
		String sPrivateSignFileName = "private.sig";

//		KeyPair keyPair = Generate.generateKeyPair();
//		SaveFile.savePublicKeyInFile(keyPair, sDestFilePath, sPublicKeyFileName);
//		SaveFile.savePrivateKeyInFile(keyPair, sDestFilePath, sPrivateKeyFileName);

		byte[] encodedPublicKey = NioFileUtil.convertFileToBytes(sDestFilePath + NioFileUtil.FOLDER_SEPARATOR + sPublicKeyFileName);
		byte[] encodedPrivateKey = NioFileUtil.convertFileToBytes(sDestFilePath + NioFileUtil.FOLDER_SEPARATOR + sPrivateKeyFileName);
		
//		String sign = Generate.generatePrivateSign(plainText, encodedPrivateKey);
//		SaveFile.savePrivateSigInFile(sign, sDestFilePath, sPrivateSignFileName);
		
		PublicKey publicKey = Generate.generatePublicKey(encodedPublicKey);
		String encryptText = Encrypt.encrypt(plainText, encodedPublicKey);
		String decryptText = Decrypt.decrypt(encryptText, encodedPrivateKey);
		
		String sSign = NioFileUtil.readFile(sDestFilePath + NioFileUtil.FOLDER_SEPARATOR + sPrivateSignFileName);
		logger.debug("\n{}\n", sSign);
		
		boolean isVerify = verifySignature(decryptText, sSign, publicKey);
		logger.debug("\n{}\n{}\n{}", encryptText, decryptText, isVerify);
	}
	*/
	
}
