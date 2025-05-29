package common.tcp.socket.multi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * 소켓 클라이언트
 *  - Multi Thread
 * </pre>
 * @since 2018. 12. 23.
 * @author 김대광
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2018. 12. 23. 김대광	최초작성
 * 2021.  8. 14. 김대광	SonarLint 지시에 따른 주저리 주저리
 * 2025.  5. 28. 김대광	제미나이에 의한 코드 대폭 개선
 * </pre>
 */
public class SocketClientThread {

	private static final Logger logger = LoggerFactory.getLogger(SocketClientThread.class);

	private static final int TIMEOUT = 15*1000;		// 15초

	private final String serverIp;
    private final int port;
    private final String charsetName;
    private final boolean useSsl;

    private Socket mSocket;
    private final ExecutorService executorService = Executors.newCachedThreadPool(); // 비동기 작업을 위한 스레드 풀

    public SocketClientThread(final String serverIp, final int port, final String charsetName, final boolean useSsl) {
    	this.serverIp = Objects.requireNonNull(serverIp, "서버 IP는 null일 수 없습니다.");

    	if (port <= 0 || port > 65535) {
    		throw new IllegalArgumentException("유효하지 않은 포트 번호: " + port + ". 포트 번호는 1에서 65535 사이여야 합니다.");
        }
        this.port = port;

        this.charsetName = Objects.requireNonNull(charsetName, "문자셋 이름은 null일 수 없습니다.");
        this.useSsl = useSsl;
    }

    /**
     * 서버에 클라이언트 연결을 시작합니다.
     * 연결은 별도의 스레드에서 비동기적으로 처리됩니다.
     */
	public void startClient() {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    if (useSsl) {
                    	/*
                    	 * 서버가 공인된 CA의 인증서를 사용하고 그 CA가 Java의 기본 TrustStore에 포함되어 있을 때는 별도의 인증서 로드 없이 동작
                    	 *
                    	 * 레거시 서버나 특정 사내 환경에서 자체 인증서를 사용하는 경우라면,
                    	 * 해당 인증서가 기본 TrustStore에 없으므로 명시적인 TrustStore 로드 및 설정이 필요할 가능성이 매우 높습니다.
                    	 * 이 경우, System.setProperty를 통해 TrustStore를 지정하거나, SSLContext를 직접 설정하는 방법을 고려
                    	 *
                    	 * System.setProperty("javax.net.ssl.trustStore", "path/to/your/truststore.jks");
						 * System.setProperty("javax.net.ssl.trustStorePassword", "your_truststore_password");
                    	 */
                        SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                        mSocket = sslSocketFactory.createSocket();
                    } else {
                        mSocket = new Socket();
                    }

                    SocketAddress socketAddr = new InetSocketAddress(serverIp, port);

                    mSocket.connect(socketAddr, TIMEOUT);
                    mSocket.setSoTimeout(TIMEOUT); // 데이터 읽기 타임아웃 설정

                    logger.info("[연결 완료: {}]", mSocket.getInetAddress().getHostAddress());

                } catch (IOException e) {
                    logger.error("클라이언트 연결 중 오류 발생", e);

                    // 연결 실패 시 소켓이 열려있다면 닫음
                    if ( mSocket != null && mSocket.isConnected() ) {
                        stopClient();
                    }
                }
            }
        });
	}

    /**
     * 클라이언트 연결을 종료합니다.
     * 열려있는 소켓을 닫고, 스레드 풀을 종료합니다.
     */
	public void stopClient() {
		try {
            if ( mSocket != null && !mSocket.isClosed() ) {
                mSocket.close();
                logger.info("[연결 끊음]");
            }
        } catch (IOException e) {
            logger.error("클라이언트 연결 종료 중 오류 발생", e);
        } finally {
            // 모든 작업을 완료했거나 더 이상 클라이언트가 필요 없으면 스레드 풀 종료
            // 주의: 이 클라이언트 인스턴스를 재사용할 예정이라면 executorService.shutdown()은 startClient 호출 전에 하지 않아야 합니다.
            // 여기서는 클라이언트 연결 종료 시 스레드 풀도 종료하는 것으로 가정합니다.
            executorService.shutdown();
        }
	}

    /**
     * 데이터를 서버로 전송합니다.
     * 데이터 전송은 별도의 스레드에서 비동기적으로 처리됩니다.
     *
     * @param bSendData 전송할 바이트 배열 데이터
     */
	public void send(final byte[] bSendData) {
		if ( mSocket == null || !mSocket.isConnected() ) {
            logger.warn("소켓이 연결되어 있지 않아 데이터를 전송할 수 없습니다.");
            return;
        }

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try (
                	OutputStream os = mSocket.getOutputStream();
                    BufferedOutputStream bos = new BufferedOutputStream(os)
                ) {
                    bos.write(bSendData);
                    bos.flush();

                    String sSendData = new String(bSendData, charsetName);
                    logger.info("[보내기 완료: {}]", sSendData);

                } catch (IOException e) {
                    logger.error("데이터 전송 중 오류 발생", e);
                    stopClient(); // 오류 발생 시 연결 종료
                }
            }
        });
	}

    /**
     * 서버로부터 데이터를 수신합니다.
     * 이 메서드는 데이터를 수신할 때까지 **블로킹**됩니다.
     *
     * @return 수신된 문자열 데이터. 오류 발생 시 null 반환.
     */
	public String receive() {
		if ( mSocket == null || !mSocket.isConnected() ) {
            logger.warn("소켓이 연결되어 있지 않아 데이터를 수신할 수 없습니다.");
            return null;
        }

		StringBuilder sb = new StringBuilder();
		try (
			InputStream is = mSocket.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is)
		) {

            byte[] buffer = new byte[4096];
            int nRead;

            // 데이터가 읽힐 때까지 블로킹
            // 주의: 레거시 서버의 경우, 특정 종료 문자나 고정 길이 프로토콜이 없다면
            // read() 메서드가 데이터를 계속 기다릴 수 있습니다.
            // 필요에 따라 프로토콜에 맞는 방식으로 데이터 수신 로직을 개선해야 합니다.
            while ( (nRead = bis.read(buffer, 0, buffer.length)) != -1 ) {
                sb.append(new String(buffer, 0, nRead, charsetName));

                // 여기에 프로토콜에 따른 메시지 종료 조건 추가 필요
                // 예: 만약 메시지 끝에 개행 문자가 있다면:
                // if (sb.toString().endsWith("\n")) {
                //     break;
                // }
                // 혹은 고정 길이 메시지라면 해당 길이만큼 읽은 후 break;

                // 일단은 단일 read() 호출로 하나의 메시지를 받는다고 가정하고 바로 종료
                // 이 부분을 실제 레거시 서버 프로토콜에 맞춰 조정해야 합니다.
                break;
            }

            String sRecvData = sb.toString();

            if ( sRecvData.isEmpty() ) {
                logger.warn("수신된 데이터가 없습니다. 서버에서 데이터가 오지 않았거나 연결이 끊어졌을 수 있습니다.");
                // 데이터가 없는 경우에도 연결 종료를 고려할 수 있음
                // stopClient();
                return null;
            }

            logger.info("[받기 완료: {}]", sRecvData);
            return sRecvData;

        } catch (IOException e) {
            logger.error("데이터 수신 중 오류 발생", e);
            stopClient(); // 오류 발생 시 연결 종료
            return null;
        }
	}

}
