package common.socket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NioSocketServer2 {
	
	private static final Logger logger = LoggerFactory.getLogger(NioSocketServer2.class);
	
	private static final int THREAD_CNT = 5;
	private static ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_CNT);
	
	public void start(int nPort) {
		try ( ServerSocketChannel serverSocketChannel = ServerSocketChannel.open() ) {
			
			// Default : true (Blocking Mode)
			serverSocketChannel.configureBlocking(true);
			serverSocketChannel.bind(new InetSocketAddress(nPort));
			
			for (int i=0; i < THREAD_CNT; i++) {
				SocketChannel socketChannel = serverSocketChannel.accept();
				
				threadPool.execute(new ServerThread(socketChannel));
			}
			
		} catch (IOException e) {
			logger.error("", e);
		}
	}
	
	class ServerThread implements Runnable {
		SocketChannel socketChannel;

		private ServerThread(SocketChannel socketChannel) {
			this.socketChannel = socketChannel;
		}
		
		boolean isRunning = true;
		String sRecvMsg = "";

		@Override
		public void run() {
			try ( 
				InputStream is = socketChannel.socket().getInputStream();
				OutputStream os = socketChannel.socket().getOutputStream();
			) {
				logger.info("NioSocketServer Connected to {}", this.socketChannel.getRemoteAddress());
				
				this.receivedFromClient(is);
				
				this.sendToClient(os);
				
				if ( this.sRecvMsg != null ) {
					this.isRunning = false;
				}
				
			} catch (Exception e) {
				logger.error("", e);
			} finally {
				try {
					if (this.socketChannel != null) {
						this.socketChannel.close();
					}
				} catch (IOException e) {
					logger.error("", e);
				}
			}
		}
		
		private void receivedFromClient(InputStream is) throws IOException {
			StringBuilder sb = new StringBuilder();
			
			BufferedInputStream bis = new BufferedInputStream(is);
			byte[] buffer = new byte[4096];
			
			int nRead = bis.read(buffer, 0, buffer.length);
			if (nRead > 0) {
				sb.append(new String(buffer, 0, nRead));
			}
			
			synchronized (this) {
				this.sRecvMsg = sb.toString();
				
				logger.info("NioSocketServer : [{}]", sRecvMsg);
			}
		}
		
		private void sendToClient(OutputStream os) throws IOException {
			BufferedOutputStream bos = new BufferedOutputStream(os);
			
			// 받은 데이터에 따른 분기 처리 및 그에 따른 응답 값 정의
			String sSendMsg = "Received Success";
			
			bos.write(sSendMsg.getBytes());
			bos.flush();
		}
		
	}
	
}
