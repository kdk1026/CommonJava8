package common.util.ini;

import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.SubnodeConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
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
public class CommonsIni {

	private static final Logger logger = LoggerFactory.getLogger(CommonsIni.class);
	
	private static final String PROP_CLASS_PATH = "/ini" + NioFileUtil.FOLDER_SEPARATOR;
	private static final String PROP_WEB_INF_PATH = "/WEB-INF" + NioFileUtil.FOLDER_SEPARATOR + "ini/";
	
	@SuppressWarnings("unused")
	private FileBasedConfigurationBuilder<INIConfiguration> builder;
	private INIConfiguration config;
	
	/**
	 * @param type		- 0: Classpath, 1: WEB-INF, 2: Path+Name
	 * @param request 	- [type 0 = null] [type 1 = required] [type 2 = webRootPath인 경우, required]
	 * @param iniFileName
	 */
	public CommonsIni(int type, HttpServletRequest request, String iniFileName) {
		Configurations configs = new Configurations();

		try {
			String sPath = "";
			String webRootPath = "";
			
			switch (type) {
			case 0:
				sPath = CommonsIni.class.getResource(PROP_CLASS_PATH + iniFileName).getPath();
				break;
			case 1:
				webRootPath = request.getSession().getServletContext().getRealPath("/");
				sPath = webRootPath + PROP_WEB_INF_PATH + iniFileName;
				break;
			default:
				if ( request == null ) {
					sPath = iniFileName;
				} else {
					webRootPath = request.getSession().getServletContext().getRealPath("/");
					sPath = webRootPath + iniFileName;
				}
				break;
			}

			builder = configs.iniBuilder(sPath);
			config = configs.ini(sPath);

		} catch (ConfigurationException e) {
			logger.error("", e);
		}	
	}
	
	public Set<String> getSections() {
		return config.getSections();
	}
	
	public Object getProperty(String section, String key) {
		SubnodeConfiguration sObj = config.getSection(section);
		return sObj.getProperty(key);
	}
	
	public Properties getProperties() {
		Properties prop = new Properties();
		
		Iterator<String> keys = config.getKeys();
		while ( keys.hasNext() ) {
			String sKey = keys.next();
			prop.setProperty( sKey, String.valueOf(config.getProperty(sKey)) );
		}
		
		return prop;
	}
	
	public Properties getProperties(String section) {
		Properties prop = new Properties();
		
		SubnodeConfiguration sObj = config.getSection(section);
		Iterator<String> keys = sObj.getKeys();
		while ( keys.hasNext() ) {
			String sKey = keys.next();
			prop.setProperty( sKey, String.valueOf(sObj.getProperty(sKey)) );
		}
		
		return prop;
	}
	
	
	public void addProperty(String section, String key, Object value) {
		SubnodeConfiguration sObj = config.getSection(section);
		sObj.addProperty(key, value);
	}
	
	public void setProperty(String section, String key, Object value) {
		SubnodeConfiguration sObj = config.getSection(section);
		sObj.setProperty(key, value);
	}
	
	public void clearProperty(String section, String key) {
		SubnodeConfiguration sObj = config.getSection(section);
		sObj.clearProperty(key);
	}
	
}
