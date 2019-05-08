package common.util.excel;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
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
	 * 엑셀 파일 읽기
	 * @param file
	 * @param cellNames
	 * @return
	 */
	public static List<Map<String, Object>> readExcel(File file, String[] cellNames) {
		List<Map<String, Object>> resList = new ArrayList<>();
		
		String sFileName = file.getName();
		String sFileExt = sFileName.substring(sFileName.lastIndexOf('.') + 1);

		try {
			InputStream is = new BufferedInputStream(new FileInputStream(file));
			Workbook wb = null;
			
			switch (sFileExt) {
			case "xls":
				wb = new HSSFWorkbook(is);
				break;
				
			case "xlsx":
				wb = new XSSFWorkbook(is);
				break;

			default:
				break;
			}
			
			is.close();
			
			Sheet sheet = wb.getSheetAt(0);
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
					int cellType = cell.getCellType();
					
					switch (cellType) {
					case Cell.CELL_TYPE_BLANK:
						obj = "";
						break;
						
					case Cell.CELL_TYPE_NUMERIC:
						obj = cell.getNumericCellValue();
						break;
						
					case Cell.CELL_TYPE_STRING:
						obj = cell.getStringCellValue();
						break;
						
					case Cell.CELL_TYPE_FORMULA:
						obj = cell.getCellFormula();
						break;
						
					case Cell.CELL_TYPE_BOOLEAN:
						obj = cell.getBooleanCellValue();
						break;
						
					case Cell.CELL_TYPE_ERROR:
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
			
		} catch (Exception e) {
			logger.error("", e);
		}
		return resList;
	}
	
	/**
	 * 엑셀 파일 생성
	 * @param destFilePath
	 * @param fileName
	 * @param contentsList
	 * @return
	 */
	public static boolean writeExcel(String destFilePath, String fileName, List<Map<String, Object>> contentsList) {
		boolean isSuccess = false;

		String sFileExt = fileName.substring(fileName.lastIndexOf('.') + 1);
		
		Workbook wb = null;
		
		switch (sFileExt) {
		case "xls":
			wb = new HSSFWorkbook();
			break;
			
		case "xlsx":
			wb = new XSSFWorkbook();
			break;

		default:
			break;
		}

		Sheet sheet = wb.createSheet();
		Row row = null;

		Map<String, Object> dataMap = null;
		int nCellCnt = 0;

		// 타이틀
		row = sheet.createRow(0);
		dataMap = contentsList.get(0);
		
		for ( String sKey : dataMap.keySet() ) {
			row.createCell(nCellCnt).setCellValue(sKey);
			nCellCnt = nCellCnt + 1;
		}

		// 내용
		for (int i=0; i < contentsList.size(); i++) {
			nCellCnt = 0;

			row = sheet.createRow(i+1);
			dataMap = contentsList.get(i);
			
			for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
				Object value = entry.getValue();
				
				if ( value instanceof String ) {
					row.createCell(nCellCnt).setCellValue((String) value);
				}
				else if ( value instanceof Integer ) {
					row.createCell(nCellCnt).setCellValue((Integer) value);
				}
				else if ( value instanceof Boolean ) {
					row.createCell(nCellCnt).setCellValue((Boolean) value);
				}
				
				nCellCnt = nCellCnt + 1;
			}
		}

		try {
			File outFile = new File(destFilePath + File.separator + fileName);
			OutputStream os = new BufferedOutputStream(new FileOutputStream(outFile));
			wb.write(os);

			os.close();
			isSuccess = true;

		} catch (Exception e) {
			logger.error("", e);
		}
		
		return isSuccess;
	}
	
}
