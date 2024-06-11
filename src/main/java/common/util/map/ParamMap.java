package common.util.map;

import java.util.HashMap;

import common.util.ConvertCaseUtil;

public class ParamMap extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;

	public Object putCamelCase(String key, Object value) {
		return super.put(ConvertCaseUtil.camelCase(key), value);
	}

	public Object putPascalCase(String key, Object value) {
		return super.put(ConvertCaseUtil.pascalCase(key), value);
	}
	
	public Object putLowerCase(String key, Object value) {
		return super.put(key.toLowerCase(), value);
	}

	public String getString(Object key) {
		return super.containsKey(key) ? String.valueOf(super.get(key)) : "";
	}

	public int getInteger(Object key) {
		return Integer.parseInt(String.valueOf(super.get(key)));
	}
	
	public double getDouble(Object key) {
		return Double.parseDouble(String.valueOf(super.get(key)));
	}
	
	public boolean getBoolean(Object key) {
		return Boolean.parseBoolean(String.valueOf(super.get(key)));
	}

}
