package com.ko.lct.job.geocoding.utilities;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class JdbcConnectionBroker {
    protected Connection conn = null;
    private ArrayList<PreparedStatement> preparedStatements = new ArrayList<PreparedStatement>();

    public void connect(String url, String userName, String password) throws SQLException {
	if (conn != null) {
	    throw new SQLException("Already connected");
	}
	/*
	int level = AnoServices.REJECTED;
	Properties props = new Properties();
	props.put("oracle.net.encryption_client", Service.getLevelString(level));
	props.put("oracle.net.crypto_checksum_client", Service.getLevelString(level));
	OracleDataSource ods = new OracleDataSource();
	ods.setURL(url);
	ods.setUser(userName);
	ods.setPassword(password);
	ods.setConnectionProperties(props);
	conn = ods.getConnection();
	*/

	conn = DriverManager.getConnection(url, userName, password);
	
	conn.setAutoCommit(false);
	DatabaseMetaData meta = conn.getMetaData();
	System.out.println("JDBC driver version is " + meta.getDriverVersion());
	
	/* Enable Trace:
	PreparedStatement stmt = conn.prepareStatement("alter session set timed_statistics=true");
	stmt.execute();
	stmt.close();
	stmt = conn.prepareStatement("alter session set max_dump_file_size=unlimited");
	stmt.execute();
	stmt.close();
	stmt = conn.prepareStatement("alter session set sql_trace=true");
	stmt.execute();
	stmt.close();
	stmt = conn.prepareStatement("alter session set events '10046 trace name context forever, level 12'");
	stmt.execute();
	stmt.close();
	*/
	
    }

    public void commit() {
	if (conn != null) {
	    try {
		conn.commit();
	    } catch (SQLException ex) {
		ex.printStackTrace();
	    }
	}
    }

    public void rollback() {
	if (conn != null) {
	    try {
		conn.rollback();
	    } catch (SQLException ex) {
		ex.printStackTrace();
	    }
	}
    }

    public PreparedStatement getNewPreparedStatement(String sql) throws SQLException {
	PreparedStatement retValue;
	synchronized (preparedStatements) {
	    retValue = conn.prepareStatement(sql);
	    preparedStatements.add(retValue);
	}
	return retValue;
    }

    public PreparedStatement getNewPreparedStatement(String sql, String schemaName)
	    throws SQLException {
	PreparedStatement retValue;
	synchronized (preparedStatements) {
	    retValue = conn.prepareStatement(setSchemaName(sql, schemaName));
	    preparedStatements.add(retValue);
	}
	return retValue;
    }

    public CallableStatement getNewCallableStatement(String sql) throws SQLException {
	CallableStatement retValue;
	synchronized (preparedStatements) {
	    retValue = conn.prepareCall(sql);
	    preparedStatements.add(retValue);
	}
	return retValue;
    }

    public CallableStatement getNewCallableStatement(String sql, String schemaName) throws SQLException {
	CallableStatement retValue;
	synchronized (preparedStatements) {
	    retValue = conn.prepareCall(setSchemaName(sql, schemaName));
	    preparedStatements.add(retValue);
	}
	return retValue;
    }

    public void closeStatement(PreparedStatement stmt) {
	if (stmt != null) {
	    synchronized (preparedStatements) {
		int i = preparedStatements.indexOf(stmt);
		if (i >= 0) {
		    preparedStatements.remove(i);
		    try {
			stmt.close();
		    } catch (Exception ex) {
			ex.printStackTrace();
		    }
		}
	    }
	}
    }

    public static void closeResultSet(ResultSet rs) {
	if (rs != null) {
	    try {
		rs.close();
	    } catch (Exception ex) {
		ex.printStackTrace();
	    }
	}
    }

    public void cleanup() {
	synchronized (preparedStatements) {
	    for (PreparedStatement stmt : preparedStatements) {
		try {
		    stmt.close();
		} catch (Exception ex) {
		    ex.printStackTrace();
		}
	    }
	    preparedStatements.clear();
	}
	if (conn != null) {
	    try {
		conn.close();
		conn = null;
	    } catch (Exception ex) {
		ex.printStackTrace();
	    }
	}
    }

    public static final String setSchemaName(String sql, String schemaName) {
	StringBuilder sb = new StringBuilder(sql);
	int i;
	while ((i = sb.indexOf("%s.")) >= 0) {
	    sb.delete(i, i + 2);
	    sb.insert(i, schemaName);
	}
	return sb.toString();
    }

    public static void setDate(PreparedStatement stmt, int paramIndex, java.util.Date value) throws SQLException {
	if (value == null) {
	    stmt.setNull(paramIndex, java.sql.Types.DATE);
	}
	else {
	    stmt.setDate(paramIndex, new java.sql.Date(value.getTime()));
	}
    }
}
