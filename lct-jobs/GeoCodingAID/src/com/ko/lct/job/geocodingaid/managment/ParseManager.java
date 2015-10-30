package com.ko.lct.job.geocodingaid.managment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

import com.ko.lct.job.common.management.AbstractManager;
import com.ko.lct.job.geocoding.utilities.ApplicationException;
import com.ko.lct.job.geocoding.utilities.GeocodingConstants;
import com.ko.lct.job.geocoding.utilities.JdbcConnectionBroker;
import com.ko.lct.job.geocodingaid.businessobjects.AidDbDto;
import com.ko.lct.job.geocodingaid.businessobjects.AidInputDto;
import com.ko.lct.job.geocodingaid.dao.GeocoderAidDAO;
import com.ko.lct.job.geocodingaid.dao.GeocoderAidQuery;
import com.ko.lct.job.geocodingaid.logger.GeocoderAidLogger;
import com.ko.lct.job.geocodingaid.utilities.GeoCodingAIDProperties;

public class ParseManager extends AbstractManager {
    private static final GeocoderAidLogger logger = GeocoderAidLogger.getInstance();

    public static void parse() throws Exception {

	JdbcConnectionBroker connectionBroker = null;
	try {
	    connectionBroker = getDbConnection(GeoCodingAIDProperties.DEFAULT_FILE_NAME);

	    logger.logInfo("Parse privacy addresses.");
	    
	    String inputDirectory = GeoCodingAIDProperties.getDirProperty("INPUT_DATA_PATH");
	    String dataFilePrefix = GeoCodingAIDProperties.getProperty("INPUT_FILE_PREFIX");
	    String dataFileSuffix = GeoCodingAIDProperties.getProperty("DATA_FILE_SUFFIX");
	    String doneFileSuffix = GeoCodingAIDProperties.getProperty("DONE_FILE_SUFFIX");
	    String processedDataDirName = GeoCodingAIDProperties.getDirProperty("PROCESSED_DATA_PATH");
	    String outputDataPath = GeoCodingAIDProperties.getDirProperty("OUTPUT_DATA_PATH");
	    String gZippedDataFileSuffix = dataFileSuffix + ".gz";

	    String[] dataFileList = getInputFilesList(inputDirectory, dataFilePrefix, dataFileSuffix, gZippedDataFileSuffix, doneFileSuffix);

	    for (String dataFileName : dataFileList) {
		int i = dataFileName.lastIndexOf(gZippedDataFileSuffix);
		if (i < 0) {
		    i = dataFileName.lastIndexOf(dataFileSuffix);
		}
		String doneFileName = dataFileName.substring(0, i) + doneFileSuffix;
		String errorFileName = outputDataPath + dataFileName.substring(0, i) + ".err.gz";
		File inputFile = new File(inputDirectory + dataFileName);
		if (isGzipFile(dataFileName)) {
		    parseGZipFile(connectionBroker, inputFile, processedDataDirName, errorFileName);
		} else {
		    parseTextFile(connectionBroker, inputFile, processedDataDirName, errorFileName);
		}
		moveFileToProcessedDataDir(processedDataDirName, inputFile);
		moveFileToProcessedDataDir(inputDirectory, doneFileName, processedDataDirName);

	    }

	    connectionBroker.commit();
	} finally {
	    if (connectionBroker != null) {
		connectionBroker.cleanup();
	    }
	}
    }

