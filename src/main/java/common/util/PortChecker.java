package common.util;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

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
			e.printStackTrace();
		}

		return isConnect;
	}

}

