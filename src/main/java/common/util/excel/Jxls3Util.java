package common.util.excel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jxls.builder.JxlsOutputFile;
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
 * @author 김대광
 */
public class Jxls3Util {

	private static final Logger logger = LoggerFactory.getLogger(Jxls3Util.class);

	private Jxls3Util() {
		super();
	}

	private static class ExceptionMessage {

		public static String isNullOrEmpty(String paramName) {
	        return String.format("'%s' is null or empty", paramName);
	    }

	}

	/**
	 * 템플릿 파일 이용해서 엑셀 파일 생성
	 *  - 3.x.x 은 JDK 17 이상에서 동작
	 * @param templateFileFullPath
	 * @param destFilePath
	 * @param fileName
	 * @param dataMap
	 * @param attributeName
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
	 * A1 메모: jx:area(lastCell="B6")
	 *
	 * 일반 : ${dataMap.customerName}
	 *
	 * 리스트 메모(예: A6) : jx:each(items="dataMap.productList" var="product" lastCell="B6")
	 * 리스트 값(예: A6, B6) : ${product.productName} | ${product.amount}
	 * </pre>
	 */
	public static boolean writeExcel(String templateFileFullPath, String destFilePath, String fileName, Map<String, Object> dataMap, String attributeName) {
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

		if ( StringUtils.isBlank(attributeName) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("attributeName"));
		}

		Map<String, Object> data = new HashMap<>();
		data.put(attributeName, dataMap);

		File outputFile = new File(destFilePath + File.separator + fileName);

		try {
			JxlsPoiTemplateFillerBuilder.newInstance()
			.withTemplate(new File(templateFileFullPath))
			.build()
			.fill(data, new JxlsOutputFile(outputFile));

			return true;
		} catch (FileNotFoundException e) {
			logger.error("", e);
		    return false;
		}
	}

}
