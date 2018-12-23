package common.commons_configuration.ini;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import common.util.ini.CommonsIni;

/**
 * @since 2018. 12. 24.
 * @author 김대광
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2018. 12. 24. 김대광	최초작성
 * </pre>
 */
public class IniTest {
	
	private static final Logger logger = LoggerFactory.getLogger(IniTest.class);

	public static void main(String[] args) {
		String sFilePath = IniTest.class.getResource("test.ini").toString();
		
		CommonsIni ini = new CommonsIni(2, null, sFilePath);
		
		logger.debug("[getSections] {}", ini.getSections());
		logger.debug("[getProperty] {}", ini.getProperty("dev", "database"));
		logger.debug("[getProperties] {}", ini.getProperties());
		logger.debug("[getProperties] {}", ini.getProperties("prod"));
		
		
		ini.addProperty("dev", "test", 1234);
		logger.debug("[addProperty - getProperty] {}", ini.getProperty("dev", "test"));
		
		ini.clearProperty("dev", "test");
		logger.debug("[clearProperty - getProperty] {}", ini.getProperty("dev", "test"));
	}
	
}
