package common.util.file;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.spi.FileTypeDetector;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
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
 * @Description	: 1.7 기반
 */
public class NioFileTypeUtil {

	private NioFileTypeUtil() {
		super();
	}

	private static class ExceptionMessage {

		public static String isNull(String paramName) {
	        return String.format("'%s' is null", paramName);
	    }

		public static String isNullOrEmpty(String paramName) {
	        return String.format("'%s' is null or empty", paramName);
	    }

	}

	private static final Logger logger = LoggerFactory.getLogger(NioFileTypeUtil.class);

	private static class NioFileTypeDetector extends FileTypeDetector {
		private final Tika tika = new Tika();

		@Override
		public String probeContentType(Path path) throws IOException {
			return tika.detect(path);
		}
	}

	private static final String EXTENSION = "sExtension";
	private static final String MIMETYPE = "sMimeType";

	/**
	 * 파일 MIME Type 구하기
	 * @param filePath
	 * @return
	 */
	public static String getFileMimeType(String filePath) {
		if ( StringUtils.isBlank(filePath) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("filePath"));
		}

		String mimeType = "";
		Path path = Paths.get(filePath);

		try {
			mimeType = Files.probeContentType(path);
		} catch (IOException e) {
			logger.error("", e);
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
		if ( StringUtils.isBlank(filePath) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("filePath"));
		}

		String mimeType = "";
		Path path = Paths.get(filePath);

		try {
			mimeType = new NioFileTypeDetector().probeContentType(path);
		} catch (IOException e) {
			logger.error("", e);
		}

		return mimeType;
	}

	/**
	 * <pre>
	 * 파일 MIME Type 구하기
	 *   - Apache Tika 사용
	 * </pre>
	 * @param is
	 * @return
	 */
	public static String getFileMimeTypeTika(InputStream is) {
		Objects.requireNonNull(is, ExceptionMessage.isNull("is"));

		String mimeType = "";
		Tika tika = new Tika();

		try {
			mimeType = tika.detect(is);

		} catch (IOException e) {
			logger.error("", e);
		}

		return mimeType;
	}

	/**
	 * 파일 체크
	 * @param sExtension
	 * @param sMimeType
	 * @return
	 */
	public static boolean isAllFile(String sExtension, String sMimeType) {
		if ( StringUtils.isBlank(sExtension) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(EXTENSION));
		}

		if ( StringUtils.isBlank(sMimeType) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(MIMETYPE));
		}

		String[] sExtArr = {
			"jpg", "jpeg", "png", "gif", "bmp",
			"pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx",
			"hwp", "txt", "zip"
		};
		List<String> listExt = Arrays.asList(sExtArr);

		String[] sMimeArr = {
			"image/jpeg", "image/png", "image/gif", "image/bmp",
			"application/pdf",
			"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
			"application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
			"application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation",
			"application/x-hwp", "application/haansofthwp", "application/vnd.hancom.hwp",
			"text/plain", "application/zip"
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
		if ( StringUtils.isBlank(sExtension) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(EXTENSION));
		}

		if ( StringUtils.isBlank(sMimeType) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(MIMETYPE));
		}

		String[] sExtArr = {
			"jpg", "jpeg", "png", "gif", "bmp"
		};
		List<String> listExt = Arrays.asList(sExtArr);

		String[] sMimeArr = {
			"image/jpeg", "image/png", "image/gif", "image/bmp"
		};
		List<String> listMime = Arrays.asList(sMimeArr);

		return listExt.contains(sExtension) && listMime.contains(sMimeType);
	}

	/**
	 * 문서 파일 체크
	 * @param sExtension
	 * @param sMimeType
	 * @return
	 */
	public static boolean isDocFile(String sExtension, String sMimeType) {
		if ( StringUtils.isBlank(sExtension) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(EXTENSION));
		}

		if ( StringUtils.isBlank(sMimeType) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(MIMETYPE));
		}

		String[] sExtArr = {
			"pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx",
			"hwp", "txt"
		};
		List<String> listExt = Arrays.asList(sExtArr);

		String[] sMimeArr = {
			"application/pdf",
			"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
			"application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
			"application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation",
			"application/x-hwp", "application/haansofthwp", "application/vnd.hancom.hwp",
			"text/plain"
		};
		List<String> listMime = Arrays.asList(sMimeArr);

		return listExt.contains(sExtension) && listMime.contains(sMimeType);
	}

	/**
	 * 압축 파일 체크
	 * @param sExtension
	 * @param sMimeType
	 * @return
	 */
	public static boolean isArchiveFile(String sExtension, String sMimeType) {
		if ( StringUtils.isBlank(sExtension) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(EXTENSION));
		}

		if ( StringUtils.isBlank(sMimeType) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(MIMETYPE));
		}

		String[] sExtArr = {
			"zip", "rar", "7z"
		};
		List<String> listExt = Arrays.asList(sExtArr);

		String[] sMimeArr = {
			"application/zip", "application/x-rar-compressed", "application/x-7z-compressed"
		};
		List<String> listMime = Arrays.asList(sMimeArr);

		return listExt.contains(sExtension) && listMime.contains(sMimeType);
	}

	/**
	 * 오디오 파일 체크
	 * @param sExtension
	 * @param sMimeType
	 * @return
	 */
	public static boolean isAudioFile(String sExtension, String sMimeType) {
		if ( StringUtils.isBlank(sExtension) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(EXTENSION));
		}

		if ( StringUtils.isBlank(sMimeType) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(MIMETYPE));
		}

		String[] sExtArr = {
			"mp3", "wav"
		};
		List<String> listExt = Arrays.asList(sExtArr);

		String[] sMimeArr = {
			"audio/mpeg", "audio/wav"
		};
		List<String> listMime = Arrays.asList(sMimeArr);

		return listExt.contains(sExtension) && listMime.contains(sMimeType);
	}

	/**
	 * 비디오 파일 체크
	 * @param sExtension
	 * @param sMimeType
	 * @return
	 */
	public static boolean isVideoFile(String sExtension, String sMimeType) {
		if ( StringUtils.isBlank(sExtension) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(EXTENSION));
		}

		if ( StringUtils.isBlank(sMimeType) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(MIMETYPE));
		}

		String[] sExtArr = {
			"mp4", "avi", "mov", "mkv"
		};
		List<String> listExt = Arrays.asList(sExtArr);

		String[] sMimeArr = {
			"video/mp4", "video/x-msvideo", "video/quicktime", "video/x-matroska"
		};
		List<String> listMime = Arrays.asList(sMimeArr);

		return listExt.contains(sExtension) && listMime.contains(sMimeType);
	}

	/**
	 * <pre>
	 * 실행 파일 체크
	 *  - 결과가 true면 업로드 불가, false면 업로드 가능
	 * </pre>
	 * @param sExtension
	 * @return
	 */
	public static boolean isRunableFile(String sExtension) {
		if ( StringUtils.isBlank(sExtension) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty(EXTENSION));
		}

		String[] sExtArr = {
			"bat", "bin", "cmd", "com", "cpl", "dll", "exe", "gadget", "inf1",
            "ins", "isu", "jse", "lnk", "msc", "msi", "msp", "mst", "paf",
            "pif", "ps1", "reg", "rgs", "scr", "sct", "sh", "shb", "shs",
            "u3p", "vb", "vbe", "vbs", "vbscript", "ws", "wsf", "wsh"
		};
		List<String> listExt = Arrays.asList(sExtArr);

		return listExt.contains(sExtension);
	}

}
