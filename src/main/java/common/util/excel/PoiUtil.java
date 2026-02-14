package common.util.excel;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
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
 * 개정이력
 * -----------------------------------
 * 2021. 8.  6. 김대광	Javadoc 작성
 * 2021. 8. 13. 김대광	SonarLint 지시에 따른 주저리 주저리
 * 2022. 5. 25. 김대광	deprecated 수정
 * 2026. 1. 12. 김대광	ServletPoiUtil 으로 일부 분리
 * 2026. 1. 15. 김대광	AI 도움으로 메소드 분리
 * </pre>
 *
 * @author 김대광
 */
public class PoiUtil {

	private static final Logger logger = LoggerFactory.getLogger(PoiUtil.class);

	private PoiUtil() {
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
	 * 엑셀 파일 읽기
	 * @param file
	 * @param customKeys
	 * @param startRow
	 * @return
	 */
	public static List<Map<String, Object>> readExcel(File file, String[] customKeys, int startRow) {
		Objects.requireNonNull(file, ExceptionMessage.isNull("file"));

		if ( customKeys == null || customKeys.length <= 0 ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("cellNames"));
		}

		try ( InputStream is = new BufferedInputStream(new FileInputStream(file)) ) {
			Workbook workbook = createWorkbook(is, file.getName());

			if ( workbook == null || workbook.getNumberOfSheets() == 0 ) {
				return Collections.emptyList();
			}

			return parseSheet(workbook.getSheetAt(0), customKeys, startRow);


		} catch (IOException e) {
			logger.error("", e);
			return Collections.emptyList();
		}
	}

	private static Workbook createWorkbook(InputStream is, String fileName) throws IOException {
		if (fileName.endsWith(".xls")) {
	        return new HSSFWorkbook(is);
	    } else if (fileName.endsWith(".xlsx")) {
	        return new XSSFWorkbook(is);
	    }

		return null;
	}

	private static List<Map<String, Object>> parseSheet(Sheet sheet, String[] customKeys, int startRow) {
		List<Map<String, Object>> resList = new ArrayList<>();
		int nRowCnt = sheet.getLastRowNum();

		for (int rowIdx = startRow; rowIdx <= nRowCnt; rowIdx++) {
			Row row = sheet.getRow(rowIdx);
			if (row == null) continue;

			// 행 단위 처리
			Map<String, Object> map = new HashMap<>();
			int nCellCnt = row.getPhysicalNumberOfCells();

			for (int cellIdx = 0; cellIdx < nCellCnt; cellIdx++) {
				if (cellIdx >= customKeys.length) break;

				Cell cell = row.getCell(cellIdx);
				String columnName = customKeys[cellIdx];
				map.put(columnName, getCellValue(cell));
			}

			resList.add(map);
		}

		return resList;
	}

	private static Object getCellValue(Cell cell) {
		if (cell == null || cell.getCellType() == CellType.BLANK) {
	        return "";
	    }

		DataFormatter formatter = new DataFormatter();

		switch (cell.getCellType()) {
		case NUMERIC:
			return formatter.formatCellValue(cell);
		case STRING:
			return cell.getStringCellValue();
		case FORMULA:
			return cell.getCellFormula();
		case BOOLEAN:
			return cell.getBooleanCellValue();
		case ERROR:
			return cell.getErrorCellValue();

		default:
			return "";
		}
	}

	/**
	 * 엑셀 파일 생성
	 * @param destFilePath
	 * @param fileName
	 * @param contentsList
	 * @param cellTitles
	 * @return
	 */
	public static boolean writeExcel(String destFilePath, String fileName, List<Map<String, Object>> contentsList, String[] cellTitles) {
		if ( StringUtils.isBlank(destFilePath) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("destFilePath"));
		}

		if ( StringUtils.isBlank(fileName) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("fileName"));
		}

		if ( contentsList == null || contentsList.isEmpty() ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("contentsList"));
		}

		if ( cellTitles == null || cellTitles.length == 0 ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("cellTitles"));
		}

		Workbook workbook = createWorkbookFromContents(fileName, contentsList, cellTitles);

		try (
			FileOutputStream fis = new FileOutputStream(new File(destFilePath + File.separator + fileName));
			OutputStream os = new BufferedOutputStream(fis)
		) {
		    workbook.write(os);
		    return true;
		} catch (IOException e) {
		    logger.error("", e);
		    return false;
		}
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
