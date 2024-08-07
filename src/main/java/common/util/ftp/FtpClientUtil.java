package common.util.ftp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 7.  7. 김대광	최초작성
 * 2021. 8. 13. 김대광	SonarLint 지시에 따른 주저리 주저리 (그냥 기본 Exception 이나 RuntimeException 쓰자... 만들려면 또 exception 패키지 만들어서 어! 상속도 받고 어!... 힘들어...)
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

	private final String ENCODING = StandardCharsets.UTF_8.toString();

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
			ftpClient.setControlEncoding(this.ENCODING);
			int nReply = ftpClient.getReplyCode();

			if ( !FTPReply.isPositiveCompletion(nReply) ) {
				ftpClient.disconnect();
				throw new Exception(host + " FTP 서버 연결 실패");
			}

			ftpClient.login(username, password);

			ftpClient.enterLocalPassiveMode();

			this.procDestPath(ftpClient, destPath);
			isSucesss = this.procFile(ftpClient, destPath);

			ftpClient.logout();
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

			this.showServerReply(ftpClient);
		}
	}

	@SuppressWarnings("resource")
	private boolean procFile(FTPClient ftpClient, String destPath) throws IOException {
		boolean isSucesss = false;

		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

		FileInputStream fis = null;

		if ( !this.isBlank(this.sourcePath) ) {
			File dir = new File(this.sourcePath);

			File[] fileNames = null;

			if ( this.isBlank(this.extension) ) {
				fileNames = dir.listFiles();
			} else {
				String sExtension = this.extension.replace(".", "");

				fileNames = dir.listFiles(new FilenameFilter() {

					@Override
					public boolean accept(File dir, String name) {
						return name.endsWith(sExtension);
					}
				});
			}

			this.removeFile(ftpClient, destPath);

			// XXX : 디렉토리는 따로 지정해서 업로드, 명령어를 통한 방법도 동일함
			for (File file : fileNames) {
				if ( file.isFile() ) {
					fis = new FileInputStream(file);
					ftpClient.storeFile(file.getName(), fis);

					this.showServerReply(ftpClient);
				}
			}

			isSucesss = true;
		}

		if ( file != null ) {
			fis = new FileInputStream(file);
			isSucesss = ftpClient.storeFile(file.getName(), fis);

			this.showServerReply(ftpClient);
		}

		if ( fileList != null ) {
			fis = null;
			for (File file : fileList) {
				fis = new FileInputStream(file);
				ftpClient.storeFile(file.getName(), fis);

				this.showServerReply(ftpClient);
			}
		}

		if (fis != null) {
			fis.close();
		}

		return isSucesss;
	}

	private void removeFile(FTPClient ftpClient, String destPath) throws IOException {
		ftpClient.changeWorkingDirectory(destPath);

		FTPFile[] ftpFiles = ftpClient.listFiles();
		for (FTPFile ftpFile : ftpFiles) {
			ftpClient.deleteFile(ftpFile.getName());

			this.showServerReply(ftpClient);
		}
	}

	private boolean isBlank(final String str) {
		return (str == null) || (str.trim().length() == 0);
	}

	public boolean downloadAll(String host, int port, String username, String password, String destPath, String downloadPath) {
		return this.download(host, port, username, password, destPath, null, downloadPath);
	}

	public boolean download(String host, int port, String username, String password, String destPath, String fileName, String downloadPath) {
		boolean isSucesss = false;

		FTPClient ftpClient = new FTPClient();

		try {
			ftpClient.connect(host, port);
			ftpClient.setControlEncoding(this.ENCODING);
			int nReply = ftpClient.getReplyCode();

			if ( !FTPReply.isPositiveCompletion(nReply) ) {
				ftpClient.disconnect();
				throw new Exception(host + " FTP 서버 연결 실패");
			}

			ftpClient.login(username, password);

			ftpClient.enterLocalPassiveMode();

			this.procDownloadDestPath(ftpClient, destPath);

			isSucesss = this.procDownloadFile(ftpClient, destPath, fileName, downloadPath);

			ftpClient.logout();
			ftpClient.disconnect();

		} catch (Exception e) {
			logger.error("", e);
		}

		return isSucesss;
	}

	private void procDownloadDestPath(FTPClient ftpClient, String destPath) throws IOException {
		String[] pathElements = destPath.split("/");
		if ( pathElements != null && pathElements.length > 0 ) {

			for (String path : pathElements) {
				ftpClient.changeWorkingDirectory(path);
			}

		}
	}

	private boolean procDownloadFile(FTPClient ftpClient, String destPath, String fileName, String downloadPath) throws IOException {
		boolean isSucesss = false;

		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

		BufferedOutputStream bos = null;
		File fPath = null;
		File fDir = null;
		File f = null;

		if ( !this.isBlank(fileName) ) {
			fPath = new File(downloadPath);
			fDir = fPath;
			fDir.mkdirs();

			f = new File(downloadPath, fileName);

			bos = new BufferedOutputStream(new FileOutputStream(f));
			isSucesss = ftpClient.retrieveFile(fileName, bos);

			this.showServerReply(ftpClient);

		} else {
			FTPFile[] ftpFiles = ftpClient.listFiles(destPath);
			for (FTPFile ftpFile : ftpFiles) {
				// XXX : 디렉토리는 따로 지정해서 다운로드, 명령어를 통한 방법도 동일함 (시도해봤으나 실패함...)
				if ( ftpFile.isFile() ) {
					fPath = new File(downloadPath);
					fDir = fPath;
					fDir.mkdirs();

					f = new File(downloadPath, ftpFile.getName());

					bos = new BufferedOutputStream(new FileOutputStream(f));
					ftpClient.retrieveFile(ftpFile.getName(), bos);

					this.showServerReply(ftpClient);
				}
			}

			isSucesss = true;
		}

		return isSucesss;
	}

}
