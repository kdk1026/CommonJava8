package common.libTest.commons;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

/**
 * <pre>
 * 	1.x
 *	 - FileBasedConfigurationBuilder 없음
 *	 - Configuration -> PropertiesConfiguration
 *	 - builder.save() -> config.save()
 * </pre>
 */
public class UsageConfiguration {

	private static FileBasedConfigurationBuilder<PropertiesConfiguration> builder;
	private static Configuration config;

	private static Configuration getProperties(String propFileName) {
		Configurations configs = new Configurations();

		try {
			String sPath = UsageConfiguration.class.getResource("/properties/" + propFileName).getPath();

			builder = configs.propertiesBuilder(sPath);
			config = builder.getConfiguration();

		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		return config;
	}

	public static String getProperty(String propFileName, String key) {
		config = getProperties(propFileName);

		return (String) config.getProperty(key);
	}

	public static void setProperty(String propFileName, String key, String value) {
		config = getProperties(propFileName);

		if ( config.containsKey(key) ) {
			config.setProperty(key, value);
		} else {
			config.addProperty(key, value);
		}

		try {
			builder.save();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

	public static void removeProperty(String propFileName, String key) {
		config = getProperties(propFileName);

		if ( config.containsKey(key) ) {
			config.clearProperty(key);
		}

		try {
			builder.save();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

}
