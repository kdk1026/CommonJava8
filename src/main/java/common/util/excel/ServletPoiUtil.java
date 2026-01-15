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
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
	 * @param request
	 * @param response
	 * @param fileName
	 * @param contentsList
	 * @param cellTitles
	 */
	public static void downloadExcel(HttpServletRequest request, HttpServletResponse response
			, String fileName, List<Map<String, Object>> contentsList, String[] cellTitles) {

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
			Workbook workbook = createWorkbookFromContents(fileName, contentsList, cellTitles);

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

	/**
	 * 컨텐츠로부터 Workbook 생성
	 * @param fileName
	 * @param contentsList
	 * @param cellTitles
	 * @return
	 */
	private static Workbook createWorkbookFromContents(String fileName, List<Map<String, Object>> contentsList, String[] cellTitles) {
		if ( StringUtils.isBlank(fileName) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("fileName"));
		}

		if ( contentsList == null || contentsList.isEmpty() ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("contentsList"));
		}

		if ( cellTitles == null || cellTitles.length == 0 ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("cellTitles"));
		}

		Workbook workbook = createEmptyWorkbook(fileName);
		if ( workbook == null ) {
			return null;
		}

		Sheet sheet = workbook.createSheet();

		createHeaderRow(sheet, cellTitles, createHeaderStyle(workbook));

		fillSheetWithData(sheet, contentsList, cellTitles);

		return workbook;
	}

	private static Workbook createEmptyWorkbook(String fileName) {
		if (fileName.endsWith(".xls")) {
			return new HSSFWorkbook();
	    } else if (fileName.endsWith(".xlsx")) {
	    	return new XSSFWorkbook();
	    }

	    return null;
	}

	private static CellStyle createHeaderStyle(Workbook workbook) {
		Font font = workbook.createFont();
	    font.setBold(true);

	    CellStyle style = workbook.createCellStyle();
	    style.setFillForegroundColor(HSSFColorPredefined.GREY_25_PERCENT.getIndex());
	    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    style.setFont(font);
	    return style;
	}

	private static void createHeaderRow(Sheet sheet, String[] titles, CellStyle style) {
	    Row headerRow = sheet.createRow(0);
	    for (int i = 0; i < titles.length; i++) {
	        Cell cell = headerRow.createCell(i);
	        cell.setCellStyle(style);
	        cell.setCellValue(titles[i]);
	    }
	}

	private static void fillSheetWithData(Sheet sheet, List<Map<String, Object>> contentsList, String[] cellTitles) {
	    for (int i = 0; i < contentsList.size(); i++) {
	        Row row = sheet.createRow(i + 1);
	        Map<String, Object> dataMap = contentsList.get(i);

	        for (int j = 0; j < cellTitles.length; j++) {
	            Object value = dataMap.get(cellTitles[j]);
	            Cell cell = row.createCell(j);

	            if (value == null) {
	                cell.setCellValue("");
	            } else if (value instanceof Integer) {
	                cell.setCellValue((Integer) value);
	            } else if (value instanceof Boolean) {
	                cell.setCellValue((Boolean) value);
	            } else if (value instanceof Double) {
	                cell.setCellValue((Double) value);
	            } else {
	                cell.setCellValue(String.valueOf(value));
	            }
	        }
	    }
	}

}
