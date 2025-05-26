package common.util.crypto.seed;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * SEED ECB 암호화
 *
 * - 암호화 키
 * > 128비트(16자)
 *
 * - Base64
 * 	 > java 8
 * </pre>
 */
public class SeedEcbUtil {

	private SeedEcbUtil() {
		super();
	}

	private static final Logger logger = LoggerFactory.getLogger(SeedEcbUtil.class);

	/**
	 * @since 1.7
	 */
	private static final String UTF_8 = StandardCharsets.UTF_8.toString();

	/**
	 * SEED ECB 암호화 (Base64 인코딩)
	 * @param sPlainData
	 * @param sKey
	 * @return
	 * @throws IOException
	 */
	public static String seedEnc(String sPlainData, String sKey) {
		if ( StringUtils.isBlank(sPlainData) ) {
			throw new IllegalArgumentException("sPlainData is null");
		}

		if ( StringUtils.isBlank(sKey) ) {
			throw new IllegalArgumentException("sKey is null");
		}

		if ( sKey.length() < 16 ) {
			throw new IllegalArgumentException("sKey length is less than 16");
		}

		byte[] bKey	= sKey.getBytes();
		byte[] bCipher = null;
		byte[] bData = sPlainData.getBytes();

		bKey = setPadding(bKey, 16);
		int nDataLen = bData.length;
		bCipher = KISA_SEED_ECB.SEED_ECB_Encrypt(bKey, bData, 0, nDataLen);

		return new String(Base64.getEncoder().encode(bCipher));
	}

	/**
	 * SEED ECB 암호화 (URL 인코딩 + Base64 인코딩)
	 * @param sPlainData
	 * @param sKey
	 * @return
	 * @throws IOException
	 */
	public static String seedUrlEnc(String sPlainData, String sKey) {
		if ( StringUtils.isBlank(sPlainData) ) {
			throw new IllegalArgumentException("sPlainData is null");
		}

		if ( StringUtils.isBlank(sKey) ) {
			throw new IllegalArgumentException("sKey is null");
		}

		if ( sKey.length() < 16 ) {
			throw new IllegalArgumentException("sKey length is less than 16");
		}

		String sEncData = "";
		try {
			byte[] bKey	= sKey.getBytes();
			byte[] bCipher = null;
			byte[] bData = sPlainData.getBytes();

			bKey = setPadding(bKey, 16);
			int nDataLen = bData.length;
			bCipher = KISA_SEED_ECB.SEED_ECB_Encrypt(bKey, bData, 0, nDataLen);

			sEncData = new String(Base64.getEncoder().encode(bCipher));

			sEncData = URLEncoder.encode(sEncData, UTF_8);
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		}
		return sEncData;
	}

	/**
	 * SEED ECB 복호화 (Base64 디코딩)
	 * @param sEncData
	 * @param sKey
	 * @return
	 * @throws IOException
	 */
	public static String seedDec(String sEncData, String sKey) {
		if ( StringUtils.isBlank(sEncData) ) {
			throw new IllegalArgumentException("sEncData is null");
		}

		if ( StringUtils.isBlank(sKey) ) {
			throw new IllegalArgumentException("sKey is null");
		}

		if ( sKey.length() < 16 ) {
			throw new IllegalArgumentException("sKey length is less than 16");
		}

		byte[] bKey	= sKey.getBytes();
		byte[] bCipher = null;
		byte[] bPlain = null;

		bCipher = Base64.getDecoder().decode(sEncData);

		bKey = setPadding(bKey, 16);
		byte[] bData = bCipher;
		int nDataLen = bData.length;

		bPlain = KISA_SEED_ECB.SEED_ECB_Decrypt(bKey, bCipher, 0, nDataLen);
		return new String(bPlain);
	}

	/**
	 * SEED ECB 복호화 (URL 디코딩 + Base64 디코딩)
	 * @param sEncData
	 * @param sKey
	 * @return
	 * @throws IOException
	 */
	public static String seedUrlDec(String sEncData, String sKey) {
		if ( StringUtils.isBlank(sEncData) ) {
			throw new IllegalArgumentException("sEncData is null");
		}

		if ( StringUtils.isBlank(sKey) ) {
			throw new IllegalArgumentException("sKey is null");
		}

		if ( sKey.length() < 16 ) {
			throw new IllegalArgumentException("sKey length is less than 16");
		}

		String sPlainData = "";
		try {
			sEncData = URLDecoder.decode(sEncData, UTF_8);

			byte[] bKey	= sKey.getBytes();
			byte[] bCipher = null;
			byte[] bPlain = null;

			bCipher = Base64.getDecoder().decode(sEncData);

			bKey = setPadding(bKey, 16);
			byte[] bData = bCipher;
			int nDataLen = bData.length;

			bPlain = KISA_SEED_ECB.SEED_ECB_Decrypt(bKey, bCipher, 0, nDataLen);
			sPlainData = new String(bPlain);

		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		}
		return sPlainData;
	}

	public static byte[] setPadding(byte[] source, int blockSize) {
		if ( source == null || source.length == 0 ) {
			throw new IllegalArgumentException("source is null or empty");
		}

		if ( blockSize <= 0 ) {
			throw new IllegalArgumentException("blockSize is less than or equal to 0");
		}

		int sourceSize = source.length;
		byte[] dest = new byte[blockSize];	// Java byte[] default value = 0

		if (sourceSize < blockSize) {
			System.arraycopy(source, 0, dest, 0, sourceSize);
		}
		else if (sourceSize > blockSize) {
			System.arraycopy(source, 0, dest, 0, blockSize);
		}
		else {
			dest = source;
		}
		return dest;
	}

}
