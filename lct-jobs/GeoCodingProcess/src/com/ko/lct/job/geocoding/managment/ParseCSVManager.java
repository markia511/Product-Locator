package com.ko.lct.job.geocoding.managment;

import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.IF_EMPTY_CHECK_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.INSERT_ADRESS_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.INSERT_BEV_CATEGORY_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.INSERT_BUSINESS_TYPE_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.INSERT_CHANNEL_ORG_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.INSERT_CHANNEL_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.INSERT_COUNTRY_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.INSERT_LOOKUP_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.INSERT_OUTLET_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.INSERT_PACKAGE_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.INSERT_PRODUCT_PCG_TYPE_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.INSERT_PRODUCT_PKG_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.INSERT_PRODUCT_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.INSERT_SB_CHANNEL_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.REMOVE_DIGITAL_NOISE;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.REMOVE_OBSOLETE_DELIVERY_DATA;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.REMOVE_OBSOLETE_OUTLET_DATA;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.REMOVE_OBSOLETE_PRODUCT_PACKAGE_DATA;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.SELECT_ADRESS_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.SELECT_ADRESS_WITHOUT_ADDR_LINE_1_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.SELECT_ALL_CONTAINER_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.SELECT_ALL_PRODUCT_PACKAGE_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.SELECT_BEV_CATEGORY_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.SELECT_BUSINESS_TYPE_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.SELECT_CHANNEL_ORG_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.SELECT_CHANNEL_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.SELECT_LOOKUP_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.SELECT_OUTLET_BY_ALL_COLUMN_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.SELECT_OUTLET_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.SELECT_PRODUCT_CODE_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.SELECT_PRODUCT_PACKAGE_BY_PRD_CODE_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.SELECT_PRODUCT_PCG_TYPE_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.SELECT_SB_CHANNEL_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.UPDATE_OUTLET_ADDR_ID;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.UPDATE_OUTLET_EMPTY_LOCATION;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.UPDATE_PRODUCT_PACKAGE_QUERY;
import static com.ko.lct.job.geocoding.Dao.GeocodingQuery.UPDATE_PRODUCT_QUERY;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import com.ko.lct.job.geocoding.businessobjects.DataRow;
import com.ko.lct.job.geocoding.businessobjects.MappingType;
import com.ko.lct.job.geocoding.businessobjects.ParseReport;
import com.ko.lct.job.geocoding.logger.GeocoderLogger;
import com.ko.lct.job.geocoding.utilities.ApplicationException;
import com.ko.lct.job.geocoding.utilities.GeoCodingProcessProperties;
import com.ko.lct.job.geocoding.utilities.GeocodingConstants;
import com.ko.lct.job.geocoding.utilities.JdbcConnectionBroker;
import com.ko.lct.job.geocoding.utilities.NotExistingCountryException;
import com.ko.lct.job.geocoding.utilities.ParseResultReport;

public class ParseCSVManager extends ParseCSVBaseManager {

    private static final GeocoderLogger logger = GeocoderLogger.getInstance();

    private static final String USA_SHORT_NM = "US";
    private static final String USA_LONG_NM = "UNITED STATES";
    private static final String CANADA_SHORT_NM = "CA";
    private static final String CANADA_LONG_NM = "CANADA";
    private static final String PRIVACY_CODE = "880";
    private static final String MILITARY_CHNL = "216";
    private static final String GOVERMENT_CCHNL = "25";

    private static final String FOOD_ITEM_PRIMARY_CONTAINER_NAME = "Other";

    private static PreparedStatement insertAdressStmt;
    private static PreparedStatement insertOutletStmt;
    private static PreparedStatement insertBusinessTypeStmt;
    private static PreparedStatement insertSubChannelStmt;
    private static PreparedStatement insertChannelStmt;
    private static PreparedStatement insertChannelOrgStmt;
    private static PreparedStatement insertProductPKGStmt;
    private static PreparedStatement insertProductPKGTypeStmt;
    private static PreparedStatement insertCountryStmt;
    private static PreparedStatement insertContainerStmt;
    private static PreparedStatement insertProductStmt;
    private static PreparedStatement updateProductStmt;
    private static PreparedStatement updateProductPackageStmt;
    private static PreparedStatement insertLookupStmt;

    private static PreparedStatement insertBevCatStmt;

    private static PreparedStatement selectAdressStmt;
    private static PreparedStatement selectAdressWithoutAddrLine1Stmt;
    private static PreparedStatement selectOutletStmt;
    private static PreparedStatement selectOutletByAllClmnStmt;
    private static PreparedStatement updateOutletAddrId;
    private static PreparedStatement selectBusinessType;
    private static PreparedStatement selectSubChannelStmt;
    private static PreparedStatement selectChannelStmt;
    private static PreparedStatement selectChannelOrgStmt;
    private static PreparedStatement selectProductPKGTypeStmt;
    private static PreparedStatement selectCountryCodes;
    private static PreparedStatement selectProductCodeStmt;
    private static PreparedStatement selectProductPackageByPrdCodeStmt;
    private static PreparedStatement selectBevCatStmt;

    private static Set<String> setBusinessType = new HashSet<String>();
    private static Set<String> setCountryType = new HashSet<String>();
    private static Set<String> setChannelType = new HashSet<String>();
    private static Set<String> setChannelOrgType = new HashSet<String>(53);
    private static Set<String> setBevCatType = new HashSet<String>();
    private static Set<String> setSubTradeChannel = new HashSet<String>(257);
    private static Map<String, String> mapContainer = new HashMap<String, String>(500);
    private static Map<String, Integer> mapProductPkg = new HashMap<String, Integer>(4000);
    private static HashSet<String> lookupProductSet = new HashSet<String>();
    private static HashSet<String> lookupBrandSet = new HashSet<String>();
    private static HashSet<String> lookupFlavorSet = new HashSet<String>();
    private static HashSet<String> lookupSubChannelSet = new HashSet<String>();

    private static String lastProcessedDistr_Outlet_Name_ID1;
    private static String lastProcessedDistr_Outlet_Name_ID2;
    private static int lastProcessedDistr_Outlet_ID;

    private static Calendar cal = Calendar.getInstance();
    private static java.sql.Date currentDate;

    private static int lineNumber = 0;
    private static int skippedOutlet = 0;

    // private static boolean outletWasJustCreated = false;
    // private static boolean productPackageWasJustCreated = false;

