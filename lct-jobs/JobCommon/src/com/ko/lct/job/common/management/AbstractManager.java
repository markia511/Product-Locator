package com.ko.lct.job.common.management;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import com.ko.lct.job.geocoding.utilities.ApplicationException;
import com.ko.lct.job.geocoding.utilities.JdbcConnectionBroker;
import com.ko.lct.job.geocoding.utilities.JobProperties;
import com.ko.lct.job.logger.AbstractLogger;

public abstract class AbstractManager {
    private static String ZIP_FILE_EXTENSION = ".zip";
    private static String GZIP_FILE_EXTENSION = ".gz";
    private static String TAR_FILE_EXTENSION = ".tar";
    private static String AR_FILE_EXTENSION = ".ar";
    private static String DUMP_FILE_EXTENSION = ".dump";
    private static String CPIO_FILE_EXTENSION = ".cpio";
    private static String JAR_FILE_EXTENSION = ".jar";

    protected static int BUFFER_SIZE = 8192 * 1024;
    
    private static final String CHARSET_NAME = "ISO-8859-1";

    protected static JdbcConnectionBroker getDbConnection(String propertyFileName)
	    throws ApplicationException {

	final String dbUrl = JobProperties.getDbUrlParam(propertyFileName);
	final String userName = JobProperties.getUserName(propertyFileName);
	final String userPassword = JobProperties.getPassword(propertyFileName);

	AbstractLogger logger = AbstractLogger.getInstance();
	logger.logInfo("Database = " + dbUrl);
	logger.logInfo("UserID = " + userName);
	// logger.logInfo("UserPassword = " + userPassword);

	JdbcConnectionBroker jdbcConnectionBroker = new JdbcConnectionBroker();
	try {
	    jdbcConnectionBroker.connect(dbUrl, userName, userPassword);
	} catch (SQLException ex) {
	    logger.logError(ex);
	    throw new ApplicationException(ex);
	}
	return jdbcConnectionBroker;
    }

    protected static JdbcConnectionBroker init(String dbUrl, String userName,
	    String password) throws SQLException {
	JdbcConnectionBroker jdbcConnectionBroker = new JdbcConnectionBroker();
	jdbcConnectionBroker.connect(dbUrl, userName, password);
	return jdbcConnectionBroker;
    }

    public static String getPercent(int part, int all, DecimalFormat format) {
	if (all == 0) {
	    return "?%";
	}
	return format.format((double) part / all * 100) + "%";
    }

    protected static boolean isGzipFile(String fileName) {
	return (fileName != null && fileName.endsWith(GZIP_FILE_EXTENSION));
    }

    protected static boolean isGzipFile(File file) {
	return (file != null && file.getName() != null && file.getName().endsWith(GZIP_FILE_EXTENSION));
    }

    protected static boolean isArchiveFile(File file) {
	return (file.getName() != null
	&& (file.getName().endsWith(ZIP_FILE_EXTENSION)
		|| file.getName().endsWith(TAR_FILE_EXTENSION)
		|| file.getName().endsWith(AR_FILE_EXTENSION)
		|| file.getName().endsWith(DUMP_FILE_EXTENSION)
		|| file.getName().endsWith(CPIO_FILE_EXTENSION)
		|| file.getName().endsWith(JAR_FILE_EXTENSION)));
    }

    protected static void moveFileToProcessedDataDir(String sourceDirectoryName, String sourceFileName, String destDirectoryName) throws IOException {
	moveFileToProcessedDataDir(destDirectoryName, new File(sourceDirectoryName + sourceFileName));
    }
    
    protected static void moveFileToProcessedDataDir(String processedDataDirName, File importFile)
	    throws IOException {
	AbstractLogger logger = AbstractLogger.getInstance();
	logger.logInfo("Start moving file to processed dir...");
	File processedDataDir = new File(processedDataDirName);
	if (!processedDataDir.exists()) {
	    processedDataDir.mkdir();
	}

	// Move file to new directory
	boolean success = importFile.renameTo(new File(processedDataDir, importFile.getName()));
	if (success) {
	    // Simple move works, no need to copy
	    logger.logInfo("Finish moving file to processed dir");
	    return;
	}

	InputStream in = null;
	OutputStream out = null;

	try {
	    in = new FileInputStream(importFile);
	    out = new FileOutputStream(new File(processedDataDir, importFile.getName()));
	    byte[] buf = new byte[1024 * 1024];
	    int len;
	    while ((len = in.read(buf)) > 0) {
		out.write(buf, 0, len);
	    }
	} finally {
	    if (in != null) {
		in.close();
	    }
	    if (out != null) {
		out.close();
	    }
	}
	logger.logInfo("Finish moving file to processed dir");
	importFile.delete();
    }
    
    protected static void writeHeader(OutputStream outputStream, ResultSetMetaData metaData) 
	    throws SQLException, IOException {
	StringBuilder sb = new StringBuilder(metaData.getColumnName(1));
	for (int i = 2; i <= metaData.getColumnCount(); i++) {
	    sb.append('|').append(metaData.getColumnName(i));
	}
	sb.append("\n");
	outputStream.write(sb.toString().getBytes(CHARSET_NAME));
    }
    
    protected static void writeData(OutputStream outputStream, ResultSet rs, ResultSetMetaData metaData) 
	    throws SQLException, UnsupportedEncodingException, IOException {
	StringBuilder sb = new StringBuilder(300);
	for (int i = 1; i <= metaData.getColumnCount(); i++) {
	    if (i > 1) {
		sb.append('|');
	    }
	    final String s = rs.getString(i);
	    if (s != null)
		sb.append(s);
	}
	sb.append("\n");
	outputStream.write(sb.toString().getBytes(CHARSET_NAME));
    }

    protected static Map<String, String> foodServiceMap = new HashMap<String, String>();
    protected static final String YES = "1";
    protected static final String NO = "0";
    static {
	foodServiceMap.put("99", YES);
	foodServiceMap.put("35", YES);
	foodServiceMap.put("12", YES);
	foodServiceMap.put("20", YES);
	foodServiceMap.put("44", YES);
	foodServiceMap.put("42", YES);
	foodServiceMap.put("114", NO);
	foodServiceMap.put("6", NO);
	foodServiceMap.put("29", YES);
	foodServiceMap.put("241", YES);
	foodServiceMap.put("3", NO);
	foodServiceMap.put("38", NO);
	foodServiceMap.put("132", NO);
	foodServiceMap.put("18", YES);
	foodServiceMap.put("8", NO);
	foodServiceMap.put("25", YES);
	foodServiceMap.put("30", YES);
	foodServiceMap.put("7", NO);
	foodServiceMap.put("43", YES);
	foodServiceMap.put("263", YES);
	foodServiceMap.put("11", YES);
	foodServiceMap.put("39", NO);
	foodServiceMap.put("204", YES);
	foodServiceMap.put("282", NO);
	foodServiceMap.put("216", YES);
	foodServiceMap.put("19", YES);
	foodServiceMap.put("207", YES);
	foodServiceMap.put("147", YES);
	foodServiceMap.put("40", NO);
	foodServiceMap.put("37", NO);
	foodServiceMap.put("41", YES);
	foodServiceMap.put("2", NO);
	foodServiceMap.put("1", NO);
	foodServiceMap.put("31", YES);
	foodServiceMap.put("36", NO);
	foodServiceMap.put("4", NO);
	foodServiceMap.put("34", YES);
    }

}
