package common.tcp.socket.multi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.Charset;

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
 * 2021.  8. 14. 김대광	SonarLint 지시에 따른 주저리 주저리 (Minor는 제낀다, 쓰레드라... null이나 빈값이면 Exception 필요할거 같은데... 아 몰라... Thread도 겁나 어려워...)
 * </pre>
 */
public class SocketClientThread {

	private static final Logger logger = LoggerFactory.getLogger(SocketClientThread.class);
	
	private static final int TIMEOUT = 15*1000;		// 15초
	
	private Socket mSocket;
	
	private String mScharsetName;
	
	public void startClient(String sServerIp, int nPort, String sCharsetName) {
		Thread thread = new Thread() {

			@Override
			public void run() {
				try {
					mSocket = new Socket();
					
					SocketAddress socketAddr = new InetSocketAddress(sServerIp, nPort);
					
					mSocket.connect(socketAddr, TIMEOUT);
					mSocket.setSoTimeout(TIMEOUT);
					
					logger.info("[연결 완료: {}]", mSocket.getInetAddress().getHostAddress());
					
					mScharsetName = sCharsetName;
					
				} catch (Exception e) {
					logger.error("", e);
					
					if ( mSocket.isConnected() ) {
						stopClient();
					}
				}
			}
			
		};
		
		thread.start();
	}
	
	public void stopClient() {
		try {
			mSocket.close();
			
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
					OutputStream os = mSocket.getOutputStream();
					BufferedOutputStream bos = new BufferedOutputStream(os);
					bos.write(bSendData);
					bos.flush();
					
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
				StringBuilder sb = new StringBuilder();
				
				InputStream is = mSocket.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);
				byte[] buffer = new byte[4096];
				
				int nRead = bis.read(buffer, 0, buffer.length);
				if (nRead > 0) {
					sb.append(new String(buffer, 0, nRead));
				}
				
				sRecvData = new String(sb.toString().getBytes(Charset.defaultCharset()));
				
				if ( sRecvData == null || "".equals(sRecvData) ) {
					throw new IOException();
				}
				
				logger.info("[받기 완료: {}]", sRecvData);
				
				break;
				
			} catch (IOException e) {
				logger.error("", e);
				
				stopClient();
				break;
			}
		}
		
		return sRecvData;
	}
	
}
