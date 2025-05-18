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
 * </pre>
 *
 *
 * @author 김대광
 */
public class NewPropertiesUtil {

	private final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

	private static NewPropertiesUtil instance;

	private NewPropertiesUtil() {
		super();
	}

	public static synchronized NewPropertiesUtil getInstance() {
		if ( instance == null ) {
			instance = new NewPropertiesUtil();
		}

		return instance;
	}

	public Properties getProperties(String propFileName) {
		if ( StringUtils.isBlank(propFileName) ) {
			throw new IllegalArgumentException("propFileName must be required");
		}

		Properties prop = new Properties();

		try ( InputStream is = PropertiesUtil.class.getClassLoader().getResourceAsStream(propFileName) ) {
			prop.load(is);
		} catch (IOException e) {
			logger.error("", e);
		}

		return prop;
	}

	public String getProperties(String propFileName, String key) {
		if ( StringUtils.isBlank(key) ) {
			throw new IllegalArgumentException("key must be required");
		}

		Properties prop = this.getProperties(propFileName);
		return prop.getProperty(key);
	}

}
