package common.util.excel;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2019. 5. 10. 김대광	최초작성
 * 2021. 8. 13. 김대광	SonarLint 지시에 따른 수정
 * 2026. 1. 12. 김대광	ServletJxlsUtil 으로 일부 분리
 * </pre>
 *
 *
 * @author 김대광
 */
public class JxlsUtil {

	private static final Logger logger = LoggerFactory.getLogger(JxlsUtil.class);

	private JxlsUtil() {
		super();
	}

	private static class ExceptionMessage {

		public static String isNullOrEmpty(String paramName) {
	        return String.format("'%s' is null or empty", paramName);
	    }

	}

	/**
	 * 템플릿 파일로부터 Workbook 생성
	 * @param bean
	 * @param templateFileFullPath
	 * @return
	 */
	private static Workbook createWorkbookTemplateFile(Map<String, Object> bean, String templateFileFullPath) {
		if ( bean == null || bean.isEmpty() ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("bean"));
		}

		if ( StringUtils.isBlank(templateFileFullPath) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("templateFileFullPath"));
		}

		Workbook workbook = null;

		try ( InputStream is = new BufferedInputStream(new FileInputStream(templateFileFullPath)) ) {

			XLSTransformer xls = new XLSTransformer();
			workbook = xls.transformXLS(is, bean);

		} catch ( IOException | ParsePropertyException | InvalidFormatException e) {
			logger.error("", e);
		}

		return workbook;
	}

	/**
	 * 템플릿 파일 이용해서 엑셀 파일 생성
	 * @param templateFileFullPath
	 * @param destFilePath
	 * @param fileName
	 * @param bean
	 * @return
	 */
	public static boolean writeExcel(String templateFileFullPath, String destFilePath, String fileName, Map<String, Object> bean) {
		if ( StringUtils.isBlank(templateFileFullPath) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("templateFileFullPath"));
		}

		if ( StringUtils.isBlank(destFilePath) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("destFilePath"));
		}

		if ( StringUtils.isBlank(fileName) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("fileName"));
		}

		if ( bean == null || bean.isEmpty() ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("bean"));
		}

		Workbook workbook = createWorkbookTemplateFile(bean, templateFileFullPath);
		if (workbook == null) {
			return false;
		}

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


}
