package common.libTest.commons;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.BinaryCodec;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

public class UsageCodec {

	private static String ENCODED_TEXT = "";
	private static String DECODED_TExT = "";
	private static final String PLAIN_TEXT = "admin!@34";

	@Test
	public void test() {
		//base64Codec();
		//urlCodec();
		hashing();
		//checkSum();
		//hex();
		//binary();
	}

	/**
	 * <pre>
	 * Bye~ Bye~
	 * Bye? 임창정 Bye ~~ ㅋ
	 * 
	 * 	[Java 8]
	 * 		Base64.getEncoder().encodeToString(byte[] src);
	 * 		new String(Base64.getDecoder().decode(String src));
	 * </pre>
	 */
	public static void base64Codec() {
		// encode
		ENCODED_TEXT = new String(Base64.encodeBase64(PLAIN_TEXT.getBytes()));

		// decode
		DECODED_TExT = new String(Base64.decodeBase64(ENCODED_TEXT.getBytes()));

		System.out.println("Base64 ENCODED_TEXT :: " + ENCODED_TEXT);
		System.out.println("Base64 DECODED_TExT :: " + DECODED_TExT);
	}

	/**
	 * <pre>
	 *  [Java]
	 *  	URLEncoder.encode(String s)
	 *  	URLEncoder.encode(String s, String enc)
	 *  
	 *  	URLDecoder.decode(String s)
	 *  	URLDecoder.decode(String s, String enc)
	 *  </pre>
	 *  
	 *  아.. Java 1부터 지원이였어 ㅋ 사용 방법은 아파치 재단이 편함
	 */
	public static void urlCodec() {
		URLCodec urlCodec = new URLCodec();

		try {
			// encode
			/*
			 * urlCodec.encode(str, charset);
			 */
			ENCODED_TEXT = urlCodec.encode(PLAIN_TEXT);

			// decode
			/*
			 * urlCodec.decode(str, charset);
			 */
			DECODED_TExT = urlCodec.decode(ENCODED_TEXT);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("URLCodec ENCODED_TEXT :: " + ENCODED_TEXT);
		System.out.println("URLCodec DECODED_TExT :: " + DECODED_TExT);
	}

	public static void hashing() {
		/*
		 * 해싱의 경우, 반듯이 소금을 팍팍 쳐줘야 한다. 안쳐주면 브루트 포스(무작위 공격)에 걸려든다.
		 */
		String sSalt = RandomStringUtils.randomAscii(20);
		
		String strMd5 = DigestUtils.md5Hex(PLAIN_TEXT + sSalt);
		String strSha256 = DigestUtils.sha256Hex(PLAIN_TEXT + sSalt);
		String strSha512 = DigestUtils.sha512Hex(PLAIN_TEXT + sSalt);

		System.out.println("DigestUtils MD5 String :: " + strMd5);
		System.out.println("DigestUtils SHA256  String :: " + strSha256);
		System.out.println("DigestUtils SHA512  String :: " + strSha512);
		
		System.out.println("Check MD5 hashing :: " + strMd5.equals(DigestUtils.md5Hex(PLAIN_TEXT + sSalt)));
		System.out.println("Check MD5 SHA256 :: " + strSha256.equals(DigestUtils.sha256Hex(PLAIN_TEXT + sSalt)));
		System.out.println("Check MD5 SHA512 :: " + strSha512.equals(DigestUtils.sha512Hex(PLAIN_TEXT + sSalt)));
	}

	public static void checkSum() {
		String sFileNm = "C:\\test\\me.before.you.2016.1080p.bluray.x264-drones[prime].smi";
		File file = new File(sFileNm);
		try {
			InputStream is = new BufferedInputStream(new FileInputStream(file));
			String ChecksumMD5 = DigestUtils.md5Hex(is);
			String ChecksumMSHA1 = DigestUtils.sha1Hex(is);

			System.out.println("DigestUtils MD5 File Checksum :: " + ChecksumMD5);
			System.out.println("DigestUtils SHA1 File Checksum :: " + ChecksumMSHA1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void hex() {
		try {
			// encode
			ENCODED_TEXT = new String(Hex.encodeHex(PLAIN_TEXT.getBytes()));

			// decode
			DECODED_TExT = new String(Hex.decodeHex(ENCODED_TEXT.toCharArray()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Hex ENCODED_TEXT :: " + ENCODED_TEXT);
		System.out.println("Hex DECODED_TExT :: " + DECODED_TExT);
	}

	public static void binary() {
		BinaryCodec binaryCodec = new BinaryCodec();

		// encode
		ENCODED_TEXT = new String(binaryCodec.encode(PLAIN_TEXT.getBytes()));

		// decode
		DECODED_TExT = new String(binaryCodec.decode(ENCODED_TEXT.getBytes()));

		System.out.println("Binary ENCODED_TEXT :: " + ENCODED_TEXT);
		System.out.println("Binary DECODED_TExT :: " + DECODED_TExT);
	}

}
