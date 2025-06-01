package common.util.crypto.seed.cbc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 5. 31. kdk	최초작성
 * </pre>
 *
 * @author 김대광
 * @author kdk
 */
public class SeedCbcUtil {

	private SeedCbcUtil() {
		super();
	}

	private static final Logger logger = LoggerFactory.getLogger(SeedCbcUtil.class);

	private static final String UTF_8 = StandardCharsets.UTF_8.toString();

	private static final String KEY_IS_NULL = "sKey는 null일 수 없습니다.";
	private static final String KEY_SIZE_INVALID = "sKey 길이는 16보다 작을 수 없습니다.";

	// 패딩 함수는 SEED의 블록 크기인 16바이트에 맞춰 PKCS7 패딩을 적용합니다.
    private static byte[] setPadding(byte[] data, int blockSize) {
        int paddingLength = blockSize - (data.length % blockSize);
        byte[] paddedData = new byte[data.length + paddingLength];
        System.arraycopy(data, 0, paddedData, 0, data.length);
        for (int i = 0; i < paddingLength; i++) {
            paddedData[data.length + i] = (byte) paddingLength;
        }
        return paddedData;
    }

	/**
	 * SEED CBC 암호화 (Base64 인코딩)
	 * @param sPlainData
	 * @param sKey
	 * @return
	 * @throws IOException
	 */
	public static String seedEnc(String sPlainData, String sKey) {
		Objects.requireNonNull(sPlainData, "sPlainData는 null일 수 없습니다.");
		if ( sPlainData.trim().isEmpty() ) {
		    throw new IllegalArgumentException("sPlainData는 비어있을 수 없습니다.");
		}

		Objects.requireNonNull(sKey, KEY_IS_NULL);
		if ( sKey.length() < 16 ) {
		    throw new IllegalArgumentException(KEY_SIZE_INVALID);
		}

		byte[] bKey = sKey.getBytes();
        byte[] bCipher = null;
        byte[] bData = sPlainData.getBytes();

        byte[] bIV = new byte[16];
        System.arraycopy(bKey, 0, bIV, 0, Math.min(bKey.length, 16));

        bData = setPadding(bData, 16);

        int nDataOffset = 0;
        int nDataLength = bData.length;

        bCipher = KISA_SEED_CBC.SEED_CBC_Encrypt(bKey, bIV, bData, nDataOffset, nDataLength);

		return new String(Base64.getEncoder().encode(bCipher));
	}

	/**
	 * SEED CBC 암호화 (URL 인코딩 + Base64 인코딩)
	 * @param sPlainData
	 * @param sKey
	 * @return
	 * @throws IOException
	 */
	public static String seedUrlEnc(String sPlainData, String sKey) {
		Objects.requireNonNull(sPlainData, "sPlainData는 null일 수 없습니다.");
		if ( sPlainData.trim().isEmpty() ) {
		    throw new IllegalArgumentException("sPlainData는 비어있을 수 없습니다.");
		}

		Objects.requireNonNull(sKey, KEY_IS_NULL);
		if ( sKey.length() < 16 ) {
		    throw new IllegalArgumentException(KEY_SIZE_INVALID);
		}

		byte[] bKey = sKey.getBytes();
        byte[] bCipher = null;
        byte[] bData = sPlainData.getBytes();

        byte[] bIV = new byte[16];
        System.arraycopy(bKey, 0, bIV, 0, Math.min(bKey.length, 16));

        bData = setPadding(bData, 16);

        int nDataOffset = 0;
        int nDataLength = bData.length;

        bCipher = KISA_SEED_CBC.SEED_CBC_Encrypt(bKey, bIV, bData, nDataOffset, nDataLength);

        String sEncData = new String(Base64.getEncoder().encode(bCipher));

        try {
        	sEncData = URLEncoder.encode(sEncData, UTF_8);
		} catch (UnsupportedEncodingException e) {
			logger.error("URL 인코딩 중 오류 발생", e);
		}

		return sEncData;
	}

