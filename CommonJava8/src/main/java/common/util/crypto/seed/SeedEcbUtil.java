package common.util.crypto.seed;

import java.io.IOException;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeedEcbUtil {
	
	private SeedEcbUtil() {
		super();
	}

	private static final Logger logger = LoggerFactory.getLogger(SeedEcbUtil.class);

	/**
	 * SEED ECB 암호화 (Base64 인코딩)
	 * @param sPlainData
	 * @param sKey
	 * @return
	 * @throws IOException
	 */
	public static String seedEnc(String sPlainData, String sKey) {
		String sEncData = "";
		try {
			byte[] bKey	= sKey.getBytes();
			byte[] bCipher = new byte[50];
			byte[] bData = sPlainData.getBytes();

			bKey = setPadding(bKey, 16);
			int nDataLen = bData.length;
			bCipher = KISA_SEED_ECB.SEED_ECB_Encrypt(bKey, bData, 0, nDataLen);

			sEncData = DatatypeConverter.printBase64Binary(bCipher);
		} catch (Exception e) {
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
		String sPlainData = "";
		try {
			byte[] bKey	= sKey.getBytes();
			byte[] bCipher = new byte[50];
			byte[] bPlain = new byte[16];
			
			bCipher = DatatypeConverter.parseBase64Binary(sEncData);
			
			bKey = setPadding(bKey, 16);
			byte[] bData = bCipher;
			int nDataLen = bData.length;
			
			bPlain = KISA_SEED_ECB.SEED_ECB_Decrypt(bKey, bCipher, 0, nDataLen);
			sPlainData = new String(bPlain);
			
		} catch (Exception e) {
			logger.error("", e);
		}
		return sPlainData;
	}
	
	public static byte[] setPadding(byte[] source, int blockSize) {
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
