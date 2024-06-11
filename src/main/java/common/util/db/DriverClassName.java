package common.util.db;

public enum DriverClassName {
	
	ORACLE("oracle.jdbc.driver.OracleDriver"),
	MY_SQL("com.mysql.jdbc.Driver"),
	MS_SQL("com.microsoft.sqlserver.jdbc.SQLServerDriver"),
	
	/**
	 * File DB 
	 */
	SQL_LITE("org.sqlite.JDBC"),
	
	/**
	 * Server File/Memory DB 
	 */
	H2("org.h2.Driver")
	;
	
	private String value;
	
	private DriverClassName(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	
}
