package common.util.file;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 1.7
 */
public class NioFileUtil {

	private NioFileUtil() {
		super();
	}
	
	private static final Logger logger = LoggerFactory.getLogger(NioFileUtil.class);
	
	/**
	 * 폴더 구분자
	 */
	public static final String FOLDER_SEPARATOR = "/";
	
	/**
	 * 확장자 구분자
	 */
	public static final char EXTENSION_SEPARATOR = '.';
	
	/**
	 * 파일의 존재여부 확인
	 * @param filePath
	 * @return
	 */
	public static boolean isExistsFile(String filePath) {
		Path path = Paths.get(filePath);
        return path.toFile().exists();
    }
	
	/**
	 * 해당 경로에서 파일명 추출
	 * @param filePath
	 * @return
	 */
	public static String getFilename(String filePath) {
		if (filePath == null) {
			return null;
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
		if (fileName.lastIndexOf(EXTENSION_SEPARATOR) == -1) {
			return null;
		}
		int pos = fileName.lastIndexOf(EXTENSION_SEPARATOR);
		return fileName.substring(pos + 1);
	}
	
	/**
	 * 파일 용량 구하기
	 * @param filename
	 * @return
	 */
	public static long getFileSize(String filePath) {
		Path path = Paths.get(filePath);
		
		try {
			return Files.size(path);
			
		} catch (IOException e) {
			return 0;
		}
	}
	
	/**
	 * <pre>
	 * 파일 용량 구하기
	 *   - B, KB, MB, GB, TB
	 * </pre>
	 * @param size
	 * @return
	 */
	public static String readableFileSize(long fileSize) {
		if (fileSize <= 0) return "0";
		String[] units = { "B", "KB", "MB", "GB", "TB" };
		
	    int digitGroups = (int) (Math.log10(fileSize)/Math.log10(1024));
	    return new DecimalFormat("#,##0.#").format(fileSize/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}
	
	/**
	 * 파일의 수정한 날짜 구하기
	 * @param filename
	 * @return
	 */
	public static String lastModified(String filePath) {
		Path path = Paths.get(filePath);
		
		FileTime time = null;
		try {
			time = Files.getLastModifiedTime(path);
			
		} catch (IOException e) {
			logger.error("lastModified IOException", e);
		}
		
		long lTime = (time != null) ? time.toMillis() : 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(lTime);
	}
	
	/**
	 * 텍스트 내용을 행당 경로에 파일로 생성
	 * @param filePath
	 * @param text
	 */
	public static void writeFile(String filePath, String text) {
		Path path = Paths.get(filePath);
		
		try (InputStream is = new ByteArrayInputStream(text.getBytes())) {
			
			Files.copy(is, path);
			
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
		Path targetFile = Paths.get(filePath);
		
		try (InputStream is = new ByteArrayInputStream(text.getBytes(encoding))) {
			
			Files.copy(is, targetFile);
			
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
		String content = "";
		byte[] bData = null;
		
		try {
			bData = Files.readAllBytes( Paths.get(filePath) );
		} catch (IOException e) {
			logger.error("", e);
		}
		
		if (bData != null) {
			content = new String(bData);
		}
		
		return content;
	}
	
	/**
	 * 파일을 텍스트로 읽음
	 * @param filePath
	 * @param encoding
	 * @return
	 */
	public static String readFile(String filePath, String encoding) {
		String content = "";
		byte[] bData = null;
		
		try {
			bData = Files.readAllBytes( Paths.get(filePath) );
		} catch (IOException e) {
			logger.error("", e);
		}
		
		try {
			if (bData != null) {
				content = new String(bData, encoding);
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		}
		
		return content;
	}
	
	/**
	 * 파일 삭제
	 * @param filePath
	 */
	public static boolean deleteFile(String filePath) {
		Path path = Paths.get(filePath);

		try {
			Files.delete(path);
			
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * 파일 복사
	 * @param srcFilePath
	 * @param destFilePath
	 */
	public static void copyFile(String srcFilePath, String destFilePath) {
		Path srcFile = Paths.get(srcFilePath);
		Path destFile = Paths.get(destFilePath);
		
		try {
			Files.copy(srcFile, destFile, StandardCopyOption.REPLACE_EXISTING);
			
		} catch (IOException e) {
			logger.error("", e);
		}
	}
	
	/**
	 * 해당 경로의 모든 파일 및 디렉토리를 반환
	 * @param path
	 * @return
	 */
	public static List<String> getAllFileList(String filePath) {
		List<String> listFiles = new ArrayList<>();
		Path path = Paths.get(filePath);
		
		if ( path.toFile().isDirectory() ) {
			try ( DirectoryStream<Path> dir = Files.newDirectoryStream(path) ) {
				for (Path file : dir) {
					listFiles.add(file.getFileName().toString());
				}
				
			} catch (IOException e) {
				logger.error("", e);
			}
			return listFiles;
		}
		
		return new ArrayList<>();
	}
	
	/**
	 * 해당 경로의 파일 반환
	 * @param path
	 * @return
	 */
	public static List<String> getFileList(String filePath) {
		List<String> listFiles = new ArrayList<>();
		Path path = Paths.get(filePath);
		
		if ( path.toFile().isDirectory() ) {
			try ( DirectoryStream<Path> dir = Files.newDirectoryStream(path) ) {
				for (Path file : dir) {
					if ( !file.toFile().isDirectory() ) {
						listFiles.add(file.getFileName().toString());
					}
				}
				
			} catch (IOException e) {
				logger.error("", e);
			}
			
		} else {
			listFiles.add(path.getFileName().toString());
		}
		
		return listFiles;
	}
	
	/**
	 * 해당 경로의 디렉토리 반환
	 * @param path
	 * @return
	 */
	public static List<String> getDirectoryList(String filePath) {
		List<String> listFiles = new ArrayList<>();
		Path path = Paths.get(filePath);
		
		if ( path.toFile().isDirectory() ) {
			try ( DirectoryStream<Path> dir = Files.newDirectoryStream(path) ) {
				for (Path file : dir) {
					if ( file.toFile().isDirectory() ) {
						listFiles.add(file.getFileName().toString());
					}
				}
				
			} catch (IOException e) {
				logger.error("", e);
			}
			
		}
		
		return listFiles;
	}
	
	/**
	 * 파일을 byte[]로 변환
	 * @param filePath
	 * @return
	 */
	public static byte[] convertFileToBytes(String filePath) {
		byte[] bData = null;
		
		try {
			bData = Files.readAllBytes( Paths.get(filePath) );
		} catch (IOException e) {
			logger.error("", e);
		}
		
		return bData;
	}
	
}
