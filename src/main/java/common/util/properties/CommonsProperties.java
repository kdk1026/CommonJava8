package common.util.properties;

import java.util.Iterator;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
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
public class CommonsProperties {

	private static final Logger logger = LoggerFactory.getLogger(CommonsProperties.class);

	private static final String PROP_CLASS_PATH = "/properties" + NioFileUtil.FOLDER_SEPARATOR;
	private static final String PROP_WEB_INF_PATH = "/WEB-INF" + NioFileUtil.FOLDER_SEPARATOR + "properties/";

	private FileBasedConfigurationBuilder<PropertiesConfiguration> builder;
	private PropertiesConfiguration config;

	/**
	 * @param type		- 0: Classpath, 1: WEB-INF, 2: Path+Name
	 * @param request 	- [type 0 = null] [type 1 = required] [type 2 = webRootPath인 경우, required]
	 * @param propFileName
	 */
	public CommonsProperties(int type, HttpServletRequest request, String propFileName) {
		if ( type < 0 || type > 2 ) {
			throw new IllegalArgumentException("type must be 0, 1 or 2");
		}

		if ( request == null && type != 0 ) {
			throw new IllegalArgumentException("request must be required when type is 1 or 2");
		}

		if ( StringUtils.isBlank(propFileName) ) {
			throw new NullPointerException("propFileName must be required");
		}

		Configurations configs = new Configurations();

		try {
			String sPath = "";
			String webRootPath = "";

			switch (type) {
			case 0:
				sPath = CommonsProperties.class.getResource(PROP_CLASS_PATH + propFileName).getPath();
				break;
			case 1:
				webRootPath = request.getSession().getServletContext().getRealPath("/");
				sPath = webRootPath + PROP_WEB_INF_PATH + propFileName;
				break;
			default:
				if ( request == null ) {
					sPath = propFileName;
				} else {
					webRootPath = request.getSession().getServletContext().getRealPath("/");
					sPath = webRootPath + propFileName;
				}
				break;
			}

			builder = configs.propertiesBuilder(sPath);
			config = builder.getConfiguration();

		} catch (ConfigurationException e) {
			logger.error("", e);
		}
	}

	public Object getProperty(String key) {
		if ( StringUtils.isBlank(key) ) {
			throw new NullPointerException("key must be required");
		}

		return config.getProperty(key);
	}

	public Properties getProperties() {
		Properties prop = new Properties();

		Iterator<String> keys = config.getKeys();
		while ( keys.hasNext() ) {
			String sKey = keys.next();
			prop.setProperty( sKey, String.valueOf(getProperty(sKey)) );
		}

		return prop;
	}


	public void addProperty(String key, Object value) {
		if ( StringUtils.isBlank(key) ) {
			throw new NullPointerException("key must be required");
		}

		if ( value == null ) {
			throw new NullPointerException("value must be required");
		}

		config.addProperty(key, value);
		this.save();
	}

	public void setProperty(String key, Object value) {
		if ( StringUtils.isBlank(key) ) {
			throw new NullPointerException("key must be required");
		}

		if ( value == null ) {
			throw new NullPointerException("value must be required");
		}

		config.setProperty(key, value);
		this.save();
	}

	public void clearProperty(String key) {
		if ( StringUtils.isBlank(key) ) {
			throw new NullPointerException("key must be required");
		}

		config.clearProperty(key);
		this.save();
	}

	private void save() {
		try {
			builder.save();
		} catch (ConfigurationException e) {
			logger.error("", e);
		}
	}

}
