package common.util.file;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 9. 17. 김대광	최초작성
 * </pre>
 *
 *
 * @author 김대광
 */
public class FileByteArrayUtil {

	private static final Logger logger = LoggerFactory.getLogger(FileByteArrayUtil.class);

	private FileByteArrayUtil() {
		super();
	}

	/**
	 * 폴더 구분자
	 */
	private static final String FOLDER_SEPARATOR = "/";

	public static class FileViewDownloadVo implements Serializable {

		private static final long serialVersionUID = 1L;

		protected byte[] fileByte;
		protected String fileMimeType;
		protected String fileName;
	}

	/**
	 * 미디어 파일 뷰어 및 모든 파일 다운로드 위한 바이트 배열 가져오기
	 *
	 * <pre>
	 * Spring 에서 활용
	 * </pre>
	 *
     * <pre>
     * if ( "view".equalsIgnoreCase(mode) ) {
     * 	return ResponseEntity.status(HttpStatus.OK)
     * 		.header(HttpHeaders.CONTENT_TYPE, vo.fileMimeType)
     * 		.body(byteFile);
     * } else if ( "download".equalsIgnoreCase(mode) ) {
     * 	return ResponseEntity.ok()
     * 		.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + vo.fileName + "\"")
     * 		.header(HttpHeaders.CONTENT_TYPE, vo.fileMimeType)
     * 		.body(byteFile);
     * } else {
     * 	return ResponseEntity.badRequest().body("Invalid mode parameter");
     * }
     * </pre>
	 *
	 * @param destFilePath
	 * @param fileNm
	 * @return
	 * @throws FileNotFoundException
	 */
	public static FileViewDownloadVo getFileBytesAndMimeType(String destFilePath, String fileNm) throws FileNotFoundException {
		FileViewDownloadVo vo = new FileViewDownloadVo();

		if ( StringUtils.isBlank(destFilePath) ) {
			throw new IllegalArgumentException("destFilePath는 null일 수 없습니다.");
		}

		if ( StringUtils.isBlank(fileNm) ) {
			throw new IllegalArgumentException("fileNm은 null일 수 없습니다.");
		}

		String fileFullPath = destFilePath + FOLDER_SEPARATOR + fileNm;
		File file = new File(fileFullPath);

		if ( !file.exists() ) {
			throw new FileNotFoundException(fileFullPath);
		}

		String fileMimeType = "application/octet-stream";

		try (
				BufferedInputStream fileInput = new BufferedInputStream(new FileInputStream(file));
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
		) {
			fileInput.mark(8192);	// MIME 타입 추출을 위한 mark
			String detectedMime = FileTypeUtil.getFileMimeTypeTika(fileInput);
			fileInput.reset();		// 스트림 위치 초기화

			if ( detectedMime != null && !detectedMime.isEmpty() ) {
				fileMimeType = detectedMime;
			}

			byte[] buffer = new byte[8192];
		    int bytesRead;
		    while ((bytesRead = fileInput.read(buffer)) != -1) {
		        outputStream.write(buffer, 0, bytesRead);
		    }

		    byte[] fileByte = outputStream.toByteArray();

		    vo.fileByte = fileByte;
		    vo.fileMimeType = fileMimeType;
		    vo.fileName = fileNm;
		} catch (IOException e) {
			logger.error("파일 처리 중 오류 발생 - 파일명: {}", fileNm, e);
		}

		return vo;
	}

}
