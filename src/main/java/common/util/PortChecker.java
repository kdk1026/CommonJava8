package common.util;

import java.net.InetSocketAddress;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2024. 9. 6. 김대광	최초작성
 * </pre>
 *
 *
 * @author 김대광
 */
public class PortChecker {

	private final Logger logger = LoggerFactory.getLogger(PortChecker.class);

	private PortChecker() {
		super();
	}

	private static class LazyHolder {
		private static final PortChecker INSTANCE = new PortChecker();
	}

	public static PortChecker getInstance() {
		return LazyHolder.INSTANCE;
	}

	public boolean isConnected(String host, int port) {
		boolean isConnect = false;

		try {
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(host,port), 5000);
			socket.close();

			isConnect = true;
		} catch (Exception e) {
			logger.error("", e);
		}

		return isConnect;
	}

}

