package common.util.map;

import java.util.HashMap;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import common.util.ConvertCaseUtil;

public class ParamMap extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;

	private static final String VALUE = "value";

	private static class ExceptionMessage {

		public static String isNull(String paramName) {
	        return String.format("'%s' is null", paramName);
	    }

		public static String isNullOrEmpty(String paramName) {
	        return String.format("'%s' is null or empty", paramName);
	    }

	}

	public Object putCamelCase(String key, Object value) {
		if ( StringUtils.isBlank(key) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("key"));
		}

		Objects.requireNonNull(value, ExceptionMessage.isNull(VALUE));

		return super.put(ConvertCaseUtil.camelCase(key), value);
	}

	public Object putPascalCase(String key, Object value) {
		if ( StringUtils.isBlank(key) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("key"));
		}

		Objects.requireNonNull(value, ExceptionMessage.isNull(VALUE));

		return super.put(ConvertCaseUtil.pascalCase(key), value);
	}

	public Object putLowerCase(String key, Object value) {
		if ( StringUtils.isBlank(key) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("key"));
		}

		Objects.requireNonNull(value, ExceptionMessage.isNull(VALUE));

		return super.put(key.toLowerCase(), value);
	}

	public String getString(Object key) {
		Objects.requireNonNull(key, ExceptionMessage.isNull("key"));

		return super.containsKey(key) ? String.valueOf(super.get(key)) : "";
	}

	public int getInteger(Object key) {
		Objects.requireNonNull(key, ExceptionMessage.isNull("key"));

		return Integer.parseInt(String.valueOf(super.get(key)));
	}

	public double getDouble(Object key) {
		Objects.requireNonNull(key, ExceptionMessage.isNull("key"));

		return Double.parseDouble(String.valueOf(super.get(key)));
	}

	public boolean getBoolean(Object key) {
		Objects.requireNonNull(key, ExceptionMessage.isNull("key"));

		return Boolean.parseBoolean(String.valueOf(super.get(key)));
	}

}
