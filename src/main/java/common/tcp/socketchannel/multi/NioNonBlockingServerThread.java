package common.tcp.socketchannel.multi;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * NIO 소켓 서버 (Non Blocking Mode)
 *  - Multi Thread
 * </pre>
 * @since 2018. 12. 23.
 * @author 김대광
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2018. 12. 23. 김대광	최초작성
 * 2021.  8. 14. 김대광	SonarLint 지시에 따른 주저리 주저리
 * 2025.  5. 29. 김대광	제미나이에 의한 코드 대폭 개선 (SSL 의무화에 따라 ServerSocketChannel 으로만 처리)
 * </pre>
 */
public class NioNonBlockingServerThread {

	private static final Logger logger = LoggerFactory.getLogger(NioNonBlockingServerThread.class);

	private Selector mSelector;
    private ServerSocketChannel mServerSocketChannel;

    private ExecutorService handshakeTaskExecutor = Executors.newCachedThreadPool();
    private List<Client> mConnections = new CopyOnWriteArrayList<>(); // ArrayList는 멀티스레드 환경에서 안전하지 않습니다.

    private Thread serverThread;

    private int mPort;
    private SSLContext mSslContext;
    private String mCharsetName;
    private String mKeyStorePath;
    private char[] mKeyStorePassword;

    public NioNonBlockingServerThread(String keyStorePath, String keyStorePassword, int nPort, String sCharsetName) {
    	Objects.requireNonNull(keyStorePath, "키 저장소 경로를 지정해야 합니다.");
    	Objects.requireNonNull(keyStorePassword, "키 저장소 비밀번호를 지정해야 합니다.");

    	if (nPort <= 0 || nPort > 65535) {
    		throw new IllegalArgumentException("유효하지 않은 포트 번호: " + nPort + ". 포트 번호는 1에서 65535 사이여야 합니다.");
        }

    	Objects.requireNonNull(sCharsetName, "문자셋 이름은 null일 수 없습니다.");

    	this.mKeyStorePath = keyStorePath;
    	this.mKeyStorePassword = keyStorePassword.toCharArray();
    	this.mPort = nPort;
    	this.mCharsetName = sCharsetName;
    }