    public static void parse() {
	ArrayList<ParseReport> reportList = new ArrayList<ParseReport>();
	ParseReport parseReport;
	JdbcConnectionBroker connectionBroker = null;
	try {
	    connectionBroker = getDbConnection(GeoCodingProcessProperties.DEFAULT_FILE_NAME);

	    // csv file containing data
	    String importDirectory = GeoCodingProcessProperties.getProperty("IMPORT_FILE_PATH");
	    String importFileName = GeoCodingProcessProperties.getProperty("IMPORT_FILE_NAME");
	    String processedDataDirName = GeoCodingProcessProperties.getProperty("PROCESSED_DATA_PATH");
	    String startRowParam = GeoCodingProcessProperties.getProperty("START_PARSING_ROW");
	    String sProcessArraySize = GeoCodingProcessProperties.getProperty("PROCESS_ARRAY_SIZE");
	    if (sProcessArraySize != null) {
		try {
		    processArraySize = Integer.valueOf(sProcessArraySize).intValue();
		    if (processArraySize < 0) {
			processArraySize = 100000;
		    }
		} catch (Exception e) {
		    System.err.println("PROCESS_ARRAY_SIZE - " + e.getMessage());
		}
	    }
	    if (startRowParam != null) {
		try {
		    startRow = Integer.valueOf(startRowParam).intValue();
		    if (startRow < 0) {
			startRow = 0;
		    }
		} catch (Exception e) {
		    System.err.println("START_PARSING_ROW - " + e.getMessage());
		}
	    }
	    File importFile;
	    if (importFileName != null && !importFileName.isEmpty()) {
		importFile = new File(importDirectory, importFileName);
		parseUsualFile(connectionBroker, importFile, processedDataDirName);
		parseReport = new ParseReport(importFile.getName(), lineNumber, newAddrCount);
		reportList.add(parseReport);
	    } else {
		File fileDir = new File(importDirectory);
		if (fileDir.exists()) {
		    List<String> fileNames = Arrays.asList(fileDir.list());
		    for (String fileName : fileNames) {
			importFile = new File(importDirectory, fileName);
			if (importFile.isFile()) {
			    if (isArchiveFile(importFile)) {
				parseZipFile(connectionBroker, importFile, processedDataDirName);
			    }
			    else if (isGzipFile(importFile)) {
				parseGZipFile(connectionBroker, importFile, processedDataDirName);
			    } else {
				parseUsualFile(connectionBroker, importFile, processedDataDirName);
			    }
			    parseReport = new ParseReport(importFile.getName(), lineNumber, newAddrCount);
			    reportList.add(parseReport);
			}
		    }
		}
	    }
	    
	    removeObsoleteData(connectionBroker);

	    connectionBroker.commit();
	    ParseResultReport.report(reportList);
	} catch (Exception e) {
	    logger.logError("Exception while reading csv file: ", e);
	} finally {
	    if (connectionBroker != null) {
		connectionBroker.cleanup();
	    }
	}
    }

    private static void removeObsoleteData(JdbcConnectionBroker connectionBroker)
	    throws SQLException, ApplicationException {

	final String schemaName = GeoCodingProcessProperties.getSchema();
	PreparedStatement removeObsoleteDataStmt =
		connectionBroker.getNewPreparedStatement(REMOVE_OBSOLETE_DELIVERY_DATA, schemaName);
	int numRemoved = 0;
	
	try {
	    do {
		numRemoved = removeObsoleteDataStmt.executeUpdate();
		logger.logInfo("Removed obsolete delivery data: " + numRemoved);
		connectionBroker.commit();
	    } while (numRemoved == GeocodingConstants.MAX_DELETE_ITEMS_COUNT);
	} finally {
	    removeObsoleteDataStmt.close();
	}

	removeObsoleteDataStmt =
		connectionBroker.getNewPreparedStatement(REMOVE_OBSOLETE_OUTLET_DATA, schemaName);
	try {
	    do {
		numRemoved = removeObsoleteDataStmt.executeUpdate();
		logger.logInfo("Removed obsolete outlet data: " + numRemoved);
		connectionBroker.commit();
	    } while (numRemoved == GeocodingConstants.MAX_DELETE_ITEMS_COUNT);
	} finally {
	    removeObsoleteDataStmt.close();
	}

	removeObsoleteDataStmt = connectionBroker.getNewPreparedStatement(REMOVE_DIGITAL_NOISE, schemaName);
	try {
	    numRemoved = removeObsoleteDataStmt.executeUpdate();
	    logger.logInfo("Removed \"digital noise\": " + numRemoved);
	    connectionBroker.commit();
	} finally {
	    removeObsoleteDataStmt.close();
	}

	removeObsoleteDataStmt = connectionBroker.getNewPreparedStatement(REMOVE_OBSOLETE_PRODUCT_PACKAGE_DATA, schemaName);
	try {
	    removeObsoleteDataStmt.executeUpdate();
	    connectionBroker.commit();
	} finally {
	    removeObsoleteDataStmt.close();
	}
    }

    private static void parseZipFile(JdbcConnectionBroker connectionBroker, File importFile,
	    String processedDataDirName) throws ArchiveException, IOException, ApplicationException, SQLException {
	FileInputStream fileInputStream = new FileInputStream(importFile);
	ArchiveInputStream is = new ArchiveStreamFactory().createArchiveInputStream(
		new BufferedInputStream(fileInputStream));
	try {
	    is.getNextEntry();
	    parseFile(connectionBroker, is, importFile);
	    logger.logInfo("Finish Parse file");
	} finally {
	    is.close();
	    fileInputStream.close();
	}
	moveFileToProcessedDataDir(processedDataDirName, importFile);
    }

    private static void parseGZipFile(JdbcConnectionBroker connectionBroker, File importFile,
	    String processedDataDirName) throws IOException, ApplicationException, SQLException {
	FileInputStream fileInputStream = new FileInputStream(importFile);
	BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
	GzipCompressorInputStream gzIn = new GzipCompressorInputStream(bufferedInputStream);
	try {
	    parseFile(connectionBroker, gzIn, importFile);
	    logger.logInfo("Finish Parse file");
	} finally {
	    gzIn.close();
	    bufferedInputStream.close();
	    fileInputStream.close();
	}
	moveFileToProcessedDataDir(processedDataDirName, importFile);
    }

    private static void parseUsualFile(JdbcConnectionBroker connectionBroker, File importFile,
	    String processedDataDirName) throws Exception {
	FileInputStream inputStream = new FileInputStream(importFile);
	try {
	    parseFile(connectionBroker, inputStream, importFile);
	} finally {
	    inputStream.close();
	}
	moveFileToProcessedDataDir(processedDataDirName, importFile);
    }

