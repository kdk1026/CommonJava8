package common.util.sshsftp;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SshClientUtil {

	private SshClientUtil() {
		super();
	}
	
	private static final Logger logger = LoggerFactory.getLogger(SshClientUtil.class);

	public static String execResult(String host, int port, String username, String password, String command) {
		StringBuilder sb = null;
		Session session = null;
		Channel channel = null;
		
		JSch jsch = new JSch();
		
		try {
			session = jsch.getSession(username, host, port);
			session.setPassword(password);
			
			session.setConfig("StrictHostKeyChecking", "no");
			session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
			
			session.connect();
			channel = session.openChannel("exec");
			
			ChannelExec channelExec = (ChannelExec) channel;
			channelExec.setCommand(command);
			channelExec.connect();
			
			InputStream is = channel.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			byte[] buffer = new byte[4096];
			
			sb = new StringBuilder();
			int nRead = bis.read(buffer, 0, buffer.length);
			if (nRead > 0) {
				sb.append(new String(buffer, 0, nRead));
			}
		
		} catch (JSchException e) {	
			logger.error("execResult JSchException", e);
		} catch (IOException e) {
			logger.error("execResult IOException", e);
		} finally {
			if (channel != null) {
				channel.disconnect();
			}
			if (session != null) {
				session.disconnect();
			}
		}
		
		return (sb != null) ? sb.toString() : null;
	}
	
	public static void exec(String host, int port, String username, String password, String command) {
		Session session = null;
		Channel channel = null;
		
		try {
			JSch jsch = new JSch();
			
			session = jsch.getSession(username, host, port);
			session.setPassword(password);
			
			session.setConfig("StrictHostKeyChecking", "no");
			session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
			
			session.connect();
			channel = session.openChannel("exec");
			
			ChannelExec channelExec = (ChannelExec) channel;
			channelExec.setCommand(command);
			channelExec.connect();
			
		} catch (JSchException e) {
			logger.error("execResult JSchException", e);
		} finally {
			if (channel != null) {
				channel.disconnect();
			}
			if (session != null) {
				session.disconnect();
			}
		}
	}
	
}
