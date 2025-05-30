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
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

	/**
	 * 템플릿 파일로부터 Workbook 생성
	 * @param bean
	 * @param templateFileFullPath
	 * @return
	 */
	private static Workbook createWorkbookTemplateFile(Map<String, Object> bean, String templateFileFullPath) {
		if ( bean == null || bean.isEmpty() ) {
			throw new IllegalArgumentException("bean is null or empty");
		}

		if ( StringUtils.isBlank(templateFileFullPath) ) {
			throw new IllegalArgumentException("templateFileFullPath is null");
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
		if ( StringUtils.isBlank(destFilePath) ) {
			throw new IllegalArgumentException("destFilePath is null");
		}

		if ( StringUtils.isBlank(fileName) ) {
			throw new IllegalArgumentException("fileName is null");
		}

		boolean isSuccess = false;

		Workbook workbook = createWorkbookTemplateFile(bean, templateFileFullPath);

		if (workbook == null) {
			return isSuccess;
		}

		File outFile = new File(destFilePath + File.separator + fileName);

		try ( OutputStream os = new BufferedOutputStream(new FileOutputStream(outFile)) ) {
			workbook.write(os);
			isSuccess = true;
		} catch (IOException e) {
			logger.error("", e);
		}

		return isSuccess;
	}

	/**
	 * 템플릿 파일 이용해서 엑셀 파일 다운로드
	 * @param request
	 * @param response
	 * @param bean
	 * @param templateFileFullPath
	 * @param filename
	 */
	public static void downloadExcel(HttpServletRequest request, HttpServletResponse response
			, Map<String, Object> bean, String templateFileFullPath, String filename) {

		if ( request == null ) {
			throw new IllegalArgumentException("request is null");
		}

		if (response == null) {
			throw new IllegalArgumentException("response is null");
		}

		if ( StringUtils.isBlank(filename) ) {
			throw new IllegalArgumentException("filename is null");
		}

		try {
			Workbook workbook = createWorkbookTemplateFile(bean, templateFileFullPath);

			filename = setFileNameByBrowser(request, filename);
			response.setHeader("Content-Disposition", "attachment; fileName=\"" + filename+ "\"");

			if (workbook != null) {
				workbook.write(response.getOutputStream());
			}

		} catch ( IOException | ParsePropertyException e) {
			logger.error("", e);
		}
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
