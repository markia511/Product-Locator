package com.ko.lct.job.geocoding.unload;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.zip.GZIPOutputStream;

import com.ko.lct.job.geocoding.utilities.ApplicationException;
import com.ko.lct.job.geocoding.utilities.JdbcConnectionBroker;
import com.ko.lct.job.geocoding.utilities.UnloadGeoDataProperties;

public class UnloadGeoData {
    private static final String ENCODING = "ISO-8859-1";

    private static final String FILE_SEPARATOR = AccessController
	    .doPrivileged(new PrivilegedAction<String>() {
		@Override
		public String run() {
		    return System.getProperty("file.separator");
		}
	    });

    private static final String SELECT_SQL =
	    "select outlet.TDL_CD, \n" +
		    "       outlet.TDL_GRP_CD, \n" +
		    "       outlet.NM_ID1, \n" +
		    "       outlet.NM_ID2, \n" +
		    "       outlet.CHN_NM, \n" +
		    "       outlet.NAME, \n" +
		    "       outlet.SB_CHNL_ID, \n" +
		    "       outlet.PHNE_NBR, \n" +
		    "       outlet.BSNS_TYPE_ID, \n" +
		    "       outlet.FOOD_SRVC_IND, \n" +
		    "       addr.CTRY_CD, \n" +
		    "       addr.STATE, \n" +
		    "       addr.CITY, \n" +
		    "       addr.PSTL_CD, \n" +
		    "       addr.ADDR_LINE_1, \n" +
		    "       addr.ADDR_LINE_2, \n" +
		    "       addr.FRMT_ADDR, \n" +
		    "       addr.LATITUDE, \n" +
		    "       addr.LONGITUDE, \n" +
		    "       to_char(addr.GEO_DT, 'MM/dd/yyyy') as GEO_DT, \n" +
		    "       addr.GEO_LVL \n" +
		    "  from %s.T_OUTLET outlet, \n" +
		    "       %s.T_ADDR addr \n" +
		    " where outlet.ADDR_ID = addr.ADDR_ID \n" +
		    "   and addr.LATITUDE is not null \n" +
		    "   and addr.GEO_LVL > 0 \n" +
		    " order by outlet.OUTLET_ID, addr.ADDR_ID";

    /**
     * @param args
     * @throws ApplicationException
     * @throws SQLException
     * @throws IOException
     */
    public static void main(String[] args) throws SQLException, ApplicationException, IOException {
	String outputDir = UnloadGeoDataProperties.getNotEmptyParam("outputdir");
	JdbcConnectionBroker connectionBroker = new JdbcConnectionBroker();
	try {
	    connectionBroker.connect(UnloadGeoDataProperties.getDbUrlParam(), UnloadGeoDataProperties.getUserName(), UnloadGeoDataProperties.getPassword());
	    processUnload(connectionBroker, outputDir);
	} finally {
	    connectionBroker.cleanup();
	}
    }

    private static void processUnload(JdbcConnectionBroker connectionBroker, String outputDir) throws SQLException, ApplicationException, IOException {
	PreparedStatement stmt = connectionBroker.getNewPreparedStatement(SELECT_SQL, UnloadGeoDataProperties.getSchema());
	try {
	    ResultSet rs = stmt.executeQuery();

	    FileOutputStream fileOutputStream = null;
	    GZIPOutputStream gzipOutputStream = null;
	    try {
		fileOutputStream = new FileOutputStream(outputDir + FILE_SEPARATOR + "outlet_addr_full.txt.gz");
		gzipOutputStream = new GZIPOutputStream(fileOutputStream, 8192);

		ResultSetMetaData metaData = rs.getMetaData();
		writeHeader(gzipOutputStream, metaData);
		while (rs.next()) {
		    writeData(gzipOutputStream, rs, metaData);
		}
	    } finally {
		if (gzipOutputStream != null) {
		    gzipOutputStream.close();
		}
		if (fileOutputStream != null) {
		    fileOutputStream.close();
		}
	    }
	} finally {
	    stmt.close();
	}

    }

    private static void writeData(OutputStream outputStream, ResultSet rs, ResultSetMetaData metaData) throws SQLException, UnsupportedEncodingException, IOException {
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
	outputStream.write(sb.toString().getBytes(ENCODING));
    }

    private static void writeHeader(OutputStream outputStream, ResultSetMetaData metaData) throws SQLException, UnsupportedEncodingException, IOException {
	StringBuilder sb = new StringBuilder(metaData.getColumnName(1));
	for (int i = 2; i <= metaData.getColumnCount(); i++) {
	    sb.append('|').append(metaData.getColumnName(i));
	}
	sb.append("\n");
	outputStream.write(sb.toString().getBytes(ENCODING));
    }

}
