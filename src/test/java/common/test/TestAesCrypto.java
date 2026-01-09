package common.test;

import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import common.util.crypto.EncryptResult;
import common.util.crypto.aes.AesCryptoUtilV2;

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
public class TestAesCrypto {

	@Test
	public void test() {
		String key = RandomStringUtils.randomAlphanumeric(32);

		key = "hEUEFcfoo7HxwDfIHmO2cYG9H0t1COCs";

		EncryptResult encryptResult = AesCryptoUtilV2.encrypt("안녕", key, null, AesCryptoUtilV2.AES_CBC_PKCS5PADDING);

		String encryptedText = encryptResult.getCipherText();
		System.out.println(encryptedText);
		String iv = encryptResult.getIv();

		String decryptedText = AesCryptoUtilV2.decrypt(encryptedText, key, iv, true, AesCryptoUtilV2.AES_CBC_PKCS5PADDING);
		System.out.println(decryptedText);

		assertTrue(true);
	}

}
