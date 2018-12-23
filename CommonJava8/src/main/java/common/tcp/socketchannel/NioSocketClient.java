package common.tcp.socketchannel;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * </pre>
 */
public class NioSocketClient {
	
	private static final Logger logger = LoggerFactory.getLogger(NioSocketClient.class);
	
	private static final int TIMEOUT = 15*1000;		// 15초
	
    private String sRecvMsg;
    
	public String getsRecvMsg() {
		return sRecvMsg;
	}
	
	public void start(String sServerIp, int nPort, byte[] bSendData) throws IOException {
		SocketAddress socketAddr = new InetSocketAddress(sServerIp, nPort);
		
		try ( SocketChannel socketChannel = SocketChannel.open() ) {
			
			socketChannel.configureBlocking(true);		// Default: true (Blocking Mode)
			
			Socket socket = socketChannel.socket();
			socket.connect(socketAddr, TIMEOUT);
			socket.setSoTimeout(TIMEOUT);
			
			logger.info("[연결 완료: {}]", socketChannel.getRemoteAddress());
			
			if ( socketChannel.isConnected() && socketChannel.isOpen() ) {
				this.sendToServer(socketChannel, bSendData);
				this.receivedFromServer(socketChannel);
			}
			
		} catch (Exception e) {
			logger.error("", e);
			throw e;
		}
	}
	
	private void sendToServer(SocketChannel socketChannel, byte[] bSendData) throws IOException  {
		/*
		 * 케릭터셋 인코딩 맞추어야 할 경우, 참고
		 * 	- common.util.bytes.ByteStringUtils
		 * 		> toByteEncoding
		 */
		
		ByteBuffer buffer = ByteBuffer.wrap(bSendData);
		socketChannel.write(buffer);
		
		logger.info("[보내기 완료: {}]", new String(bSendData));
	}
	
	private void receivedFromServer(SocketChannel socketChannel) throws IOException {
		InputStream is = socketChannel.socket().getInputStream();
		
		StringBuilder sb = new StringBuilder();
		
		BufferedInputStream bis = new BufferedInputStream(is);
		byte[] buffer = new byte[4096];
		
		int nRead = bis.read(buffer, 0, buffer.length);
		if (nRead > 0) {
			sb.append(new String(buffer, 0, nRead));
		}
		
		this.sRecvMsg = sb.toString();
		
		logger.info("[받기 완료: {}]", this.sRecvMsg);
	}
	
}
