package common.util;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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

	private ResponseUtil() {
		super();
	}

	private static final Logger logger = LoggerFactory.getLogger(ResponseUtil.class);

	/**
	 * @since 1.7
	 */
	private static final String UTF8 = StandardCharsets.UTF_8.toString();

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
		if ( request == null ) {
			throw new IllegalArgumentException("request");
		}

		if ( StringUtils.isBlank(str) ) {
			throw new IllegalArgumentException("str");
		}

		String sRes = "";
		String userAgent = request.getHeader("User-Agent");

		try {
			if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
				sRes = URLEncoder.encode(str, UTF8).replace("\\+", " ");
			} else {
				sRes = new String(str.getBytes(UTF8), StandardCharsets.ISO_8859_1);
			}

		} catch (Exception e) {
			logger.error("", e);
		}

		return sRes;
	}

	public static void downloadReportFile(HttpServletRequest request, HttpServletResponse response, String fileName) {
		if ( request == null ) {
			throw new IllegalArgumentException("request");
		}

		if ( response == null ) {
			throw new IllegalArgumentException("response");
		}

		if ( StringUtils.isBlank(fileName) ) {
			throw new IllegalArgumentException("fileName");
		}

		String reportFileName = contentDisposition(request, fileName);

		response.setHeader("Content-Transfer-Encoding", "binary");
		response.setHeader("Content-Disposition", "attachment; fileName=\"" + reportFileName+ "\"");
	}

	public static void setJsonResponse(HttpServletResponse response, String message) throws IOException {
		if ( response == null ) {
			throw new IllegalArgumentException("response");
		}

		if ( StringUtils.isBlank(message) ) {
			throw new IllegalArgumentException("message");
		}

		response.setContentType("application/json");
		response.setCharacterEncoding(UTF8);
		response.getWriter().write(message);
	}

}
