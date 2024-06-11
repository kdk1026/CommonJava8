package common.socket;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import common.tcp.socketchannel.multi.NioSocketClientThread;

/**
 * @since 2018. 12. 23.
 * @author 김대광
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2018. 12. 23. 김대광	최초작성
 * </pre>
 */
public class ThreadClientTest {
	
	private static final Logger logger = LoggerFactory.getLogger(ThreadClientTest.class);

	public static void main(String[] args) {
		String sCharsetName = Charset.forName("euc-kr").name();
		
		JsonObject obj = null;
		byte[] bSendData = null;
		String sRecvData = null;
		
//		SocketClientThread client = new SocketClientThread();
		NioSocketClientThread client = new NioSocketClientThread();
		
		client.startClient("127.0.0.1", 9797, sCharsetName);
		
		try {
			obj = new JsonObject();
			obj.addProperty("aa", "가나1");
			obj.addProperty("bb", "다라1");
			obj.addProperty("cc", "마바1");
			
			bSendData = obj.toString().getBytes(sCharsetName);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		client.send(bSendData);
		sRecvData = client.receive();
		
		logger.debug("========== {}", sRecvData);
		
		
		try {
			obj = new JsonObject();
			obj.addProperty("aa", "가나1");
			obj.addProperty("bb", "다라1");
			obj.addProperty("cc", "마바1");
			
			bSendData = obj.toString().getBytes(sCharsetName);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		client.send(bSendData);
		sRecvData = client.receive();
		
		logger.debug("========== {}", sRecvData);
		
		client.stopClient();
	}
	
}
