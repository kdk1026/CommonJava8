package common.tcp.socket.multi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * 소켓 서버
 *  - Multi Thread
 * </pre>
 * @since 2018. 12. 23.
 * @author 김대광
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2018. 12. 23. 김대광	최초작성
 * 2021.  8. 14. 김대광	SonarLint 지시에 따른 주저리 주저리
 * 2025.  5. 28. 김대광	제미나이에 의한 코드 대폭 개선 (SSL 의무화에 따른 소켓 서버는 SSLServerSocket 으로만 처리)
 * </pre>
 */
public class SocketServerRunnable {

	private static final Logger logger = LoggerFactory.getLogger(SocketServerRunnable.class);

	private ExecutorService mExecutorService;
	private SSLServerSocket mServerSocket;
	private final Set<ClientHandler> mConnections = Collections.synchronizedSet(new HashSet<>()); // 동기화된 Set 사용

	private String mCharsetName;
	private String mKeyStorePath;
    private char[] mKeyStorePassword;

    public SocketServerRunnable(String keyStorePath, String keyStorePassword) {
    	this.mKeyStorePath = keyStorePath;
    	this.mKeyStorePassword = keyStorePassword.toCharArray();
    }

	public void startServer(int nPort, String sCharsetName) {
		// 스레드 풀 생성
		mExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		mCharsetName = sCharsetName;

		try {
			SSLContext sslContext = createSSLContext();
            SSLServerSocketFactory ssf = sslContext.getServerSocketFactory();
            mServerSocket = (SSLServerSocket) ssf.createServerSocket(nPort);

            logger.info("[서버 시작] 포트: {}", nPort);

            // 람다 대신 익명 클래스 사용
            mExecutorService.submit(new Runnable() {
                @Override
                public void run() {
                    while (!mServerSocket.isClosed()) {
                        try {
                            SSLSocket socket = (SSLSocket) mServerSocket.accept();
                            logger.info("[연결 수락: {} : {}]", socket.getInetAddress().getHostAddress(), Thread.currentThread().getName());

                            ClientHandler client = new ClientHandler(socket, mCharsetName, mExecutorService);
                            mConnections.add(client);
                            client.startHandling(new Runnable() {
                                @Override
                                public void run() {
                                    removeClient(client);
                                }
                            });

                            logger.info("[현재 연결 개수: {}]", mConnections.size());

                        } catch (SocketException se) {
                            if (!mServerSocket.isClosed()) {
                                logger.error("서버 소켓 오류 발생: {}", se.getMessage());
                            }
                            break;
                        } catch (IOException e) {
                            logger.error("클라이언트 연결 수락 중 오류 발생", e);
                        }
                    }
                    logger.info("[서버 수락 루프 종료]");
                }
            });

        } catch (Exception e) {
            logger.error("서버 시작 중 치명적인 오류 발생", e);
            stopServer();
        }
	}

	public void stopServer() {
		logger.info("[서버 종료 중]");
		mConnections.forEach(new java.util.function.Consumer<ClientHandler>() {
            @Override
            public void accept(ClientHandler clientHandler) {
                clientHandler.close();
            }
        });
        mConnections.clear();

        try {
            if ( mServerSocket != null && !mServerSocket.isClosed() ) {
                mServerSocket.close();
            }
        } catch (IOException e) {
            logger.error("서버 소켓 닫는 중 오류 발생", e);
        } finally {
            if ( mExecutorService != null && !mExecutorService.isShutdown() ) {
                mExecutorService.shutdownNow();
            }
            logger.info("[서버 종료 완료]");
        }
	}

