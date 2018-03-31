package common.util.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PoiUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(PoiUtil.class);
	
	private PoiUtil() {
		super();
	}

	/**
	 * 엑셀 파일 생성
	 * @param strDestFilePath
	 * @param fileName
	 * @param list
	 * @return
	 */
	public static boolean writeExcel(String strDestFilePath, String fileName, List<Map<String, Object>> list) {
		boolean isSuccess = false;

		Workbook wb = null;
		String fileExt = fileName.substring(fileName.lastIndexOf('.')+1);
		
		if ( "xls".equals(fileExt) ) {
			wb = new HSSFWorkbook();
		}
		else if ( "xlsx".equals(fileExt) ) {
			wb = new XSSFWorkbook();
		}

		Sheet sheet = wb.createSheet();
		Row row = null;

		Map<String, Object> dataMap = null;
		int cellCnt = 0;

		// 타이틀
		row = sheet.createRow(0);
		dataMap = list.get(0);
		for ( String key : dataMap.keySet() ) {
			row.createCell(cellCnt).setCellValue(key);
			cellCnt = cellCnt + 1;
		}

		// 내용
		for (int i=0; i < list.size(); i++) {
			cellCnt = 0;

			row = sheet.createRow(i+1);
			dataMap = list.get(i);
			
			for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
				Object value = entry.getValue();
				
				if ( value instanceof String ) {
					row.createCell(cellCnt).setCellValue((String) value);
				}
				else if ( value instanceof Integer ) {
					row.createCell(cellCnt).setCellValue((Integer) value);
				}
				else if ( value instanceof Boolean ) {
					row.createCell(cellCnt).setCellValue((Boolean) value);
				}
				
				cellCnt = cellCnt + 1;
			}
		}

		OutputStream os = null;
		try {
			File outFile = new File(strDestFilePath + File.separator + fileName);
			os = new FileOutputStream(outFile);
			wb.write(os);

			os.close();
			isSuccess = true;

		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (wb != null) {
				try {
					wb.close();
				} catch (IOException e) {
					logger.error("", e);
				}
			}
		}
		
		return isSuccess;
	}

	/**
	 * <pre>
	 * 엑셀 파일 읽기
	 * </pre>
	 * @param is
	 * @param fileName
	 * @return
	 */
	public static List<Map<String, Object>> readExcel(InputStream is, String fileName) {
		List<Map<String, Object>> resList = new ArrayList<>();

		Workbook wb = null;
		String fileExt = fileName.substring(fileName.lastIndexOf('.')+1);
		
		try {
			if ( "xls".equals(fileExt) ) {
				wb = new HSSFWorkbook(is);
			}
			else if ( "xlsx".equals(fileExt) ) {
				wb = new XSSFWorkbook(is);
			}

			Sheet sheet = wb.getSheetAt(0);
			int rowCnt = sheet.getPhysicalNumberOfRows();
			int cellCnt = 0;

			Row row = null;
			Cell cell = null;

			String cellName = "";
			Map<String, Object> map = null;
			Object obj = null;
			CellType cellType = null;
			
			for (int rowIdx=0; rowIdx < rowCnt; rowIdx++) {
				row = sheet.getRow(rowIdx+1);

				if (row == null) {
					break;
				}
				
				cellCnt = row.getPhysicalNumberOfCells();
				map = new HashMap<>();

				for (int cellIdx=0; cellIdx < cellCnt; cellIdx++) {
					cell = row.getCell(cellIdx);

					obj = null;
					
					cellType = cell.getCellTypeEnum();
					
					if ( cellType.equals(CellType.STRING) ) {
						obj = cell.getStringCellValue();
					}
					else if ( cellType.equals(CellType.NUMERIC) ) {
						obj = cell.getNumericCellValue();
					}
					else if ( cellType.equals(CellType.BOOLEAN) ) {
						obj = cell.getBooleanCellValue();
					}
					else if ( cellType.equals(CellType.ERROR) ) {
						obj = cell.getErrorCellValue();
					}
					else if ( cellType.equals(CellType.BLANK) ) {
						obj = "";
					}
					
					cellName = cell.getSheet().getRow(0).getCell(cellIdx).getRichStringCellValue().toString();
					map.put(cellName, obj);
				}
				resList.add(map);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return resList;
	}
	
}
