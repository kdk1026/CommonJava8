package common.tcp.socketchannel.multi;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
 * 2021.  8. 14. 김대광	SonarLint 지시에 따른 주저리 주저리 (하나같이 어쩔 수 없는 것들이다... 블로킹/논블로킹 잘 몰라요... 이거야 말로 완벽한 맨땅에 헤딩 일단 돌아감 OK)
 * </pre>
 */
public class NioNonBlockingServerThread {

	private static final Logger logger = LoggerFactory.getLogger(NioNonBlockingServerThread.class);
	
	private Selector mSelector;
	private ServerSocketChannel mServerSocketChannel;
	public List<Client> mConnections = new ArrayList<>();
	
	private String mScharsetName;
	
	public void startServer(int nPort, String sCharsetName) {
		try {
			mSelector = Selector.open();
			
			mServerSocketChannel = ServerSocketChannel.open();
			mServerSocketChannel.configureBlocking(false);
			mServerSocketChannel.bind(new InetSocketAddress(nPort));
			
			mScharsetName = sCharsetName;
			
			mServerSocketChannel.register(mSelector, SelectionKey.OP_ACCEPT);
			
		} catch (IOException e) {
			logger.error("", e);
			
			if ( mServerSocketChannel.isOpen() ) {
				stopServer();
			}
		}
		
		Thread thread = new Thread() {

			@Override
			public void run() {
				logger.info("[서버 시작]");
				
				while (true) {
					try {
						int nKeyCnt = mSelector.select();
						
						if ( nKeyCnt == 0 ) {
							continue;
						}
						
						Set<SelectionKey> selectedKeys = mSelector.selectedKeys();
						Iterator<SelectionKey> it = selectedKeys.iterator();
						
						while ( it.hasNext() ) {
							SelectionKey selectionKey = it.next();
							
							if ( selectionKey.isAcceptable() ) {
								accept(selectionKey);
							}
							else if ( selectionKey.isReadable() ) {
								Client client = (Client) selectionKey.attachment();
								client.receive(selectionKey);
							}
							else if ( selectionKey.isWritable() ) {
								Client client = (Client) selectionKey.attachment();
								client.send(selectionKey);
							}
							
							it.remove();
						}
						
					} catch (IOException e) {
						logger.error("", e);
						
						stopServer();
						break;
					}
				}
			}
			
		};
		
		thread.start();
	}
	
	public void stopServer() {
		try {
			Iterator<Client> it = mConnections.iterator();
			
			while ( it.hasNext() ) {
				Client client = it.next();
				client.mmSocketChannel.close();
				it.remove();
			}
			
			mServerSocketChannel.close();
			mSelector.close();
			
			logger.info("[서버 종료]");
			
		} catch (IOException e) {
			logger.error("", e);
		}
	}
	
	void accept(SelectionKey selectionKey) {
		try {
			ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
			SocketChannel socketChannel = serverSocketChannel.accept();
			
			logger.info("[연결 수락: {} : {}]", socketChannel.getRemoteAddress(), Thread.currentThread().getName());
			
			Client client = new Client(socketChannel);
			mConnections.add(client);
			
			logger.info("[연결 개수: {}]", mConnections.size());
			
		} catch (IOException e) {
			logger.error("", e);
			
			stopServer();
		}
	}
	
	class Client {
		private SocketChannel mmSocketChannel;
		private String mmSremoteAddr;
		private byte[] mmBsendData;
		
		public Client(SocketChannel socketChannel) throws IOException {
			this.mmSocketChannel = socketChannel;
			this.mmSremoteAddr = socketChannel.getRemoteAddress().toString();
			
			socketChannel.configureBlocking(false);
			SelectionKey selectionKey = socketChannel.register(mSelector, SelectionKey.OP_READ);
			
			selectionKey.attach(this);
		}
		
		public String getsRemoteAddr() {
			return this.mmSremoteAddr;
		}
		
		void receive(SelectionKey selectionKey) {
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
						client.mmBsendData = bSendData;
						SelectionKey key = client.mmSocketChannel.keyFor(mSelector);
						key.interestOps(SelectionKey.OP_WRITE);
					}
				}
				mSelector.wakeup();
				
			} catch (IOException e) {
				try {
					mConnections.remove(this);
					mmSocketChannel.close();
					
				} catch (IOException e2) {
					logger.error("", e2);
				}
			}
		}
		
		void send(SelectionKey selectionKey) {
			try {
				ByteBuffer byteBuffer = ByteBuffer.wrap(mmBsendData);
				
				mmSocketChannel.write(byteBuffer);
				selectionKey.interestOps(SelectionKey.OP_READ);
				mSelector.wakeup();
				
			} catch (IOException e) {
				try {
					mConnections.remove(Client.this);
					mmSocketChannel.close();
					
				} catch (IOException e1) {
					logger.error("", e1);
				}
			}
		}
	}
	
}