    private static void parseFile(JdbcConnectionBroker connectionBroker, InputStream csvFile, File importFile) throws ApplicationException, SQLException, IOException {

	// create BufferedReader to read csv file
	BufferedReader br = new BufferedReader(new InputStreamReader(csvFile, GeocodingConstants.ENCODE_UTF_8), BUFFER_SIZE);
	String curLine = null;
	try {

	    final String schemaName = GeoCodingProcessProperties.getSchema();
	    initOutletIdSeq(connectionBroker, schemaName);
	    initAddrIdSeq(connectionBroker, schemaName);
	    initProductPackageIdSeq(connectionBroker, schemaName);
	    loadFullProductPackageMap(connectionBroker, schemaName);
	    loadFullContainerMap(connectionBroker, schemaName);
	    loadLookupSet(connectionBroker, schemaName, MappingType.PRODUCT, lookupProductSet);
	    loadLookupSet(connectionBroker, schemaName, MappingType.BRAND, lookupBrandSet);
	    loadLookupSet(connectionBroker, schemaName, MappingType.FLAVOR, lookupFlavorSet);
	    loadLookupSet(connectionBroker, schemaName, MappingType.SUB_CHANNEL, lookupSubChannelSet);
	    
	    initBasePreparedStatements(connectionBroker, schemaName);
	    loadLookupMap(connectionBroker, schemaName);
	    initPreparedStatemets(connectionBroker, schemaName);

	    final SimpleDateFormat dateFormat = new SimpleDateFormat(GeocodingConstants.DEFAULT_SHORT_DATE_FORMAT);
	    currentDate = new java.sql.Date(cal.getTime().getTime());
	    String strLine = "";
	    lineNumber = 0;
	    // read comma separated file line by line

	    DataRow dr;
	    logger.logInfo("Parse File: " + importFile);
	    while ((strLine = br.readLine()) != null) {
		lineNumber++;
		// break comma separated line using ","
		if (startRow <= lineNumber)
		    break;
	    }

	    String[] arrayToProcess = new String[processArraySize];
	    while (true) {
		int numToProcess = 0;
		if (strLine != null) {
		    do {
			arrayToProcess[numToProcess] = strLine;
			numToProcess++;
		    } while ((strLine = br.readLine()) != null && numToProcess < arrayToProcess.length);
		}
		if (numToProcess > 0) {
		    logger.logInfo("Sort");
		    Arrays.sort(arrayToProcess, 0, numToProcess - 1);
		}

		for (int c = 0; c < numToProcess; c++) {
		    lineNumber++;
		    dr = new DataRow();
		    curLine = arrayToProcess[c];
		    fastSplit(arrayToProcess[c], dr);

		    if (dr.getOutletName() == null
			    || dr.getOutletName().isEmpty()
			    || (dr.getOutletTdlinxCode() != null && TDLinxStoreExcpetionCodeList.contains(
				    dr.getOutletTdlinxCode().substring(dr.getOutletTdlinxCode().length() - 3)))) {
			String outletName = dr.getOutletNameDesc();
			outletName = outletName.replaceAll("#[0-9]+", "").trim();
			dr.setOutletName(outletName);
		    }

		    // arrayToProcess[c] = null;

		    if (dr.getPrimaryContainerName() == null || dr.getPrimaryContainerName().isEmpty()) {
			if ("Q99".equals(dr.getPrimaryContainerCode())) {
			    dr.setPrimaryContainerName(FOOD_ITEM_PRIMARY_CONTAINER_NAME);
			}
		    }

		    // Skip private location & goverment & military channels;
		    if (PRIVACY_CODE.equals(dr.getOutletNameId1()))
			continue;

		    if (GOVERMENT_CCHNL.equals(dr.getTradeChannelCode()) || MILITARY_CHNL.equals(dr.getTradeChannelCode()))
			continue;

		    if (validateDataRow(dr, lineNumber, arrayToProcess[c])) {
			if (dr.getState() != null && !dr.getState().isEmpty()
			        && dr.getCity() != null && !dr.getCity().isEmpty()) {
			    try {
			        checkDelivery(dr, dateFormat);
			    } catch(NotExistingCountryException ex) {
	                logger.logError("Incorrect country " + dr.getCountry() + " in line " + lineNumber + 
	                        ". Only " + USA_LONG_NM + " and " + CANADA_LONG_NM + " are correct countries");
			    }
			}
			if (lineNumber % BATCH_SIZE == 0) {
			    logger.logInfo("ExecuteBatch - Line # " + lineNumber + " Skipped outlets: " + skippedOutlet);
			    skippedOutlet = 0;
			    logger.logInfo("Commit");
			    connectionBroker.commit();
			    currentDate = new java.sql.Date(cal.getTime().getTime());
			    logger.logInfo("Parsing...");
			}
		    }
		    // dr = null;
		}
		System.gc();
		if (numToProcess == 0)
		    break;
	    }

	    clearInitialMaps();
	    flushMergeDelivery(connectionBroker, schemaName);
	    
	    updateOutletEmptyLocations(connectionBroker, schemaName);

	    logger.logInfo("Commit - Line # " + lineNumber);
	    connectionBroker.commit();
	    
	} catch (SQLException e) {
	    logger.logInfo("Error line " + lineNumber + ": " + curLine);
	    throw e;
	} finally {
	    br.close();
	}
    }

    private static void updateOutletEmptyLocations(JdbcConnectionBroker connectionBroker, String schemaName) throws SQLException {
	PreparedStatement stmt = connectionBroker.getNewPreparedStatement(UPDATE_OUTLET_EMPTY_LOCATION, schemaName);
	try {
	    final int updatedCount = stmt.executeUpdate();
	    logger.logInfo("Updated empty coordinates in T_OUTLET: " + Integer.toString(updatedCount));
	}
	finally {
	    connectionBroker.closeStatement(stmt);
	}
    }

    private static void initPreparedStatemets(JdbcConnectionBroker connectionBroker, String schemaName)
	    throws SQLException {

	insertAdressStmt = connectionBroker.getNewPreparedStatement(INSERT_ADRESS_QUERY, schemaName);
	insertOutletStmt = connectionBroker.getNewPreparedStatement(INSERT_OUTLET_QUERY, schemaName);
	insertBusinessTypeStmt = connectionBroker.getNewPreparedStatement(INSERT_BUSINESS_TYPE_QUERY, schemaName);
	insertSubChannelStmt = connectionBroker.getNewPreparedStatement(INSERT_SB_CHANNEL_QUERY, schemaName);
	insertChannelStmt = connectionBroker.getNewPreparedStatement(INSERT_CHANNEL_QUERY, schemaName);
	insertChannelOrgStmt = connectionBroker.getNewPreparedStatement(INSERT_CHANNEL_ORG_QUERY, schemaName);
	insertProductPKGStmt = connectionBroker.getNewPreparedStatement(INSERT_PRODUCT_PKG_QUERY, schemaName);
	insertProductPKGTypeStmt = connectionBroker.getNewPreparedStatement(INSERT_PRODUCT_PCG_TYPE_QUERY, schemaName);
	insertCountryStmt = connectionBroker.getNewPreparedStatement(INSERT_COUNTRY_QUERY, schemaName);
	insertContainerStmt = connectionBroker.getNewPreparedStatement(INSERT_PACKAGE_QUERY, schemaName);
	insertProductStmt = connectionBroker.getNewPreparedStatement(INSERT_PRODUCT_QUERY, schemaName);
	updateProductStmt = connectionBroker.getNewPreparedStatement(UPDATE_PRODUCT_QUERY, schemaName);
	updateProductPackageStmt = connectionBroker.getNewPreparedStatement(UPDATE_PRODUCT_PACKAGE_QUERY, schemaName);
	insertLookupStmt = connectionBroker.getNewPreparedStatement(INSERT_LOOKUP_QUERY, schemaName);
	insertBevCatStmt = connectionBroker.getNewPreparedStatement(INSERT_BEV_CATEGORY_QUERY, schemaName);

	selectAdressStmt = connectionBroker.getNewPreparedStatement(SELECT_ADRESS_QUERY, schemaName);
	selectAdressWithoutAddrLine1Stmt = connectionBroker.getNewPreparedStatement(SELECT_ADRESS_WITHOUT_ADDR_LINE_1_QUERY, schemaName);
	selectOutletStmt = connectionBroker.getNewPreparedStatement(SELECT_OUTLET_QUERY, schemaName);
	selectOutletByAllClmnStmt = connectionBroker.getNewPreparedStatement(SELECT_OUTLET_BY_ALL_COLUMN_QUERY, schemaName);
	updateOutletAddrId = connectionBroker.getNewPreparedStatement(UPDATE_OUTLET_ADDR_ID, schemaName);
	selectBusinessType = connectionBroker.getNewPreparedStatement(SELECT_BUSINESS_TYPE_QUERY, schemaName);
	selectSubChannelStmt = connectionBroker.getNewPreparedStatement(SELECT_SB_CHANNEL_QUERY, schemaName);
	selectChannelStmt = connectionBroker.getNewPreparedStatement(SELECT_CHANNEL_QUERY, schemaName);
	selectChannelOrgStmt = connectionBroker.getNewPreparedStatement(SELECT_CHANNEL_ORG_QUERY, schemaName);
	selectProductPKGTypeStmt = connectionBroker.getNewPreparedStatement(SELECT_PRODUCT_PCG_TYPE_QUERY, schemaName);
	selectCountryCodes = connectionBroker.getNewPreparedStatement(IF_EMPTY_CHECK_QUERY, schemaName);
	selectProductCodeStmt = connectionBroker.getNewPreparedStatement(SELECT_PRODUCT_CODE_QUERY, schemaName);
	selectProductPackageByPrdCodeStmt = connectionBroker.getNewPreparedStatement(SELECT_PRODUCT_PACKAGE_BY_PRD_CODE_QUERY, schemaName);
	selectBevCatStmt = connectionBroker.getNewPreparedStatement(SELECT_BEV_CATEGORY_QUERY, schemaName);

    }

