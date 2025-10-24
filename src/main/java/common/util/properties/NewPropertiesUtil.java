package common.util.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2024. 8. 2.  김대광	최초작성
 * 2025. 5. 18. 김대광	AI가 추천한 Singleton 패턴으로 변경
 * 2025. 5. 27. 김대광	유틸은 Singleton 패턴을 사용하지 않는 것이 좋다는 의견 반영
 * </pre>
 *
 *
 * @author 김대광
 */
public class NewPropertiesUtil {

	private static final Logger logger = LoggerFactory.getLogger(NewPropertiesUtil.class);

	private NewPropertiesUtil() {
		super();
	}

	private static class ExceptionMessage {

		public static String isNullOrEmpty(String paramName) {
	        return String.format("'%s' is null or empty", paramName);
	    }

	}

	public static Properties getProperties(String propFileName) {
		if ( StringUtils.isBlank(propFileName) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("propFileName"));
		}

		Properties prop = new Properties();

		try ( InputStream is = PropertiesUtil.class.getClassLoader().getResourceAsStream(propFileName) ) {
			prop.load(is);
		} catch (IOException e) {
			logger.error("", e);
		}

		return prop;
	}

	public static String getProperties(String propFileName, String key) {
		if ( StringUtils.isBlank(key) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("key"));
		}

		Properties prop = getProperties(propFileName);
		return prop.getProperty(key);
	}

}