	private SSLContext createSSLContext() throws IOException, KeyStoreException, NoSuchAlgorithmException,
		CertificateException, UnrecoverableKeyException, KeyManagementException {

        KeyStore keyStore = KeyStore.getInstance("JKS");
        try (InputStream ksIs = new FileInputStream(mKeyStorePath)) {
            keyStore.load(ksIs, mKeyStorePassword);
        }

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, mKeyStorePassword);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), null, null);
        return sslContext;
    }

    private void removeClient(ClientHandler client) {
        mConnections.remove(client);
        logger.info("[클라이언트 연결 종료: {}] 현재 연결 개수: {}", client.getRemoteAddress(), mConnections.size());
    }

	class ClientHandler implements Runnable {
		private SSLSocket mmSocket;
        private String mmSremoteAddr;
        private String mCharsetName;
        private ExecutorService mClientExecutorService;
        private volatile boolean isRunning = true; // 스레드 종료를 위한 플래그
        private Runnable onClientClosed; // 클라이언트 종료 시 호출될 콜백

        public ClientHandler(SSLSocket sock, String charsetName, ExecutorService clientExecutorService) {
        	this.mmSocket = sock;
            this.mmSremoteAddr = sock.getInetAddress().getHostAddress();
            this.mCharsetName = charsetName;
            this.mClientExecutorService = clientExecutorService;
        }

        public String getRemoteAddress() {
            return this.mmSremoteAddr;
        }

        public void startHandling(Runnable onClientClosed) {
            this.onClientClosed = onClientClosed;
            mClientExecutorService.submit(this); // Runnable의 run 메서드를 실행
        }

		@Override
		public void run() {
			logger.info("[클라이언트 핸들러 시작: {}]", mmSremoteAddr);
			try {
				// SSL 핸드쉐이크 수행 (필요한 경우)
                mmSocket.startHandshake();
                logger.info("[SSL 핸드쉐이크 완료: {}]", mmSremoteAddr);

                while ( isRunning && !mmSocket.isClosed() ) {
                	try {
                		String receivedData = receive();
                		if ( receivedData == null || receivedData.isEmpty() ) {
                			logger.info("[클라이언트 {} 연결 종료 감지]", mmSremoteAddr);
                			break;
                		}

                		logger.info("[요청 처리: {}]", mmSremoteAddr);
                        logger.info("[요청 데이터: {}]", receivedData);

                        String responseMsg = receivedData;
                        logger.info("[응답 데이터: {}]", responseMsg);

                        send(responseMsg.getBytes(Charset.forName(mCharsetName)));

                	} catch (SocketException se) {
                        logger.info("[클라이언트 {} 연결 강제 종료됨: {}]", mmSremoteAddr, se.getMessage());
                        break;
                    } catch (IOException e) {
                        logger.error("[클라이언트 {} 통신 오류 발생]", mmSremoteAddr, e);
                        break;
                    } catch (Exception e) {
                        logger.error("[클라이언트 {} 처리 중 예상치 못한 오류 발생]", mmSremoteAddr, e);
                        break;
                    }
                }
			} catch (IOException e) {
				logger.error("[클라이언트 {} 핸들러 오류 발생]", mmSremoteAddr, e);
			} finally {
				close(); // 루프 종료 시 소켓 닫기
				if (onClientClosed != null) {
                    onClientClosed.run(); // 클라이언트 종료 콜백 호출
                }
                logger.info("[클라이언트 핸들러 종료: {}]", mmSremoteAddr);
			}
		}

        private String receive() throws IOException {
            StringBuilder sb = new StringBuilder();
            try (
            		InputStream is = mmSocket.getInputStream();
            		BufferedInputStream bis = new BufferedInputStream(is)
            ) {

                byte[] buffer = new byte[4096];
                int nRead = bis.read(buffer); // read(byte[])는 EOF에 도달하면 -1 반환
                if (nRead == -1) { // 클라이언트가 연결을 정상적으로 닫음
                    return null;
                }
                if (nRead > 0) {
                    sb.append(new String(buffer, 0, nRead, mCharsetName));
                }
            }
            return sb.toString();
        }

        private void send(byte[] bSendData) throws IOException {
            try (
            		OutputStream os = mmSocket.getOutputStream();
            		BufferedOutputStream bos = new BufferedOutputStream(os)
            ) {
                bos.write(bSendData);
                bos.flush();
            }
        }

        public void close() {
            isRunning = false; // 스레드 종료 플래그 설정
            try {
                if (mmSocket != null && !mmSocket.isClosed()) {
                    mmSocket.close();
                }
            } catch (IOException e) {
                logger.error("[클라이언트 {} 소켓 닫는 중 오류 발생]", mmSremoteAddr, e);
            }
        }
	}

}
