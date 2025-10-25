package common.util.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBUtil {

	private static final Logger logger = LoggerFactory.getLogger(DBUtil.class);

	private DBUtil() {
		super();
	}

	private static class ExceptionMessage {

		public static String isNull(String paramName) {
	        return String.format("'%s' is null", paramName);
	    }

		public static String isNullOrEmpty(String paramName) {
	        return String.format("'%s' is null or empty", paramName);
	    }

		public static String isNegative(String paramName) {
			return String.format("'%s' is negative", paramName);
		}

	}

	public static Connection getConnection(Properties prop) {
		Objects.requireNonNull(prop, ExceptionMessage.isNull("prop"));

		Connection conn = null;
		String sDriver = prop.getProperty("jdbc.driverClassName");
		String sUrl = prop.getProperty("jdbc.url");
        String sUsername = prop.getProperty("jdbc.username");
        String sPassword = prop.getProperty("jdbc.password");

		try {
			Class.forName(sDriver);
			conn = DriverManager.getConnection(sUrl, sUsername, sPassword);

		} catch (ClassNotFoundException | SQLException e) {
			logger.error("getConnection Exception", e);
		}

		return conn;
	}

	public static PreparedStatement getPreparedStatement(Connection conn, String sQuery, List<String> params) {
		Objects.requireNonNull(conn, ExceptionMessage.isNull("conn"));

		if ( StringUtils.isBlank(sQuery) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("sQuery"));
		}

		if ( params == null || params.isEmpty() ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("params"));
		}

		PreparedStatement pstmt = null;

		try {
			pstmt = conn.prepareStatement(sQuery);

			int nSize = params.size();
			for (int i=0; i < nSize; i++) {
				pstmt.setString(i+1, params.get(i));
			}

		} catch (SQLException e) {
			logger.error("getPreparedStatement SQLException", e);
		}

		return pstmt;
	}

	public static ResultSet getResultSet(PreparedStatement pstmt) {
		Objects.requireNonNull(pstmt, ExceptionMessage.isNull("pstmt"));

		ResultSet rs = null;

		try {
			rs = pstmt.executeQuery();

		} catch (SQLException e) {
			logger.error("getResultSet SQLException", e);
		}

		return rs;
	}

	public static void runQuery(Connection conn, String sQuery) {
		Objects.requireNonNull(conn, ExceptionMessage.isNull("conn"));

		if ( StringUtils.isBlank(sQuery) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("sQuery"));
		}

		try ( PreparedStatement pstmt = conn.prepareStatement(sQuery) ) {
			pstmt.executeUpdate();

		} catch (SQLException e) {
			logger.error("runQuery SQLException", e);
		}
	}

	public static void close(ResultSet rs, PreparedStatement pstmt, Connection conn) {
		Objects.requireNonNull(rs, ExceptionMessage.isNull("rs"));
		Objects.requireNonNull(pstmt, ExceptionMessage.isNull("pstmt"));
		Objects.requireNonNull(conn, ExceptionMessage.isNull("conn"));

		try {
			rs.close();
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			logger.error("close(ResultSet rs, PreparedStatement pstmt, Connection conn) SQLException", e);
		}
	}

	public static void close(ResultSet rs, PreparedStatement pstmt) {
		Objects.requireNonNull(rs, ExceptionMessage.isNull("rs"));
		Objects.requireNonNull(pstmt, ExceptionMessage.isNull("pstmt"));

		try {
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			logger.error("close(ResultSet rs, PreparedStatement pstmt) SQLException", e);
		}
	}

	public static void close(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			logger.error("close(Connection conn) SQLException", e);
		}
	}

	public static CallableStatement getCallableStatement(Connection conn, String sQuery, List<String> params) {
		Objects.requireNonNull(conn, ExceptionMessage.isNull("conn"));

		if ( StringUtils.isBlank(sQuery) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("sQuery"));
		}

		if ( params == null || params.isEmpty() ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("params"));
		}

		CallableStatement cstmt = null;

		try {
			cstmt = conn.prepareCall(sQuery);

			int nSize = params.size();
			for (int i=0; i < nSize; i++) {
				cstmt.setString(i+1, params.get(i));
			}

			cstmt.executeUpdate();

		} catch (SQLException e) {
			logger.error("getCallableStatement SQLException", e);
		}

		return cstmt;
	}

	public static ResultSet getCallableResultSet(Connection conn, String sQuery, List<String> params, int nCursorIdx) {
		Objects.requireNonNull(conn, ExceptionMessage.isNull("conn"));

		if ( StringUtils.isBlank(sQuery) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("sQuery"));
		}

		if ( params == null || params.isEmpty() ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("params"));
		}

		if ( nCursorIdx < 1 ) {
			throw new IllegalArgumentException(ExceptionMessage.isNegative("nCursorIdx"));
		}

		ResultSet rs = null;

		try ( CallableStatement cstmt = conn.prepareCall(sQuery) ) {
			int nSize = params.size();
			for (int i=1; i <= nSize; i++) {
				cstmt.setString(i, params.get(i));
			}

			// XXX : Oracle 에서 OracleTypes.CURSOR 만 사용 가능한지 확인

			cstmt.executeUpdate();
			rs = (ResultSet) cstmt.getObject(nCursorIdx);

			if (rs != null) {
				rs.close();
			}

		} catch (SQLException e) {
			logger.error("getCallableResultSet SQLException", e);
		}

		return rs;
	}

	public static void close(ResultSet rs, CallableStatement cstmt, Connection conn) {
		Objects.requireNonNull(rs, ExceptionMessage.isNull("rs"));
		Objects.requireNonNull(cstmt, ExceptionMessage.isNull("cstmt"));
		Objects.requireNonNull(conn, ExceptionMessage.isNull("conn"));

		try {
			rs.close();
			cstmt.close();
			conn.close();
		} catch (SQLException e) {
			logger.error("close SQLException", e);
		}
	}

	public static void close(ResultSet rs, CallableStatement cstmt) {
		Objects.requireNonNull(rs, ExceptionMessage.isNull("rs"));
		Objects.requireNonNull(cstmt, ExceptionMessage.isNull("cstmt"));

		try {
			rs.close();
			cstmt.close();
		} catch (SQLException e) {
			logger.error("close SQLException", e);
		}
	}

}
