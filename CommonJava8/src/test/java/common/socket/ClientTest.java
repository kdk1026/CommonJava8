package common.socket;

import java.nio.charset.StandardCharsets;

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
public class ClientTest {
	
	private static final Logger logger = LoggerFactory.getLogger(ClientTest.class);

	public static void main(String[] args) {
//		SocketClient client = new SocketClient();
//		NioSocketClient client = new NioSocketClient();
		
//		SocketClientThread client = new SocketClientThread();
		NioSocketClientThread client = new NioSocketClientThread();
		
		client.startClient("127.0.0.1", 9797, StandardCharsets.UTF_8.name());
		
		String sRecvData = "";
		JsonObject obj = new JsonObject();
		obj.addProperty("a", "가1");
		obj.addProperty("b", "나1");
		obj.addProperty("c", "다1");
		
		client.send(obj.toString().getBytes());
		sRecvData = client.receive();
		
		logger.debug("========== {}", sRecvData);
		
		obj = new JsonObject();
		obj.addProperty("aa", "가나1");
		obj.addProperty("bb", "다라1");
		obj.addProperty("cc", "마바1");
		
		client.send(obj.toString().getBytes());
		sRecvData = client.receive();
		
		logger.debug("========== {}", sRecvData);
		
		client.stopClient();
	}
	
}
