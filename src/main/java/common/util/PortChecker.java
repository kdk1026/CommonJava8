package common.util;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

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

	private PortChecker() {
		super();
	}

	private static final Logger logger = LoggerFactory.getLogger(PortChecker.class);

	private static class LazyHolder {
		private static final PortChecker INSTANCE = new PortChecker();
	}

	public static PortChecker getInstance() {
		return LazyHolder.INSTANCE;
	}

	public boolean isConnected(String host, int port) {
		boolean isConnect = false;

		try ( SSLSocket sslSocket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(host, port) ) {

			isConnect = true;
		} catch (Exception e) {
			logger.error("", e);
		}

		return isConnect;
	}

}

