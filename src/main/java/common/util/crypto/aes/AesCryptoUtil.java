package common.util.crypto.aes;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 8. 13. 김대광	SonarLint 지시에 따른 수정 (SecureRandom 추가로... 이 버전에 맞춰놓은 Node.js 유틸도 수정해야 돼... 아... 귀찮아 죽겠다..)
 * 2021. 8. 15. 김대광	SecureRandom 관련 메소드 추가해서 주저리 주저리 하고, 암/복호화 시에는 걷어냄
 * </pre>
 *
 * <pre>
 * 블록암호 알고리즘 기능 중 대중적인 AES 제공
 * - 복호화가 가능한 양방향 암호화
 *   (예: 주민등록번호, 신용카드번호, 계좌번호, 여권번호, 운전면허번호 등)
 * - 일정한 블록 크기로 나누어,
 *   각 블록을 송·수신자간에 공유한 비밀키를 사용하여 암호화하는 방식
 *
 * - 암호화 키
 *   > 128비트(16자), 192비트(24자), 256비트(32자)
 *   > 192비트, 256비트 사용 조건 - Java 8부터는 Default로 지원하는 듯
 *     1. Java Cryptography Extension (JCE) 다운로드
 *     2. local_policy.jar, US_export_policy.jar 파일을 다음 경로에 덮어쓰기
 *        → $JAVA_HOME/jre/lib/security
 * - 패딩
 *   > NoPadding 인 경우, 평문 길이가 암호화 키와 동일해야 함
 *
 * - Base64
 * 	 > java 8
 * </pre>
 *
 * @author 김대광
 */
public class AesCryptoUtil {

	private AesCryptoUtil() {
		super();
	}

	private static final Logger logger = LoggerFactory.getLogger(AesCryptoUtil.class);

	private static final byte[] IV_BYTES = new byte[16];

	/**
	 * @since 1.7
	 */
	private static final String CHARSET = StandardCharsets.UTF_8.toString();

	public static final String AES_CBC_NOPADDING ="AES/CBC/NoPadding";
	public static final String AES_CBC_PKCS5PADDING ="AES/CBC/PKCS5Padding";
	public static final String AES_ECB_NOPADDING ="AES/ECB/NoPadding";
	public static final String AES_ECB_PKCS5PADDING ="AES/ECB/PKCS5Padding";

	/**
	 * <pre>
	 * 랜덤 바이트 어레이 생성
	 *   - 자세히 모르겠음...
	 *   - 다른 시스템과 연계시에는 사용못할 듯
	 *
	 *   - 단독 사용 시, 다음과 같은 형태로 사용해야 할 듯
	 *   - 암호화/복호화 메소드 인자에 byte[] bytesIV 추가
	 *   - new IvParameterSpec(bytesIV)
	 * </pre>
	 * @return
	 */
	public static byte[] makeSecureIv() {
		byte[] bytesIV = new byte[16];
		SecureRandom random = new SecureRandom();
		random.nextBytes(bytesIV);
		return bytesIV;
	}

	public static String aesEncrypt(String sKey, String sPadding, String strPlainText) {
		String strEncryptText = "";
		try {
			SecretKey secretKey = new SecretKeySpec(sKey.getBytes(CHARSET), "AES");

			String padding = sPadding;
			Cipher cipher = Cipher.getInstance(padding);

			if ( padding.indexOf("CBC") > -1 ) {
				// CBC의 경우, IvParameterSpec 생략 가능
				cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(IV_BYTES));

			} else {
				// ECB의 경우, IvParameterSpec 사용 불가
				cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			}

			byte[] textBytes = cipher.doFinal(strPlainText.getBytes(CHARSET));
			strEncryptText = Base64.getEncoder().encodeToString(textBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException |
                UnsupportedEncodingException | IllegalArgumentException e) {
        	logger.error("", e);
        }
		return strEncryptText;
	}

	public static String aesDecrypt(String sKey, String sPadding, String strEncryptText) {
		String strDecryptText = "";
		try {
			SecretKey secretKey = new SecretKeySpec(sKey.getBytes(CHARSET), "AES");

			String padding = sPadding;
			Cipher cipher = Cipher.getInstance(padding);

			if ( padding.indexOf("CBC") > -1 ) {
				// CBC의 경우, IvParameterSpec 생략 가능
				cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(IV_BYTES));

			} else {
				// ECB의 경우, IvParameterSpec 사용 불가
				cipher.init(Cipher.DECRYPT_MODE, secretKey);
			}

			byte[] textBytes = Base64.getDecoder().decode(strEncryptText);
			strDecryptText = new String(cipher.doFinal(textBytes), CHARSET);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException |
                UnsupportedEncodingException | IllegalArgumentException e) {
        	logger.error("", e);
        }
		return strDecryptText;
	}
}
