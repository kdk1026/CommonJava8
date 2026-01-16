package common.util.excel;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.jxls.builder.JxlsOutput;
import org.jxls.transform.poi.JxlsPoiTemplateFillerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2026. 1. 16. 김대광	최초작성
 * </pre>
 *
 *
 * @author 김대광
 */
public class ServletJxls3Util {

	private static final Logger logger = LoggerFactory.getLogger(ServletJxls3Util.class);

	private ServletJxls3Util() {
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
	 * 템플릿 파일 이용해서 엑셀 파일 다운로드
	 * @param request
	 * @param response
	 * @param templateFileFullPath
	 * @param fileName
	 * @param dataMap
	 * @param attributeName
	 *
	 * <pre>
	 * {@code
	 * @GetMapping("/download-excel")
	 * public ResponseEntity<Void> downloadExcel(HttpServletRequest request, HttpServletResponse response)
	 * 	ServletJxls3Util.downloadExcel(request, response, templateFileFullPath, fileName, dataMap, attributeName);
	 *
	 * 	return ResponseEntity.noContent().build();
	 * }
	 * </pre>
	 */
	public static void downloadExcel(HttpServletRequest request, HttpServletResponse response
			, String templateFileFullPath, String fileName, Map<String, Object> dataMap, String attributeName) {

		Objects.requireNonNull(request, ExceptionMessage.isNull("request"));
		Objects.requireNonNull(response, ExceptionMessage.isNull("response"));

		if ( dataMap == null || dataMap.isEmpty() ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("contentsList"));
		}

		if ( StringUtils.isBlank(templateFileFullPath) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("templateFileFullPath"));
		}

		if ( StringUtils.isBlank(fileName) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("fileName"));
		}

		try (
			InputStream is = new BufferedInputStream(new FileInputStream(templateFileFullPath));
		) {
			fileName = setFileNameByBrowser(request, fileName);
			response.setHeader("Content-Disposition", "attachment; fileName=\"" + fileName+ "\"");

			Map<String, Object> data = new HashMap<>();
			data.put(attributeName, dataMap);

			JxlsPoiTemplateFillerBuilder.newInstance()
				.withTemplate(new File(templateFileFullPath))
				.build()
				.fill(data, new JxlsOutput() {

					@Override
					public OutputStream getOutputStream() throws IOException {
						return response.getOutputStream();
					}
				});

			response.getOutputStream().flush();

		} catch ( IOException e) {
			logger.error("", e);
		}
	}

	private static String setFileNameByBrowser(HttpServletRequest request, String str) {
		String fileName = "";
		String userAgent = request.getHeader("User-Agent");

		try {
			final String UTF_8 = StandardCharsets.UTF_8.name();
			// final String ISO_8859_1 = StandardCharsets.ISO_8859_1.name();

			if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
				fileName = URLEncoder.encode(str, UTF_8).replace("\\+", " ");
			} else {
				// 브라우저에서는 처리되지만 Swagger에서는 한글 깨짐
				// fileName = new String(str.getBytes(UTF_8), ISO_8859_1);

				fileName = URLEncoder.encode(str, "UTF-8");
			}

		} catch (IOException e) {
			logger.error("", e);
		}

		return fileName;
	}

}
