package common.util;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageUtil {
	
	private MessageUtil() {
		super();
	}
	
	private static final Logger logger = LoggerFactory.getLogger(MessageUtil.class);

	private static final String PROP_CLASS_PATH = "messages/";

	private static Properties getMessagePropertiesClasspath(String propFileName) {
		Properties prop = new Properties();
		InputStream is = null;

		try {
			String sFileName = PROP_CLASS_PATH + propFileName;
			is = MessageUtil.class.getClassLoader().getResourceAsStream(sFileName);
			prop.load(is);
			is.close();
		} catch (Exception e) {
			logger.error("", e);
		}
		return prop;
	}

	public static String getMessage(String propertiesFileName, Object ... arguments) {
		Properties prop = getMessagePropertiesClasspath(propertiesFileName);
		String sMsg = prop.getProperty("message.confirm");

		return MessageFormat.format(sMsg, arguments);
	}

}
