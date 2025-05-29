package common.tcp.socketchannel;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Objects;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import common.tcp.socket.SslSocketTrustStoreVo;

/**
 * <pre>
 * NIO 소켓 클라이언트 (Blocking Mode)
 *  - Java 7 base: Try-with-resources
 * </pre>
 * @since 2018. 12. 22.
 * @author 김대광
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2018. 12. 22. 김대광	최초작성
 * 2021.  8. 14. 김대광	SonarLint 지시에 따른 수정
 * 2025.  5. 28. 김대광   제미나이에 의한 코드 대폭 개선
 * </pre>
 */
public class NioSocketClient {

	private static final Logger logger = LoggerFactory.getLogger(NioSocketClient.class);

	private static final int TIMEOUT = 20*1000;		// 20초

    private String sRecvData;
    private String mScharsetName;

	public String getsRecvData() {
		return sRecvData;
	}

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

		try ( SocketChannel socketChannel = SocketChannel.open() ) {

			socketChannel.configureBlocking(true);		// Default: true (Blocking Mode)

			Socket socket = socketChannel.socket();
			socket.connect(socketAddr, TIMEOUT);
			socket.setSoTimeout(TIMEOUT);

			logger.info("[연결 완료: {}]", socketChannel.getRemoteAddress());

			mScharsetName = sCharsetName;

			if (isSsl) {
				SSLSocket sslSocket = createSslSocket(socket, isTrust, sslSocketTrustStoreVo);
                sslSocket.startHandshake(); // SSL 핸드셰이크 시작
                logger.info("[SSL/TLS 핸드셰이크 완료: {}]", sslSocket.getRemoteSocketAddress());

                if (sslSocket.isConnected()) {
                    this.sendToServer(sslSocket, bSendData);
                    this.receivedFromServer(sslSocket);
                }
			} else {
				if (socketChannel.isConnected() && socketChannel.isOpen()) {
                    this.sendToServer(socketChannel, bSendData);
                    this.receivedFromServer(socketChannel);
                }
			}
		} catch (IOException e) {
			logger.error("", e);
		}
	}

	private void sendToServer(SocketChannel socketChannel, byte[] bSendData) throws IOException  {
		ByteBuffer buffer = ByteBuffer.wrap(bSendData);
		socketChannel.write(buffer);

		String sSendData = new String(bSendData, mScharsetName);
		logger.info("[보내기 완료: {}]", sSendData);
	}

	private void sendToServer(SSLSocket sslSocket, byte[] bSendData) throws IOException {
        sslSocket.getOutputStream().write(bSendData);
        sslSocket.getOutputStream().flush(); // 중요: SSL에서는 flush를 해주어야 합니다.

        String sSendData = new String(bSendData, mScharsetName);
        logger.info("[보내기 완료 (SSL): {}]", sSendData);
    }

	private void receivedFromServer(SocketChannel socketChannel) throws IOException {
		ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
		int bytesRead = socketChannel.read(byteBuffer);

		if (bytesRead == -1) {
            logger.warn("[받기 완료 (TCP): 서버에서 연결을 종료했습니다.]");
            this.sRecvData = "";
            return;
        }

		byteBuffer.flip();

		Charset charset = Charset.forName(mScharsetName);
		this.sRecvData = charset.decode(byteBuffer).toString();

		logger.info("[받기 완료: {}]", this.sRecvData);
	}

	private void receivedFromServer(SSLSocket sslSocket) throws IOException {
        byte[] buffer = new byte[4096];
        int bytesRead = sslSocket.getInputStream().read(buffer);

        if (bytesRead == -1) {
            logger.warn("[받기 완료 (SSL): 서버에서 연결을 종료했습니다.]");
            this.sRecvData = "";
            return;
        }

        this.sRecvData = new String(buffer, 0, bytesRead, mScharsetName);

        logger.info("[받기 완료 (SSL): {}]", this.sRecvData);
    }

	/**
     * SSLSocket을 생성하고 초기화합니다.
	 * @throws KeyManagementException
     */
    private SSLSocket createSslSocket(Socket socket, boolean isTrust, SslSocketTrustStoreVo sslSocketTrustStoreVo)
            throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, KeyManagementException {

        SSLContext sslContext;
        TrustManager[] trustManagers = null;
        if (isTrust) {
            if (sslSocketTrustStoreVo == null || sslSocketTrustStoreVo.getTrustStorePath() == null || sslSocketTrustStoreVo.getTrustStorePassword() == null) {
                throw new IllegalArgumentException("isTrust가 true인 경우, sslSocketTrustStoreVo 및 TrustStore 정보는 필수입니다.");
            }

            Objects.requireNonNull(sslSocketTrustStoreVo, "신뢰할 수 있는 저장소 정보는 null일 수 없습니다.");
			Objects.requireNonNull(sslSocketTrustStoreVo.getTrustStorePath(), "신뢰할 수 있는 저장소 경로는 null일 수 없습니다.");
			Objects.requireNonNull(sslSocketTrustStoreVo.getTrustStorePassword(), "신뢰할 수 있는 저장소 비밀번호는 null일 수 없습니다.");

            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            try ( FileInputStream fis = new FileInputStream(sslSocketTrustStoreVo.getTrustStorePath()) ) {
                trustStore.load(fis, sslSocketTrustStoreVo.getTrustStorePassword().toCharArray());
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

        sslContext = SSLContext.getInstance("TLS"); // 또는 "TLSv1.2", "TLSv1.3"
        sslContext.init(null, trustManagers, null);

        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        return (SSLSocket) sslSocketFactory.createSocket(socket, socket.getInetAddress().getHostAddress(), socket.getPort(), true);
    }

}
