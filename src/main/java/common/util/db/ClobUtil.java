package common.util.db;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClobUtil {

	// XXX : Oracle 에서 oracle.sql.Clob 만 사용 가능한지 확인

	private ClobUtil() {
		super();
	}

	private static class ExceptionMessage {

		public static String isNull(String paramName) {
	        return String.format("'%s' is null", paramName);
	    }

		public static String isNullOrEmpty(String paramName) {
	        return String.format("'%s' is null or empty", paramName);
	    }

	}

	private static final Logger logger = LoggerFactory.getLogger(ClobUtil.class);

	public static String toString(Object obj) {
		Objects.requireNonNull(obj, ExceptionMessage.isNull("obj"));

		StringBuilder sb = new StringBuilder();

		try {
			Clob clob = (Clob) obj;
			Reader reader = clob.getCharacterStream();

			char[] buff = new char[1024];
			int nChars = 0;

			while ( (nChars = reader.read(buff)) > 0 ) {
				sb.append(buff, 0, nChars);
			}
		} catch (SQLException | IOException e) {
			logger.error("toString Exception", e);
		}

		return sb.toString();
	}

	public static String toString(Map<String, Object> map, String key) {
		if ( map == null || map.isEmpty() ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("map"));
		}

		if ( StringUtils.isBlank(key) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("key"));
		}

		StringBuilder sb = new StringBuilder();

		try {
			Clob clob = (Clob) map.get(key);
			Reader reader = clob.getCharacterStream();

			char[] buff = new char[1024];
			int nChars = 0;

			while ( (nChars = reader.read(buff)) > 0 ) {
				sb.append(buff, 0, nChars);
			}
		} catch (SQLException | IOException e) {
			logger.error("toString Exception", e);
		}

		return sb.toString();
	}

	public static void write(Clob clob, String text) {
		Objects.requireNonNull(clob, ExceptionMessage.isNull("clob"));

		if ( StringUtils.isBlank(text) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("text"));
		}

		try {
			Writer out = clob.setCharacterStream(1L);
			out.write(text);
			out.flush();
			out.close();
		} catch (SQLException | IOException e) {
			logger.error("write Exception", e);
		}
	}

}
