package common.util.crypto.aes;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;

import common.util.ExceptionMessage;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 8.  6. 김대광	최초작성
 * 2021. 8. 13. 김대광	SonarLint 지시에 따른 수정 (그냥 복사해 왔지... throw new 해놓고, 메소드에 throws 왜 걸었지???)
 * 			Cipher.getInstance 패딩 권장이긴 하지만... node.js 랑 맞춰진거니 별 수 있나 뭐...
 * 2021. 8. 15. 김대광	LazyHolder Singleton 패턴으로 변경 및 파일명 변경
 * 2025. 5. 18. 김대광	AI가 추천한 Singleton 패턴으로 변경
 * 2025. 5. 27. 김대광	유틸은 Singleton 패턴을 사용하지 않는 것이 좋다는 의견 반영, 제미나이에 의한 일부 코드 개선
 * </pre>
 *
 * <pre>
 * 참조 링크를 보면 알겠지만 node.js의 결과와 java의 결과가 동일함
 * node.js 알고리즘 : aes-128-ecb
 * </pre>
 *
 * {@link} <a href="https://www.steemcoinpan.com/hive-101145/@wonsama/aes-128-ecb-java-nodejs">Ref</a>
 * @author 김대광
 */
public class AesCryptoHexUtil {

	private AesCryptoHexUtil() {
		super();
	}

	/**
     * 모드에 따른 암복호화 처리기
     * @param mode Cipher.ENCRYPT_MODE / Cipher.DECRYPT_MODE
     * @param cipherKey 키의 길이는 32바이트
     * @return Cipher
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @since 2021.02.24
     */
    private static Cipher getCipher(int mode, String cipherKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
    	Objects.requireNonNull(cipherKey, ExceptionMessage.isNull("cipherKey"));

        Key key = new SecretKeySpec(toBytes(cipherKey, 16), "AES");
        // TODO https://github.com/kdk1026/node_utils/blob/main/libs/aescrypto_hex.js 함께 수정해서 결과를 봐야 할 듯
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(mode, key);

        return cipher;
    }

    /**
     * AES(aes-128-ecb)암호화
     * @param src 평문
     * @param cipherKey 키의 길이는 32바이트
     * @return 암호화 된 HEX문자열
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @since 2021.02.24
     */
    public static String encrypt(String src, String cipherKey) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
    	if ( StringUtils.isBlank(src) ) {
    		throw new IllegalArgumentException(ExceptionMessage.isNull("src"));
    	}

    	Objects.requireNonNull(cipherKey, ExceptionMessage.isNull("cipherKey"));

        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE, cipherKey);
        byte[] plain = src.getBytes();
        byte[] encrypt = cipher.doFinal(plain);
        return toHexString(encrypt);
    }

    /**
     * AES(aes-128-ecb)복호화
     * @param hex 암호화 된 HEX문자열
     * @param cipherKey 키의 길이는 32바이트
     * @return 복호화 된 평문
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @since 2021.02.24
     */
    public static String decrypt(String hex, String cipherKey) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
    	if ( StringUtils.isBlank(hex) ) {
    		throw new IllegalArgumentException(ExceptionMessage.isNull("hex"));
    	}

        Cipher cipher = getCipher(Cipher.DECRYPT_MODE, cipherKey);
        byte[] encrypt = toBytesFromHexString(hex);
        byte[] decrypt = cipher.doFinal(encrypt);
        return new String(decrypt);
    }

    /**
     * 8, 10, 16진수 문자열을 바이트 배열로 변환한다.
     * 8, 10진수인 경우는 문자열의 3자리가, 16진수인 경우는 2자리가, 하나의 byte로 바뀐다.
     * @param digits 문자열
     * @param radix 진수 (8,10,16만 가능)
     * @return byte[]
     */
    private static byte[] toBytes(String digits, int radix) {
       	if ( StringUtils.isBlank(digits) ) {
            return new byte[0];
        }

        if (radix != 16 && radix != 10 && radix != 8) {
            throw new IllegalArgumentException("For input radix: \"" + radix+ "\"");
        }

        int divLen = (radix == 16) ? 2 : 3;
        int length = digits.length();

        if (length % divLen == 1) {
            throw new IllegalArgumentException("For input string: \"" + digits+ "\"");
        }

        length = length / divLen;
        byte[] bytes = new byte[length];

        for (int i = 0; i < length; i++) {
            int index = i * divLen;
            bytes[i] = (byte) (Short.parseShort(digits.substring(index, index + divLen), radix));
        }
        return bytes;
    }

    /**
     * 입력받은 HEX 문자열을 byte[] 로 변환
     * @param digits 입력 문자열
     * @return byte[]
     */
    private static byte[] toBytesFromHexString(String digits) {
    	if ( StringUtils.isBlank(digits) ) {
            return new byte[0];
        }

        int length = digits.length();

        if (length % 2 == 1) {
            throw new IllegalArgumentException("For input string: \"" + digits + "\"");
        }

        length = length / 2;
        byte[] bytes = new byte[length];

        for (int i = 0; i < length; i++) {
            int index = i * 2;
            bytes[i] = (byte) (Short.parseShort(digits.substring(index, index + 2), 16));
        }
        return bytes;
    }

    /**
     * unsigned byte(바이트) 배열을 16진수 문자열로 바꾼다.
     * @param bytes 배열
     * @return 16진수 문자열
     */
    private static String toHexString(byte[] bytes) {
        if ( bytes == null || bytes.length == 0 ) {
            return null;
        }

        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(Integer.toString((b & 0xF0) >> 4, 16));
            result.append(Integer.toString(b & 0x0F, 16));
        }
        return result.toString();
    }

}
