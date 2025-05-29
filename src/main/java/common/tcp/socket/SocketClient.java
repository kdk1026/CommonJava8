package common.tcp.socket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.Objects;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * 소켓 클라이언트
 *  - Java 7 base: Try-with-resources
 * </pre>
 * @since 2018. 12. 22.
 * @author 김대광
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2018. 12. 22. 김대광	최초작성
 * 2021.  8. 14. 김대광	SonarLint 지시에 따른 수정
 * 2025.  5. 28. 김대광	제미나이에 의한 코드 대폭 개선
 * </pre>
 */
public class SocketClient {

	private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);

	private static final int TIMEOUT = 15*1000;		// 15초

    private String sRecvData;
    private String mScharsetName;

	public String getsRecvData() {
		return sRecvData;
	}

	/**
	 * 지정된 서버에 소켓 연결을 시작
	 * @param sServerIp
	 * @param nPort
	 * @param bSendData
	 * @param sCharsetName
	 * @param isSsl
	 * @param isTrust
	 * @param sslSocketTrustStoreVo
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws KeyStoreException
	 * @throws CertificateException
	 */
	public void start(String sServerIp, int nPort, byte[] bSendData, String sCharsetName,
			boolean isSsl, boolean isTrust, SslSocketTrustStoreVo sslSocketTrustStoreVo)
					throws IOException, NoSuchAlgorithmException, KeyManagementException, KeyStoreException, CertificateException {

		Objects.requireNonNull(sServerIp, "서버 IP는 null일 수 없습니다.");

		if (nPort <= 0 || nPort > 65535) {
            throw new IllegalArgumentException("유효하지 않은 포트 번호: " + nPort + ". 포트 번호는 1에서 65535 사이여야 합니다.");
        }

		Objects.requireNonNull(bSendData, "전송 데이터는 null일 수 없습니다.");

		if (sCharsetName == null || sCharsetName.trim().isEmpty()) {
            throw new IllegalArgumentException("문자셋 이름은 null이거나 비어 있을 수 없습니다.");
        }

		SocketAddress socketAddr = new InetSocketAddress(sServerIp, nPort);
		mScharsetName = sCharsetName;

		SSLContext sslContext = null;
		SSLSocketFactory sslSocketFactory = null;
		if (isSsl) {
			TrustManager[] trustManagers = null;

			if (isTrust) {
				Objects.requireNonNull(sslSocketTrustStoreVo, "신뢰할 수 있는 저장소 정보는 null일 수 없습니다.");
				Objects.requireNonNull(sslSocketTrustStoreVo.getTrustStorePath(), "신뢰할 수 있는 저장소 경로는 null일 수 없습니다.");
				Objects.requireNonNull(sslSocketTrustStoreVo.getTrustStorePassword(), "신뢰할 수 있는 저장소 비밀번호는 null일 수 없습니다.");

				KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
				try ( InputStream trustStoreStream = new FileInputStream(sslSocketTrustStoreVo.getTrustStorePath()) ) {
	                trustStore.load(trustStoreStream, sslSocketTrustStoreVo.getTrustStorePassword().toCharArray());
	            }

				TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
				tmf.init(trustStore);
				trustManagers = tmf.getTrustManagers();
			} else {
				// 명시적으로 TrustManager를 설정하지 않으면 JVM의 기본 TrustManager가 사용됩니다.
				// 이 기본 TrustManager는 JDK의 'cacerts' 파일에 있는 신뢰할 수 있는 CA 인증서를 기반으로 서버 인증서를 검증합니다.
				// 이는 일반적으로 안전하고 권장되는 방법입니다.
				trustManagers = null;
			}

            sslContext = SSLContext.getInstance("TLS");	// 또는 "TLSv1.2", "TLSv1.3"
            sslContext.init(null, trustManagers, new SecureRandom());
            sslSocketFactory = sslContext.getSocketFactory();
		}

		try (
				Socket socket = isSsl ? (SSLSocket) sslSocketFactory.createSocket() : new Socket()
		) {
			socket.connect(socketAddr, TIMEOUT);
            socket.setSoTimeout(TIMEOUT);

            if (isSsl) {
            	((SSLSocket) socket).startHandshake();
            	logger.info("[SSL 연결 완료: {}]", socket.getRemoteSocketAddress());
                logger.info("[암호화 스위트: {}]", ((SSLSocket) socket).getSession().getCipherSuite());
            } else {
            	logger.info("[일반 소켓 연결 완료: {}]", socket.getRemoteSocketAddress());
            }

            if ( socket.isConnected() ) {
            	try (
        			OutputStream os = socket.getOutputStream();
                    InputStream is = socket.getInputStream();
                    BufferedOutputStream bos = new BufferedOutputStream(os);
                    BufferedInputStream bis = new BufferedInputStream(is)
            	) {
            		this.sendToServer(bos, bSendData);
            		this.receivedFromServer(bis);
            	}
            }
		} finally {
			String connectionType = isSsl ? "SSL" : "일반 소켓";
            logger.info("[{} 연결 종료: {}]", connectionType, socketAddr);
		}
	}

	private void sendToServer(BufferedOutputStream bos, byte[] bSendData) throws IOException {
		bos.write(bSendData);
		bos.flush();

		String sSendData;
		try {
			sSendData = new String(bSendData, Charset.forName(mScharsetName));
		} catch (IllegalArgumentException e) {
            logger.warn("지원되지 않는 문자셋 이름: {}. 로깅을 위해 기본 문자셋을 사용합니다.", mScharsetName);
            sSendData = new String(bSendData, Charset.defaultCharset());
        }

        logger.info("[보내기 완료: {}]", sSendData);
	}

	private void receivedFromServer(BufferedInputStream bis) throws IOException {
		StringBuilder sb = new StringBuilder();
		byte[] buffer = new byte[4096];
        int nRead;

		while ( (nRead = bis.read(buffer)) != -1 ) {
			sb.append(new String(buffer, 0, nRead, Charset.forName(mScharsetName)));
			if (bis.available() == 0) {
				break;
			}
		}

		this.sRecvData = sb.toString();

		logger.info("[받기 완료: {}]", this.sRecvData);
	}

}
