package common.tcp.socketchannel.multi;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Objects;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * NIO 소켓 클라이언트
 *  - Multi Thread
 * </pre>
 * @since 2018. 12. 23.
 * @author 김대광
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2018. 12. 23. 김대광	최초작성
 * 2021.  8. 14. 김대광	SonarLint 지시에 따른 주저리 주저리
 * 2025.  5. 29. 김대광	제미나이에 의한 코드 대폭 개선
 * </pre>
 */
public class NioSocketClientThread {

	private static final Logger logger = LoggerFactory.getLogger(NioSocketClientThread.class);

	private static final int TIMEOUT = 15*1000;		// 15초

	private final String serverIp;
    private final int port;
    private final String charsetName;
    private final boolean useSsl;

	private SocketChannel mSocketChannel;
    private NioSocketClientListener mListener; // 데이터 수신 콜백 리스너
    private boolean mUseSsl;

    private String sRecvData;

    // 데이터 수신을 위한 콜백 인터페이스
    public interface NioSocketClientListener {
        void onDataReceived(String data);
        void onDisconnected();
        void onError(Exception e);
    }

    public void setListener(NioSocketClientListener listener) {
        this.mListener = listener;
    }

    public NioSocketClientThread(final String serverIp, final int port, final String charsetName, final boolean useSsl) {
    	this.serverIp = Objects.requireNonNull(serverIp, "서버 IP는 null일 수 없습니다.");

    	if (port <= 0 || port > 65535) {
    		throw new IllegalArgumentException("유효하지 않은 포트 번호: " + port + ". 포트 번호는 1에서 65535 사이여야 합니다.");
        }
        this.port = port;

        this.charsetName = Objects.requireNonNull(charsetName, "문자셋 이름은 null일 수 없습니다.");
        this.useSsl = useSsl;
    }

	public void startClient() {
		Thread thread = new Thread() {

			@Override
			public void run() {
				mUseSsl = useSsl;
				Socket socket = null;

				try {
					mSocketChannel = SocketChannel.open();
                    SocketAddress socketAddr = new InetSocketAddress(serverIp, port);

                    if (mUseSsl) {
                    	SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                        socket = sslSocketFactory.createSocket(mSocketChannel.socket(), serverIp, port, true);
                        ((SSLSocket) socket).startHandshake(); // SSL 핸드셰이크 시작
                        logger.info("[SSL 연결 완료: {}]", socket.getRemoteSocketAddress());
                    } else {
                    	socket = mSocketChannel.socket();
                        socket.connect(socketAddr, TIMEOUT);
                        logger.info("[일반 연결 완료: {}]", socket.getRemoteSocketAddress());
                    }

                    socket.setSoTimeout(TIMEOUT); // 소켓 읽기/쓰기 타임아웃 설정

                    // 연결 성공 후 데이터 수신 스레드 시작
                    startReceivingData();
				} catch (IOException e) {
					logger.error("클라이언트 연결 중 오류 발생", e);
                    if (mSocketChannel != null && mSocketChannel.isOpen()) {
                        stopClient();
                    }
                    if (mListener != null) {
                        mListener.onError(e);
                    }
				}
			}
		};
		thread.start();
	}

	public void stopClient() {
		if ( mSocketChannel == null ) {
            return;
        }

		try {
			mSocketChannel.close();

			logger.info("[연결 끊음]");

			if ( mListener != null ) {
                mListener.onDisconnected();
            }

		} catch (IOException e) {
			logger.error("클라이언트 종료 중 오류 발생", e);

            if (mListener != null) {
                mListener.onError(e);
            }
		} finally {
			mSocketChannel = null; // 채널 참조 해제
		}
	}

	public void send(byte[] bSendData) {
		if ( mSocketChannel == null || !mSocketChannel.isConnected() ) {
            logger.warn("클라이언트가 연결되지 않아 데이터를 보낼 수 없습니다.");
            return;
        }

		Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ByteBuffer byteBuffer = ByteBuffer.wrap(bSendData);
                    mSocketChannel.write(byteBuffer);

                    String sSendData = new String(bSendData, charsetName);
                    logger.info("[보내기 완료: {}]", sSendData);

                } catch (IOException e) {
                    logger.error("데이터 전송 중 오류 발생", e);
                    stopClient();
                    if ( mListener != null ) {
                        mListener.onError(e);
                    }
                }
            }
        });
        thread.start();
	}

	// 데이터 수신을 위한 내부 스레드
    private void startReceivingData() {
        Thread receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while ( mSocketChannel != null && mSocketChannel.isConnected() ) {
                    try {
                        ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
                        int nByteCnt = mSocketChannel.read(byteBuffer);

                        if (nByteCnt == -1) {
                            // 스트림의 끝 (상대방이 연결을 끊음)
                            logger.warn("상대방이 연결을 끊었습니다.");
                            throw new IOException("스트림의 끝 도달");
                        }
                        if (nByteCnt == 0) {
                            // 읽을 데이터가 없음 (블로킹 모드가 아니거나, 일시적으로 데이터 없음)
                            // 바쁜 대기를 피하기 위해 잠시 대기
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt(); // 인터럽트 상태 복원
                                logger.warn("수신 스레드 대기 중 인터럽트 발생", e);
                                break; // 스레드 종료
                            }
                            continue;
                        }

                        byteBuffer.flip(); // 쓰기 모드에서 읽기 모드로 전환

                        // mCharsetName이 유효한지 확인하고 기본 Charset 대신 사용
                        Charset charset = (charsetName != null && !charsetName.isEmpty()) ?
                                Charset.forName(charsetName) : Charset.defaultCharset();
                        sRecvData = charset.decode(byteBuffer).toString();

                        logger.info("[받기 완료: {}]", sRecvData);

                        if (mListener != null) {
                            mListener.onDataReceived(sRecvData);
                        }

                    } catch (IOException e) {
                        logger.error("데이터 수신 중 IO 오류 발생", e);
                        stopClient();
                        if (mListener != null) {
                            mListener.onError(e);
                        }
                        break; // IO 오류 발생 시 수신 루프 종료
                    } catch (Exception e) {
                        logger.error("데이터 수신 중 예상치 못한 오류 발생", e);
                        stopClient();
                        if (mListener != null) {
                            mListener.onError(e);
                        }
                        break; // 기타 오류 발생 시 수신 루프 종료
                    }
                }
                logger.info("데이터 수신 스레드 종료.");
            }
        });
        receiveThread.setName("NioSocketClient-ReceiveThread");
        receiveThread.start();
    }

    public String receive() {
    	return sRecvData.isEmpty() ? null : sRecvData;
    }

}
