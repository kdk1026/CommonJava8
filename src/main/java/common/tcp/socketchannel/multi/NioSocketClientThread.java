package common.tcp.socketchannel.multi;

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
 * NIO 소켓 클라이언트
 *  - Multi Thread
 * </pre>
 * @since 2018. 12. 23.
 * @author 김대광
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2018. 12. 23. 김대광	최초작성
 * 2021.  8. 14. 김대광	SonarLint 지시에 따른 주저리 주저리 (필요해서 해놓은 걸텐데... Minor 니까 뭐 신경 안씀)
 * </pre>
 */
public class NioSocketClientThread {

	private static final Logger logger = LoggerFactory.getLogger(NioSocketClientThread.class);
	
	private static final int TIMEOUT = 15*1000;		// 15초
	
	private SocketChannel mSocketChannel;
	private String mScharsetName;
	
	public void startClient(String sServerIp, int nPort, String sCharsetName) {
		Thread thread = new Thread() {

			@Override
			public void run() {
				try {
					mSocketChannel = SocketChannel.open();
					
					SocketAddress socketAddr = new InetSocketAddress(sServerIp, nPort);
					
					Socket socket = mSocketChannel.socket();
					socket.connect(socketAddr, TIMEOUT);
					socket.setSoTimeout(TIMEOUT);
					
					logger.info("[연결 완료: {}]", mSocketChannel.getRemoteAddress());
					
					mScharsetName = sCharsetName;
					
				} catch (Exception e) {
					logger.error("", e);
					
					if ( mSocketChannel.isOpen() ) {
						stopClient();
					}
				}
			}
			
		};
		
		thread.start();
	}
	
	public void stopClient() {
		try {
			mSocketChannel.close();
			
			logger.info("[연결 끊음]");
			
		} catch (IOException e) {
			logger.error("", e);
		}
	}
	
	public void send(byte[] bSendData) {
		Thread thread = new Thread() {

			@Override
			public void run() {
				try {
					ByteBuffer byteBuffer = ByteBuffer.wrap(bSendData);
					mSocketChannel.write(byteBuffer);
					
					String sSendData = new String(bSendData, mScharsetName);
					logger.info("[보내기 완료: {}]", sSendData);
					
				} catch (Exception e) {
					logger.error("", e);
					stopClient();
				}
			}
			
		};
		
		thread.start();
	}
	
	public String receive() {
		String sRecvData = null;
		
		while (true) {
			try {
				ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
				int nByteCnt = mSocketChannel.read(byteBuffer);
				
				if ( nByteCnt == -1 ) {
					throw new IOException();
				}
				
				byteBuffer.flip();
				
				Charset charset = Charset.defaultCharset();
				sRecvData = charset.decode(byteBuffer).toString();
				
				logger.info("[받기 완료: {}]", sRecvData);
				
				break;
				
			} catch (Exception e) {
				logger.error("", e);
				
				stopClient();
				break;
			}
		}
		
		return sRecvData;
	}
	
}
