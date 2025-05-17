package common.util.properties;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import common.util.file.NioFileUtil;

/**
 * @since 2018. 12. 24.
 * @author 김대광
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2018. 12. 24. 김대광	최초작성
 * </pre>
 */
public class PropertiesUtil {

	private PropertiesUtil() {
		super();
	}

	private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

	private static final String PROP_CLASS_PATH = "properties" + NioFileUtil.FOLDER_SEPARATOR;
	private static final String PROP_WEB_INF_PATH = "/WEB-INF" + NioFileUtil.FOLDER_SEPARATOR + "properties/";

	/**
	 * <pre>
	 * Properties 로드
	 *   - properties 파일 / properties xml 파일
	 *   - Java 7 base: Try-with-resources
	 * </pre>
	 * @param request
	 * @param propFileName
	 */
	public static Properties getProperties(HttpServletRequest request, String propFileName) {
		if ( StringUtils.isBlank(propFileName) ) {
			throw new NullPointerException("propFileName must be required");
		}

		Properties prop = new Properties();

		String fileNmae = "";

		if ( request == null ) {
			fileNmae = propFileName;

			try ( InputStream is = PropertiesUtil.class.getClassLoader().getResourceAsStream(fileNmae) ) {
				prop.load(is);

				if ( propFileName.lastIndexOf("xml") > -1 ) {
					prop.loadFromXML(is);
				}

			} catch (IOException e) {
				logger.error("", e);
			}
		} else {
			String webRootPath = request.getSession().getServletContext().getRealPath("/");
			fileNmae = webRootPath + propFileName;

			try ( InputStream is = new BufferedInputStream(new FileInputStream(fileNmae)) ) {
				prop.load(is);

				if ( propFileName.lastIndexOf("xml") > -1 ) {
					prop.loadFromXML(is);
				}

			} catch (IOException e) {
				logger.error("", e);
			}
		}

		return prop;
	}

	/**
	 * <pre>
	 * Classpath의 Properties 로드
	 *   - properties 파일 / properties xml 파일
	 *   - Java 7 base: Try-with-resources
	 * </pre>
	 * @param propFileName
	 * @return
	 */
	public static Properties getPropertiesClasspath(String propFileName) {
		if ( StringUtils.isBlank(propFileName) ) {
			throw new NullPointerException("propFileName must be required");
		}

		Properties prop = new Properties();
		String fileNmae = PROP_CLASS_PATH + propFileName;

		try ( InputStream is = PropertiesUtil.class.getClassLoader().getResourceAsStream(fileNmae) ) {
			prop.load(is);

			if ( propFileName.lastIndexOf("xml") > -1 ) {
				prop.loadFromXML(is);
			}

		} catch (IOException e) {
			logger.error("", e);
		}

		return prop;
	}

	/**
	 * <pre>
	 * WEB-INF의 Properties 로드
	 *   - properties 파일 / properties xml 파일
	 *   - Java 7 base: Try-with-resources
	 * </pre>
	 * @param request
	 * @param propFileName
	 * @return
	 */
	public static Properties getPropertiesWebInf(HttpServletRequest request, String propFileName) {
		if ( request == null ) {
			throw new NullPointerException("request must be required");
		}

		if ( StringUtils.isBlank(propFileName) ) {
			throw new NullPointerException("propFileName must be required");
		}

		Properties prop = new Properties();

		String webRootPath = request.getSession().getServletContext().getRealPath("/");
		String fileNmae = webRootPath + PROP_WEB_INF_PATH + propFileName;

		try ( InputStream is = new BufferedInputStream(new FileInputStream(fileNmae)) ) {
			prop.load(is);

			if ( propFileName.lastIndexOf("xml") > -1 ) {
				prop.loadFromXML(is);
			}

		} catch (IOException e) {
			logger.error("", e);
		}

		return prop;
	}

	/**
	 * <pre>
	 * Properties 생성/덮어쓰기
	 *   - Classpath의 경우 서버 리로드를 해야 반영되므로 생성/덮어쓰기 권장안함
	 *   - Java 7 base: Try-with-resources
	 * </pre>
	 * @param type		- 0: Classpath, 1: WEB-INF, 2: Path+Name
	 * @param request 	- [type 0 = null] [type 1 = required] [type 2 = webRootPath인 경우, required]
	 * @param propFileName
	 * @param prop
	 */
	public static void saveProperties(int type, HttpServletRequest request, String propFileName, Properties prop) {
		if ( type < 0 || type > 2 ) {
			throw new IllegalArgumentException("type must be 0, 1 or 2");
		}

		if ( request == null && type == 1 ) {
			throw new IllegalArgumentException("request must be required when type is 1");
		}

		if ( StringUtils.isBlank(propFileName) ) {
			throw new NullPointerException("propFileName must be required");
		}

		if ( prop == null || prop.isEmpty() ) {
			throw new NullPointerException("prop must be required");
		}

		String fileNmae = "";
		String webRootPath = "";
		HttpSession session = null;

		switch (type) {
		case 0:
			fileNmae = PropertiesUtil.class.getClassLoader().getResource(PROP_CLASS_PATH + propFileName).getPath();
			break;
		case 1:
			session = request.getSession();
			webRootPath = session.getServletContext().getRealPath("/");
			fileNmae = webRootPath + PROP_WEB_INF_PATH + propFileName;
			break;
		case 2:
			if (request == null) {
				fileNmae = propFileName;
			} else {
				session = request.getSession();
				webRootPath = session.getServletContext().getRealPath("/");
				fileNmae = webRootPath + propFileName;
			}
			break;
		default:
			break;
		}

		try (
				FileOutputStream fos = new FileOutputStream(fileNmae);
				OutputStream os = new BufferedOutputStream(fos);
        ) {
			prop.store(os, null);

		} catch (IOException e) {
			logger.error("", e);
		}
	}

}
