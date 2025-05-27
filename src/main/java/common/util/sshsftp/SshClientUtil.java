package common.util.sshsftp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
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
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2019. 2. 12. 김대광	최초작성
 * 2025. 3 .20  김대광	정리하면서 누락된 부분 추가
 * 2025. 5. 27  김대광    제미나이에 의한 코드 개선
 * </pre>
 */
public class SshClientUtil {

	private static final Logger logger = LoggerFactory.getLogger(SshClientUtil.class);

	public SshClientUtil() {
		super();
	}

	private Session session = null;

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
		if ( StringUtils.isBlank(sHost) ) {
			throw new IllegalArgumentException("sHost is null");
		}

		if ( nPort <= 0 ) {
			throw new IllegalArgumentException("nPort is null");
		}

		if ( StringUtils.isBlank(sUsername) ) {
			throw new IllegalArgumentException("sUsername is null");
		}

		if ( StringUtils.isBlank(sPassword) ) {
			throw new IllegalArgumentException("sPassword is null");
		}

		JSch jsch = new JSch();

		try {
			session = jsch.getSession(sUsername, sHost, nPort);
			session.setPassword(sPassword);

			Properties config = new Properties();
			// 실제 운영 환경에서는 "yes"로 설정하고 known_hosts 파일을 관리하는 것이 보안상 안전
			config.put("StrictHostKeyChecking", "no");
			config.put("PreferredAuthentications", "publickey,keyboard-interactive,password");

			session.setConfig(config);
			session.connect();
			logger.info("SSH Connected to {}:{}", sHost, nPort);

			return true;
		} catch (JSchException e) {
			logger.error("SSH Connection failed to {}:{}: {}", sHost, nPort, e.getMessage());
			return false;
		}
	}

	/**
	 * 명령어 수행, 결과 응답
	 *
	 * @param sCommand
	 * @return
	 * @throws JSchException
	 * @throws IOException
	 * @throws Exception
	 */
	public String runExecRet(String sCommand) throws JSchException, IOException {
		if ( StringUtils.isBlank(sCommand) ) {
			throw new IllegalArgumentException("sCommand is null");
		}

		if (session == null || !session.isConnected()) {
            throw new JSchException("SSH session is not connected. Call init() first.");
        }

		ChannelExec channelExec = null;
		try {
			channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setCommand(sCommand);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();

            channelExec.setInputStream(null); // 명령에 대한 입력은 없음을 명시
            channelExec.setOutputStream(outputStream);
            channelExec.setErrStream(errorStream);

            channelExec.connect(); // 채널 연결 및 명령 실행

            // 명령 종료까지 대기
            while (!channelExec.isClosed()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Command execution interrupted.", e);
                }
            }

            int exitStatus = channelExec.getExitStatus();
            String stdout = outputStream.toString(StandardCharsets.UTF_8.name());
            String stderr = errorStream.toString(StandardCharsets.UTF_8.name());

            if (exitStatus != 0) {
            	// 명령이 성공적으로 실행되지 않았을 경우
            	String errorMessage = String.format("Command '%s' failed with exit status %d. Stderr: %s",
                        sCommand, exitStatus, stderr);
            	logger.error(errorMessage);
            }

            // 표준 에러가 있지만 종료 코드가 0인 경우 (경고성 메시지 등)
            if (!stderr.isEmpty()) {
            	logger.warn("Command '{}' finished with warnings (stderr): {}", sCommand, stderr);
                return stdout + "\n" + stderr; // stderr도 반환에 포함시킬 수 있음
            }

            logger.info("Command '{}' executed successfully. Exit Status: {}", sCommand, exitStatus);
            return stdout;
		} finally {
			if (channelExec != null) {
				channelExec.disconnect();
			}
		}
	}

	/**
	 * 명령어 수행 (결과 응답 없이), 종료 상태 확인
	 *
	 * @param sCommand
	 * @throws JSchException
	 * @throws IOException
	 */
	public int runExec(String sCommand) throws JSchException, IOException {
		if ( StringUtils.isBlank(sCommand) ) {
			throw new IllegalArgumentException("sCommand is null");
		}

		if (session == null || !session.isConnected()) {
            throw new JSchException("SSH session is not connected. Call init() first.");
        }

        ChannelExec channelExec = null;
        try {
            channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setCommand(sCommand);

            // 출력을 읽지 않을 경우, stream을 무시
            channelExec.setInputStream(null);
            channelExec.setOutputStream(null);
            channelExec.setErrStream(null);

            channelExec.connect(); // 채널 연결 및 명령 실행

            // 명령 종료까지 대기
            while (!channelExec.isClosed()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Command execution interrupted.", e);
                }
            }

            int exitStatus = channelExec.getExitStatus();
            logger.info("Command '{}' executed. Exit Status: {}", sCommand, exitStatus);
            return exitStatus;

        } finally {
            if (channelExec != null) {
                channelExec.disconnect();
            }
        }
	}

	/**
	 * <pre>
	 * SSH 종료
	 *  - 세션만 해제합니다. 채널은 각 명령 실행 후 자동으로 해제
	 * </pre>
	 */
	public void disconnect() {
		if (session != null) {
			session.disconnect();
		}
	}

}
