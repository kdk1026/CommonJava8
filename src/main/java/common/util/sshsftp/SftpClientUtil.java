package common.util.sshsftp;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * <pre>
 * SFTP 클라이언트 유틸
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
 * 2021. 8. 13. 김대광	JavaDoc pre 태그 공백 수정
 * 2025. 3 .20  김대광	정리하면서 누락된 부분 추가
 *         </pre>
 */
public class SftpClientUtil {

	private static final Logger logger = LoggerFactory.getLogger(SftpClientUtil.class);

	public SftpClientUtil() {

	}

	private Session session = null;
	private Channel channel = null;
	private ChannelSftp channelSftp = null;

	/**
	 * SFTP 연결
	 *
	 * @param sHost
	 * @param nPort
	 * @param sUsername
	 * @param sPassword
	 * @return
	 * @throws JSchException
	 */
	public boolean init(String sHost, int nPort, String sUsername, String sPassword) throws JSchException {
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
			channelSftp.connect();
		}

		return isConnected;
	}

	/**
	 * 파일 전송
	 *
	 * @param sDestPath
	 * @param file
	 */
	public void upload(String sDestPath, File file) throws Exception {
		try (FileInputStream fis = new FileInputStream(file)) {
			channelSftp.cd(sDestPath);
			this.delete(sDestPath, file);
			channelSftp.put(fis, file.getName());

		} catch (Exception e) {
			logger.error("", e);
			throw e;
		}
	}

	/**
	 * 파일 삭제
	 *
	 * @param sDestPath
	 * @param file
	 */
	public void delete(String sDestPath, File file) throws Exception {
		try {
			@SuppressWarnings("unchecked")
			Vector<LsEntry> lsVec = channelSftp.ls(sDestPath);

			String sFileNm = "";
			for (LsEntry le : lsVec) {
				sFileNm = le.getFilename();

				if (sFileNm.equals(file.getName())) {
					channelSftp.rm(file.getAbsolutePath());
				}
			}

		} catch (Exception e) {
			logger.error("", e);
			throw e;
		}
	}

	/**
	 * 해당 경로의 파일, 디렉토리 확인
	 *
	 * @param sDestPath
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Vector<LsEntry> ls(String sDestPath) throws Exception {
		Vector<LsEntry> lsVec = null;

		try {
			lsVec = channelSftp.ls(sDestPath);

		} catch (Exception e) {
			logger.error("", e);
			throw e;
		}

		return lsVec;
	}

	/**
	 * 파일/디렉토리 명 변경
	 *
	 * @param sOldPath
	 * @param sNewPath
	 * @return
	 */
	public boolean rename(String sOldPath, String sNewPath) throws Exception {
		boolean isSuccess = false;

		try {
			channelSftp.rename(sOldPath, sNewPath);
			isSuccess = true;

		} catch (Exception e) {
			logger.error("", e);
			throw e;
		}

		return isSuccess;
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
