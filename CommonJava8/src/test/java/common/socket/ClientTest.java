package common.socket;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import com.google.gson.JsonObject;

import common.tcp.socketchannel.NioSocketClient;

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
		String sCharsetName = Charset.forName("utf-8").name();
		
		JsonObject obj = new JsonObject();
		obj.addProperty("a", "가나다");
		obj.addProperty("b", "라마바");
		obj.addProperty("c", "사아자");
		
		byte[] bSendData = null;
		
		try {
			if ( sCharsetName != Charset.defaultCharset().name() ) {
				bSendData = obj.toString().getBytes(sCharsetName);
			} else {
				bSendData = obj.toString().getBytes();
			}
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		
//		SocketClient client = new SocketClient();
		NioSocketClient client = new NioSocketClient();
		try {
			client.start("127.0.0.1", 9797, bSendData, sCharsetName);
			
			System.out.println( client.getsRecvData() );
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
}