	// PKCS7Padding을 제거하는 메소드 (복호화 후 필요)
    private static byte[] removePadding(byte[] paddedData) {
        if (paddedData == null || paddedData.length == 0) {
            return paddedData;
        }
        int paddingLength = paddedData[paddedData.length - 1];
        // 패딩 길이가 유효한지 확인 (1~16 사이)
        if (paddingLength < 1 || paddingLength > 16 || paddingLength > paddedData.length) {
            // 유효하지 않은 패딩: 데이터를 그대로 반환하거나 예외를 발생시킬 수 있습니다.
            // 여기서는 원본 데이터를 그대로 반환합니다.
            return paddedData;
        }
        // 패딩 바이트들이 모두 동일한지 확인 (PKCS7)
        for (int i = 0; i < paddingLength; i++) {
            if (paddedData[paddedData.length - 1 - i] != paddingLength) {
                // 유효하지 않은 패딩: 데이터를 그대로 반환합니다.
                return paddedData;
            }
        }
        byte[] originalData = new byte[paddedData.length - paddingLength];
        System.arraycopy(paddedData, 0, originalData, 0, originalData.length);
        return originalData;
    }

	/**
	 * SEED CBC 복호화 (Base64 디코딩)
	 * @param sEncData
	 * @param sKey
	 * @return
	 * @throws IOException
	 */
	public static String seedDec(String sEncData, String sKey) {
		Objects.requireNonNull(sEncData, "sEncData는 null일 수 없습니다.");
		if ( sEncData.trim().isEmpty() ) {
		    throw new IllegalArgumentException("sEncData는 비어있을 수 없습니다.");
		}

		Objects.requireNonNull(sKey, KEY_IS_NULL);
		if ( sKey.length() < 16 ) {
		    throw new IllegalArgumentException(KEY_SIZE_INVALID);
		}

		byte[] bKey = sKey.getBytes();
        byte[] bCipher = null;
        byte[] bPlain = null;

		bCipher = Base64.getDecoder().decode(sEncData);

		byte[] bIV = new byte[16];
        System.arraycopy(bKey, 0, bIV, 0, Math.min(bKey.length, 16));

		bPlain = KISA_SEED_CBC.SEED_CBC_Decrypt(bKey, bIV, bCipher, 0, bCipher.length);

		bPlain = removePadding(bPlain);
		return new String(bPlain);
	}

	/**
	 * SEED CBC 복호화 (URL 디코딩 + Base64 디코딩)
	 * @param sEncData
	 * @param sKey
	 * @return
	 * @throws IOException
	 */
	public static String seedUrlDec(String sEncData, String sKey) {
		Objects.requireNonNull(sEncData, "sEncData는 null일 수 없습니다.");
		if ( sEncData.trim().isEmpty() ) {
		    throw new IllegalArgumentException("sEncData는 비어있을 수 없습니다.");
		}

		Objects.requireNonNull(sKey, KEY_IS_NULL);
		if ( sKey.length() < 16 ) {
		    throw new IllegalArgumentException(KEY_SIZE_INVALID);
		}

		byte[] bKey = sKey.getBytes();
        byte[] bCipher = null;
        byte[] bPlain = null;

		String sPlainData = "";
		try {
			sEncData = URLDecoder.decode(sEncData, UTF_8);

			bCipher = Base64.getDecoder().decode(sEncData);

			byte[] bIV = new byte[16];
	        System.arraycopy(bKey, 0, bIV, 0, Math.min(bKey.length, 16));

			bPlain = KISA_SEED_CBC.SEED_CBC_Decrypt(bKey, bIV, bCipher, 0, bCipher.length);

			bPlain = removePadding(bPlain);

			sPlainData = new String(bPlain, UTF_8);

		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		}
		return sPlainData;
	}

}
