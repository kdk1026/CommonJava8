package common.util.sshsftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * <pre>
 * SFTP 클라이언트 유틸
 *  - Jsch Standard
 * </pre>
 *
 * @since 2019. 2. 12.
 * @author 김대광
 *
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2019. 2. 12. 김대광	최초작성
 * 2021. 8. 13. 김대광	JavaDoc pre 태그 공백 수정
 * 2025. 3 .20  김대광	정리하면서 누락된 부분 추가
 * 2025. 5. 27  김대광    제미나이에 의한 코드 개선
 * </pre>
 */
public class SftpClientUtil {

	private static final Logger logger = LoggerFactory.getLogger(SftpClientUtil.class);

	private SftpClientUtil() {

	}

	private static class ExceptionMessage {

		public static String isNull(String paramName) {
	        return String.format("'%s' is null", paramName);
	    }

		public static String isNullOrEmpty(String paramName) {
	        return String.format("'%s' is null or empty", paramName);
	    }

		public static String isNegative(String paramName) {
			return String.format("'%s' is negative", paramName);
		}

	}

	private Session session = null;
	private Channel channel = null;
	private ChannelSftp channelSftp = null;

	private static final String DEST_PATH = "sDestPath";

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
		if ( StringUtils.isBlank(sHost) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("sHost"));
		}

		if ( nPort < 0 || nPort > 65535 ) {
			throw new IllegalArgumentException(ExceptionMessage.isNegative("nPort"));
		}

		if ( StringUtils.isBlank(sUsername) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("sUsername"));
		}

		if ( StringUtils.isBlank(sPassword) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("sPassword"));
		}

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

		} catch (JSchException e) {
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
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws SftpException
	 * @throws Exception
	 */
	public void upload(String sDestPath, File file) throws IOException, SftpException {
		if ( StringUtils.isBlank(sDestPath) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(DEST_PATH));
		}

		Objects.requireNonNull(file, ExceptionMessage.isNull("file"));

		try ( FileInputStream fis = new FileInputStream(file) ) {
			channelSftp.cd(sDestPath);
			this.delete(sDestPath, file);
			channelSftp.put(fis, file.getName());

		}
	}

	/**
	 * 파일 삭제
	 *
	 * @param sDestPath
	 * @param file
	 * @throws SftpException
	 */
	public void delete(String sDestPath, File file) throws SftpException {
		if ( StringUtils.isBlank(sDestPath) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(DEST_PATH));
		}

		Objects.requireNonNull(file, ExceptionMessage.isNull("file"));

		String fileName = file.getName();

		// 원격 서버의 파일 경로를 올바르게 구성
		String remoteFilePath = sDestPath;
		if (!remoteFilePath.endsWith("/")) {
	        remoteFilePath += "/";
	    }
		remoteFilePath += fileName;

		try {
			channelSftp.rm(remoteFilePath);
		} catch (SftpException e) {
			if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
				logger.warn("File to delete not found on SFTP server: {}", remoteFilePath);
			} else {
				logger.error("Failed to delete file {}: {}", remoteFilePath, e.getMessage());
				throw e;
			}
		}
	}

	/**
	 * 해당 경로의 파일, 디렉토리 확인
	 *
	 * @param sDestPath
	 * @return
	 * @throws SftpException
	 */
	public Vector<LsEntry> ls(String sDestPath) throws SftpException {
		if ( StringUtils.isBlank(sDestPath) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(DEST_PATH));
		}

		@SuppressWarnings("unchecked")
		Vector<LsEntry> lsVec = channelSftp.ls(sDestPath);
		return lsVec;
	}

	/**
	 * 파일/디렉토리 명 변경
	 *
	 * @param sOldPath
	 * @param sNewPath
	 * @return
	 */
	public boolean rename(String sOldPath, String sNewPath) {
		if ( StringUtils.isBlank(sOldPath) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("sOldPath"));
		}

		if ( StringUtils.isBlank(sNewPath) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("sNewPath"));
		}

		boolean isSuccess = false;

		try {
			channelSftp.rename(sOldPath, sNewPath);
			isSuccess = true;
		} catch (SftpException e) {
			logger.error("", e);
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
