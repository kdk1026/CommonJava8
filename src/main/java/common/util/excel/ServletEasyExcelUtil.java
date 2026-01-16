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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;

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
public class ServletEasyExcelUtil {

	private static final Logger logger = LoggerFactory.getLogger(ServletEasyExcelUtil.class);

	private ServletEasyExcelUtil() {
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
	 * @param listKey
	 *
	 * <pre>
	 * 템플릿 작성
	 * 	일반 : {customerName}
	 * 	리스트 : {.productName} | {.amount}
	 * </pre>
	 *
	 * <pre>
	 * {@code
	 * @GetMapping("/download-excel")
	 * public ResponseEntity<Void> downloadExcel(HttpServletRequest request, HttpServletResponse response)
	 * 	Map<String, Object> dataMap = new HashMap<>();
	 * 	dataMap.put("customerName", "홍길동");
	 * 	dataMap.put("productList", list);
	 *
	 * 	ServletEasyExcelUtil.downloadExcel(request, response, templateFileFullPath, fileName, dataMap, listKey);
	 *
	 * 	return ResponseEntity.noContent().build();
	 * }
	 * </pre>
	 */
	public static void downloadExcel(HttpServletRequest request, HttpServletResponse response
			, String templateFileFullPath, String fileName, Map<String, Object> dataMap, String listKey) {

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

		try (ExcelWriter excelWriter = EasyExcelFactory.write(response.getOutputStream()).withTemplate(templateFileFullPath).build()) {
		    WriteSheet writeSheet = EasyExcelFactory.writerSheet().build();

			fileName = setFileNameByBrowser(request, fileName);
			response.setHeader("Content-Disposition", "attachment; fileName=\"" + fileName+ "\"");

		    excelWriter.fill(dataMap, writeSheet);

		    if ( !StringUtils.isBlank(listKey) ) {
		    	@SuppressWarnings("unchecked")
		    	List<Map<String, Object>> list = (List<Map<String, Object>>) dataMap.get(listKey);

		    	FillConfig fillConfig = FillConfig.builder().forceNewRow(true).build();
		    	excelWriter.fill(list, fillConfig, writeSheet);
		    }
		} catch (IOException e) {
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

				fileName = URLEncoder.encode(str, UTF_8);
			}

		} catch (IOException e) {
			logger.error("", e);
		}

		return fileName;
	}

}
