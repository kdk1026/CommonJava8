package common.socket;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import com.google.gson.JsonObject;

import common.tcp.socket.SocketClient;

/**
 * @since 2018. 12. 23.
 * @author 김대광
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2018. 12. 23. 김대광	최초작성
 * </pre>
 */
public class ClientTest {

	public static void main(String[] args) {
		String sCharsetName = Charset.forName("euc-kr").name();

		JsonObject obj = new JsonObject();
		obj.addProperty("a", "가나다");
		obj.addProperty("b", "라마바");
		obj.addProperty("c", "사아자");

		byte[] bSendData = null;
		try {
			bSendData = obj.toString().getBytes(sCharsetName);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		SocketClient client = new SocketClient();
//		NioSocketClient client = new NioSocketClient();
		try {
			client.start("127.0.0.1", 9797, bSendData, sCharsetName, false, false, null);

		} catch (IOException | KeyManagementException | NoSuchAlgorithmException | KeyStoreException | CertificateException e) {
			e.printStackTrace();
		}

	}

}