    private static void closeResultSet(ResultSet rs) {
	try {
	    if (rs != null)
		rs.close();
	} catch (SQLException e) {
	    logger.logError(e);
	}
    }

    private static void checkBusinessType(DataRow dr) throws SQLException {
	String id = dr.getCbsBusinessTypeId();

	if (setBusinessType.contains(id))
	    return;
	setBusinessType.add(id);

	selectBusinessType.setString(1, id);
	ResultSet rs = selectBusinessType.executeQuery();
	try {
	    if (!rs.next()) {
		insertBusinessTypeStmt.setString(1, dr.getCbsBusinessTypeId());
		insertBusinessTypeStmt.setString(2, dr.getCbsBusinessTypeDesc());
		insertBusinessTypeStmt.executeUpdate();
	    }
	} finally {
	    rs.close();
	}
    }

    private static int checkOutlet(DataRow dr) throws SQLException, NotExistingCountryException {
	// outletWasJustCreated = false;
	if (dr.getOutletNameId1().equals(lastProcessedDistr_Outlet_Name_ID1) &&
		dr.getOutletNameId2().equals(lastProcessedDistr_Outlet_Name_ID2)) {
	    skippedOutlet++;
	    return lastProcessedDistr_Outlet_ID;
	}

	int paramIndex = 0;
	int outletId = 0;
	ResultSet outletRs = null;
	ResultSet outletByAllClmnRs = null;
	try {
	    // Many deliveries to the same outlet appear in the order
	    // It is very likely that previous delivery is the same as a current
	    // one

	    selectOutletStmt.setString(1, dr.getOutletNameId1());
	    selectOutletStmt.setString(2, dr.getOutletNameId2());

	    outletRs = selectOutletStmt.executeQuery();
	    if (outletRs.next()) {
		outletId = outletRs.getInt(1);
		int oldAddrId = outletRs.getInt(2);
		final String stateInBd = outletRs.getString(3);
		final String cityInBd = outletRs.getString(4);
		final String addrLineInBd = outletRs.getString(5);
		final String stateFromFile = dr.getState();
		final String cityFromFile = dr.getCity();
		final String addrLineFromFile = dr.getAddressLine1();

		if ((
			(stateInBd != null && stateInBd.equals(stateFromFile))
			||
			(
			(stateInBd == null || stateInBd.isEmpty()) && (stateFromFile == null || stateFromFile.isEmpty())
			)
			) &&
			(
			(cityInBd != null && cityInBd.equals(cityFromFile))
			||
			(
			(cityInBd == null || cityInBd.isEmpty()) && (cityFromFile == null || cityFromFile.isEmpty())
			)
			) &&
			(
			(addrLineInBd != null && addrLineInBd.equals(addrLineFromFile))
			||
			(
			(addrLineInBd == null || addrLineInBd.isEmpty()) && (addrLineFromFile == null || addrLineFromFile.isEmpty())
			)
			)) {
		    // Addresses are equal
		}
		else {
		    int newAddrId = checkAddress(dr);
		    if (oldAddrId != newAddrId) {
			updateOutletAddrId.setInt(1, newAddrId);
			updateOutletAddrId.setInt(2, outletId);
			updateOutletAddrId.executeUpdate();
		    }
		}
		// OUTLET_ID, OWNRSHP_OUTLET2, NBR, CHN_NM, NAME, ADDR_ID,
		// SB_CHNL_ID, PHNE_NBR, CREATED_DT, CREATED_BY
		// OUTLET_ID, TDL_CD, TDL_GRP_CD, NM_ID1, NM_ID2, CHN_NM, NAME,
		// ADDR_ID, SB_CHNL_ID, PHNE_NBR, CREATED_DT, CREATED_BY
	    } else {
		int addrId = checkAddress(dr);
		// dr.getSub_Trade_Channel_Code()
		checkBusinessType(dr);
		checkSubChannel(dr);
		// inset new outlet

		/*
		 * moved to other place if (dr.getOutletName() == null || dr.getOutletName().isEmpty() || TDLinxStoreExcpetionCodeList.contains(dr.getOutletTdlinxCode()
		 * .substring(dr.getOutletTdlinxCode().length() - 3))) { String outletName = dr.getOutletNameDesc(); outletName = outletName.replaceAll("#[0-9]+",
		 * "").trim(); dr.setOutletName(outletName); }
		 */

		selectOutletByAllClmnStmt.setString(++paramIndex, dr.getOutletNameId1());
		selectOutletByAllClmnStmt.setString(++paramIndex, dr.getOutletName());
		selectOutletByAllClmnStmt.setString(++paramIndex, dr.getOutletTdlinxCode());
		selectOutletByAllClmnStmt.setInt(++paramIndex, addrId);
		selectOutletByAllClmnStmt.setString(++paramIndex, dr.getCbsBusinessTypeId());
		selectOutletByAllClmnStmt.setString(++paramIndex, dr.getSubTradeChannelCode());
		outletByAllClmnRs = selectOutletByAllClmnStmt.executeQuery();

		if (outletByAllClmnRs.next()) {
		    outletId = outletRs.getInt(1);
		} else {
		    paramIndex = 0;
		    outletIdSeq++;
		    outletId = outletIdSeq;

		    insertOutletStmt.setInt(++paramIndex, outletId);
		    insertOutletStmt.setString(++paramIndex, dr.getOutletTdlinxCode());
		    insertOutletStmt.setString(++paramIndex, dr.getOutletMktgGrpCode());
		    insertOutletStmt.setString(++paramIndex, dr.getOutletNameId1());
		    insertOutletStmt.setString(++paramIndex, dr.getOutletNameId2());
		    insertOutletStmt.setString(++paramIndex, dr.getOutletName());
		    insertOutletStmt.setString(++paramIndex, dr.getOutletNameDesc());
		    insertOutletStmt.setInt(++paramIndex, addrId);
		    insertOutletStmt.setString(++paramIndex, dr.getSubTradeChannelCode());
		    insertOutletStmt.setString(++paramIndex, dr.getTdlPhoneNumber());
		    insertOutletStmt.setDate(++paramIndex, currentDate);
		    insertOutletStmt.setString(++paramIndex, dr.getCbsBusinessTypeId());
		    String foodServiceIndicator = foodServiceMap.get(dr.getTradeChannelCode());
		    insertOutletStmt.setString(++paramIndex, foodServiceIndicator == null ? NO : foodServiceIndicator);

		    insertOutletStmt.executeUpdate();
		    // outletWasJustCreated = true;
		}

	    }

	    lastProcessedDistr_Outlet_Name_ID1 = dr.getOutletNameId1();
	    lastProcessedDistr_Outlet_Name_ID2 = dr.getOutletNameId2();
	    lastProcessedDistr_Outlet_ID = outletId;

	} finally {
	    closeResultSet(outletRs);
	    closeResultSet(outletByAllClmnRs);
	}
	return outletId;
    }

