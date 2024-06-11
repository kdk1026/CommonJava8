package common.tcp.socket.multi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
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
 * 소켓 서버
 *  - Multi Thread
 * </pre>
 * @since 2018. 12. 23.
 * @author 김대광
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2018. 12. 23. 김대광	최초작성
 * 2021.  8. 14. 김대광	SonarLint 지시에 따른 주저리 주저리 (어절수 없는건 없는거고... 왜 lambda 를 제시하니... 몰라... 연구할 날이 올까? 애초에... Ctrl + Space 신공이 가능한건가???)
 * </pre>
 */
public class SocketServerRunnable {

	private static final Logger logger = LoggerFactory.getLogger(SocketServerRunnable.class);
	
	private ExecutorService mExecutorService;
	private ServerSocket mServerSocket;
	public List<Client> mConnections = new ArrayList<>();
	
	private String mScharsetName;
	
	public void startServer(int nPort, String sCharsetName) {
		// 스레드 풀 생성
		mExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		
		try {
			mServerSocket = new ServerSocket(nPort);
			
			mScharsetName = sCharsetName;
			
		} catch (IOException e) {
			logger.error("", e);
			
			if ( !mServerSocket.isClosed() ) {
				stopServer();
			}
		}
		
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				logger.info("[서버 시작]");
				
				while (true) {
					try {
						Socket socket = mServerSocket.accept();
						
						logger.info("[연결 수락: {} : {}]", socket.getInetAddress().getHostAddress(), Thread.currentThread().getName());
						
						Client client = new Client(socket);
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
				client.mmSocket.close();
				it.remove();
			}
			
			mServerSocket.close();
			mExecutorService.shutdownNow();
			
			logger.info("[서버 종료]");
			
		} catch (IOException e) {
			logger.error("", e);
		}
	}
	
	class Client {
		private Socket mmSocket;
		private String mmSremoteAddr;

		public Client(Socket sock) {
			this.mmSocket = sock;
			this.mmSremoteAddr = sock.getInetAddress().getHostAddress();
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
							StringBuilder sb = new StringBuilder();
							
							InputStream is = mmSocket.getInputStream();
							BufferedInputStream bis = new BufferedInputStream(is);
							byte[] buffer = new byte[4096];
							
							int nRead = bis.read(buffer, 0, buffer.length);
							if (nRead > 0) {
								sb.append(new String(buffer, 0, nRead, mScharsetName));
							}
							
							String sRecvData = sb.toString();
							
							if ( sRecvData == null || "".equals(sRecvData) ) {
								throw new IOException();
							}
							
							logger.info("[요청 처리: {} : {}]", sRemoteAddr, Thread.currentThread().getName());
							logger.info("[요청 데이터: {}]", sRecvData);
							
							// 받은 데이터에 따른 분기 처리 및 그에 따른 응답 값 정의
							String sSendMsg = "";
							sSendMsg = sRecvData;
							
							logger.info("[응답 데이터: {}]", sSendMsg);
							
							byte[] bSendData = sSendMsg.getBytes(Charset.defaultCharset());
							
							for ( Client client : mConnections ) {
								if ( sRemoteAddr.equals(client.getsRemoteAddr()) ) {
									client.send(bSendData);
								}
							}
							
						} catch (IOException e) {
							try {
								mConnections.remove(Client.this);
								mmSocket.close();
								
							} catch (IOException e1) {
								logger.error("", e1);
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
						OutputStream os = mmSocket.getOutputStream();
						BufferedOutputStream bos = new BufferedOutputStream(os);
						
						bos.write(bSendData);
						bos.flush();
						
					} catch (IOException e) {
						try {
							mConnections.remove(Client.this);
							mmSocket.close();
							
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
