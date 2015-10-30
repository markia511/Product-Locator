package com.ko.lct.job.geocodingaid.managment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

import com.ko.lct.job.common.management.AbstractManager;
import com.ko.lct.job.geocoding.utilities.ApplicationException;
import com.ko.lct.job.geocoding.utilities.GeocodingConstants;
import com.ko.lct.job.geocoding.utilities.JdbcConnectionBroker;
import com.ko.lct.job.geocodingaid.businessobjects.AidOutputDto;
import com.ko.lct.job.geocodingaid.dao.GeocoderAidDAO;
import com.ko.lct.job.geocodingaid.dao.GeocoderAidQuery;
import com.ko.lct.job.geocodingaid.logger.GeocoderAidLogger;
import com.ko.lct.job.geocodingaid.managment.ParseManager.Validator;
import com.ko.lct.job.geocodingaid.utilities.GeoCodingAIDProperties;

public class ParseGeocodedManager extends AbstractManager {

    private static final GeocoderAidLogger logger = GeocoderAidLogger.getInstance();

    public static void parse(final String sourceFileName) throws Exception {

	JdbcConnectionBroker connectionBroker = null;
	try {
	    connectionBroker = getDbConnection(GeoCodingAIDProperties.DEFAULT_FILE_NAME);

	    String outputDataPath = GeoCodingAIDProperties.getDirProperty("OUTPUT_DATA_PATH");

	    String errorFileName = sourceFileName;
	    int i = errorFileName.lastIndexOf('\\');
	    if (i >= 0) {
		errorFileName = errorFileName.substring(i + 1);
	    }
	    i = errorFileName.lastIndexOf('/');
	    if (i >= 0) {
		errorFileName = errorFileName.substring(i + 1);
	    }
	    i = errorFileName.lastIndexOf('.');
	    errorFileName = outputDataPath + errorFileName.substring(0, i) + ".err.gz";

	    File inputFile = new File(sourceFileName);
	    if (isGzipFile(sourceFileName)) {
		parseGZipFile(connectionBroker, inputFile, errorFileName);
	    } else {
		parseTextFile(connectionBroker, inputFile, errorFileName);
	    }
	    connectionBroker.commit();
	} finally {
	    if (connectionBroker != null) {
		connectionBroker.cleanup();
	    }
	}
    }

    private static void parseGZipFile(JdbcConnectionBroker connectionBroker, File inputFile,
	    String errorFileName) throws IOException, ApplicationException, SQLException, ParseException {
	FileInputStream fileInputStream = new FileInputStream(inputFile);
	BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
	GzipCompressorInputStream gzIn = new GzipCompressorInputStream(bufferedInputStream);
	try {
	    processInputFile(connectionBroker, gzIn, errorFileName);
	    logger.logInfo("Finish Parse file");
	} finally {
	    gzIn.close();
	    bufferedInputStream.close();
	    fileInputStream.close();
	}
    }

    private static void parseTextFile(JdbcConnectionBroker connectionBroker, File inputFile, String errorFileName) throws SQLException, IOException,
	    ApplicationException, ParseException {
	FileInputStream inputStream = new FileInputStream(inputFile);
	try {
	    processInputFile(connectionBroker, inputStream, errorFileName);
	} finally {
	    inputStream.close();
	}
    }

    private static void processInputFile(JdbcConnectionBroker connectionBroker, InputStream inputFile, String errorFileName)
	    throws SQLException, IOException, ApplicationException, ParseException {

	int addrId = GeocoderAidDAO.getNewAddrPrvId(connectionBroker);
	int insUpdCount = 0;
	int skipCount = 0;
	int batchCounter = 0;
	int commitCounter = 0;
	boolean errorFileCreated = false;
	GzipCompressorOutputStream errorStream = null;
	Validator validator = new Validator();
	BufferedReader br = new BufferedReader(new InputStreamReader(inputFile, GeocodingConstants.ENCODE_UTF_8), BUFFER_SIZE);
	try {
	    String str;
	    PreparedStatement insertAddrStmt = null;
	    try {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String schema = GeoCodingAIDProperties.getSchema();
		insertAddrStmt = connectionBroker.getNewPreparedStatement(GeocoderAidQuery.INSERT_ADDR_PRV_ROW, schema);
		while ((str = br.readLine()) != null) {
		    AidOutputDto dto = parseInputString(str, df);
		    try {
			setParametersForInsertRow(insertAddrStmt, dto, addrId);
			insertAddrStmt.addBatch();
			batchCounter++;
			if (batchCounter >= 200) {
			    insertAddrStmt.executeBatch();
			    batchCounter = 0;
			    commitCounter++;
			    if (commitCounter > 5) {
				connectionBroker.commit();
				commitCounter = 0;
				logger.logInfo("Records Inserted: " + Integer.toString(insUpdCount) + "  Records Skipped: " + Integer.toString(skipCount));
			    }
			}
			addrId++;
			insUpdCount++;
		    } catch (Exception e) {
			skipCount++;
			logger.logError("Insert error: " + str + "\n" + e.getMessage());
			if (!errorFileCreated) {
			    errorStream = new GzipCompressorOutputStream(
				    new BufferedOutputStream(
					    new FileOutputStream(errorFileName)));
			    errorFileCreated = true;
			}
			errorStream.write((str + "\n").getBytes("UTF-8"));
		    }
		}
		if (batchCounter > 0) {
		    insertAddrStmt.executeBatch();
		}
		connectionBroker.commit();
		logger.logInfo("Total Records Inserted: " + Integer.toString(insUpdCount));
		logger.logInfo("Total Records Skipped: " + Integer.toString(skipCount));
		validator.logStatistics(logger);
		
	    } finally {
		if (errorStream != null) {
		    errorStream.close();
		}
		connectionBroker.closeStatement(insertAddrStmt);
	    }

	} finally {
	    br.close();
	}
    }

