package common.util.file;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;

import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 8. 13. 김대광	SonarLint 지시에 따른 주저리 주저리 (정규식은 방법이 없는 듯 하구나...)
 * </pre>
 * 
 *
 * @author 김대광
 * @Description	: 1.6 기반
 */
public class FileTypeUtil {

	private FileTypeUtil() {
		super();
	}

	private static final Logger logger = LoggerFactory.getLogger(FileTypeUtil.class);

	/**
	 * 파일 MIME Type 구하기
	 * @param filePath
	 * @return
	 */
	public static String getFileMimeType(String filePath) {
		File file = new File(filePath);
		MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

		return mimeTypesMap.getContentType(file);
	}

	/**
	 * <pre>
	 * 파일 MIME Type 구하기
	 *   - Apache Tika 사용
	 * </pre>
	 * @param filePath
	 * @return
	 */
	public static String getFileMimeTypeTika(String filePath) {
		String mimeType = "";
		File file = new File(filePath);
		Tika tika = new Tika();

		try {
			mimeType = tika.detect(file);

		} catch (IOException e) {
			logger.error("getFileMimeType IOException", e);
		}

		return mimeType;
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
	
	/**
	 * 실행 파일 체크
	 * @param sFileName
	 * @return
	 */
	public static boolean isRunableFile(String sFileName) {
		final String RUNABLE_FILE_EXT = "^(.*\\.)(?i)(bat|bin|cmd|com|cpl|dll|exe|gadget|inf1|ins|isu|jse|lnk|msc|msi|msp|mst|paf|pif|ps1|reg|rgs|scr|sct|sh|shb|shs|u3p|vb|vbe|vbs|vbscript|ws|wsf|wsh)$";
		
		if(sFileName == null) return false;
		
		return sFileName.matches(RUNABLE_FILE_EXT);
	}

}
