package common.util.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.spi.FileTypeDetector;
import java.util.Arrays;
import java.util.List;

import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NioFileTypeUtil {
	
	private NioFileTypeUtil() {
		super();
	}
	
	private static final Logger logger = LoggerFactory.getLogger(NioFileTypeUtil.class);
	
	
	/**
	 * 파일 MIME Type 구하기
	 * @param filePath
	 * @return
	 */
	public static String getFileMimeType(String filePath) {
		String mimeType = "";
		Path path = Paths.get(filePath);
		
		try {
			mimeType = Files.probeContentType(path);
		} catch (IOException e) {
			logger.error("getFileMimeType IOException", e);
		}
		
		return mimeType;
	}
	
	/**
	 * <pre>
	 * 파일 MIME Type 구하기
	 *   - nio + Apache Tika 사용
	 * </pre>
	 * @param filePath
	 * @return
	 */
	public static String getFileMimeTypeTika(String filePath) {
		String mimeType = "";
		Path path = Paths.get(filePath);
		
		try {
			mimeType = new NioFileTypeDetector().probeContentType(path);
		} catch (IOException e) {
			logger.error("getFileMimeTypeTika IOException", e);
		}
		
		return mimeType;
	}
	
	private static class NioFileTypeDetector extends FileTypeDetector {
		private final Tika tika = new Tika();

		@Override
		public String probeContentType(Path path) throws IOException {
			return tika.detect(path);
		}
	}
	
	/**
	 * 문서 파일 체크
	 * @param sExtension
	 * @param sMimeType
	 * @return
	 */
	public static boolean isDocFile(String sExtension, String sMimeType) {
		String[] sExtArr = {
			"txt", "rtf", "pdf", 
			"doc", "docx", "ppt", "pptx", "xls", "xlsx", 
			"hwp", 
			"odt", "odp", "ods"
		};
		List<String> listExt = Arrays.asList(sExtArr);
		
		String[] sMimeArr = {
			"text/plain", "application/rtf", "application/pdf", 
			"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
			"application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation",
			"application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
			"application/x-hwp", "document/unknown", "application/unknown", "application/x-hwp-v5",
			"application/vnd.oasis.opendocument.text", "application/vnd.oasis.opendocument.presentation", "application/vnd.oasis.opendocument.spreadsheet"
		};
		List<String> listMime = Arrays.asList(sMimeArr);
		
		return listExt.contains(sExtension) && listMime.contains(sMimeType);
	}
	
	/**
	 * 이미지 파일 체크
	 * @param sExtension
	 * @param sMimeType
	 * @return
	 */
	public static boolean isImgFile(String sExtension, String sMimeType) {
		String[] sExtArr = {
			"jpg", "jpeg", "gif", "png"
		};
		List<String> listExt = Arrays.asList(sExtArr);
		
		String[] sMimeArr = {
			"image/jpeg", "image/gif", "image/png"
		};
		List<String> listMime = Arrays.asList(sMimeArr);
		
		return listExt.contains(sExtension) && listMime.contains(sMimeType);
	}

}
