package common.util.excel;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2026. 1. 12. 김대광	최초작성
 * </pre>
 *
 *
 * @author 김대광
 */
public class ServletPoiUtil {

	private static final Logger logger = LoggerFactory.getLogger(ServletPoiUtil.class);

	private ServletPoiUtil() {
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
	 * 엑셀 파일 다운로드
	 * @param workbook
	 * @param request
	 * @param response
	 * @param fileName
	 * @param contentsList
	 * @param cellTitles
	 */
	public static void downloadExcel(Workbook workbook, HttpServletRequest request, HttpServletResponse response
			, String fileName, List<Map<String, Object>> contentsList, String[] cellTitles) {

		Objects.requireNonNull(workbook, ExceptionMessage.isNull("workbook"));
		Objects.requireNonNull(request, ExceptionMessage.isNull("request"));
		Objects.requireNonNull(response, ExceptionMessage.isNull("response"));

		if ( StringUtils.isBlank(fileName) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("fileName"));
		}

		if ( contentsList == null || contentsList.isEmpty() ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("contentsList"));
		}

		if ( cellTitles == null || cellTitles.length == 0 ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("cellTitles"));
		}

		setResponseForFile(request, response, fileName);

		try {
			workbook.write(response.getOutputStream());

		} catch (IOException e) {
			logger.error("", e);
		}
	}

	private static void setResponseForFile(HttpServletRequest request, HttpServletResponse response, String fileName) {
		String reportFileName = setFileNameByBrowser(request, fileName);

		response.setHeader("Content-Transfer-Encoding", "binary");
		response.setHeader("Content-Disposition", "attachment; fileName=\"" + reportFileName+ "\"");
	}

	private static String setFileNameByBrowser(HttpServletRequest request, String str) {
		String sRes = "";
		String userAgent = request.getHeader("User-Agent");

		try {
			final String UTF_8 = StandardCharsets.UTF_8.name();
			final String ISO_8859_1 = StandardCharsets.ISO_8859_1.name();

			if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
				sRes = URLEncoder.encode(str, UTF_8).replace("\\+", " ");
			} else {
				sRes = new String(str.getBytes(UTF_8), ISO_8859_1);
			}

		} catch (IOException e) {
			logger.error("", e);
		}

		return sRes;
	}

}
