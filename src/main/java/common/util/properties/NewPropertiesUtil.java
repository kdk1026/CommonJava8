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
 * 2024. 8. 2. 김대광	최초작성
 * </pre>
 *
 *
 * @author 김대광
 */
public class NewPropertiesUtil {

	private final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

	private NewPropertiesUtil() {
		super();
	}

	private static class LazyHolder {
		private static final NewPropertiesUtil INSTANCE = new NewPropertiesUtil();
	}

	public static NewPropertiesUtil getInstance() {
		return LazyHolder.INSTANCE;
	}

	public Properties getProperties(String propFileName) {
		if ( StringUtils.isBlank(propFileName) ) {
			throw new NullPointerException("propFileName must be required");
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
			throw new NullPointerException("key must be required");
		}

		Properties prop = this.getProperties(propFileName);
		return prop.getProperty(key);
	}

}
