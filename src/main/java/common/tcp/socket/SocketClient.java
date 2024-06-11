package common.tcp.socket;

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
public class SocketClient {
	
	private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);
	
	private static final int TIMEOUT = 15*1000;		// 15초
	
    private String sRecvData;
    
	public String getsRecvData() {
		return sRecvData;
	}

	private String mScharsetName;
	
	public void start(String sServerIp, int nPort, byte[] bSendData, String sCharsetName) throws IOException {
		SocketAddress socketAddr = new InetSocketAddress(sServerIp, nPort);
		
		try ( Socket socket = new Socket() ) {
			
			socket.connect(socketAddr, TIMEOUT);
			socket.setSoTimeout(TIMEOUT);
			
			logger.info("[연결 완료: {}]", socket.getRemoteSocketAddress());
			
			mScharsetName = sCharsetName;
			
			if ( socket.isConnected() ) {
				OutputStream os = socket.getOutputStream();
				InputStream is = socket.getInputStream();
				
				this.sendToServer(os, bSendData);
				this.receivedFromServer(is);
			}
			
		} catch (IOException e) {
			logger.error("", e);
			//throw e;
		}
	}
	
	private void sendToServer(OutputStream os, byte[] bSendData) throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(os);
		bos.write(bSendData);
		bos.flush();
		
		String sSendData = new String(bSendData, mScharsetName);
		logger.info("[보내기 완료: {}]", sSendData);
	}
	
	private void receivedFromServer(InputStream is) throws IOException {
		StringBuilder sb = new StringBuilder();
		
		BufferedInputStream bis = new BufferedInputStream(is);
		byte[] buffer = new byte[4096];
		
		int nRead = bis.read(buffer, 0, buffer.length);
		if (nRead > 0) {
			sb.append(new String(buffer, 0, nRead));
		}
		
		this.sRecvData = new String(sb.toString().getBytes(Charset.defaultCharset()));
		
		logger.info("[받기 완료: {}]", this.sRecvData);
	}
	
}
