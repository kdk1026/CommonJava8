package common.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageUtil {

	private MessageUtil() {
		super();
	}

	private static class ExceptionMessage {

		public static String isNullOrEmpty(String paramName) {
	        return String.format("'%s' is null or empty", paramName);
	    }

	}

	private static final Logger logger = LoggerFactory.getLogger(MessageUtil.class);

	private static final String PROP_CLASS_PATH = "messages/";

	private static Properties getMessagePropertiesClasspath(String propFileName) {
		if ( StringUtils.isBlank(propFileName) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("propFileName"));
		}

		String sFileName = PROP_CLASS_PATH + propFileName;

		Properties prop = new Properties();

		try ( InputStream is = MessageUtil.class.getClassLoader().getResourceAsStream(sFileName) ) {
			prop.load(is);
		} catch (IOException e) {
			logger.error("", e);
		}
		return prop;
	}

	public static String getMessage(String propertiesFileName, Object ... arguments) {
		if ( arguments == null || arguments.length == 0 ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("arguments"));
		}

		Properties prop = getMessagePropertiesClasspath(propertiesFileName);
		String sMsg = prop.getProperty("message.confirm");

		return MessageFormat.format(sMsg, arguments);
	}

}