    // return address id
    private static int checkAddress(DataRow dr) throws SQLException, NotExistingCountryException {
	int addrId = 0;
	ResultSet addressRs = null;
	try {
	    // STATE, CITY, ADDR_LINE_1
	    if (dr.getAddressLine1() != null
		    && !dr.getAddressLine1().isEmpty()) {
		selectAdressStmt.setString(1, dr.getState());
		selectAdressStmt.setString(2, dr.getCity());
		selectAdressStmt.setString(3, dr.getAddressLine1());
		addressRs = selectAdressStmt.executeQuery();
	    } else {
		selectAdressWithoutAddrLine1Stmt.setString(1, dr.getState());
		selectAdressWithoutAddrLine1Stmt.setString(2, dr.getCity());
		addressRs = selectAdressWithoutAddrLine1Stmt.executeQuery();
	    }

	    if (addressRs.next()) {
		addrId = addressRs.getInt(1);
	    } else {
		checkCountry(dr);
		// ADDR_ID, STATE, CITY, PSTL_CD, ADDR_LINE_1, ADDR_LINE_2,
		// CTRY_CD, CREATED_DT, CREATED_BY
		addrIdSeq++;
		newAddrCount++;
		addrId = addrIdSeq;

		insertAdressStmt.setInt(1, addrId);
		insertAdressStmt.setString(2, dr.getState());
		insertAdressStmt.setString(3, dr.getCity());
		insertAdressStmt.setString(4, dr.getPostalCode());

		insertAdressStmt.setString(5, dr.getAddressLine1());
		insertAdressStmt.setString(6, dr.getAddressLine2());
		if (USA_LONG_NM.equals(dr.getCountry())) {
		    insertAdressStmt.setString(7, USA_SHORT_NM);
		} else if (CANADA_LONG_NM.equals(dr.getCountry())) {
		    insertAdressStmt.setString(7, CANADA_SHORT_NM);
		}

		insertAdressStmt.setDate(8, currentDate);
		insertAdressStmt.executeUpdate();

	    }
	} finally {
	    closeResultSet(addressRs);
	}

	return addrId;
    }

    private static void checkCountry(DataRow dr) throws SQLException, NotExistingCountryException {
	// /check country
	ResultSet ctrRs = null;
	try {
	    String id = dr.getCountry();

	    if (setCountryType.contains(id))
		return;
	    setCountryType.add(id);

	    selectCountryCodes.setString(1, dr.getCountry());

	    ctrRs = selectCountryCodes.executeQuery();
	    if (ctrRs.next()) {
		// ctrRs.getInt(1);
	    } else {
		// PRD_PKG_TYPE_ID,NAME
    		if (USA_LONG_NM.equals(dr.getCountry())) {
    		    insertCountryStmt.setString(1, USA_SHORT_NM);
    		    insertCountryStmt.setString(2, USA_LONG_NM);
    	        insertCountryStmt.executeUpdate();
    		} else if (CANADA_LONG_NM.equals(dr.getCountry())) {
    		    insertCountryStmt.setString(1, CANADA_SHORT_NM);
    		    insertCountryStmt.setString(2, CANADA_LONG_NM);
    	        insertCountryStmt.executeUpdate();
    		} else {
    		    throw new NotExistingCountryException();
    		}    		
	    }
	} finally {
	    closeResultSet(ctrRs);
	}
    }

    private static void checkSubChannel(DataRow dr) throws SQLException {
	// check sub channel
	ResultSet subchannelRs = null;
	try {
	    String id = dr.getSubTradeChannelCode();

	    if (setSubTradeChannel.contains(id))
		return;
	    setSubTradeChannel.add(id);

	    selectSubChannelStmt.setString(1, id);

	    subchannelRs = selectSubChannelStmt.executeQuery();
	    if (!subchannelRs.next()) {
		checkChannel(dr);
		insertSubChannelStmt.setString(1, id);
		insertSubChannelStmt.setString(2, dr.getSubTradeChannelName());
		insertSubChannelStmt.setString(3, dr.getTradeChannelCode());
		insertSubChannelStmt.executeUpdate();
		
		checkLookupValue(MappingType.SUB_CHANNEL, dr.getSubTradeChannelName(), id, lookupSubChannelSet);
	    }
	} finally {
	    closeResultSet(subchannelRs);
	}
    }

    private static void checkChannel(DataRow dr) throws SQLException {
	// check channel
	String id = dr.getTradeChannelCode();

	if (setChannelType.contains(id))
	    return;
	setChannelType.add(id);

	selectChannelStmt.setString(1, dr.getTradeChannelCode());
	ResultSet channelRs = selectChannelStmt.executeQuery();
	try {
	    if (!channelRs.next()) {
		checkChannelOrg(dr);
		insertChannelStmt.setString(1, dr.getTradeChannelCode());
		insertChannelStmt.setString(2, dr.getTradeChannelName());
		insertChannelStmt.setString(3, dr.getChannelOrgId());
		String foodServiceIndicator = foodServiceMap.get(dr.getTradeChannelCode());
		insertChannelStmt.setString(4, foodServiceIndicator == null ? NO : foodServiceIndicator);
		insertChannelStmt.executeUpdate();
	    }
	} finally {
	    channelRs.close();
	}
    }

    private static void checkChannelOrg(DataRow dr) throws SQLException {
	// check if Channel Org exist
	String id = dr.getChannelOrgDesc();

	if (setChannelOrgType.contains(id))
	    return;
	setChannelOrgType.add(id);

	selectChannelOrgStmt.setString(1, dr.getChannelOrgDesc());
	ResultSet channelOrgRs = selectChannelOrgStmt.executeQuery();
	try {
	    if (!channelOrgRs.next()) {
		insertChannelOrgStmt.setString(1, dr.getChannelOrgId());
		insertChannelOrgStmt.setString(2, dr.getChannelOrgDesc());
		insertChannelOrgStmt.executeUpdate();
	    }
	} finally {
	    channelOrgRs.close();
	}
    }

    private static void checkDelivery(DataRow dr, SimpleDateFormat dateFormat) 
            throws SQLException, NotExistingCountryException {

	// check if Delivery exist
	int outletID = checkOutlet(dr);
	int prodPKGID = checkProductPkg(dr);

	java.util.Date ddd = null;
	try {
	    ddd = dateFormat.parse(dr.getDeliveryDate());
	} catch (Exception e) {
	    logger.logError(e);
	}

	if (ddd == null) {
	    ddd = new java.util.Date(0); // FIXME: we should skip records with
					 // incorrect date
	}
	java.sql.Date dlDate = new java.sql.Date(ddd.getTime());
	addMergeDeliveryRow(dlDate, outletID, prodPKGID);
    }

    private static int checkProductPkg(DataRow dr) throws SQLException {
	// productPackageWasJustCreated = false;
	// check if product pkg exist
	checkContainer(dr);
	final String key = getProductPackageKey(dr.getProductCode(),
		dr.getPrimaryContainerCode(), dr.getSecondaryPackageCode(),
		dr.getBppCode(), dr.getPhysicalStateCode(), dr.getProductTypeId());

	Integer idFromMap = mapProductPkg.get(key);
	if (idFromMap != null)
	    return idFromMap.intValue();

	checkProduct(dr);
	checkProductPKGType(dr);
	insertProductPKGStmt.setInt(1, ++productPackageIdSeq);
	insertProductPKGStmt.setString(2, dr.getProductCode());
	insertProductPKGStmt.setString(3, dr.getPrimaryContainerCode());
	insertProductPKGStmt.setString(4, dr.getSecondaryPackageCode());
	insertProductPKGStmt.setString(5, dr.getBppCode());
	insertProductPKGStmt.setString(6, dr.getBppName());
	insertProductPKGStmt.setString(7, dr.getPhysicalStateCode());
	insertProductPKGStmt.setString(8, dr.getPhysicalStateName());
	insertProductPKGStmt.setString(9, dr.getProductTypeId());
	insertProductPKGStmt.setString(10, dr.getBrandCode());
	insertProductPKGStmt.setString(11, dr.getFlavorCode());
	insertProductPKGStmt.executeUpdate();
	int id = productPackageIdSeq;
	// productPackageWasJustCreated = true;
	mapProductPkg.put(key, Integer.valueOf(id));
	return id;
    }