    private static String[] getInputFilesList(final String inputDirectory, final String dataFilePrefix, final String dataFileSuffix, final String gZippedDataFileSuffix,
	    final String doneFileSuffix) {
	ArrayList<String> filesList = new ArrayList<String>();
	File fileDir = new File(inputDirectory);
	if (fileDir.exists()) {
	    String[] doneFiles = fileDir.list(new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
		    return name.endsWith(doneFileSuffix) && name.startsWith(dataFilePrefix);
		}
	    });
	    for (String doneFile : doneFiles) {
		String dataFileName = doneFile.substring(0, doneFile.lastIndexOf(doneFileSuffix));
		if (new File(inputDirectory + dataFileName + dataFileSuffix).exists()) {
		    filesList.add(dataFileName + dataFileSuffix);
		}
		else if (new File(inputDirectory + dataFileName + gZippedDataFileSuffix).exists()) {
		    filesList.add(dataFileName + gZippedDataFileSuffix);
		}

	    }
	    Collections.sort(filesList);
	}
	return filesList.toArray(new String[0]);
    }

    private static void parseGZipFile(JdbcConnectionBroker connectionBroker, File inputFile,
	    String processedDataDirName, String errorFileName) throws IOException, ApplicationException, SQLException {
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

    private static void parseTextFile(JdbcConnectionBroker connectionBroker, File inputFile,
	    String processedDataDirName, String errorFileName) throws SQLException, IOException, ApplicationException {
	FileInputStream inputStream = new FileInputStream(inputFile);
	try {
	    processInputFile(connectionBroker, inputStream, errorFileName);
	} finally {
	    inputStream.close();
	}
    }

    private static void processInputFile(JdbcConnectionBroker connectionBroker, InputStream inputFile, String errorFileName)
	    throws SQLException, IOException, ApplicationException {

	int addrId = GeocoderAidDAO.getNewAddrPrvId(connectionBroker);
	int insUpdCount = 0;
	int skipCount = 0;
	int errorCount = 0;
	int duplicateCount = 0;
	int batchCounter = 0;
	boolean errorFileCreated = false;
	GzipCompressorOutputStream errorStream = null;
	Validator validator = new Validator();
	// int insertBatchCounter = 0;
	BufferedReader br = new BufferedReader(new InputStreamReader(inputFile, GeocodingConstants.ENCODE_UTF_8), BUFFER_SIZE);
	try {
	    String str;
	    PreparedStatement findAddrStmt = null;
	    PreparedStatement insertAddrStmt = null;
	    PreparedStatement updateAddrStmt = null;
	    try {
		StringBuffer skippedIdsBuffer = new StringBuffer(8192);
		String schema = GeoCodingAIDProperties.getSchema();
		findAddrStmt = connectionBroker.getNewPreparedStatement(GeocoderAidQuery.FIND_ADDR_PRV, schema);
		insertAddrStmt = connectionBroker.getNewPreparedStatement(GeocoderAidQuery.INSERT_ADDR, schema);
		updateAddrStmt = connectionBroker.getNewPreparedStatement(GeocoderAidQuery.UPDATE_ADDR, schema);
		while ((str = br.readLine()) != null) {
		    AidInputDto dto = parseInputString(str);
		    if (!validator.validateDto(dto)) {
			skipCount++;
			errorCount++;
			logger.logError(str);
			if (!errorFileCreated) {
			    errorStream = new GzipCompressorOutputStream(
				    new BufferedOutputStream(
					    new FileOutputStream(errorFileName)));
			    errorFileCreated = true;
			}
			errorStream.write((str + "\n").getBytes("UTF-8"));
		    }
		    else {
			AidDbDto dbDto = GeocoderAidDAO.findAddress(findAddrStmt, dto);
			if (dbDto == null) {
			    // insert
			    try {
				setParametersForInsert(insertAddrStmt, dto, addrId);
				insertAddrStmt.addBatch();
				batchCounter++;
				if (batchCounter >= 200) {
				    insertAddrStmt.executeBatch();
				    batchCounter = 0;
				}
				addrId++;
				insUpdCount++;
			    } catch (Exception e) {
				skipCount++;
				errorCount++;
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
			else 
//			    if (recordUpdateNeeded(dto, dbDto)) 
			    {
			    // update
			    try {
				setParametersForUpdate(updateAddrStmt, dto, dbDto.getAddrPrvId());
				updateAddrStmt.executeUpdate();
				insUpdCount++;
			    } catch (Exception e) {
				skipCount++;
				errorCount++;
				logger.logError("Update error: " + str + "\n" + e.getMessage());
				if (!errorFileCreated) {
				    errorStream = new GzipCompressorOutputStream(
					    new BufferedOutputStream(
						    new FileOutputStream(errorFileName)));
				    errorFileCreated = true;
				}
				errorStream.write((str + "\n").getBytes("UTF-8"));
			    }
			}
//			else {
//			    // skip duplicate record
//			    skippedIdsBuffer.append(Integer.toString(dbDto.getAddrPrvId())).append(", ");
//			    skipCount++;
//			    duplicateCount++;
//			}
			if (insUpdCount % 1000 == 0 && insUpdCount > 0) {
			    logger.logInfo("Records Inserted/Updated: " + Integer.toString(insUpdCount) + "  Records Skipped: " + Integer.toString(skipCount));
			    connectionBroker.commit();
			}
		    }
		}
		if (batchCounter > 0) {
		    insertAddrStmt.executeBatch();
		}
		if (skippedIdsBuffer.length() > 0) {
		    skippedIdsBuffer.delete(skippedIdsBuffer.length() - 2, skippedIdsBuffer.length());
		    logger.logInfo("Skipped duplicate ADDR_PRV_ID: " + skippedIdsBuffer.toString());
		}
		logger.logInfo("Total Records Inserted/Updated: " + Integer.toString(insUpdCount));
		logger.logInfo("Total Records Skipped: " + Integer.toString(skipCount));
		logger.logInfo("Total Errors: " + Integer.toString(errorCount));
		logger.logInfo("Total Duplicates: " + Integer.toString(duplicateCount));
		validator.logStatistics(logger);
		connectionBroker.commit();
	    } finally {
		if (errorStream != null) {
		    errorStream.close();
		}
		connectionBroker.closeStatement(updateAddrStmt);
		connectionBroker.closeStatement(insertAddrStmt);
		connectionBroker.closeStatement(findAddrStmt);
	    }

	} finally {
	    br.close();
	}
    }

    private static void setParametersForInsert(PreparedStatement insertAddrStmt, AidInputDto dto, int addrId) throws SQLException {
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
    }

    private static void setParametersForUpdate(PreparedStatement updateAddrStmt, AidInputDto dto, int addrId) throws SQLException {
	int i = 1;
	updateAddrStmt.setString(i++, dto.getBtlrDlvrPntNm());
	updateAddrStmt.setString(i++, dto.getAddressLine2());
	updateAddrStmt.setString(i++, dto.getZip());
	updateAddrStmt.setString(i++, dto.getCountryDesc());
	updateAddrStmt.setString(i++, dto.getAreaCode());
	updateAddrStmt.setString(i++, dto.getPhnNo());
	updateAddrStmt.setInt(i++, addrId);
    }

    private static AidInputDto parseInputString(String str) {
	int paramIndex = 0;

	String s;
	int j = 0;
	int l = str.length();
	AidInputDto dto = new AidInputDto();
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
	if(str_temp != null && str_temp.length() > length) {
	    str_temp = str_temp.substring(0, length);
	}
	return str_temp;
    }

    private static boolean recordUpdateNeeded(AidInputDto dto, AidDbDto dbDto) {
	return (dto.getBtlrDlvrPntNm() == null && dbDto.getBtlrDlvrPntNm() != null)
		|| (dto.getBtlrDlvrPntNm() != null && dbDto.getBtlrDlvrPntNm() == null)
		|| (dto.getBtlrDlvrPntNm() != null && !dto.getBtlrDlvrPntNm().equals(dbDto.getBtlrDlvrPntNm()))
		|| (dto.getAddressLine2() == null && dbDto.getAddressLine2() != null)
		|| (dto.getAddressLine2() != null && dbDto.getAddressLine2() == null)
		|| (dto.getAddressLine2() != null && !dto.getAddressLine2().equals(dbDto.getAddressLine2()))
		|| (dto.getZip() == null && dbDto.getZip() != null)
		|| (dto.getZip() != null && dbDto.getZip() == null)
		|| (dto.getZip() != null && !dbDto.getZip().equals(dbDto.getZip()))
		|| !dto.getCountryDesc().equals(dbDto.getCountryDesc())
		|| (dto.getAreaCode() == null && dbDto.getAreaCode() != null)
		|| (dto.getAreaCode() != null && dbDto.getAreaCode() == null)
		|| (dto.getAreaCode() != null && !dto.getAreaCode().equals(dbDto.getAreaCode()))
		|| (dto.getPhnNo() == null && dbDto.getPhnNo() != null)
		|| (dto.getPhnNo() != null && dbDto.getPhnNo() == null)
		|| (dto.getPhnNo() != null && !dto.getPhnNo().equals(dbDto.getPhnNo()));
    }

    static class Validator {
	int btlrDlvrPntNoErrors = 0;
	int btlrDlvrPntNmErrors = 0;
	int stateErrors;
	int cityErrors = 0;
	int addressLine1Errors = 0;
	int zipErrors = 0;

	public boolean validateDto(AidInputDto dto) {
	    boolean retValue = true;
	    if (dto.getBtlrDlvrPntNo() == null) {
		btlrDlvrPntNoErrors++;
		retValue = false;
	    }
	    if (dto.getBtlrDlvrPntNm() == null) {
		btlrDlvrPntNmErrors++;
		retValue = false;
	    }
	    if (dto.getState() == null) {
		stateErrors++;
		retValue = false;
	    }
	    if (dto.getCity() == null) {
		cityErrors++;
		retValue = false;
	    }
	    if (dto.getAddressLine1() == null) {
		addressLine1Errors++;
		retValue = false;
	    }
	    if (dto.getZip() == null) {
		zipErrors++;
		retValue = false;
	    }
	    return retValue;
	}

	public void logStatistics(GeocoderAidLogger logger) {
	    logger.logInfo("Errors Statistic: \r\n" +
		    "BTLR_DLVR_PNT_NO: " + Integer.toString(btlrDlvrPntNoErrors) + "\r\n" +
		    "BTLR_DLVR_PNT_NM: " + Integer.toString(btlrDlvrPntNmErrors) + "\r\n" +
		    "STATE: " + Integer.toString(stateErrors) + "\r\n" +
		    "CITY: " + Integer.toString(cityErrors) + "\r\n" +
		    "ADDRESS_LINE_1: " + Integer.toString(addressLine1Errors) + "\r\n" +
		    "ZIP: " + Integer.toString(zipErrors));
	}
    }

}
