package common.util.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBUtil {

	private DBUtil() {
		super();
	}
	
	private static final Logger logger = LoggerFactory.getLogger(DBUtil.class);
	
	public static Connection getConnection(Properties prop) {
		Connection conn = null;
		String sDriver = prop.getProperty("jdbc.driverClassName");
		String sUrl = prop.getProperty("jdbc.url");
        String sUsername = prop.getProperty("jdbc.username");
        String sPassword = prop.getProperty("jdbc.password");
		
		try {
			Class.forName(sDriver);
			conn = DriverManager.getConnection(sUrl, sUsername, sPassword);
			
		} catch (Exception e) {
			logger.error("getConnection Exception", e);
		}
		
		return conn;
	}
	
	public static PreparedStatement getPreparedStatement(Connection conn, String sQuery, List<String> params) {
		PreparedStatement pstmt = null;
		
		try {
			if (conn != null) {
				pstmt = conn.prepareStatement(sQuery);
				
				int nSize = params.size();
				for (int i=0; i < nSize; i++) {
					pstmt.setString(i+1, params.get(i));
				}
			}
			
		} catch (SQLException e) {
			logger.error("getPreparedStatement SQLException", e);
		}
		
		return pstmt;
	}
	
	public static ResultSet getResultSet(PreparedStatement pstmt) {
		ResultSet rs = null;
		
		try {
			rs = pstmt.executeQuery();
			
		} catch (SQLException e) {
			logger.error("getResultSet SQLException", e);
		}
		
		return rs;
	}
	
	public static void runQuery(Connection conn, String sQuery) {
		PreparedStatement pstmt = null;
		
		try {
			if (conn != null) {
				pstmt = conn.prepareStatement(sQuery);
				pstmt.executeUpdate();
			}
			
		} catch (SQLException e) {
			logger.error("runQuery SQLException", e);
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (SQLException e) {
				logger.error("runQuery SQLException", e);
			}
		}
	}
	
	public static void close(ResultSet rs, PreparedStatement pstmt, Connection conn) {
		try {
			if (rs != null) {
				rs.close();
			}
			
			if (pstmt != null) {
				pstmt.close();
			}
			
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			logger.error("close(ResultSet rs, PreparedStatement pstmt, Connection conn) SQLException", e);
		}
	}
	
	public static void close(ResultSet rs, PreparedStatement pstmt) {
		try {
			if (rs != null) {
				rs.close();
			}
			
			if (pstmt != null) {
				pstmt.close();
			}
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
		CallableStatement cstmt = null;
		
		try {
			if (conn != null) {
				cstmt = conn.prepareCall(sQuery);
				
				int nSize = params.size();
				for (int i=0; i < nSize; i++) {
					cstmt.setString(i+1, params.get(i));
				}
				
				cstmt.executeUpdate();
			}
			
		} catch (SQLException e) {
			logger.error("getCallableStatement SQLException", e);
		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
				}
			} catch (SQLException e) {
				logger.error("getCallableStatement SQLException", e);
			}
		}
		
		return cstmt;
	}
	
	public static ResultSet getCallableResultSet(Connection conn, String sQuery, List<String> params, int nCursorIdx) {
		ResultSet rs = null;
		CallableStatement cstmt = null;
		
		try {
			if (conn != null) {
				cstmt = conn.prepareCall(sQuery);
				
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
			}
			
		} catch (SQLException e) {
			logger.error("getCallableResultSet SQLException", e);
		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
				}
			} catch (SQLException e) {
				logger.error("getCallableResultSet SQLException", e);
			}
		}
		
		return rs;
	}
	
	public static void close(ResultSet rs, CallableStatement cstmt, Connection conn) {
		try {
			if (rs != null) {
				rs.close();
			}
			
			if (cstmt != null) {
				cstmt.close();
			}
			
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			logger.error("close SQLException", e);
		}
	}
	
	public static void close(ResultSet rs, CallableStatement cstmt) {
		try {
			if (rs != null) {
				rs.close();
			}
			
			if (cstmt != null) {
				cstmt.close();
			}
		} catch (SQLException e) {
			logger.error("close SQLException", e);
		}
	}
	
}
