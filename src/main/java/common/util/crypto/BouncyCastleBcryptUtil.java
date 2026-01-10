package common.util.crypto;

import java.security.SecureRandom;
import java.security.Security;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.crypto.generators.OpenBSDBCrypt;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2026. 1. 10. 김대광	최초작성
 * </pre>
 *
 *
 * @author 김대광
 */
public class BouncyCastleBcryptUtil {

	private BouncyCastleBcryptUtil() {
		super();
	}

	private static final Logger logger = LoggerFactory.getLogger(BouncyCastleBcryptUtil.class);

	private static final String ORIGINAL_TEXT_IS_NULL = "원본 텍스트가 비어 있거나 null입니다. 해싱을 수행할 수 없습니다.";

	static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
            logger.debug("Bouncy Castle Provider 등록 완료.");
        }
    }

	/**
	 * Bcrypt 해싱
	 * @param originalText
	 * @return
	 */
	public static String bcryptHash(String originalText) {
		if ( StringUtils.isBlank(originalText) ) {
			throw new IllegalArgumentException(ORIGINAL_TEXT_IS_NULL);
		}

		SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        int cost = 12;
        return OpenBSDBCrypt.generate(originalText.toCharArray(), salt, cost);
	}

	/**
	 * Bcrypt 해싱 검증
	 * @param originalText
	 * @param hashedText
	 * @return
	 */
	public static boolean checkBcryptHash(String originalText, String hashedText) {
		if ( StringUtils.isBlank(originalText) ) {
			throw new IllegalArgumentException(ORIGINAL_TEXT_IS_NULL);
		}

		if ( StringUtils.isBlank(hashedText) ) {
			throw new IllegalArgumentException("해시된 텍스트가 비어 있거나 null입니다. 검증을 수행할 수 없습니다.");
		}

		return OpenBSDBCrypt.checkPassword(hashedText, originalText.toCharArray());
	}

}
