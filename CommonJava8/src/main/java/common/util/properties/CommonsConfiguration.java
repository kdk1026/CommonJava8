package common.util.properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import common.util.file.NioFileUtil;

public class CommonsConfiguration {
	
	private static final Logger logger = LoggerFactory.getLogger(CommonsConfiguration.class);
	
	private static final String PROP_CLASS_PATH = "/properties" + NioFileUtil.FOLDER_SEPARATOR;
	private static final String PROP_WEB_INF_PATH = "/WEB-INF" + NioFileUtil.FOLDER_SEPARATOR + "properties/";
	
	private FileBasedConfigurationBuilder<PropertiesConfiguration> builder;
	private Configuration config;
	
	/**
	 * @param type (0: Classpath, 1 : WEB-INF)
	 * @param request (Classpath 는 null)
	 * @param propFileName
	 */
	public CommonsConfiguration(int type, HttpServletRequest request, String propFileName) {
		Configurations configs = new Configurations();

		try {
			String sPath = "";
			
			switch (type) {
			case 0:
				sPath = CommonsConfiguration.class.getResource(PROP_CLASS_PATH + propFileName).getPath();
				break;
			case 1:
				String webRootPath = request.getSession().getServletContext().getRealPath("/");
				sPath = webRootPath + PROP_WEB_INF_PATH + propFileName;
				break;
			default:
				break;
			}

			builder = configs.propertiesBuilder(sPath);
			config = builder.getConfiguration();

		} catch (ConfigurationException e) {
			logger.error("CommonsConfiguration ConfigurationException", e);
		}		
	}
	
	public Object getProperty(String key) {
		return config.getProperty(key);
	}
	
	public void setProperty(String key, Object value) {
		config.setProperty(key, value);
		this.save();
	}
	
	public void clearProperty(String key) {
		config.clearProperty(key);
		this.save();
	}
	
	private void save() {
		try {
			builder.save();
		} catch (ConfigurationException e) {
			logger.error("save ConfigurationException", e);
		}
	}

}
