package common.util.excel;

import java.io.File;
import java.util.List;
import java.util.Map;

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
 * <pre>
 * 중국 알리바바닷컴에서 개발 (공식 깃허브 중국어)
 * 한국 외의 국가에서는 성능과 대규모 데이터 처리 때문에 JXLS보다 많이 사용함 (mvnrepository 사이트에서 엑셀 라이브러리 POI 다음으로 2위)
 * </pre>
 *
 * <pre>
 * 템플릿 작성
 * 	일반 : {customerName}
 * 	리스트 : {.productName} | {.amount}
 * </pre>
 *
 * <pre>
 * 템플릿 없이 쓰기 가능
 * - VO 멤버 변수에 @ExcelProperty("헤더 이름 지정")
 *
 * {@code
 * EasyExcel.write(fileName, UserData.class).sheet("목록").doWrite(dataList);
 * }
 * </pre>
 *
 * <pre>
 * 엑셀 파일 읽기 가능
 *
 * {@code
 * 리스너 정의
 * public class DemoDataListener extends AnalysisEventListener<DemoData>
 *	@Override
 *	public void invoke(DemoData data, AnalysisContext context)
 *		한 줄 읽을 때마다 실행할 로직 (예: DB 저장)
 *
 *	@Override
 *	public void doAfterAllAnalysed(AnalysisContext context)
 *		모든 데이터를 다 읽은 후 실행
 * }
 *
 * EasyExcel.read(fileName, DemoData.class, new DemoDataListener()).sheet().doRead();
 * </pre>
 * </pre>
 *
 * @author 김대광
 */
public class EasyExcelUtil {

	private static final Logger logger = LoggerFactory.getLogger(EasyExcelUtil.class);

	private EasyExcelUtil() {
		super();
	}

	private static class ExceptionMessage {

		public static String isNullOrEmpty(String paramName) {
	        return String.format("'%s' is null or empty", paramName);
	    }

	}

	/**
	 * 템플릿 파일 이용해서 엑셀 파일 생성
	 * @param templateFileFullPath
	 * @param destFilePath
	 * @param fileName
	 * @param dataMap
	 * @param listKey
	 * @return
	 *
	 * <pre>
	 * {@code
	 * dataMap.put("customerName", "홍길동");
	 * dataMap.put("productList", list);
	 * }
	 * </pre>
	 *
	 * <pre>
	 * 일반 : {customerName}
	 *
	 * 리스트 값(예: A6, B6) : {.productName} | {.amount}
	 * </pre>
	 */
	public static boolean writeExcel(String templateFileFullPath, String destFilePath, String fileName, Map<String, Object> dataMap, String listKey) {
		if ( StringUtils.isBlank(templateFileFullPath) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("templateFileFullPath"));
		}

		if ( StringUtils.isBlank(destFilePath) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("destFilePath"));
		}

		if ( StringUtils.isBlank(fileName) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("fileName"));
		}

		if ( dataMap == null || dataMap.isEmpty() ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("dataMap"));
		}

		String destFileFullPath = String.join(File.separator, destFilePath, fileName);

		try (ExcelWriter excelWriter = EasyExcelFactory.write(destFileFullPath).withTemplate(templateFileFullPath).build()) {
		    WriteSheet writeSheet = EasyExcelFactory.writerSheet().build();

		    excelWriter.fill(dataMap, writeSheet);

		    if ( !StringUtils.isBlank(listKey) ) {
		    	@SuppressWarnings("unchecked")
		    	List<Map<String, Object>> list = (List<Map<String, Object>>) dataMap.get(listKey);

		    	FillConfig fillConfig = FillConfig.builder().forceNewRow(true).build();
		    	excelWriter.fill(list, fillConfig, writeSheet);
		    }

		    return true;
		} catch (Exception e) {
			logger.error("", e);
		    return false;
		}
	}

}
