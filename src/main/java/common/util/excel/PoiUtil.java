package common.util.excel;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * 개정이력
 * -----------------------------------
 * 2021. 8.  6. 김대광	Javadoc 작성
 * 2021. 8. 13. 김대광	SonarLint 지시에 따른 주저리 주저리
 * 2022. 5. 25. 김대광	deprecated 수정
 * </pre>
 *
 * @author 김대광
 */
public class PoiUtil {

	private static final Logger logger = LoggerFactory.getLogger(PoiUtil.class);

	private PoiUtil() {
		super();
	}

	/**
	 * 엑셀 파일 읽기
	 * @param file
	 * @param cellNames
	 * @return
	 */
	public static List<Map<String, Object>> readExcel(File file, String[] cellNames, boolean isDecimal) {
		if ( file == null ) {
			throw new IllegalArgumentException("file is null");
		}

		if ( cellNames == null ) {
			throw new IllegalArgumentException("cellNames is null");
		}

		List<Map<String, Object>> resList = new ArrayList<>();

		String sFileName = file.getName();
		String sFileExt = sFileName.substring(sFileName.lastIndexOf('.') + 1);

		try ( InputStream is = new BufferedInputStream(new FileInputStream(file)) ) {
			Workbook workbook = null;

			switch (sFileExt) {
			case "xls":
				workbook = new HSSFWorkbook(is);
				break;

			case "xlsx":
				workbook = new XSSFWorkbook(is);
				break;

			default:
				break;
			}

			if ( workbook == null ) {
				return Collections.emptyList();
			}

			if ( workbook.getNumberOfSheets() == 0 ) {
				return Collections.emptyList();
			}

			Sheet sheet = workbook.getSheetAt(0);
			int nRowCnt = sheet.getPhysicalNumberOfRows();

			for (int rowIdx=0; rowIdx < nRowCnt; rowIdx++) {
				Row row = sheet.getRow(rowIdx+1);

				if (row == null) {
					continue;
				}

				int nCellCnt = row.getPhysicalNumberOfCells();
				Map<String, Object> map = new HashMap<>();

				for (int cellIdx=0; cellIdx < nCellCnt; cellIdx++) {
					Cell cell = row.getCell(cellIdx);

					if (cell == null) {
						continue;
					}

					Object obj = null;

					switch (cell.getCellTypeEnum()) {
					case BLANK:
						obj = "";
						break;

					case NUMERIC:
						double numericValue = cell.getNumericCellValue();
						obj = isDecimal ? numericValue : (int) numericValue;
						break;

					case STRING:
						obj = cell.getStringCellValue();
						break;

					case FORMULA:
						obj = cell.getCellFormula();
						break;

					case BOOLEAN:
						obj = cell.getBooleanCellValue();
						break;

					case ERROR:
						obj = cell.getErrorCellValue();
						break;

					default:
						break;
					}

					String sCellNm = "";

					if ( cellNames != null ) {
						sCellNm = cellNames[cellIdx];
					} else {
						sCellNm = cell.getSheet().getRow(0).getCell(cellIdx).getRichStringCellValue().toString();
					}

					map.put(sCellNm, obj);
				}

				resList.add(map);
			}

		} catch (IOException e) {
			logger.error("", e);
		}
		return resList;
	}

	/**
	 * 컨텐츠로부터 Workbook 생성
	 * @param fileName
	 * @param contentsList
	 * @param cellTitles
	 * @return
	 */
	private static Workbook createWorkbookFromContents(String fileName, List<Map<String, Object>> contentsList, String[] cellTitles) {
		if ( fileName == null ) {
			throw new IllegalArgumentException("fileName is null");
		}

		if ( contentsList == null || contentsList.isEmpty() ) {
			throw new IllegalArgumentException("contentsList is null");
		}

		if ( cellTitles == null || cellTitles.length == 0 ) {
			throw new IllegalArgumentException("cellTitles is null");
		}

		String sFileExt = fileName.substring(fileName.lastIndexOf('.') + 1);

		Workbook workbook = null;

		switch (sFileExt) {
		case "xls":
			workbook = new HSSFWorkbook();
			break;

		case "xlsx":
			workbook = new XSSFWorkbook();
			break;

		default:
			break;
		}

		if ( workbook == null ) {
			return null;
		}

		Sheet sheet = workbook.createSheet();
		Row row = null;

		Font font = workbook.createFont();
		font.setBold(true);

		CellStyle cellStyle = workbook.createCellStyle();

		cellStyle.setFillForegroundColor(HSSFColorPredefined.GREY_25_PERCENT.getIndex());
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cellStyle.setFont(font);

		int nCellCnt = 0;

		// 타이틀
		row = sheet.createRow(0);

		for ( String sCellTitle : cellTitles ) {
			Cell rowCell = row.createCell(nCellCnt);

			rowCell.setCellStyle(cellStyle);
			rowCell.setCellValue(sCellTitle);
			nCellCnt = nCellCnt + 1;
		}

		// 내용
		for (int i=0; i < contentsList.size(); i++) {
			nCellCnt = 0;

			row = sheet.createRow(i+1);
			Map<String, Object> dataMap = contentsList.get(i);

			for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
				Object value = entry.getValue();

				if ( value == null ) {
					row.createCell(nCellCnt).setCellValue("");
				}
				else if ( value instanceof String ) {
					row.createCell(nCellCnt).setCellValue((String) value);
				}
				else if ( value instanceof Integer ) {
					row.createCell(nCellCnt).setCellValue((Integer) value);
				}
				else if ( value instanceof Boolean ) {
					row.createCell(nCellCnt).setCellValue((Boolean) value);
				}
				else {
					row.createCell(nCellCnt).setCellValue(String.valueOf(value));
				}

				nCellCnt = nCellCnt + 1;
			}
		}

		return workbook;
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
			throw new IllegalArgumentException("destFilePath is null");
		}

		boolean isSuccess = false;

		Workbook workbook = createWorkbookFromContents(fileName, contentsList, cellTitles);

		try {
			File outFile = new File(destFilePath + File.separator + fileName);
			OutputStream os = new BufferedOutputStream(new FileOutputStream(outFile));
			workbook.write(os);

			os.close();
			isSuccess = true;

		} catch (IOException e) {
			logger.error("", e);
		}

		return isSuccess;
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

		if ( request == null ) {
			throw new IllegalArgumentException("request is null");
		}

		if (response == null) {
			throw new IllegalArgumentException("response is null");
		}

		Workbook workbook = createWorkbookFromContents(fileName, contentsList, cellTitles);

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
