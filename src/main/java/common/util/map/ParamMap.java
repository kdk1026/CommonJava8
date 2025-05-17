package common.util.map;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import common.util.ConvertCaseUtil;

public class ParamMap extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;

	public Object putCamelCase(String key, Object value) {
		if ( StringUtils.isBlank(key) ) {
			throw new NullPointerException("key is null");
		}

		if ( value == null ) {
			throw new NullPointerException("value is null");
		}

		return super.put(ConvertCaseUtil.camelCase(key), value);
	}

	public Object putPascalCase(String key, Object value) {
		if ( StringUtils.isBlank(key) ) {
			throw new NullPointerException("key is null");
		}

		if ( value == null ) {
			throw new NullPointerException("value is null");
		}

		return super.put(ConvertCaseUtil.pascalCase(key), value);
	}

	public Object putLowerCase(String key, Object value) {
		if ( StringUtils.isBlank(key) ) {
			throw new NullPointerException("key is null");
		}

		if ( value == null ) {
			throw new NullPointerException("value is null");
		}

		return super.put(key.toLowerCase(), value);
	}

	public String getString(Object key) {
		if ( key == null ) {
			throw new NullPointerException("key is null");
		}

		return super.containsKey(key) ? String.valueOf(super.get(key)) : "";
	}

	public int getInteger(Object key) {
		if ( key == null ) {
			throw new NullPointerException("key is null");
		}

		return Integer.parseInt(String.valueOf(super.get(key)));
	}

	public double getDouble(Object key) {
		if ( key == null ) {
			throw new NullPointerException("key is null");
		}

		return Double.parseDouble(String.valueOf(super.get(key)));
	}

	public boolean getBoolean(Object key) {
		if ( key == null ) {
			throw new NullPointerException("key is null");
		}

		return Boolean.parseBoolean(String.valueOf(super.get(key)));
	}

}
