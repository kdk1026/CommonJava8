package common.util.ftp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
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
 * 2021. 8. 13. 김대광	SonarLint 지시에 따른 주저리 주저리
 * 2025. 5. 18. 김대광	AI가 추천한 Singleton 패턴으로 변경
 * 2025. 5. 29  김대광	static 으로 변경
 * </pre>
 *
 * @Description	Apache Commons Net 기반
 * @author 김대광
 */
public class FtpClientUtil {

	private static final Logger logger = LoggerFactory.getLogger(FtpClientUtil.class);

	private FtpClientUtil() {
		super();
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

	private static final String ENCODING = StandardCharsets.UTF_8.toString();

	private static String sourcePath = "";
	private static String extension = "";
	private static File file = null;
	private static List<File> fileList = null;

	public static boolean upload(String host, int port, String username, String password, String destPath, String sourcePath, String extension) {
		if ( StringUtils.isBlank(sourcePath) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("sourcePath"));
		}

		if ( StringUtils.isBlank(extension) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("extension"));
		}

		FtpClientUtil.sourcePath = sourcePath;
		FtpClientUtil.extension = extension;

		return upload(host, port, username, password, destPath);
	}

	public static boolean upload(String host, int port, String username, String password, String destPath, File file) {
		Objects.requireNonNull(file, ExceptionMessage.isNull("file"));


		FtpClientUtil.file = file;

		return upload(host, port, username, password, destPath);
	}

	public static boolean upload(String host, int port, String username, String password, String destPath, List<File> fileList) {
		if ( fileList == null || fileList.isEmpty() ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("fileList"));
		}

		FtpClientUtil.fileList = fileList;

		return upload(host, port, username, password, destPath);
	}

	private static boolean upload(String host, int port, String username, String password, String destPath) {
		if ( StringUtils.isBlank(host) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("host"));
		}

		if ( port < 0 || port > 65535 ) {
			throw new IllegalArgumentException(ExceptionMessage.isNegative("port"));
		}

		if ( StringUtils.isBlank(username) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("username"));
		}

		if ( StringUtils.isBlank(password) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("password"));
		}

		if ( StringUtils.isBlank(destPath) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("destPath"));
		}

		boolean isSucesss = false;

		FTPClient ftpClient = new FTPClient();

		try {
			ftpClient.connect(host, port);
			ftpClient.setControlEncoding(ENCODING);
			int nReply = ftpClient.getReplyCode();

			if ( !FTPReply.isPositiveCompletion(nReply) ) {
				ftpClient.disconnect();
				throw new IllegalArgumentException(host + " FTP 서버 연결 실패");
			}

			ftpClient.login(username, password);

			ftpClient.enterLocalPassiveMode();

			procDestPath(ftpClient, destPath);
			isSucesss = procFile(ftpClient, destPath);

			ftpClient.logout();
			ftpClient.disconnect();

		} catch (IOException e) {
			logger.error("", e);
		}

		return isSucesss;
	}

	private static void showServerReply(FTPClient ftpClient) {
		String[] replies = ftpClient.getReplyStrings();

		if (replies != null && replies.length > 0) {
			for (String aReply : replies) {
				logger.debug("SERVER : {}", aReply);
			}
		}
	}

	private static void procDestPath(FTPClient ftpClient, String destPath) throws IOException {
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

	@SuppressWarnings("resource")
	private static boolean procFile(FTPClient ftpClient, String destPath) throws IOException {
		boolean isSucesss = false;

		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

		FileInputStream fis = null;

		if ( !FtpClientUtil.isBlank(FtpClientUtil.sourcePath) ) {
			File dir = new File(FtpClientUtil.sourcePath);

			File[] fileNames = null;

			if ( FtpClientUtil.isBlank(FtpClientUtil.extension) ) {
				fileNames = dir.listFiles();
			} else {
				String sExtension = FtpClientUtil.extension.replace(".", "");

				fileNames = dir.listFiles(new FilenameFilter() {

					@Override
					public boolean accept(File dir, String name) {
						return name.endsWith(sExtension);
					}
				});
			}

			FtpClientUtil.removeFile(ftpClient, destPath);

			// XXX : 디렉토리는 따로 지정해서 업로드, 명령어를 통한 방법도 동일함
			for (File file : fileNames) {
				if ( file.isFile() ) {
					fis = new FileInputStream(file);
					ftpClient.storeFile(file.getName(), fis);

					FtpClientUtil.showServerReply(ftpClient);
				}
			}

			isSucesss = true;
		}

		if ( file != null ) {
			fis = new FileInputStream(file);
			isSucesss = ftpClient.storeFile(file.getName(), fis);

			showServerReply(ftpClient);
		}

		if ( fileList != null ) {
			fis = null;
			for (File file : fileList) {
				fis = new FileInputStream(file);
				ftpClient.storeFile(file.getName(), fis);

				showServerReply(ftpClient);
			}
		}

		if (fis != null) {
			fis.close();
		}

		return isSucesss;
	}

	private static void removeFile(FTPClient ftpClient, String destPath) throws IOException {
		ftpClient.changeWorkingDirectory(destPath);

		FTPFile[] ftpFiles = ftpClient.listFiles();
		for (FTPFile ftpFile : ftpFiles) {
			ftpClient.deleteFile(ftpFile.getName());

			showServerReply(ftpClient);
		}
	}

	private static boolean isBlank(final String str) {
		return (str == null) || (str.trim().isEmpty());
	}

	public static boolean downloadAll(String host, int port, String username, String password, String destPath, String downloadPath) {
		if ( StringUtils.isBlank(host) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("host"));
		}

		if ( port < 0 || port > 65535 ) {
			throw new IllegalArgumentException(ExceptionMessage.isNegative("port"));
		}

		if ( StringUtils.isBlank(username) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("username"));
		}

		if ( StringUtils.isBlank(password) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("password"));
		}

		if ( StringUtils.isBlank(destPath) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("destPath"));
		}

		if ( StringUtils.isBlank(downloadPath) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("downloadPath"));
		}

		return download(host, port, username, password, destPath, null, downloadPath);
	}

	public static boolean download(String host, int port, String username, String password, String destPath, String fileName, String downloadPath) {
		if ( StringUtils.isBlank(host) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("host"));
		}

		if ( port < 0 || port > 65535 ) {
			throw new IllegalArgumentException(ExceptionMessage.isNegative("port"));
		}

		if ( StringUtils.isBlank(username) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("username"));
		}

		if ( StringUtils.isBlank(password) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("password"));
		}

		if ( StringUtils.isBlank(destPath) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("destPath"));
		}

		if ( StringUtils.isBlank(fileName) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("fileName"));
		}

		if ( StringUtils.isBlank(downloadPath) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("downloadPath"));
		}

		boolean isSucesss = false;

		FTPClient ftpClient = new FTPClient();

		try {
			ftpClient.connect(host, port);
			ftpClient.setControlEncoding(ENCODING);
			int nReply = ftpClient.getReplyCode();

			if ( !FTPReply.isPositiveCompletion(nReply) ) {
				ftpClient.disconnect();
				throw new IllegalArgumentException(host + " FTP 서버 연결 실패");
			}

			ftpClient.login(username, password);

			ftpClient.enterLocalPassiveMode();

			procDownloadDestPath(ftpClient, destPath);

			isSucesss = procDownloadFile(ftpClient, destPath, fileName, downloadPath);

			ftpClient.logout();
			ftpClient.disconnect();

		} catch (IOException e) {
			logger.error("", e);
		}

		return isSucesss;
	}

	private static void procDownloadDestPath(FTPClient ftpClient, String destPath) throws IOException {
		String[] pathElements = destPath.split("/");
		if ( pathElements != null && pathElements.length > 0 ) {

			for (String path : pathElements) {
				ftpClient.changeWorkingDirectory(path);
			}

		}
	}

	private static boolean procDownloadFile(FTPClient ftpClient, String destPath, String fileName, String downloadPath) throws IOException {
		boolean isSucesss = false;

		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

		BufferedOutputStream bos = null;
		File fPath = null;
		File fDir = null;
		File f = null;

		if ( !FtpClientUtil.isBlank(fileName) ) {
			fPath = new File(downloadPath);
			fDir = fPath;
			fDir.mkdirs();

			f = new File(downloadPath, fileName);

			bos = new BufferedOutputStream(new FileOutputStream(f));
			isSucesss = ftpClient.retrieveFile(fileName, bos);

			showServerReply(ftpClient);

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

					showServerReply(ftpClient);
				}
			}

			isSucesss = true;
		}

		return isSucesss;
	}

}
