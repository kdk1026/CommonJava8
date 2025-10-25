package common.util.map;

import java.math.BigDecimal;
import java.util.Objects;

import org.apache.commons.collections4.map.ListOrderedMap;

import common.util.ConvertCaseUtil;

/**
 * <pre>
 * MyBatis resultType 권장
 * </pre>
 */
@SuppressWarnings("rawtypes")
public class ResultSetMap extends ListOrderedMap {

	private static final long serialVersionUID = 1L;

	private static final String VALUE = "value";

	private static class ExceptionMessage {

		public static String isNull(String paramName) {
	        return String.format("'%s' is null", paramName);
	    }

	}

	@SuppressWarnings("unchecked")
	@Override
	public Object put(Object key, Object value) {
		Objects.requireNonNull(key, ExceptionMessage.isNull("key"));
		Objects.requireNonNull(value, ExceptionMessage.isNull(VALUE));

		return super.put(key.toString().toLowerCase(), value);
	}

	@SuppressWarnings("unchecked")
	public Object putCamel(Object key, Object value) {
		Objects.requireNonNull(key, ExceptionMessage.isNull("key"));
		Objects.requireNonNull(value, ExceptionMessage.isNull(VALUE));

		return super.put(ConvertCaseUtil.camelCase(key.toString()), value);
	}

	@SuppressWarnings("unchecked")
	public Object putBasic(Object key, Object value) {
		Objects.requireNonNull(key, ExceptionMessage.isNull("key"));
		Objects.requireNonNull(value, ExceptionMessage.isNull(VALUE));

		return super.put(key, value);
	}

	public String getString(Object key) {
		Objects.requireNonNull(key, ExceptionMessage.isNull("key"));

		return super.containsKey(key) ? String.valueOf(super.get(key)) : "";
	}

	public int getInteger(Object key) {
		Objects.requireNonNull(key, ExceptionMessage.isNull("key"));

		return Integer.parseInt(String.valueOf(super.get(key)));
	}

	public boolean getBoolean(Object key) {
		Objects.requireNonNull(key, ExceptionMessage.isNull("key"));

		return Boolean.parseBoolean(String.valueOf(super.get(key)));
	}

	public long getLong(Object key) {
		Objects.requireNonNull(key, ExceptionMessage.isNull("key"));

		return Long.parseLong(String.valueOf(super.get(key)));
	}

	/**
	 * <pre>
	 * DB 타입이 NUMBER 인 경우
	 *   - DB에서 NUMBER 타입으로 된 칼럼을 SqlMapClient.queryForList()로 조회하여 java.util.HashMap에 담게 되면 BigDecimal 형태로 리턴
	 * </pre>
	 * @param key
	 * @return
	 */
	public int getNumber(Object key) {
		Objects.requireNonNull(key, ExceptionMessage.isNull("key"));

		int nRet = 0;
		if ( super.containsKey(key) && super.get(key) != null ) {
			BigDecimal bd = (BigDecimal) super.get(key);
			nRet = bd.intValue();

		}
		return nRet;
	}

}
