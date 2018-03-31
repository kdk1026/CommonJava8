package common.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NioNonBloackingSocketClient {
	
	private static final Logger logger = LoggerFactory.getLogger(NioNonBloackingSocketClient.class);
	
    private String sRecvMsg;
    
	public String getsRecvMsg() {
		return sRecvMsg;
	}
	
	public void start(String sServerIp, int nPort, byte[] bSendMsg) throws IOException {
		InetSocketAddress isa = new InetSocketAddress(sServerIp, nPort);
		
		try (SocketChannel socketChannel = SocketChannel.open()) {
			socketChannel.connect(isa);
			
			sendToServer(socketChannel, bSendMsg);
			receivedFromServer(socketChannel);
			
		} catch (IOException e) {
			logger.error("", e);
		}
	}
	
	private void sendToServer(SocketChannel socketChannel, byte[] bSendMsg) throws IOException {
		/*
		 * 케릭터셋 인코딩 맞추어야 할 경우, 참고
		 * 	- common.util.bytes.ByteStringUtils
		 * 		> toByteEncoding
		 */
		
		ByteBuffer buffer = ByteBuffer.wrap(bSendMsg);
		socketChannel.write(buffer);
	}
	
	private void receivedFromServer(SocketChannel socketChannel) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(4096);
		socketChannel.read(buffer);
		
		byte[] bRecvMsg = buffer.array();
		sRecvMsg = new String(bRecvMsg).trim();
	}
	
}
