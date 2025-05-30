package common.test;

import static org.junit.Assert.assertTrue;

import java.security.PrivateKey;
import java.security.PublicKey;

import org.junit.Test;

import common.util.crypto.rsa.RsaCryptoUtil;

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
public class TestRsaCrypto {

	@Test
	public void test() {
//		KeyPair keyPair = RsaCryptoUtil.Generate.generateKeyPair();
//		String publicKeyString = RsaCryptoUtil.Convert.getBase64PublicKey(keyPair);
//		String publicKeyString = RsaCryptoUtil.Convert.getBase64PrivateKey(keyPair);
		String base64PublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAueJUpTD1NXFqj3/D20okS+lekZ6LzvE5bIxL1r8uENKH2fvit6lmEiP5E76+rG7u9RY/L1Jp1ryvk1qEgxix4w9JCHARFNDNyzTccsom2lHLL77Kiatge7DwbVvHbMut1bRJI6mXSF//lQeuGAUYSUwQaV6ON7/Pguji+yrjxrv0cKsWs7/V8vsT7SZMdyMxVGubN1aYdnj3DwoQSixfVHLM0DZe2e4b7ns7D8qhDU75wX2QLUZFwx/XhjONoQj7rfBbUSfIa2RqBsltzVCA0+9aRKU7aC0+JUDBe4RW4hBW+JmEtm0ykCVrywZX7uij/s0sxWu8y785p0FE+n5LbQIDAQAB";
		String base64PrivateKey = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQC54lSlMPU1cWqPf8PbSiRL6V6RnovO8TlsjEvWvy4Q0ofZ++K3qWYSI/kTvr6sbu71Fj8vUmnWvK+TWoSDGLHjD0kIcBEU0M3LNNxyyibaUcsvvsqJq2B7sPBtW8dsy63VtEkjqZdIX/+VB64YBRhJTBBpXo43v8+C6OL7KuPGu/Rwqxazv9Xy+xPtJkx3IzFUa5s3Vph2ePcPChBKLF9UcszQNl7Z7hvuezsPyqENTvnBfZAtRkXDH9eGM42hCPut8FtRJ8hrZGoGyW3NUIDT71pEpTtoLT4lQMF7hFbiEFb4mYS2bTKQJWvLBlfu6KP+zSzFa7zLvzmnQUT6fkttAgMBAAECggEAD4AhINlRSVCY2ziDQ5EOL8pZGXmIHQyehj+4v0KX+80iiPnpMPmOSmr3hT79tXFWuddOE4siykZXucjtjeUMSGvo4iw+MGctEgnpbCURUllJwUR+rTY+SHI7ylLB8X+WkmCDTNcCh4WB0ZKfYN9j5BDUuYSqBVoYWslCzEEKV3MGQVeAlFGBuhZgIWESbQzPXpjrKBKaGWCeTgC0U8iWRfl44y/CXd+y9krihPMEeMg4N/DUc68npZxHny9ASXtWn/uf8YoCYaByTrEI6FZHZ8+oUXjAt0bBnQFUQh+QEM47WZxcrPypGskMA1/NyOk9/2vKViarKDPq6ahDqOuofQKBgQD4gIseRtfIgY4W8uCbJbB52lWA/rncSBpqZJmZWujh2KMQxVbOJw75/FJIBWjiOSxYSQWzWtif0RjrC509sPTQAPerHLYqy27NG8erx0c8zOjfYOurWhdHHm6JOwL2C/2dwRB/UVBXqDxpzfGUOcWjGnzvtDW4KbqJQ4UjoQpDfwKBgQC/fh55oNoXTJoWnM9UAAXhJgkXaTNIPoso0CXfy2KvcHy79vs5TkuhRGCnuVE8nOvS4g0idEqMAU0tndHIDqNpxnFQWvOQPqBoWmKCNIsI0B8pGDuvcFdpGhOZ2k0Xjj9/p1FDij5wCO2Dob6omTd6d3V9ktOt1nrGtQAJ30U3EwKBgB4yoLVBLp+y/q1TPnAcGGH2YkWWrsdSC8O6DeBQ59PqG4bCNFZA8NT4EJen8KLtB7KCzSqKc6CYLEsD0kvY4ScVtwMYLhrtNb2/DniVcta0n+flQz5DaGjADfanMSjtNBijiXmvS/AbBZxl5mF408eKRHxsjcqfZ4eeqOM3gqPJAoGAYrOrGZ4jS6ccNVOWOgHZBfgI75w15qL2L486NmQq9arRYEEptJv6t6D7APiwOsHXe9cwVfYDBq8VDNIv8yI4cKjsdzKqIEtneuI7rElQ8LX1mcT33rHVHENonwC6g826RR4E8P8st6qiYhrYnyZ6RhGU6ajoms3Wct2Qq4X9sykCgYB+y4nwFqfN0brIffLYQZDcZbaHL1II+yLvPBYMhCwbM0o/FFIcingrkKc5ivWCAh563RKfz596UKECWgfJCd9161Emorfap7WWxuQf9Gh4CUMHUMLs2cD1pNbnMFz8DesTrAZAKeMkB4Z/W5ru1JSHoc84t+MAGuDV55UOn2swHA==";

		//System.out.println(base64PublicKey);
		//System.out.println(base64PrivateKey);

		PublicKey publicKey = RsaCryptoUtil.Convert.getPublicKeyFromBase64(base64PublicKey);
		PrivateKey privateKey = RsaCryptoUtil.Convert.getPrivateKeyFromBase64(base64PrivateKey);

		String encrypt = RsaCryptoUtil.encrypt("안녕", publicKey, RsaCryptoUtil.RSA_ECB_PKCS1PADDING);
		System.out.println(encrypt);

		String decrypt = RsaCryptoUtil.decrypt(encrypt, privateKey, RsaCryptoUtil.RSA_ECB_PKCS1PADDING);
		System.out.println(decrypt);

		assertTrue(true);
	}

}
