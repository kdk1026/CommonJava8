package common.socket;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NioSocketClient {
	
	private static final Logger logger = LoggerFactory.getLogger(NioSocketClient.class);
	
    private String sRecvMsg;
    
	public String getsRecvMsg() {
		return sRecvMsg;
	}
	
	public void start(String sServerIp, int nPort, byte[] bSendMsg) throws IOException {
		InetSocketAddress isa = new InetSocketAddress(sServerIp, nPort);
		
		try (SocketChannel socketChannel = SocketChannel.open()) {
			socketChannel.connect(isa);
			
			this.sendToServer(socketChannel, bSendMsg);
			this.receivedFromServer(socketChannel);
			
		} catch (IOException e) {
			logger.error("", e);
		}
	}
	
	private void sendToServer(SocketChannel socketChannel, byte[] bSendMsg) throws IOException  {
		/*
		 * 케릭터셋 인코딩 맞추어야 할 경우, 참고
		 * 	- common.util.bytes.ByteStringUtils
		 * 		> toByteEncoding
		 */
		
		ByteBuffer buffer = ByteBuffer.wrap(bSendMsg);
		socketChannel.write(buffer);
	}
	
	private void receivedFromServer(SocketChannel socketChannel) throws IOException {
		
		try ( InputStream is = socketChannel.socket().getInputStream() ) {
			StringBuilder sb = new StringBuilder();
			
			BufferedInputStream bis = new BufferedInputStream(is);
			byte[] buffer = new byte[4096];
			
			int nRead = bis.read(buffer, 0, buffer.length);
			if (nRead > 0) {
				sb.append(new String(buffer, 0, nRead));
			}
			
			this.sRecvMsg = sb.toString();
		}
	}
	
}