    private static void checkProduct(DataRow dr) throws SQLException {
	int paramIndex = 0;
	selectProductPackageByPrdCodeStmt.setString(++paramIndex, dr.getProductCode());
	ResultSet pacRs = selectProductPackageByPrdCodeStmt.executeQuery();
	paramIndex = 0;
	try {
	    if (!pacRs.next()) {
		if(dr.getBppName() != null) {
        		checkLookupValue(MappingType.PRODUCT, dr.getBppName(), dr.getProductCode(), lookupProductSet);
        		checkLookupValue(MappingType.BRAND, dr.getBrandName(), dr.getBrandCode(), lookupBrandSet);
        		checkLookupValue(MappingType.FLAVOR, dr.getFlavorName(), dr.getFlavorCode(), lookupFlavorSet);
        		checkBevCat(dr);
        
        		selectProductCodeStmt.setString(++paramIndex, dr.getProductCode());
        		ResultSet prdRs = selectProductCodeStmt.executeQuery();
        		try {
    			    paramIndex = 0;
        		    if(!prdRs.next()) {
        			insertProductStmt.setString(++paramIndex, dr.getProductCode());
        			insertProductStmt.setString(++paramIndex, dr.getBrandCode());
        			insertProductStmt.setString(++paramIndex, dr.getFlavorCode());
        			insertProductStmt.executeUpdate();
        			
        			insertBevCatPrdMap(dr.getBeverageCategoryCode(), dr.getProductCode());
        		    } else {        			
        			updateProductStmt.setString(++paramIndex, dr.getBrandCode());
        			updateProductStmt.setString(++paramIndex, dr.getFlavorCode());
        			updateProductStmt.setString(++paramIndex, dr.getProductCode());
        			updateProductStmt.executeUpdate();
        			
        			paramIndex = 0;        			
        			updateProductPackageStmt.setString(++paramIndex, dr.getBrandCode());
        			updateProductPackageStmt.setString(++paramIndex, dr.getFlavorCode());
        			updateProductPackageStmt.setString(++paramIndex, dr.getProductCode());
        			updateProductPackageStmt.executeUpdate();
        			
        			checkBevCatPrdLnk(dr.getBeverageCategoryCode(), dr.getProductCode());
        		    }
        		} catch (SQLException e) {
        		    throw e;
        		} finally {
        		    prdRs.close();
        		}
		}
	    } else {
		String brandCode = pacRs.getString("BRND_CD");
		String flavorCode = pacRs.getString("FLVR_CD");
		if(dr.getBrandCode() != null && !dr.getBrandCode().equals(brandCode)) {
		    	checkLookupValue(MappingType.BRAND, dr.getBrandName(), dr.getBrandCode(), lookupBrandSet);
		}
		if(dr.getFlavorCode() != null && !dr.getFlavorCode().equals(flavorCode)) {
		    	checkLookupValue(MappingType.FLAVOR, dr.getFlavorName(), dr.getFlavorCode(), lookupFlavorSet);
		}
		if((dr.getBrandCode() != null && !dr.getBrandCode().equals(brandCode))
			|| (dr.getFlavorCode() != null && !dr.getFlavorCode().equals(flavorCode))) {
		
        		paramIndex = 0;
        		updateProductStmt.setString(++paramIndex, dr.getBrandCode());
        		updateProductStmt.setString(++paramIndex, dr.getFlavorCode());
        		updateProductStmt.setString(++paramIndex, dr.getProductCode());
        		updateProductStmt.executeUpdate();
        			
        		paramIndex = 0;        			
        		updateProductPackageStmt.setString(++paramIndex, dr.getBrandCode());
        		updateProductPackageStmt.setString(++paramIndex, dr.getFlavorCode());
        		updateProductPackageStmt.setString(++paramIndex, dr.getProductCode());
        		updateProductPackageStmt.executeUpdate();
		}
		
		checkBevCatPrdLnk(dr.getBeverageCategoryCode(), dr.getProductCode());
	    }
	} catch (SQLException e) {
	    logger.logInfo("Error line: Product code:" + dr.getProductCode() +
			    "|Brand code:" + dr.getBrandCode() +
			    "|Brand name:" + dr.getBrandName() +
			    "|Flavor code:" + dr.getFlavorCode() +
			    "|Flavor name:" + dr.getFlavorName() +
			    "|Bpp code:" + dr.getBppCode() +
			    "|Bpp name:" + dr.getBppName());
	    throw e;
	} finally {
	    pacRs.close();
	}
    }

    private static void checkLookupValue(MappingType lookupType, String sourceValue, String lookupCode,
            HashSet<String> lookupSet) throws SQLException {       
	    
	    if (!lookupSet.contains(lookupCode)) {	    
	            int paramIndex = 0;
	            insertLookupStmt.setInt(++paramIndex, lookupType.getValue());
		        insertLookupStmt.setString(++paramIndex, lookupCode);
    	    	insertLookupStmt.setString(++paramIndex, sourceValue);
    	    	insertLookupStmt.setString(++paramIndex, ACTIVE_INDEX);
    	    	insertLookupStmt.executeUpdate();
    	    	lookupSet.add(lookupCode);
	    }
    }

    private static void checkBevCat(DataRow dr) throws SQLException {
	// check if product pkg exist
	ResultSet prodprkgRs = null;
	try {
	    String id = dr.getBeverageCategoryCode();

	    if (setBevCatType.contains(id))
		return;
	    setBevCatType.add(id);

	    selectBevCatStmt.setString(1, dr.getBeverageCategoryCode());// *
	    prodprkgRs = selectBevCatStmt.executeQuery();
	    if (!prodprkgRs.next()) {
		insertBevCatStmt.setString(1, dr.getBeverageCategoryCode());// *
		insertBevCatStmt.setString(2, dr.getBeverageCategoryName());// *
		insertBevCatStmt.executeUpdate();
	    }
	} finally {
	    closeResultSet(prodprkgRs);
	}
    }

    private static void checkContainer(DataRow dr) throws SQLException {
	int paramIndex = 0;
	final String key = getContainerKey(dr.getPrimaryContainerCode(), dr.getSecondaryPackageCode());
	String primaryContainerCode = mapContainer.get(key);
	if (primaryContainerCode != null) {
	    return;
	}
	insertContainerStmt.setString(++paramIndex, dr.getPrimaryContainerCode());
	insertContainerStmt.setString(++paramIndex, dr.getSecondaryPackageCode());
	insertContainerStmt.setString(++paramIndex, dr.getPackageCategoryName());

	String primContShrtCode = getPrimaryContainerShrtCode(dr.getPrimaryContainerName());
	if (primContShrtCode == null) {
	    primContShrtCode = addUnmappedValue(LOOKUP_PRIMARY_CONTAINER, dr.getPrimaryContainerName());
	}
	insertContainerStmt.setString(++paramIndex, primContShrtCode);

	Double baseSize = getBaseSize(dr.getPrimaryContainerName());
	if (baseSize == null) {
	    insertContainerStmt.setNull(++paramIndex, Types.DOUBLE);
	}
	else {
	    insertContainerStmt.setDouble(++paramIndex, baseSize.doubleValue());
	}

	String secPkgShrtCode = getSecondaryPackageShrtCode(dr.getSecondaryPackageDesc());
	if (secPkgShrtCode == null) {
	    addUnmappedValue(LOOKUP_SECONDARY_PACKAGE, dr.getSecondaryPackageDesc());
	    secPkgShrtCode = getSecondaryPackageShrtCodeBySize(dr.getSecondaryPackageDesc());
	}
	insertContainerStmt.setString(++paramIndex, secPkgShrtCode);

	insertContainerStmt.executeUpdate();
	mapContainer.put(key, dr.getPrimaryContainerCode());
    }

