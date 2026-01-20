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
	 * <pre>
	 * 브라우저에 따른 인코딩 설정
	 *   - str은 다운로드할 파일명이나 출력할 문자열
	 * </pre>
	 * @param request
	 * @param str
	 * @return
	 */
	public static String contentDisposition(HttpServletRequest request, String str) {
		Objects.requireNonNull(request, ExceptionMessage.isNull("request"));

		if ( StringUtils.isBlank(str) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("str"));
		}

		String fileName = "";
		String userAgent = request.getHeader("User-Agent");

		try {
			if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
				fileName = URLEncoder.encode(str, UTF8).replace("\\+", " ");
			} else {
				// 브라우저에서는 처리되지만 Swagger에서는 한글 깨짐
				// fileName = new String(str.getBytes(UTF8), StandardCharsets.ISO_8859_1);

				fileName = URLEncoder.encode(str, StandardCharsets.UTF_8.toString());
			}

		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		}

		return fileName;
	}

	public static void downloadReportFile(HttpServletRequest request, HttpServletResponse response, String fileName) {
		Objects.requireNonNull(request, ExceptionMessage.isNull("request"));
		Objects.requireNonNull(response, ExceptionMessage.isNull("response"));

		if ( StringUtils.isBlank(fileName) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("fileName"));
		}

		String reportFileName = contentDisposition(request, fileName);

		response.setHeader("Content-Transfer-Encoding", "binary");
		response.setHeader("Content-Disposition", "attachment; fileName=\"" + reportFileName+ "\"");
	}

	public static void setJsonResponse(HttpServletResponse response, String message) throws IOException {
		Objects.requireNonNull(response, ExceptionMessage.isNull("response"));

		if ( StringUtils.isBlank(message) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("message"));
		}

		response.setContentType("application/json");
		response.setCharacterEncoding(UTF8);
		response.getWriter().write(message);
	}

}