	public void startServer() {
		try {
			mSslContext = createSSLContext();

			mSelector = Selector.open();
			mServerSocketChannel = ServerSocketChannel.open();
            mServerSocketChannel.configureBlocking(false);
            mServerSocketChannel.bind(new InetSocketAddress(this.mPort));
            mServerSocketChannel.register(mSelector, SelectionKey.OP_ACCEPT);

        } catch (IOException | KeyStoreException | NoSuchAlgorithmException |
        		CertificateException | UnrecoverableKeyException | KeyManagementException e) {
            logger.error("서버 시작 중 치명적인 오류 발생: {}", e.getMessage(), e);
            stopServer();
        }

		serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                logger.info("[서버 시작]");

                while ( !Thread.currentThread().isInterrupted() ) { // 스레드 인터럽트 여부 확인
                	boolean terminateOuterLoop = true;

                    try {
                        int nKeyCnt = mSelector.select();

                        if (nKeyCnt > 0) {
                        	Set<SelectionKey> selectedKeys = mSelector.selectedKeys();
                        	Iterator<SelectionKey> it = selectedKeys.iterator();

                        	while (it.hasNext()) {
                        		SelectionKey selectionKey = it.next();
                        		it.remove(); // 처리된 키는 즉시 제거

                        		boolean isSuccess = processSelectionKey(selectionKey);
                        		if (!isSuccess) {
                        			break;
                        		}
                        	}
                        }

                    } catch (IOException e) {
                        logger.error("셀렉터 작업 중 오류 발생: {}", e.getMessage(), e);
                        stopServer();
                        terminateOuterLoop = false;
                    }

                    if (!terminateOuterLoop) {
                    	break;
                    }
                }
                logger.info("[서버 루프 종료]");
            }
        });

		serverThread.setName("NioNonBlockingServer-Thread");
		serverThread.setDaemon(true); // 애플리케이션 종료 시 함께 종료되도록 데몬 스레드로 설정
		serverThread.start();
	}

	private boolean processSelectionKey(SelectionKey selectionKey) {
        try {
            if (selectionKey.isAcceptable()) {
                accept(selectionKey);
            } else if (selectionKey.isReadable()) {
                Client client = (Client) selectionKey.attachment();
                client.receive(selectionKey);
            } else if (selectionKey.isWritable()) {
                Client client = (Client) selectionKey.attachment();
                client.send(selectionKey);
            }

        } catch (Exception e) {
            logger.error("클라이언트 처리 중 오류 발생: {}", e.getMessage(), e);
            if (selectionKey.isValid()) {
                selectionKey.cancel(); // 유효한 키만 취소
            }

            if (selectionKey.attachment() instanceof Client) {
                Client client = (Client) selectionKey.attachment();
                client.closeClient(); // 클라이언트 자원 정리 (내부에서 mConnections.remove 처리)
            } else if (selectionKey.isAcceptable()) {
                // accept 실패 시 서버 전체 종료를 고려 (예: 포트 충돌 등)
                logger.error("연결 수락 중 치명적인 오류 발생. 서버 종료를 시도합니다.", e);
                stopServer();
                return false;
            }
        }

        return true;
    }

	public void stopServer() {
		logger.info("[서버 종료 시도]");

		try {
	        // 모든 연결된 클라이언트 종료
	        for (Client client : mConnections) { // for-each 루프 사용
	        	closeClientSafely(client);
	        }
            mConnections.clear();	// 혹시 남아있을 수 있는 요소 정리

            if ( mServerSocketChannel != null && mServerSocketChannel.isOpen() ) {
                mServerSocketChannel.close();
            }

            if ( mSelector != null && mSelector.isOpen() ) {
                mSelector.close();
            }

			if ( serverThread != null && serverThread.isAlive() ) {
				serverThread.interrupt(); // 스레드 인터럽트
			}

	        if (handshakeTaskExecutor != null) {
	        	shutdownAndAwaitTermination(handshakeTaskExecutor, 5, TimeUnit.SECONDS);
	        }

	        mKeyStorePath = null;
	        mKeyStorePassword = null;

            logger.info("[서버 종료 완료]");
		} catch (IOException e) {
			logger.error("서버 종료 중 오류 발생: {}", e.getMessage(), e);
		}
	}

    /**
     * handshakeTaskExecutor를 안전하게 종료하는 메서드
     *
     * @param executor 종료할 ExecutorService
     * @param timeout  종료 대기 시간
     * @param unit     시간 단위
     */
    private void shutdownAndAwaitTermination(ExecutorService executor, long timeout, TimeUnit unit) {
        // ExecutorService에 새로운 작업이 제출되는 것을 막고, 현재 실행 중인 작업을 완료시킵니다.
        executor.shutdown();
        try {
            // 스레드 풀의 모든 작업이 종료될 때까지 지정된 시간만큼 대기합니다.
            if (!executor.awaitTermination(timeout, unit)) {
                // 대기 시간 내에 종료되지 않으면, 모든 실행 중인 작업을 강제 종료합니다.
                executor.shutdownNow();
                String unitStr = unit.toString().toLowerCase();
                logger.warn("ExecutorService가 {} {} 내에 종료되지 않아 강제 종료합니다.", timeout, unitStr);
            } else {
                logger.info("ExecutorService가 성공적으로 종료되었습니다.");
            }
        } catch (InterruptedException ie) {
            // 현재 스레드가 대기 중에 인터럽트되면, 즉시 강제 종료합니다.
            executor.shutdownNow();
            // 현재 스레드의 인터럽트 상태를 복원하여, 호출자에게 인터럽트가 발생했음을 알립니다.
            Thread.currentThread().interrupt();
            logger.warn("ExecutorService 종료 대기 중 스레드가 인터럽트되었습니다. 강제 종료합니다.", ie);
        }
    }

	// 단일 클라이언트를 안전하게 닫는 로직을 처리
	private void closeClientSafely(Client client) {
        try {
            client.closeClient(); // Client 내부에서 mConnections.remove(this) 처리
        } catch (Exception e) {
        	String clientAddr = client.getsRemoteAddr();
            logger.error("클라이언트 {} 종료 중 오류 발생: {}", clientAddr, e.getMessage(), e);
        }
    }

	private SSLContext createSSLContext() throws IOException, KeyStoreException, NoSuchAlgorithmException,
		CertificateException, UnrecoverableKeyException, KeyManagementException {

	    KeyStore keyStore = KeyStore.getInstance("JKS");
	    try ( InputStream ksIs = new FileInputStream(mKeyStorePath) ) {
	        keyStore.load(ksIs, mKeyStorePassword);
	    }

	    KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
	    kmf.init(keyStore, mKeyStorePassword);

	    SSLContext sslContext = SSLContext.getInstance("TLS");
	    sslContext.init(kmf.getKeyManagers(), null, null);
	    return sslContext;
    }

	void accept(SelectionKey selectionKey) throws IOException {
        try (
        		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
                SocketChannel socketChannel = serverSocketChannel.accept()
        ) {

           if (socketChannel != null) {
               logger.info("[연결 수락: {} : {}]", socketChannel.getRemoteAddress(), Thread.currentThread().getName());

               Client client = new Client(socketChannel); // Client 생성자에서 SSL 핸드쉐이크 시작
               mConnections.add(client);

               logger.info("[연결 개수: {}]", mConnections.size());
           }
       } catch (IOException e) {
           // 여기서 예외를 다시 던지거나 (throws IOException),
           // 해당 오류가 치명적이라고 판단되면 여기서 stopServer()를 직접 호출할 수도 있습니다.
           // 현재는 상위 호출자인 startServer의 catch (Exception e) 블록에서 처리됩니다.
           throw new IOException("클라이언트 연결 수락 중 오류가 발생했습니다. (포트: " + mPort + ")", e);
       }
	}

	class Client {
		private SocketChannel mmSocketChannel;
        private String mmSremoteAddr;
        private SSLEngine mmSslEngine;

        // 암호화되지 않은 데이터를 저장하는 버퍼
        private ByteBuffer mmAppRecvBuffer;

        // 암호화된 데이터를 저장하는 버퍼
        private ByteBuffer mmNetSendBuffer;
        private ByteBuffer mmNetRecvBuffer;

        private ConcurrentLinkedQueue<ByteBuffer> mOutgoingDataQueue = new ConcurrentLinkedQueue<>();

		public Client(SocketChannel socketChannel) throws IOException {
			this.mmSocketChannel = socketChannel;
            this.mmSremoteAddr = socketChannel.getRemoteAddress().toString();

            // SSLEngine 생성
            mmSslEngine = mSslContext.createSSLEngine(mmSocketChannel.socket().getInetAddress().getHostAddress(),
                    mmSocketChannel.socket().getPort());
            mmSslEngine.setUseClientMode(false); 	// 서버 모드로 설정
            mmSslEngine.setNeedClientAuth(false); 	// 클라이언트 인증 불필요 (필요시 true로 변경)

            // 버퍼 초기화
            int appBufferSize = mmSslEngine.getSession().getApplicationBufferSize();
            int netBufferSize = mmSslEngine.getSession().getPacketBufferSize();

            mmAppRecvBuffer = ByteBuffer.allocate(appBufferSize);
            mmNetRecvBuffer = ByteBuffer.allocate(netBufferSize);
            mmNetSendBuffer = ByteBuffer.allocate(netBufferSize);

            socketChannel.configureBlocking(false);
            SelectionKey selectionKey = socketChannel.register(mSelector, SelectionKey.OP_READ);
            selectionKey.attach(this);

            // SSL 핸드쉐이크 시작
            doHandshake(selectionKey);
		}

		public String getsRemoteAddr() {
			return this.mmSremoteAddr;
		}

		private void doHandshake(SelectionKey selectionKey) throws IOException {
            logger.info("[핸드쉐이크 시작: {}]", mmSremoteAddr);
            mmSslEngine.beginHandshake();

            SSLEngineResult result;
            int appBufferSize = mmSslEngine.getSession().getApplicationBufferSize();
            int netBufferSize = mmSslEngine.getSession().getPacketBufferSize();

            mmNetRecvBuffer.clear();
            mmNetSendBuffer.clear();

            while (true) {
                // 핸드쉐이크 상태에 따라 읽기/쓰기 필요
                switch (mmSslEngine.getHandshakeStatus()) {
                    case NOT_HANDSHAKING:
                    case FINISHED:
                        logger.info("[핸드쉐이크 완료: {}]", mmSremoteAddr);
                        selectionKey.interestOps(SelectionKey.OP_READ); // 핸드쉐이크 완료 후 읽기 모드로 전환
                        mSelector.wakeup();
                        return;

                    case NEED_UNWRAP:
                        // 클라이언트로부터 데이터 수신 (암호화된 데이터)
                        int bytesRead = mmSocketChannel.read(mmNetRecvBuffer);
                        if (bytesRead == -1) {
                            throw new IOException("소켓 채널이 닫혔습니다.");
                        }
                        if (bytesRead == 0 && mmNetRecvBuffer.position() == 0) {
                            // 읽을 데이터가 없으면 잠시 대기
                            selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_READ);
                            mSelector.wakeup();
                            return;
                        }

                        mmNetRecvBuffer.flip();
                        result = mmSslEngine.unwrap(mmNetRecvBuffer, mmAppRecvBuffer);
                        mmNetRecvBuffer.compact(); // 사용된 버퍼 압축

                        switch (result.getStatus()) {
                            case OK:
                                break;
                            case BUFFER_OVERFLOW:
                                mmAppRecvBuffer = enlargeBuffer(mmAppRecvBuffer, appBufferSize);
                                break;
                            case BUFFER_UNDERFLOW:
                                // 더 많은 데이터가 필요함, 읽기 대기
                                selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_READ);
                                mSelector.wakeup();
                                return;
                            case CLOSED:
                                throw new IOException("SSL 엔진이 닫혔습니다.");
                        }
                        break;

                    case NEED_WRAP:
                        // 클라이언트로 데이터 전송 (암호화된 데이터)
                        mmNetSendBuffer.clear();
                        result = mmSslEngine.wrap(ByteBuffer.allocate(0), mmNetSendBuffer); // 빈 버퍼를 랩핑하여 핸드쉐이크 데이터 생성
                        mmNetSendBuffer.flip();

                        switch (result.getStatus()) {
                            case OK:
                                while (mmNetSendBuffer.hasRemaining()) {
                                    mmSocketChannel.write(mmNetSendBuffer);
                                }
                                break;
                            case BUFFER_OVERFLOW:
                                mmNetSendBuffer = enlargeBuffer(mmNetSendBuffer, netBufferSize);
                                break;
                            case CLOSED:
                                throw new IOException("SSL 엔진이 닫혔습니다.");
                            case BUFFER_UNDERFLOW: // wrap에서는 거의 발생하지 않지만, 컴파일러 경고 해결을 위해 추가
                                // 이 경우 wrap에서는 발생할 가능성이 매우 낮으므로, 별도 처리 없이 break
                                break;
                            default: // 모든 다른 상태 처리 (향후 enum에 새 상수가 추가될 경우 대비)
                                logger.warn("mmSslEngine.wrap()에서 예상치 못한 상태 발생: {}", result.getStatus());
                                break;
                        }
                        break;

                    case NEED_TASK:
                        Runnable task;
                        while ( (task = mmSslEngine.getDelegatedTask()) != null ) {
                        	handshakeTaskExecutor.execute(task); // 스레드 풀 사용
                        }
                        break;

                    default:
                        // 다른 상태 (e.g., NEED_UNWRAP_AGAIN)는 루프를 통해 다시 처리
                        break;
                }
                mSelector.wakeup(); // 상태 변경을 즉시 반영
            }
        }

        private ByteBuffer enlargeBuffer(ByteBuffer buffer, int newSize) {
            ByteBuffer newBuffer = ByteBuffer.allocate(newSize);
            buffer.flip();
            newBuffer.put(buffer);
            return newBuffer;
        }

		void receive(SelectionKey selectionKey) {
			mmNetRecvBuffer.clear(); // 네트워크 버퍼 초기화

			try {
				int nByteCnt = mmSocketChannel.read(mmNetRecvBuffer);

                if (nByteCnt == -1) {
                    throw new IOException("클라이언트 연결이 종료되었습니다.");
                }

                mmNetRecvBuffer.flip(); // 읽기 모드로 전환

                // SSL 엔진으로 데이터 복호화
                SSLEngineResult result = mmSslEngine.unwrap(mmNetRecvBuffer, mmAppRecvBuffer);
                mmNetRecvBuffer.compact(); // 사용된 데이터는 압축

                switch (result.getStatus()) {
	                case OK:
	                    mmAppRecvBuffer.flip(); // 복호화된 데이터 읽기 모드로 전환

	                    Charset charset = Charset.forName(mCharsetName);
	                    String sRecvData = charset.decode(mmAppRecvBuffer).toString();
	                    mmAppRecvBuffer.clear(); // 사용된 데이터는 초기화

	                    logger.info("[요청 처리: {} : {}]", mmSremoteAddr, Thread.currentThread().getName());
	                    logger.info("[요청 데이터: {}]", sRecvData);

	                    // 받은 데이터에 따른 분기 처리 및 그에 따른 응답 값 정의
	                    String sSendMsg = sRecvData; // 단순 Echo 예시

	                    logger.info("[응답 데이터: {}]", sSendMsg);

	                    // 응답 데이터 큐에 추가
	                    mOutgoingDataQueue.offer(ByteBuffer.wrap(sSendMsg.getBytes(mCharsetName)));

	                    // 쓰기 준비 완료
	                    selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_WRITE);
	                    mSelector.wakeup();
	                    break;

	                case BUFFER_OVERFLOW:
	                    // mmAppRecvBuffer가 작을 경우 확장
	                    mmAppRecvBuffer = enlargeBuffer(mmAppRecvBuffer, mmSslEngine.getSession().getApplicationBufferSize());
	                    break;
	                case BUFFER_UNDERFLOW:
	                    // 더 많은 암호화된 데이터가 필요함
	                    break;
	                case CLOSED:
	                    throw new IOException("SSL 엔진이 닫혔습니다.");
                }

			} catch (IOException e) {
				logger.error("데이터 수신 중 오류 발생: {}", e.getMessage(), e);
                closeClient();
			}
		}

		void send(SelectionKey selectionKey) {
            try {
                // 전송할 데이터가 있을 경우에만 처리
                while (!mOutgoingDataQueue.isEmpty()) {
                    ByteBuffer appData = mOutgoingDataQueue.peek(); // 큐에서 데이터를 가져오되 제거하지 않음

                    mmNetSendBuffer.clear(); // 네트워크 버퍼 초기화

                    // SSL 엔진으로 데이터 암호화
                    SSLEngineResult result = mmSslEngine.wrap(appData, mmNetSendBuffer);
                    mmNetSendBuffer.flip(); // 쓰기 모드로 전환

                    switch (result.getStatus()) {
                        case OK:
                            while (mmNetSendBuffer.hasRemaining()) {
                                mmSocketChannel.write(mmNetSendBuffer);
                            }
                            if (!appData.hasRemaining()) {
                                mOutgoingDataQueue.poll(); // 모든 데이터 전송 완료 시 큐에서 제거
                            }
                            break;
                        case BUFFER_OVERFLOW:
                            mmNetSendBuffer = enlargeBuffer(mmNetSendBuffer, mmSslEngine.getSession().getPacketBufferSize());
                            // 다시 시도
                            break;
                        case BUFFER_UNDERFLOW:
                            // 더 많은 애플리케이션 데이터가 필요하지만, 큐에 데이터가 더 없으므로 대기
                            return;
                        case CLOSED:
                            throw new IOException("SSL 엔진이 닫혔습니다.");
                    }
                }

                // 모든 데이터 전송 완료 시 쓰기 관심 OP 제거, 읽기 관심 OP 설정
                selectionKey.interestOps(selectionKey.interestOps() & ~SelectionKey.OP_WRITE | SelectionKey.OP_READ);
                mSelector.wakeup();

            } catch (IOException e) {
                logger.error("데이터 전송 중 오류 발생: {}", e.getMessage(), e);
                closeClient();
            }
		}

		public void closeClient() {
            try {
                if ( mmSocketChannel != null && mmSocketChannel.isOpen() ) {
                    logger.info("[클라이언트 연결 종료: {}]", mmSremoteAddr);
                    mConnections.remove(this);
                    mmSocketChannel.close();
                    if (mmSslEngine != null) {
                        mmSslEngine.closeOutbound();
                        mmSslEngine.closeInbound();
                    }
                }
            } catch (IOException e) {
                logger.error("클라이언트 종료 중 오류 발생: {}", e.getMessage(), e);
            }
        }
	}

}
