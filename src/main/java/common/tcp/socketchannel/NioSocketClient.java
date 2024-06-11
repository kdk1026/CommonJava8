package common.tcp.socketchannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

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
 * 2021.  8. 14. 김대광	SonarLint 지시에 따른 수정
 * </pre>
 */
public class NioSocketClient {
	
	private static final Logger logger = LoggerFactory.getLogger(NioSocketClient.class);
	
	private static final int TIMEOUT = 20*1000;		// 20초
	
    private String sRecvData;
    
	public String getsRecvData() {
		return sRecvData;
	}

	private String mScharsetName;
	
	public void start(String sServerIp, int nPort, byte[] bSendData, String sCharsetName) throws IOException {
		SocketAddress socketAddr = new InetSocketAddress(sServerIp, nPort);
		
		try ( SocketChannel socketChannel = SocketChannel.open() ) {
			
			socketChannel.configureBlocking(true);		// Default: true (Blocking Mode)
			
			Socket socket = socketChannel.socket();
			socket.connect(socketAddr, TIMEOUT);
			socket.setSoTimeout(TIMEOUT);
			
			logger.info("[연결 완료: {}]", socketChannel.getRemoteAddress());
			
			mScharsetName = sCharsetName;
			
			if ( socketChannel.isConnected() && socketChannel.isOpen() ) {
				this.sendToServer(socketChannel, bSendData);
				this.receivedFromServer(socketChannel);
			}
			
		} catch (Exception e) {
			logger.error("", e);
//			throw e;
		}
	}
	
	private void sendToServer(SocketChannel socketChannel, byte[] bSendData) throws IOException  {
		ByteBuffer buffer = ByteBuffer.wrap(bSendData);
		socketChannel.write(buffer);
		
		String sSendData = new String(bSendData, mScharsetName);
		logger.info("[보내기 완료: {}]", sSendData);
	}
	
	private void receivedFromServer(SocketChannel socketChannel) throws IOException {
		ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
		socketChannel.read(byteBuffer);
		byteBuffer.flip();
		
		Charset charset = Charset.defaultCharset();
		this.sRecvData = charset.decode(byteBuffer).toString();
		
		logger.info("[받기 완료: {}]", this.sRecvData);
	}
	
}
