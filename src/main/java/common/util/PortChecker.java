package common.util;

import java.io.IOException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2024. 9. 6.  김대광	최초작성
 * 2025. 5. 18. 김대광	AI가 추천한 Singleton 패턴으로 변경
 * 2025. 5. 27. 김대광	유틸은 Singleton 패턴을 사용하지 않는 것이 좋다는 의견 반영
 * </pre>
 *
 *
 * @author 김대광
 */
public class PortChecker {

	private static final Logger logger = LoggerFactory.getLogger(PortChecker.class);

	private PortChecker() {
		super();
	}

	private static class ExceptionMessage {

		public static String isNullOrEmpty(String paramName) {
	        return String.format("'%s' is null or empty", paramName);
	    }

	}

	public static boolean isConnected(String host, int port) {
		if ( StringUtils.isBlank(host) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("host"));
		}

		if ( port < 0 || port > 65535 ) {
			throw new IllegalArgumentException("port must be between 0 and 65535");
		}

		boolean isConnect = false;

		try ( SSLSocket sslSocket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(host, port) ) {

			isConnect = true;
		} catch (IOException e) {
			logger.error("", e);
		}

		return isConnect;
	}

}

