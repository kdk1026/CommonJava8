package common.util.crypto.aes;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 8. 6. 김대광	최초작성
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
public class AESCrypto {

	private String cipherKey;

    /**
     * 생성자
     * @param cipherKey 암/복호화 키
     * @since 2021.02.24
     */
    public AESCrypto(String cipherKey){
        this.cipherKey = cipherKey;
    }

    /**
     * 모드에 따른 암복호화 처리기
     * @param mode Cipher.ENCRYPT_MODE / Cipher.DECRYPT_MODE
     * @return Cipher
     * @throws Exception - NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException
     * @since 2021.02.24
     */
    private Cipher getCipher(int mode) throws Exception{
        Key key = new SecretKeySpec(toBytes(cipherKey, 16), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(mode, key);

        return cipher;
    }

    /**
     * AES(aes-128-ecb)암호화
     * @param src 평문
     * @return 암호화 된 HEX문자열
     * @throws Exception
     * @since 2021.02.24
     */
    public String encrypt(String src) throws Exception {
        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
        byte[] plain = src.getBytes();
        byte[] encrypt = cipher.doFinal(plain);
        return toHexString(encrypt);
    }

    /**
     * AES(aes-128-ecb)복호화
     * @param hex 암호화 된 HEX문자열
     * @return 복호화 된 평문
     * @throws Exception
     * @since 2021.02.24
     */
    public String decrypt(String hex) throws Exception {
        Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
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
     * @throws Exception - NumberFormatException, IllegalArgumentException
     */
    private byte[] toBytes(String digits, int radix) throws Exception {
        if (digits == null) {
            return null;
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
     * @throws Exception - IllegalArgumentException, NumberFormatException
     */
    private byte[] toBytesFromHexString(String digits) throws Exception {
        if (digits == null) {
            return null;
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
    private String toHexString(byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        StringBuffer result = new StringBuffer();
        for (byte b : bytes) {
            result.append(Integer.toString((b & 0xF0) >> 4, 16));
            result.append(Integer.toString(b & 0x0F, 16));
        }
        return result.toString();
    }
	
}
