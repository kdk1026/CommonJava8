package common.util.sshsftp;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * <pre>
 * SSH 클라이언트 유틸
 *  - Jsch Standard
 * </pre>
 *
 * @since 2019. 2. 12.
 * @author 김대광
 *
 *         <pre>
 * -----------------------------------
 * 개정이력
 * 2019. 2. 12. 김대광	최초작성
 * 2025. 3 .20  김대광	정리하면서 누락된 부분 추가
 *         </pre>
 */
public class SshClientUtil {

	private static final Logger logger = LoggerFactory.getLogger(SshClientUtil.class);

	public SshClientUtil() {
		super();
	}

	private Session session = null;
	private Channel channel = null;
	private ChannelExec channelExec = null;

	/**
	 * SSH 연결
	 *
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
			config.put("StrictHostKeyChecking", "no");
			config.put("PreferredAuthentications", "publickey,keyboard-interactive,password");

			session.setConfig(config);

			session.connect();

			channel = session.openChannel("exec");

			isConnected = true;
			logger.info("SSH Connected");

		} catch (Exception e) {
			isConnected = false;

			logger.info("SSH Not Connected");
			logger.error("", e);
		}

		if (isConnected) {
			channelExec = (ChannelExec) channel;
		}

		return isConnected;
	}

	/**
	 * 명령어 수행, 결과 응답
	 *
	 * @param sCommand
	 * @return
	 * @throws Exception
	 */
	public String runExecRet(String sCommand) throws Exception {
		String sRet = "";

		try {
			channelExec.setCommand(sCommand);
			channelExec.connect();

			InputStream is = channel.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			byte[] buffer = new byte[4096];

			StringBuilder sb = new StringBuilder();
			int nRead = bis.read(buffer, 0, buffer.length);
			if (nRead > 0) {
				sb.append(new String(buffer, 0, nRead));
			}

			sRet = sb.toString();

		} catch (Exception e) {
			logger.error("", e);
			throw e;
		}

		return sRet;
	}

	/**
	 * 명령어 수행
	 *
	 * @param sCommand
	 */
	public void runExec(String sCommand) throws Exception {
		try {
			channelExec.setCommand(sCommand);
			channelExec.connect();

		} catch (Exception e) {
			logger.error("", e);
			throw e;
		}
	}

	/**
	 * SSH 종료
	 */
	public void disconnect() {
		if (channelExec != null) {
			channelExec.disconnect();
		}

		if (channel != null) {
			channel.disconnect();
		}
		if (session != null) {
			session.disconnect();
		}
	}

}
