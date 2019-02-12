package common.util.sshsftp;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * <pre>
 * SFTP 클라이언트 유틸
 *  - Jsch Standard
 * </pre>
 * @since 2019. 2. 12.
 * @author 김대광
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2019. 2. 12. 김대광	최초작성
 * </pre>
 */
public class SftpClientUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(SftpClientUtil.class);

	public SftpClientUtil() {
		super();
	}
	
	private Session session = null;
	private Channel channel = null;
	private ChannelSftp channelSftp = null;

	/**
	 * SFTP 연결
	 * @param sHost
	 * @param nPort
	 * @param sUsername
	 * @param sPassword
	 * @return
	 */
	public boolean init(String sHost, int nPort, String sUsername, String sPassword) {
		boolean isConnected = false;
		
		JSch jsch = new JSch();
		
		try {
			session = jsch.getSession(sUsername, sHost, nPort);
			session.setPassword(sPassword);
			
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", 	"no");
			config.put("PreferredAuthentications", 	"publickey,keyboard-interactive,password");
			
			session.setConfig(config);
			
			session.connect();
			
			channel = session.openChannel("sftp");
			
			
			isConnected = true;
			logger.info("SFTP Connected");
			
		} catch (Exception e) {
			isConnected = false;
			
			logger.info("SFTP Not Connected");
			logger.error("", e);
		}
		
		if (isConnected) {
			channelSftp = (ChannelSftp) channel;
		}
		
		return isConnected;
	}
	
	/**
	 * 파일 전송
	 * @param sDestPath
	 * @param file
	 */
	public void upload(String sDestPath, File file) {
		try ( FileInputStream fis = new FileInputStream(file) ) {
			channelSftp.cd(sDestPath);
			channelSftp.rm(file.getAbsolutePath());
			channelSftp.put(fis, file.getName());
			
		} catch (Exception e) {
			logger.error("", e);
		}
	}
	
	/**
	 * SFTP 종료
	 */
	public void disconnect() {
		if (channelSftp != null) {
			channelSftp.disconnect();
		}
		
		if (channel != null) {
			channel.disconnect();
		}
		if (session != null) {
			session.disconnect();
		}
	}
	
}
