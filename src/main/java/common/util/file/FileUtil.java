package common.util.file;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 8. 13. 김대광	SonarLint 지시에 따른 주저리 주저리 (안바꿔 이건 Java 6 전용이거든~ Java 7 NIO 알고부터는 버렸거든 그리 알도록)
 * </pre>
 *
 *
 * @author 김대광
 * @Description	: 1.6 기반
 */
public class FileUtil {

	private FileUtil() {
		super();
	}

	private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

	/**
	 * 폴더 구분자
	 */
	public static final String FOLDER_SEPARATOR = "/";

	/**
	 * 확장자 구분자
	 */
	public static final char EXTENSION_SEPARATOR = '.';

	private static final int BUFFER_SIZE = 4096;

	/**
	 * 파일의 존재여부 확인
	 * @param filePath
	 * @return
	 */
	public static boolean isExistsFile(String filePath) {
		if ( StringUtils.isBlank(filePath) ) {
			throw new IllegalArgumentException("filePath is null");
		}

		File file = new File(filePath);
        return file.exists();
    }

	/**
	 * 해당 경로에서 파일명 추출
	 * @param filePath
	 * @return
	 */
	public static String getFilename(String filePath) {
		if ( StringUtils.isBlank(filePath) ) {
			throw new IllegalArgumentException("filePath is null");
		}

		int pos = filePath.lastIndexOf(FOLDER_SEPARATOR);
		return (pos != -1 ? filePath.substring(pos + 1) : filePath);
	}

	/**
	 * 파일 확장자 구하기
	 * @param fileName
	 * @return
	 */
	public static String getFileExtension(String fileName) {
		if ( StringUtils.isBlank(fileName) ) {
			throw new IllegalArgumentException("fileName is null");
		}

		if (fileName.lastIndexOf(EXTENSION_SEPARATOR) == -1) {
			return null;
		}
		int pos = fileName.lastIndexOf(EXTENSION_SEPARATOR);
		return fileName.substring(pos + 1);
	}

	/**
	 * 파일 용량 구하기
	 * @param filePath
	 * @return
	 */
	public static long getFileSize(String filePath) {
		if ( StringUtils.isBlank(filePath) ) {
			throw new IllegalArgumentException("filePath is null");
		}

		File file = new File(filePath);
		return file.length();
	}

