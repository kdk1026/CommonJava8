package common.util.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 7. 7. 김대광	최초작성
 * </pre>
 * 
 * @Description	Apache Commons Net 기반
 * @author 김대광
 */
public class FtpClientUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(FtpClientUtil.class);

	/*
	 * 외부에서 객체 인스턴스화 불가
	 */
	private FtpClientUtil() {
		super();
	}
	
	private static class LazyHolder {
		private static final FtpClientUtil INSTANCE = new FtpClientUtil();
	}

	public static FtpClientUtil getInstance() {
		return LazyHolder.INSTANCE;
	}
	
	private String sourcePath = "";
	private String extension = "";
	private File file = null;
	private List<File> fileList = null;
	
	public boolean upload(String host, int port, String username, String password, String destPath, String sourcePath, String extension) {
		this.sourcePath = sourcePath;
		this.extension = extension;
		
		return this.upload(host, port, username, password, destPath);
	}
	
	public boolean upload(String host, int port, String username, String password, String destPath, File file) {
		this.file = file;
		
		return this.upload(host, port, username, password, destPath);
	}
	
	public boolean upload(String host, int port, String username, String password, String destPath, List<File> fileList) {
		this.fileList = fileList;
		
		return this.upload(host, port, username, password, destPath);
	}
	
	private boolean upload(String host, int port, String username, String password, String destPath) {
		boolean isSucesss = false;
		
		FTPClient ftpClient = new FTPClient();
		
		try {
			ftpClient.connect(host, port);
			ftpClient.setControlEncoding(StandardCharsets.UTF_8.toString());
			int nReply = ftpClient.getReplyCode();
			
			if ( !FTPReply.isPositiveCompletion(nReply) ) {
				ftpClient.disconnect();
				throw new Exception(host + " FTP 서버 연결 실패");
			}
			
			ftpClient.login(username, password);
			
			ftpClient.enterLocalPassiveMode();
			
			this.procDestPath(ftpClient, destPath);
			isSucesss = this.procFile(ftpClient);
			
			ftpClient.disconnect();
			
		} catch (Exception e) {
			logger.error("", e);
		}
		
		return isSucesss;
	}
	
	private void showServerReply(FTPClient ftpClient) {
		String[] replies = ftpClient.getReplyStrings();
		
		if (replies != null & replies.length > 0) {
			for (String aReply : replies) {
				logger.debug("SERVER : {}", aReply);
			}
		}
	}
	
	private void procDestPath(FTPClient ftpClient, String destPath) throws IOException {
		String[] pathElements = destPath.split("/");
		if ( pathElements != null && pathElements.length > 0 ) {
			boolean isExist;
			
			for (String path : pathElements) {
				isExist = ftpClient.changeWorkingDirectory(path);
				if ( !isExist ) {
					boolean isCreate = ftpClient.makeDirectory(path);
					showServerReply(ftpClient);
					
					if ( isCreate ) {
						ftpClient.changeWorkingDirectory(ftpClient.printWorkingDirectory() + "/" + path);
					}
				}
			}
			
		} else {
			ftpClient.makeDirectory(destPath);
			showServerReply(ftpClient);				
		}
	}
	
	private boolean procFile(FTPClient ftpClient) throws IOException {
		boolean isSucesss = false;
		
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		
		FileInputStream fis = null;
		
		if ( !this.isBlank(this.sourcePath) && !this.isBlank(this.extension) ) {
			File dir = new File(this.sourcePath);
			
			String sExtension = this.extension.replace(".", "");
					
			File[] fileNames = dir.listFiles(new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(sExtension);
				}
			});
			
			for (File file : fileNames) {
				fis = new FileInputStream(file);
				ftpClient.storeFile(file.getName(), fis);
			}
			
			isSucesss = true;
		}
		
		if ( file != null ) {
			fis = new FileInputStream(file);
			isSucesss = ftpClient.storeFile(file.getName(), fis);
		}
		
		if ( fileList != null ) {
			fis = null;
			for (File file : fileList) {
				fis = new FileInputStream(file);
				ftpClient.storeFile(file.getName(), fis);
			}
		}
		
		if (fis != null) {
			fis.close();
		}
		
		return isSucesss;
	}
	
	private boolean isBlank(final String str) {
		return (str == null) || (str.trim().length() == 0);
	}
	
}
