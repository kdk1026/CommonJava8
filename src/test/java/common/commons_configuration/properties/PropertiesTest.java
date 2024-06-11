package common.commons_configuration.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import common.util.properties.CommonsProperties;

/**
 * @since 2018. 12. 24.
 * @author 김대광
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2018. 12. 24. 김대광	최초작성
 * </pre>
 */
public class PropertiesTest {
	
	private static final Logger logger = LoggerFactory.getLogger(PropertiesTest.class);
	
	public static void main(String[] args) {
		String sFilePath = PropertiesTest.class.getResource("test.properties").toString();
		
		CommonsProperties properties = new CommonsProperties(2, null, sFilePath);
		
		logger.debug("[getProperty] {}", properties.getProperty("dev.database"));
		logger.debug("[getProperties] {}", properties.getProperties());
		
		
		properties.addProperty("test", 1234);
		logger.debug("[addProperty - getProperty] {}", properties.getProperty("test"));
		
		properties.clearProperty("test");
		logger.debug("[clearProperty - getProperty] {}", properties.getProperty("test"));
	}

}