    private static void checkProductPKGType(DataRow dr) throws SQLException {
	// check prod pkg type
	selectProductPKGTypeStmt.setString(1, dr.getProductTypeId());
	selectProductPKGTypeStmt.setString(2, dr.getProductTypeDesc());
	ResultSet prodprkgRs = selectProductPKGTypeStmt.executeQuery();
	try {
	    if (!prodprkgRs.next()) {
		// PRD_PKG_TYPE_ID,NAME
		insertProductPKGTypeStmt.setString(1, dr.getProductTypeId());
		insertProductPKGTypeStmt.setString(2, dr.getProductTypeDesc());
		insertProductPKGTypeStmt.executeUpdate();
	    }
	} finally {
	    prodprkgRs.close();
	}
    }

    private static void fastSplit(final String str, DataRow dr) {
	int item = 0;
	int j = 0;
	int i;
	while ((i = str.indexOf('|', j)) >= 0) {
	    dr.setValue(item++, str.substring(j, i).trim());
	    j = i + 1;
	}
	if (j < str.length()) {
	    dr.setValue(item++, str.substring(j).trim());
	}
    }

    private static List<String> TDLinxStoreExcpetionCodeList = new ArrayList<String>();
    static {
	TDLinxStoreExcpetionCodeList.add("91Z");
	TDLinxStoreExcpetionCodeList.add("92Z");
	TDLinxStoreExcpetionCodeList.add("93Z");
	TDLinxStoreExcpetionCodeList.add("94Z");
	TDLinxStoreExcpetionCodeList.add("95Z");
	TDLinxStoreExcpetionCodeList.add("96Z");
	TDLinxStoreExcpetionCodeList.add("97Z");
	TDLinxStoreExcpetionCodeList.add("98Z");
	TDLinxStoreExcpetionCodeList.add("99Z");
	TDLinxStoreExcpetionCodeList.add("00Z");
    }

    private static void loadFullProductPackageMap(JdbcConnectionBroker connectionBroker, String schemaName) throws SQLException {
	logger.logInfo("Load product packages...");
	PreparedStatement stmt = connectionBroker.getNewPreparedStatement(SELECT_ALL_PRODUCT_PACKAGE_QUERY, schemaName);
	try {
	    ResultSet rs = stmt.executeQuery();
	    try {
		while (rs.next()) {
		    mapProductPkg.put(
			    getProductPackageKey(
				    rs.getString("PRD_CD"),
				    rs.getString("PKG_PRIM_CD"),
				    rs.getString("PKG_SECN_CD"),
				    rs.getString("BPP_CD"),
				    rs.getString("PHYS_ST_CD"),
				    rs.getString("PRD_PKG_TYPE_ID")),
			    Integer.valueOf(rs.getInt("PRD_PKG_ID")));
		}
	    } finally {
		rs.close();
	    }
	} finally {
	    stmt.close();
	}
    }

    private static String getProductPackageKey(String productCode, String primaryContainerCode, String secondaryPackageCode, String bppCode,
	    String physicalStateCode, String productTypeId) {
	return (new StringBuilder(30))
		.append(productCode)
		.append(primaryContainerCode)
		.append(secondaryPackageCode)
		.append(bppCode == null ? "null" : bppCode)
		.append(physicalStateCode == null ? "null" : physicalStateCode)
		.append(productTypeId == null ? "null" : productTypeId)
		.toString();
    }

    private static void loadFullContainerMap(JdbcConnectionBroker connectionBroker, String schemaName) throws SQLException {
	logger.logInfo("Load containers...");
	PreparedStatement stmt = connectionBroker.getNewPreparedStatement(SELECT_ALL_CONTAINER_QUERY, schemaName);
	try {
	    ResultSet rs = stmt.executeQuery();
	    try {
		while (rs.next()) {
		    mapContainer.put(
			    getContainerKey(
				    rs.getString("PKG_PRIM_CD"),
				    rs.getString("PKG_SECN_CD")
			    ),
			    rs.getString("PKG_PRIM_CD"));
		}
	    } finally {
		rs.close();
	    }
	} finally {
	    stmt.close();
	}
    }

    private static void loadLookupSet(JdbcConnectionBroker connectionBroker, String schemaName,
	    MappingType mappingType, HashSet<String> lookupSet) throws SQLException {
	logger.logInfo("Load lookup set for " + mappingType.name() + "...");
	PreparedStatement stmt = connectionBroker.getNewPreparedStatement(SELECT_LOOKUP_QUERY, schemaName);
	try {
	    stmt.setInt(1, mappingType.getValue());
	    ResultSet rs = stmt.executeQuery();
	    try {
		while (rs.next()) {
		    lookupSet.add(rs.getString("LKP_CD"));
		}
	    } finally {
		rs.close();
	    }
	} finally {
	    stmt.close();
	}
    }

    private static String getContainerKey(String primaryContainerCode, String secondaryPackageCode) {
	return (new StringBuilder(100)).append(primaryContainerCode).append('+').append(secondaryPackageCode).toString();
    }

    private static void clearInitialMaps() {
	lookupProductSet.clear();
	lookupBrandSet.clear();
	lookupFlavorSet.clear();
    lookupSubChannelSet.clear();
    }

