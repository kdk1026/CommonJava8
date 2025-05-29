package common.socket;

import java.nio.charset.Charset;

import common.tcp.socketchannel.multi.NioNonBlockingServerThread;

/**
 * @since 2018. 12. 23.
 * @author 김대광
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2018. 12. 23. 김대광	최초작성
 * </pre>
 */
public class ThreadServerTest {

	public static void main(String[] args) {
		NioNonBlockingServerThread server = new NioNonBlockingServerThread(null, null, 9797, Charset.forName("euc-kr").name());
		server.startServer();
	}

}
