/**
 * 
 */
package common.util.file;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 2018. 9. 5.
 * @author 김대광
 * @Description	: commons io Standard
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2018. 9. 5. 김대광	최초작성
 * </pre>
 */
public class CommonsFileUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(CommonsFileUtil.class);
	
	/**
	 * 폴더 구분자
	 */
	public static final String FOLDER_SEPARATOR = "/";
	
	/**
	 * 확장자 구분자
	 */
	public static final char EXTENSION_SEPARATOR = FilenameUtils.EXTENSION_SEPARATOR;

	private CommonsFileUtil() {
		super();
	}
	
	/**
	 * 파일의 존재여부 확인
	 * @param filePath
	 * @return
	 * @since 1.7
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
		return FilenameUtils.getBaseName(filePath);
	}
	
	/**
	 * 파일 확장자 구하기
	 * @param fileName
	 * @return
	 */
	public static String getFileExtension(String fileName) {
		return FilenameUtils.getExtension(fileName);
	}

	/**
	 * 파일 용량 구하기
	 * @param filename
	 * @return
	 */
	public static long getFileSize(String filePath) {
		File file = FileUtils.getFile(filePath);
		return FileUtils.sizeOf(file);
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
	    return FileUtils.byteCountToDisplaySize(fileSize);
	}
	
	/**
	 * 파일의 수정한 날짜 구하기
	 * @param filename
	 * @return
	 */
	public static String lastModified(String filePath) {
		File file = FileUtils.getFile(filePath);
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
		try {
			File file = FileUtils.getFile(filePath);
			FileUtils.writeStringToFile(file, text, Charset.defaultCharset());
			
		} catch (IOException e) {
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
		try {
			File file = FileUtils.getFile(filePath);
			FileUtils.writeStringToFile(file, text, encoding);
			
		} catch (IOException e) {
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
		
		try {
			File file = FileUtils.getFile(filePath);
			content = FileUtils.readFileToString(file, Charset.defaultCharset());
			
		} catch (IOException e) {
			logger.error("", e);
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
		
		try {
			File file = FileUtils.getFile(filePath);
			content = FileUtils.readFileToString(file, encoding);
			
		} catch (IOException e) {
			logger.error("", e);
		}
		
		return content;
	}
	
	/**
	 * 파일 삭제
	 * @param filePath
	 */
	public static boolean deleteFile(String filePath) {
		File file = FileUtils.getFile(filePath);
		return FileUtils.deleteQuietly(file);
	}
	
	/**
	 * 파일 복사
	 * @param srcFilePath
	 * @param destFilePath
	 */
	public static void copyFile(String srcFilePath, String destFilePath) {
		File srcFile = FileUtils.getFile(srcFilePath);
		File destFile = FileUtils.getFile(destFilePath);
		
		try {
			FileUtils.copyFile(srcFile, destFile);
			
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
		File file = FileUtils.getFile(filePath);
		
		File[] files = file.listFiles();
		for (File f : files) {
			listFiles.add(f.getName());
		}
		
		return listFiles;
	}
	
	/**
	 * 해당 경로의 파일 반환
	 * @param path
	 * @return
	 */
	public static List<String> getFileList(String filePath) {
		List<String> listFiles = new ArrayList<>();
		File file = FileUtils.getFile(filePath);
		
		File[] files = file.listFiles( 
				(FilenameFilter) new NotFileFilter(DirectoryFileFilter.DIRECTORY) );
		
		for (File f : files) {
			listFiles.add(f.getName());
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
		File file = FileUtils.getFile(filePath);
		
		File[] files = file.listFiles( 
				(FilenameFilter) DirectoryFileFilter.DIRECTORY );
		
		for (File f : files) {
			listFiles.add(f.getName());
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
		
		File file = FileUtils.getFile(filePath);
		
		try {
			bData = FileUtils.readFileToByteArray(file);
		} catch (IOException e) {
			logger.error("", e);
		}
		
		return bData;
	}

}
