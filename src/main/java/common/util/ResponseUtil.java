package common.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 8. 13. 김대광	JavaDoc 작성 (SonarLint 지시에 따른 수정)
 * </pre>
 *
 *
 * @author 김대광
 */
public class ResponseUtil {

	private static final Logger logger = LoggerFactory.getLogger(ResponseUtil.class);

	/**
	 * @since 1.7
	 */
	private static final String UTF8 = StandardCharsets.UTF_8.toString();

	private ResponseUtil() {
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

	/**
	 * 브라우저에 따른 파일명 인코딩 설정 (Content-Disposition 값 생성)
	 * @param request
	 * @param str
	 * @return
	 */
	public static String getEncodedFileName(HttpServletRequest request, String fileName) {
		Objects.requireNonNull(request, ExceptionMessage.isNull("request"));

		if ( StringUtils.isBlank(fileName) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("fileName"));
		}

		try {
			return URLEncoder.encode(fileName, UTF8).replace("+", "%20");

		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
			return fileName;
		}
	}

	/**
	 * 브라우저별로 최적화된 파일 다운로드 응답 헤더를 설정
	 * @param request
	 * @param response
	 * @param fileName
	 */
	public static void setContentDisposition(HttpServletRequest request, HttpServletResponse response, String fileName) {
		Objects.requireNonNull(request, ExceptionMessage.isNull("request"));
		Objects.requireNonNull(response, ExceptionMessage.isNull("response"));

		if ( StringUtils.isBlank(fileName) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("fileName"));
		}

		String encodedFileName = getEncodedFileName(request, fileName);

		String userAgent = request.getHeader("User-Agent");

		response.setHeader("Content-Transfer-Encoding", "binary");

		// IE / Edge (Trident) 대응
		if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
			response.setHeader("Content-Disposition", "attachment; fileName=\"" + encodedFileName+ "\"");
		} else {
			// 최신 브라우저: filename* 파라미터 사용 (RFC 5987)
			response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);
		}
	}

	/**
	 * HttpServletResponse 객체에 JSON 문자열을 직접 작성하여 응답
	 * @param response
	 * @param message
	 * @throws IOException
	 */
	public static void writeJsonResponse(HttpServletResponse response, String message) throws IOException {
		Objects.requireNonNull(response, ExceptionMessage.isNull("response"));

		if ( StringUtils.isBlank(message) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("message"));
		}

		response.setContentType("application/json");
		response.setCharacterEncoding(UTF8);
		response.getWriter().write(message);
	}

}
