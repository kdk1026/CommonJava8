package common.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NioNonBloackingSocketServer {
	
	private static final Logger logger = LoggerFactory.getLogger(NioNonBloackingSocketServer.class);
	
	private ServerSocketChannel serverSocketChannel;
	private String sRecvMsg;
	
	public void start(int nPort) throws IOException {
		serverSocketChannel = null;
		Selector selector = null;
		
		try {
			selector = Selector.open();
			serverSocketChannel = ServerSocketChannel.open();
			
			// (Non-Blocking 참고) https://gs.saro.me/#!m=elec&jn=535
			serverSocketChannel.configureBlocking(false);
			
			serverSocketChannel.bind(new InetSocketAddress(nPort));
			
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

			execute(selector);
			
		} catch (IOException e) {
			logger.error("start IOException", e);
			stop();
			
		} finally {
			if (selector != null) {
				selector.close();
			}
		}
	}
	
	public void stop() {
		try {
			serverSocketChannel.close();
			
		} catch (IOException e) {
			logger.error("stop IOException", e);
		}
	}
	
	public void execute(Selector selector) throws IOException {
		logger.info("Trying to connect to server : NioSocketServer");
		
		// TODO : 쓰레드 확인 
//		while (true) {
			selector.select();
			Iterator<SelectionKey> it = selector.selectedKeys().iterator();
			
			while (it.hasNext()) {
				SelectionKey selectionKey = it.next();
				SocketChannel clientSocketChannel = null;
				
				if (selectionKey.isAcceptable()) {
					clientSocketChannel = serverSocketChannel.accept();
					clientSocketChannel.configureBlocking(false);
					clientSocketChannel.register(selector, SelectionKey.OP_READ);
				} 
				else if(selectionKey.isReadable()) {
					clientSocketChannel = (SocketChannel) selectionKey.channel();
					receivedFromClient(clientSocketChannel);
					
					if (sRecvMsg.length() > 0) {
						sendToClient(clientSocketChannel);
					}
				}
				
				it.remove();
			}
//		}
	}
	
	private void receivedFromClient(SocketChannel clientSocketChannel) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(4096);
		clientSocketChannel.read(buffer);
		
		byte[] bRecvMsg = buffer.array();
		sRecvMsg = new String(bRecvMsg).trim();
		
		logger.info("NioSocketServer : [{}]", sRecvMsg);
	}
	
	private void sendToClient(SocketChannel clientSocketChannel) throws IOException {
		// 받은 데이터에 따른 분기 처리 및 그에 따른 응답 값 정의
		String sSendMsg = "Hi Client";
		
		Charset charset = Charset.forName("UTF-8");
		ByteBuffer buffer = charset.encode(sSendMsg);
		
		int nRet = clientSocketChannel.write(buffer);
		
		if (nRet > 0) {
			clientSocketChannel.close();
		}
	}
	
}
