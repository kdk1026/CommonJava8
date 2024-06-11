package common.util.crypto.aes;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 8.  6. 김대광	최초작성
 * 2021. 8. 13. 김대광	SonarLint 지시에 따른 수정 (그냥 복사해 왔지... throw new 해놓고, 메소드에 throws 왜 걸었지???)
 * 			Cipher.getInstance 패딩 권장이긴 하지만... node.js 랑 맞춰진거니 별 수 있나 뭐...
 * 2021. 8. 15. 김대광	LazyHolder Singleton 패턴으로 변경 및 파일명 변경
 * </pre>
 * 
 * <pre>
 * 참조 링크를 보면 알겠지만 node.js의 결과와 java의 결과가 동일함
 * node.js 알고리즘 : aes-128-ecb
 * </pre>
 * 
 * <pre>
 * [사용 방법]
 * 	AesCryptoHexUtil.getInstance().setCipherKey("012345678901234567890123456789ab");
 * 	String en = AesCryptoHexUtil.getInstance().encrypt("apple");
 * 	String de = AesCryptoHexUtil.getInstance().decrypt(en);
 * </pre>
 * 
 * {@link} <a href="https://www.steemcoinpan.com/hive-101145/@wonsama/aes-128-ecb-java-nodejs">Ref</a>
 * @author 김대광
 */
public class AesCryptoHexUtil {
	
	/** 외부에서 객체 인스턴스화 불가 */
	private AesCryptoHexUtil() {
		super();
	}

	/**
	 * Singleton 인스턴스 생성
	 * 
	 * @return
	 */
	public static AesCryptoHexUtil getInstance() {
		return LazyHolder.INSTANCE;
	}

	/**
	 * LazyHolder Singleton 패턴
	 * 
	 * @return
	 */
	private static class LazyHolder {
		private static final AesCryptoHexUtil INSTANCE = new AesCryptoHexUtil();
	}

	private String cipherKey;
	
    /**
     * 키의 길이는 32바이트
     * @param cipherKey
     */
    public void setCipherKey(String cipherKey) {
		this.cipherKey = cipherKey;
	}

    /**
     * 모드에 따른 암복호화 처리기
     * @param mode Cipher.ENCRYPT_MODE / Cipher.DECRYPT_MODE
     * @return Cipher
     * @throws NoSuchPaddingException 
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeyException 
     * @since 2021.02.24
     */
    private Cipher getCipher(int mode) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        Key key = new SecretKeySpec(toBytes(cipherKey, 16), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(mode, key);

        return cipher;
    }

    /**
     * AES(aes-128-ecb)암호화
     * @param src 평문
     * @return 암호화 된 HEX문자열
     * @throws NoSuchPaddingException 
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeyException 
     * @throws BadPaddingException 
     * @throws IllegalBlockSizeException 
     * @since 2021.02.24
     */
    public String encrypt(String src) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
        byte[] plain = src.getBytes();
        byte[] encrypt = cipher.doFinal(plain);
        return toHexString(encrypt);
    }

    /**
     * AES(aes-128-ecb)복호화
     * @param hex 암호화 된 HEX문자열
     * @return 복호화 된 평문
     * @throws NoSuchPaddingException 
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeyException 
     * @throws BadPaddingException 
     * @throws IllegalBlockSizeException 
     * @since 2021.02.24
     */
    public String decrypt(String hex) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
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
     */
    private byte[] toBytes(String digits, int radix) {
        if (digits == null) {
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
    private byte[] toBytesFromHexString(String digits) {
        if (digits == null) {
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
    private String toHexString(byte[] bytes) {
        if (bytes == null) {
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