    private static void setParametersForInsertRow(PreparedStatement insertAddrStmt, AidOutputDto dto, int addrId) throws SQLException {
	int i = 1;
	insertAddrStmt.setInt(i++, addrId);
	insertAddrStmt.setInt(i++, dto.getOwnrshpId());
	insertAddrStmt.setString(i++, dto.getBtlrDlvrPntNo());
	insertAddrStmt.setString(i++, dto.getBtlrDlvrPntNm());
	insertAddrStmt.setString(i++, dto.getAddressLine1());
	insertAddrStmt.setString(i++, dto.getAddressLine2());
	insertAddrStmt.setString(i++, dto.getCity());
	insertAddrStmt.setString(i++, dto.getState());
	insertAddrStmt.setString(i++, dto.getZip());
	insertAddrStmt.setString(i++, dto.getCountryDesc());
	insertAddrStmt.setString(i++, dto.getAreaCode());
	insertAddrStmt.setString(i++, dto.getPhnNo());
	insertAddrStmt.setString(i++, dto.getFrmtAddr());
	if (dto.getLatitude() == 0.0) {
	    insertAddrStmt.setNull(i++, Types.NUMERIC);
	}
	else {
	    insertAddrStmt.setDouble(i++, dto.getLatitude());
	}
	if (dto.getLongitude() == 0.0) {
	    insertAddrStmt.setNull(i++, Types.NUMERIC);
	}
	else {
	    insertAddrStmt.setDouble(i++, dto.getLongitude());
	}
	insertAddrStmt.setDate(i++, new java.sql.Date(dto.getGeoDt().getTime()));
	insertAddrStmt.setInt(i++, dto.getGeoLvl());
    }

    private static AidOutputDto parseInputString(String str, DateFormat df) throws ParseException {
	int paramIndex = 0;

	String s;
	int j = 0;
	int l = str.length();
	AidOutputDto dto = new AidOutputDto();
	int i;
	while (j < l) {
	    i = str.indexOf('|', j);
	    if (i < 0) {
		i = l;
	    }
	    s = str.substring(j, i).trim();
	    if (s != null && s.isEmpty()) {
		s = null;
	    }
	    switch (paramIndex++) {
	    case 0:
		dto.setOwnrshpId(Integer.parseInt(s));
		break;
	    case 1:
		dto.setBtlrDlvrPntNo(getSubstr(s, 13));
		break;
	    case 2:
		dto.setBtlrDlvrPntNm(getSubstr(s, 30));
		break;
	    case 3:
		dto.setAddressLine1(getSubstr(s, 50));
		break;
	    case 4:
		if (dto.getAddressLine1() == null || dto.getAddressLine1().isEmpty()) {
		    dto.setAddressLine1(getSubstr(s, 50));
		}
		else {
		    dto.setAddressLine2(getSubstr(s, 50));
		}
		break;
	    case 5:
		dto.setCity(getSubstr(s, 50));
		break;
	    case 6:
		dto.setState(getSubstr(s, 50));
		break;
	    case 7:
		dto.setZip(getSubstr(s, 20));
		break;
	    case 8:
		dto.setCountryDesc(getSubstr(s, 3));
		break;
	    case 9:
		dto.setAreaCode(getSubstr(s, 3));
		break;
	    case 10:
		dto.setPhnNo(getSubstr(s, 7));
		break;
	    case 11:
		dto.setFrmtAddr(s);
		break;
	    case 12:
		dto.setLatitude(s == null ? 0.0 : Double.parseDouble(s));
		break;
	    case 13:
		dto.setLongitude(s == null ? 0.0 : Double.parseDouble(s));
		break;
	    case 14:
		dto.setGeoDt(df.parse(s));
		break;
	    case 15:
		dto.setGeoLvl(Integer.parseInt(s));
	    default:
		break;
	    }
	    j = i + 1;
	}
	if (dto.getCountryDesc() == null || dto.getCountryDesc().isEmpty()) {
	    dto.setCountryDesc("USA");
	}

	return dto;
    }

    private static String getSubstr(String str, int length) {
	String str_temp = str;
	if (str_temp != null && str_temp.length() > length) {
	    str_temp = str_temp.substring(0, length);
	}
	return str_temp;
    }

}
