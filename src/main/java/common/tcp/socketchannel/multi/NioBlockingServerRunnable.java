package common.tcp.socketchannel.multi;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * NIO 소켓 서버 (Blocking Mode)
 *  - Multi Thread
 * </pre>
 * @since 2018. 12. 23.
 * @author 김대광
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2018. 12. 23. 김대광	최초작성
 * 2021.  8. 14. 김대광	SonarLint 지시에 따른 주저리 주저리 (하나같이 어쩔 수 없는 것들이다... 블로킹/논블로킹 잘 몰라요... 이거야 말로 완벽한 맨땅에 헤딩 일단 돌아감 OK)
 * </pre>
 */
public class NioBlockingServerRunnable {
	
	private static final Logger logger = LoggerFactory.getLogger(NioBlockingServerRunnable.class);
	
	private ExecutorService mExecutorService;
	private ServerSocketChannel mServerSocketChannel;
	public List<Client> mConnections = new ArrayList<>();
    
    private String mScharsetName;
    
	public void startServer(int nPort, String sCharsetName) {
		// 스레드 풀 생성
		mExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		
		try {
			mServerSocketChannel = ServerSocketChannel.open();
			mServerSocketChannel.configureBlocking(true);		// Default: true
			mServerSocketChannel.bind(new InetSocketAddress(nPort));
			
			mScharsetName = sCharsetName;
			
		} catch (IOException e) {
			logger.error("", e);
			
			if ( mServerSocketChannel.isOpen() ) {
				stopServer();
			}
		}
		
		Runnable runnable = new Runnable() {
			
			@Override
			public void run() {
				logger.info("[서버 시작]");
				
				while (true) {
					try {
						SocketChannel socketChannel = mServerSocketChannel.accept();
						
						logger.info("[연결 수락: {} : {}]", socketChannel.getRemoteAddress(), Thread.currentThread().getName());
						
						Client client = new Client(socketChannel);
						mConnections.add(client);
						
						logger.info("[연결 개수: {}]", mConnections.size());
						
					} catch (IOException e) {
						logger.error("", e);
						
						stopServer();
						break;
					}
				}
			}
		};
		
		mExecutorService.submit(runnable);
	}
	
	public void stopServer() {
		Iterator<Client> it = mConnections.iterator();
		
		try {
			while ( it.hasNext() ) {
				Client client = it.next();
				client.mmSocketChannel.close();
				it.remove();
			}
			
			mServerSocketChannel.close();
			mExecutorService.shutdownNow();
			
			logger.info("[서버 종료]");
			
		} catch (IOException e) {
			logger.error("", e);
		}
	}
	
	class Client {
		private SocketChannel mmSocketChannel;
		private String mmSremoteAddr;

		public Client(SocketChannel socketChannel) throws IOException {
			this.mmSocketChannel = socketChannel;
			this.mmSremoteAddr = socketChannel.getRemoteAddress().toString();
			receive();
		}
		
		public String getsRemoteAddr() {
			return this.mmSremoteAddr;
		}

		void receive() {
			Runnable runnable = new Runnable() {
				
				@Override
				public void run() {
					while (true) {
						try {
							String sRemoteAddr = mmSremoteAddr;
							
							ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
							int nByteCnt = mmSocketChannel.read(byteBuffer);
							
							if ( nByteCnt == -1 ) {
								throw new IOException();
							}
							
							byteBuffer.flip();
							
							Charset charset = Charset.forName(mScharsetName);
							String sRecvData = charset.decode(byteBuffer).toString();
							
							logger.info("[요청 처리: {} : {}]", sRemoteAddr, Thread.currentThread().getName());
							logger.info("[요청 데이터: {}]", sRecvData);
							
							// 받은 데이터에 따른 분기 처리 및 그에 따른 응답 값 정의
							String sSendMsg = "";
							sSendMsg = sRecvData;
							
							logger.info("[응답 데이터: {}]", sSendMsg);
							
							byte[] bSendData = sSendMsg.getBytes();
							
							for ( Client client : mConnections ) {
								if ( sRemoteAddr.equals(client.getsRemoteAddr()) ) {
									client.send(bSendData);
								}
							}
							
						} catch (IOException e) {
							try {
								mConnections.remove(Client.this);
								mmSocketChannel.close();
								
							} catch (IOException e2) {
								logger.error("", e2);
							}
							
							break;
						}	
					}
				}
				
			};
			
			mExecutorService.submit(runnable);
		}
		
		void send(byte[] bSendData) {
			Runnable runnable = new Runnable() {
				
				@Override
				public void run() {
					try {
						ByteBuffer byteBuffer = ByteBuffer.wrap(bSendData);
						mmSocketChannel.write(byteBuffer);
						
					} catch (IOException e) {
						try {
							mConnections.remove(Client.this);
							mmSocketChannel.close();
							
						} catch (IOException e1) {
							logger.error("", e1);
						}
					}
				}
				
			};
			
			mExecutorService.submit(runnable);
		}
	}
	
}