    private static boolean validateDataRow(DataRow dr, int rowNumber, String sourceString) {
	if (dr.getOutletName() == null || dr.getOutletName().isEmpty()) {
	    logger.logError("Outlet_Name is null or empty");
	    logDataRow(rowNumber, sourceString);
	    return false;
	}

	/*
	 * if (dr.getOutlet_Code_TDLinx_Code() == null || dr.getOutlet_Code_TDLinx_Code().isEmpty()) { logger.severe("Outlet_Code_TDLinx_Code is null or empty");
	 * logDataRow(rowNumber, sourceString); return false; }
	 */

	/*
	 * if (dr.getOutlet_Code_Mktg_Grp() == null || dr.getOutlet_Code_Mktg_Grp().isEmpty()) { logger.severe("Outlet_Code_Mktg_Grp is null or empty");
	 * logDataRow(rowNumber, sourceString); return false; }
	 */

	if (dr.getOutletNameDesc() == null || dr.getOutletNameDesc().isEmpty()) {
	    logger.logError("Distr_Outlet_Name_DESC is null or empty");
	    logDataRow(rowNumber, sourceString);
	    return false;
	}

	if (dr.getOutletNameId1() == null || dr.getOutletNameId1().isEmpty()) {
	    logger.logError("Distr_Outlet_Name_ID1 is null or empty");
	    logDataRow(rowNumber, sourceString);
	    return false;
	}

	if (dr.getOutletNameId2() == null || dr.getOutletNameId2().isEmpty()) {
	    logger.logError("Distr_Outlet_Name_ID2 is null or empty");
	    logDataRow(rowNumber, sourceString);
	    return false;
	}

	/*
	 * if (dr.getDistr_Outlet_Address() == null || dr.getDistr_Outlet_Address().isEmpty()) { logger.severe("Distr_Outlet_Address is null or empty");
	 * logDataRow(rowNumber, sourceString); return false; }
	 */

	if (dr.getPostalCode() == null || dr.getPostalCode().isEmpty()) {
	    if (dr.getCity() == null || dr.getCity().isEmpty()) {
		logger.logError("Distr_Outlet_Zip is null or empty");
		logger.logError("Distr_Outlet_City is null or empty");
		logDataRow(rowNumber, sourceString);
		return false;
	    }

	    if (dr.getState() == null || dr.getState().isEmpty()) {
		logger.logError("Distr_Outlet_Zip is null or empty");
		logger.logError("Distr_Outlet_State is null or empty");
		logDataRow(rowNumber, sourceString);
		return false;
	    }

	    if (dr.getCountry() == null || dr.getCountry().isEmpty()) {
		logger.logError("Distr_Outlet_Zip is null or empty");
		logger.logError("Country is null or empty");
		logDataRow(rowNumber, sourceString);
		return false;
	    }
	}

	if (dr.getCbsBusinessTypeDesc() == null || dr.getCbsBusinessTypeDesc().isEmpty()) {
	    logger.logError("CBS_Business_Type_DESC is null or empty");
	    logDataRow(rowNumber, sourceString);
	    return false;
	}

	if (dr.getCbsBusinessTypeId() == null || dr.getCbsBusinessTypeId().isEmpty()) {
	    logger.logError("CBS_Business_Type_ID is null or empty");
	    logDataRow(rowNumber, sourceString);
	    return false;
	}

	/*
	 * if (dr.getTDL_Area_Code() == null || dr.getTDL_Area_Code().isEmpty()) { logger.severe("TDL_Area_Code is null or empty"); logDataRow(rowNumber, sourceString);
	 * return false; }
	 */

	if (dr.getDeliveryDate() == null || dr.getDeliveryDate().isEmpty()) {
	    logger.logError("Delivery_Date is null or empty");
	    logDataRow(rowNumber, sourceString);
	    return false;
	}

	if (dr.getProductTypeDesc() == null || dr.getProductTypeDesc().isEmpty()) {
	    logger.logError("Product_Type_DESC is null or empty");
	    logDataRow(rowNumber, sourceString);
	    return false;
	}

	if (dr.getProductTypeId() == null || dr.getProductTypeId().isEmpty()) {
	    logger.logError("Product_Type_ID is null or empty");
	    logDataRow(rowNumber, sourceString);
	    return false;
	}

	if (dr.getBeverageCategoryName() == null || dr.getBeverageCategoryName().isEmpty()) {
	    logger.logError("Beverage_Category_Nm is null or empty");
	    logDataRow(rowNumber, sourceString);
	    return false;
	}

	if (dr.getBeverageCategoryCode() == null || dr.getBeverageCategoryCode().isEmpty()) {
	    logger.logError("Beverage_Category_Code is null or empty");
	    logDataRow(rowNumber, sourceString);
	    return false;
	}

	if (dr.getBrandName() == null || dr.getBrandName().isEmpty()) {
	    logger.logError("Brand_Nm is null or empty");
	    logDataRow(rowNumber, sourceString);
	    return false;
	}

	if (dr.getBrandCode() == null || dr.getBrandCode().isEmpty()) {
	    logger.logError("Brand_Code is null or empty");
	    logDataRow(rowNumber, sourceString);
	    return false;
	}

	if (dr.getFlavorName() == null || dr.getFlavorName().isEmpty()) {
	    logger.logError("Flavor_Nm is null or empty");
	    logDataRow(rowNumber, sourceString);
	    return false;
	}

	if (dr.getFlavorCode() == null || dr.getFlavorCode().isEmpty()) {
	    logger.logError("Flavor_Code is null or empty");
	    logDataRow(rowNumber, sourceString);
	    return false;
	}

	/*
	if (dr.getPrimaryContainerName() == null || dr.getPrimaryContainerName().isEmpty()) {
	    if ("Q99".equals(dr.getPrimaryContainerCode())) {
		dr.setPrimaryContainerName(FOOD_ITEM_PRIMARY_CONTAINER_NAME);
	    }
	}
	*/
	
	
	/*
	 * if (dr.getPrimary_Container_Nm() == null || dr.getPrimary_Container_Nm().isEmpty()) { logger.severe("Primary_Container_Nm is null or empty");
	 * logDataRow(rowNumber, sourceString); return false; }
	 */

	if (dr.getPrimaryContainerCode() == null || dr.getPrimaryContainerCode().isEmpty()) {
	    logger.logError("Primary_Container_Code is null or empty");
	    logDataRow(rowNumber, sourceString);
	    return false;
	}

	/*
	 * if (dr.getSecondary_Package_Desc() == null || dr.getSecondary_Package_Desc().isEmpty()) { logger.severe("Secondary_Package_Desc is null or empty");
	 * logDataRow(rowNumber, sourceString); return false; }
	 */

	if (dr.getSecondaryPackageCode() == null || dr.getSecondaryPackageCode().isEmpty()) {
	    logger.logError("Secondary_Package_Code is null or empty");
	    logDataRow(rowNumber, sourceString);
	    return false;
	}

	if (dr.getChannelOrgDesc() == null || dr.getChannelOrgDesc().isEmpty()) {
	    logger.logError("Channel_Org_DESC is null or empty");
	    logDataRow(rowNumber, sourceString);
	    return false;
	}

	if (dr.getChannelOrgId() == null || dr.getChannelOrgId().isEmpty()) {
	    logger.logError("Channel_Org_ID is null or empty");
	    logDataRow(rowNumber, sourceString);
	    return false;
	}

	if (dr.getTradeChannelName() == null || dr.getTradeChannelName().isEmpty()) {
	    logger.logError("Trade_Channel_Name is null or empty");
	    logDataRow(rowNumber, sourceString);
	    return false;
	}

	if (dr.getTradeChannelCode() == null || dr.getTradeChannelCode().isEmpty()) {
	    logger.logError("Trade_Channel_Code is null or empty");
	    logDataRow(rowNumber, sourceString);
	    return false;
	}

	if (dr.getSubTradeChannelName() == null || dr.getSubTradeChannelName().isEmpty()) {
	    logger.logError("Sub_Trade_Channel_Name is null or empty");
	    logDataRow(rowNumber, sourceString);
	    return false;
	}

	if (dr.getSubTradeChannelCode() == null || dr.getSubTradeChannelCode().isEmpty()) {
	    logger.logError("Sub_Trade_Channel_Code is null or empty");
	    logDataRow(rowNumber, sourceString);
	    return false;
	}

	if (dr.getBppCode() == null || dr.getBppCode().isEmpty()) {
	    logger.logError("BPPCode is null or empty");
	    logDataRow(rowNumber, sourceString);
	    return false;
	}

	if (dr.getBppName() == null || dr.getBppName().isEmpty()) {
	    logger.logError("BPPName is null or empty");
	    logDataRow(rowNumber, sourceString);
	    return false;
	}

	if (dr.getPhysicalStateCode() == null || dr.getPhysicalStateCode().isEmpty()) {
	    logger.logError("physicalStateCode is null or empty");
	    logDataRow(rowNumber, sourceString);
	    return false;
	}

	if (dr.getPhysicalStateName() == null || dr.getPhysicalStateName().isEmpty()) {
	    logger.logError("physicalStateName is null or empty");
	    logDataRow(rowNumber, sourceString);
	    return false;
	}

	if (dr.getPackageCategoryName() == null || dr.getPackageCategoryName().isEmpty()) {
	    logger.logError("packageCategoryName is null or empty");
	    logDataRow(rowNumber, sourceString);
	    return false;
	}
	return true;
    }

    private static void logDataRow(int rowNumber, String sourceString) {
	StringBuilder sb = new StringBuilder("Row Number ");
	sb.append(rowNumber).append(": ").append(sourceString);
	logger.logInfo(sb.toString());
    }

}
