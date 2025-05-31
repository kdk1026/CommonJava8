package common.util.crypto;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

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
 * 권장되는 Bouncy Castle 라이브러리를 사용하여 해싱을 수행
 *  - 암호화와는 다르게, 원본 데이터를 고정된 길이의 "해시 값" 또는 "다이제스트"로 변환하는 단방향 함수
 *  - 해시 값은 원본 데이터의 무결성을 확인하거나, 비밀번호를 저장할 때 직접 저장하는 대신 해시 값을 저장하는 등 다양한 용도로 사용
 * </pre>
 *
 * @author kdk
 */
public class BouncyCastleHashingUtil {

	private BouncyCastleHashingUtil() {
		super();
	}

	private static final Logger logger = LoggerFactory.getLogger(BouncyCastleHashingUtil.class);

	private static final String UTF_8 = StandardCharsets.UTF_8.toString();

	static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
            logger.debug("Bouncy Castle Provider 등록 완료.");
        }
    }

    /**
     * <pre>
     * 주어진 데이터를 특정 해시 알고리즘을 사용하여 해싱
     *  - "MD5", "SHA-256", "SHA-512"
     * </pre>
     * @param
     * @param
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     */
    private static byte[] hashData(String algorithm, byte[] data)
            throws NoSuchAlgorithmException, NoSuchProviderException {

        MessageDigest digest = MessageDigest.getInstance(algorithm, BouncyCastleProvider.PROVIDER_NAME);
        return digest.digest(data);
    }

    /**
     * 바이트 배열을 16진수 문자열로 변환
     * @param bytes
     * @return
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

	/**
	 * <pre>
	 * MD5 해싱
	 * 	- SHA-1과 함께 해시 값 길이가 64비트로 낮아 권고하지 않음
	 * </pre>
	 * @param strPlainText
	 * @return
	 */
	public static String md5Hash(String originalText) {
		byte[] md5Hash = null;

		try {
			byte[] dataBytes = originalText.getBytes(UTF_8);
			md5Hash = hashData("MD5", dataBytes);
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchProviderException e) {
			logger.error("MD5 해싱 중 오류 발생: {}", e.getMessage(), e);
		}

		return bytesToHex(md5Hash);
	}

	/**
	 * <pre>
	 * SHA-256 해싱
	 * 	- 해시 값 길이가 128비트로 권고
	 * </pre>
	 * @param strPlainText
	 * @return
	 */
	public static String sha256Hash(String originalText) {
		byte[] md5Hash = null;

		try {
			byte[] dataBytes = originalText.getBytes(UTF_8);
			md5Hash = hashData("SHA-256", dataBytes);
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchProviderException e) {
			logger.error("SHA-256 해싱 중 오류 발생: {}", e.getMessage(), e);
		}

		return bytesToHex(md5Hash);
	}

	/**
	 * <pre>
	 * SHA-512 해싱
	 * 	- 해시 값 길이가 256비트로 권고
	 * </pre>
	 * @param strPlainText
	 * @return
	 */
	public static String sha512Hash(String originalText) {
		byte[] md5Hash = null;

		try {
			byte[] dataBytes = originalText.getBytes(UTF_8);
			md5Hash = hashData("SHA-512", dataBytes);
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchProviderException e) {
			logger.error("SHA-512 해싱 중 오류 발생: {}", e.getMessage(), e);
		}

		return bytesToHex(md5Hash);
	}

}