	/**
	 * <pre>
	 * 파일 용량 구하기
	 *   - B, KB, MB, GB, TB
	 * </pre>
	 * @param fileSize
	 * @return
	 */
	public static String readableFileSize(long fileSize) {
		if ( fileSize < 0 ) {
			throw new IllegalArgumentException("fileSize is negative");
		}

		if (fileSize <= 0) return "0";
		String[] units = { "B", "KB", "MB", "GB", "TB" };

	    int digitGroups = (int) (Math.log10(fileSize)/Math.log10(1024));
	    return new DecimalFormat("#,##0.#").format(fileSize/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	/**
	 * 파일의 수정한 날짜 구하기
	 * @param filePath
	 * @return
	 */
	public static String lastModified(String filePath) {
		if ( StringUtils.isBlank(filePath) ) {
			throw new IllegalArgumentException("filePath is null");
		}

		File file = new File(filePath);
		Date date = new Date(file.lastModified());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}

	/**
	 * 텍스트 내용을 행당 경로에 파일로 생성
	 * @param filePath
	 * @param text
	 */
	public static void writeFile(String filePath, String text) {
		if ( StringUtils.isBlank(filePath) ) {
			throw new IllegalArgumentException("filePath is null");
		}

		if ( StringUtils.isBlank(text) ) {
			throw new IllegalArgumentException("text is null");
		}

		File file = new File(filePath);

		try (
			FileOutputStream fos = new FileOutputStream(file);
			OutputStream os = fos;
        ) {
			os.write(text.getBytes());
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/**
	 * 텍스트 내용을 행당 경로에 파일로 생성
	 * @param filePath
	 * @param text
	 * @param encoding
	 */
	public static void writeFile(String filePath, String text, String encoding) {
		if ( StringUtils.isBlank(filePath) ) {
			throw new IllegalArgumentException("filePath is null");
		}

		if ( StringUtils.isBlank(text) ) {
			throw new IllegalArgumentException("text is null");
		}

		if ( StringUtils.isBlank(encoding) ) {
			throw new IllegalArgumentException("encoding is null");
		}

		File file = new File(filePath);

		try (
			FileOutputStream fos = new FileOutputStream(file);
			OutputStream os = fos;
        ) {
			os.write(text.getBytes(encoding));
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/**
	 * 파일을 텍스트로 읽음
	 * @param filePath
	 * @return
	 */
	public static String readFile(String filePath) {
		if ( StringUtils.isBlank(filePath) ) {
			throw new IllegalArgumentException("filePath is null");
		}

		File file = new File(filePath);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		try (
				FileInputStream fis = new FileInputStream(file);
				InputStream is = new BufferedInputStream(fis)
        ) {

			int nRead = 0;
			byte[] buffer = new byte[BUFFER_SIZE];

			while ( (nRead = is.read(buffer)) != -1) {
				bos.write(buffer, 0, nRead);
			}

			bos.flush();

		} catch (Exception e) {
			logger.error("", e);
		}

		return bos.toString();
	}

	/**
	 * 파일을 텍스트로 읽음
	 * @param filePath
	 * @param encoding
	 * @return
	 */
	public static String readFile(String filePath, String encoding) {
		if ( StringUtils.isBlank(filePath) ) {
			throw new IllegalArgumentException("filePath is null");
		}

		if ( StringUtils.isBlank(encoding) ) {
			throw new IllegalArgumentException("encoding is null");
		}

		File file = new File(filePath);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		try (
				FileInputStream fis = new FileInputStream(file);
				InputStream is = new BufferedInputStream(fis)
        ) {

			int nRead = 0;
			byte[] buffer = new byte[BUFFER_SIZE];

			while ( (nRead = is.read(buffer)) != -1) {
				bos.write(buffer, 0, nRead);
			}

			bos.flush();

		} catch (Exception e) {
			logger.error("", e);
		}

		String content = "";
		byte[] bData = bos.toByteArray();

		try {
			content = new String(bData, encoding);
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		} finally {
			try {
				bos.close();
			} catch (IOException e) {
				logger.error("", e);
			}
		}

		return content;
	}

	/**
	 * 파일 삭제
	 * @param filePath
	 */
	public static boolean deleteFile(String filePath) {
		if ( StringUtils.isBlank(filePath) ) {
			throw new IllegalArgumentException("filePath is null");
		}

		File file = new File(filePath);

		if ( file.isDirectory() ) {
			File[] files = file.listFiles();

            for (File f : files) {
                deleteFile(f.getPath());
                logger.debug("파일이 삭제되었습니다.");
            }

            file.delete();
            logger.debug("폴더가 삭제되었습니다.");

		} else {
			logger.debug("파일이 삭제되었습니다.");
			return file.delete();
		}

        return true;
	}

	/**
	 * 파일 복사
	 * @param srcFilePath
	 * @param destFilePath
	 */
	public static void copyFile(String srcFilePath, String destFilePath) {
		if ( StringUtils.isBlank(srcFilePath) ) {
			throw new IllegalArgumentException("srcFilePath is null");
		}

		if ( StringUtils.isBlank(destFilePath) ) {
			throw new IllegalArgumentException("destFilePath is null");
		}

		File srcFile = new File(srcFilePath);
		File destFile = new File(destFilePath);

		try (
				FileInputStream fis = new FileInputStream(srcFile);
				InputStream is = new BufferedInputStream(fis);
				FileOutputStream fos = new FileOutputStream(destFile);
				OutputStream os = fos;
        ) {
			int nRead = 0;
			byte[] buffer = new byte[BUFFER_SIZE];

			while ( (nRead = is.read(buffer)) != -1) {
				os.write(buffer, 0, nRead);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/**
	 * 해당 경로의 모든 파일 및 디렉토리를 반환
	 * @param filePath
	 * @return
	 */
	public static List<String> getAllFileList(String filePath) {
		if ( StringUtils.isBlank(filePath) ) {
			throw new IllegalArgumentException("filePath is null");
		}

		File file = new File(filePath);
		if (file.isDirectory()) {
			return Arrays.asList(file.list());
		}
		return new ArrayList<>();
	}

	/**
	 * 해당 경로의 파일 반환
	 * @param filePath
	 * @return
	 */
	public static List<String> getFileList(String filePath) {
		if ( StringUtils.isBlank(filePath) ) {
			throw new IllegalArgumentException("filePath is null");
		}

		List<String> listFiles = new ArrayList<>();
		File file = new File(filePath);

		if (file.isDirectory()) {
			File[] files = file.listFiles();

			for (File f : files) {
				if (f.isFile()) {
					listFiles.add(f.getName());
				}
			}
		} else {
			listFiles.add(file.getName());
		}

		return listFiles;
	}

	/**
	 * 해당 경로의 디렉토리 반환
	 * @param filePath
	 * @return
	 */
	public static List<String> getDirectoryList(String filePath) {
		if ( StringUtils.isBlank(filePath) ) {
			throw new IllegalArgumentException("filePath is null");
		}

		List<String> listDirectories = new ArrayList<>();
		File file = new File(filePath);

		if (file.isDirectory()) {
			File[] files = file.listFiles();

			for (File f : files) {
				if (f.isDirectory()) {
					listDirectories.add(f.getName());
				}
			}
		}

		return listDirectories;
	}

	/**
	 * 파일을 byte[]로 변환
	 * @param filePath
	 * @return
	 */
	public static byte[] convertFileToBytes(String filePath) {
		if ( StringUtils.isBlank(filePath) ) {
			throw new IllegalArgumentException("filePath is null");
		}

		File file = new File(filePath);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		try (
				FileInputStream fis = new FileInputStream(file);
				InputStream is = new BufferedInputStream(fis);
		) {
			int nRead = 0;
			byte[] buffer = new byte[BUFFER_SIZE];

			while ( (nRead = is.read(buffer)) != -1) {
				bos.write(buffer, 0, nRead);
			}

			bos.flush();

		} catch (IOException e) {
			logger.error("", e);
		}

		return bos.toByteArray();
	}
}
