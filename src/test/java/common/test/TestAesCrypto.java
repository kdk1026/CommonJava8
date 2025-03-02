package common.test;

import org.apache.commons.lang3.RandomStringUtils;

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

	public static void main(String[] args) {
		String key = RandomStringUtils.randomAlphanumeric(32);
		String iv = RandomStringUtils.randomAlphanumeric(16);

		key = "hEUEFcfoo7HxwDfIHmO2cYG9H0t1COCs";
		iv = "HxGvsFbYXagRxRUn";

		String encryptedText = AesCryptoUtilV2.encrypt("안녕", key, iv, AesCryptoUtilV2.AES_CBC_PKCS5PADDING);
		System.out.println(encryptedText);

		String decryptedText = AesCryptoUtilV2.decrypt("oBYzw4pH/RMRas+E1QdVVw==", key, iv, AesCryptoUtilV2.AES_CBC_PKCS5PADDING);
		System.out.println(decryptedText);
	}

}
