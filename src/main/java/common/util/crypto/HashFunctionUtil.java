package common.util.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * 해쉬함수 알고리즘 기능 제공(대중적인 MD5, SHA256, SHA512)<br/>
 *  - 복호화가 불가능한 단방향 암호화 (예: 비밀번호)<br/>
 *  - 임의길이의 정보를 입력 받아, 고정된 길이의 해쉬값 출력 방식<br/>
 *  - commons.codec.DigestUtils 사용 권장
 *  	> 소금도 쳐야 하는데, DigestUtils에서 제공하므로 무슨일이 있어도 이딴 유틸대신 DigestUtils 사용할 것!!!!
 *  	> 아파치 재단은 진리인 것이다!!!
 *
 *  참고 - {@link common.libTest.commons.UsageCodec#hashing}
 *  </pre>
 */
public class HashFunctionUtil {

	private static final Logger logger = LoggerFactory.getLogger(HashFunctionUtil.class);

	private HashFunctionUtil() {
		super();
	}

	/**
	 * Md5 해쉬함수로 인코딩<br/>
	 * 	- SHA-1과 함께 암호화 길이가 128비트로 낮아 권고하지 않는 해쉬함수<br/>
	 *  - commons.codec.DigestUtils.md5Hex(plain)
	 * @param strPlainText
	 * @return
	 */
	public static String md5Encode(String strPlainText) {
		return searchHashAlgorithm(strPlainText, "MD5");
	}

	/**
	 * SHA-256 해쉬함수로 인코딩<br/>
	 * 	- 암호화 길이가 256비트로 권고하는 해쉬함수<br/>
	 *  - commons.codec.DigestUtils.sha256Hex(plain)
	 * @param strPlainText
	 * @return
	 */
	public static String sha256Encode(String strPlainText) {
		return searchHashAlgorithm(strPlainText, "SHA-256");
	}

	/**
	 * SHA-512 해쉬함수로 인코딩<br/>
	 * 	- 암호화 길이가 512비트로 권고하는 해쉬함수<br/>
	 *  - commons.codec.DigestUtils.sha512Hex(plain)
	 * @param strPlainText
	 * @return
	 */
	public static String sha512Encode(String strPlainText) {
		return searchHashAlgorithm(strPlainText, "SHA-512");
	}

	private static String searchHashAlgorithm(String strPlainText, String strAlgorithm) {
		if ( StringUtils.isBlank(strPlainText) ) {
			throw new IllegalArgumentException("strPlainText is null");
		}

		if ( StringUtils.isBlank(strAlgorithm) ) {
			throw new IllegalArgumentException("strAlgorithm is null");
		}

		String strEncodedText = "";
		try {
			MessageDigest md = MessageDigest.getInstance(strAlgorithm);
			md.update(strPlainText.getBytes());

			byte[] mdBytes = md.digest();
			StringBuilder sb = new StringBuilder();

			for (int i=0; i < mdBytes.length; i++) {
				sb.append(Integer.toString((mdBytes[i]&0xff) + 0x100, 16).substring(1));
			}
			strEncodedText = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			logger.error("", e);
		}
		return strEncodedText;
	}

}
