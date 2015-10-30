package com.ko.lct.job.geocoding.managment;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;

import com.ko.lct.job.geocoding.Dao.GeocodingQuery;
import com.ko.lct.job.geocoding.businessobjects.Delivery;
import com.ko.lct.job.geocoding.businessobjects.GenericValueObject;
import com.ko.lct.job.geocoding.businessobjects.MappingType;
import com.ko.lct.job.geocoding.businessobjects.NielsonProperty;
import com.ko.lct.job.geocoding.businessobjects.Outlet;
import com.ko.lct.job.geocoding.businessobjects.ParseReport;
import com.ko.lct.job.geocoding.businessobjects.Product;
import com.ko.lct.job.geocoding.logger.GeocoderLogger;
import com.ko.lct.job.geocoding.utilities.GeoCodingProcessProperties;
import com.ko.lct.job.geocoding.utilities.GeocodingConstants;
import com.ko.lct.job.geocoding.utilities.JdbcConnectionBroker;
import com.ko.lct.job.geocoding.utilities.ParseResultReport;

public class ParseCSVNielsonManager extends ParseCSVBaseManager {

    private static final GeocoderLogger logger = GeocoderLogger.getInstance();

    private static PreparedStatement productPackageSelectStmnt;
    private static PreparedStatement productPackageInsertStmnt;
    private static PreparedStatement productInsertStmnt;
    private static PreparedStatement packageSelectStmnt;
    private static PreparedStatement packageInsertStmnt;
    private static PreparedStatement outletSelectByIdStmnt;
    private static PreparedStatement outletSelectStmnt;
    private static PreparedStatement outletInsertStmnt;
    private static PreparedStatement checkImproveAddrStmnt;
    private static PreparedStatement improveAddrStmnt;
    private static PreparedStatement improveOutletAddrStmnt;
    private static PreparedStatement addrSelectStmnt;
    private static PreparedStatement addrInsertStmnt;
    private static PreparedStatement geocodedAddrInsertStmnt;
    // private static PreparedStatement selectProductStmt;
    
    private static final String CSV_SPLITER = ",";

    private static final String DATE_FORMAT = "MM/dd/yy";
    private static final int PRODUCT_TYPE_POST_MIX_ID = 2;
    private static final int PRODUCT_TYPE_BC_ID = 3;
    private static final int BUSINESS_TYPE_TRADE_SALE_ID = 1;
    private static final int NOT_SELECTED_VALUE = -1;
    private static final int NIELSON_GEOCODE_LEVEL = 7;
    private static final String UNFLAVORED = "UNFLAVORED";

    private static String lastOutletKey;
    private static int lastOutletId = 0;
    private static int delivLineNumber = 0;
    private static int newOutletCount = 0;

    private static Map<String, GenericValueObject> convertedProductMap = new HashMap<String, GenericValueObject>();
    private static Map<String, GenericValueObject> convertedBrandMap = new HashMap<String, GenericValueObject>();
    private static Map<String, GenericValueObject> convertedFlavorMap = new HashMap<String, GenericValueObject>();
    private static Map<String, GenericValueObject> convertedSubChannelMap = new HashMap<String, GenericValueObject>();
    private static Map<String, GenericValueObject> primaryContainerMap = new HashMap<String, GenericValueObject>();
    private static Map<String, GenericValueObject> productBrandFlavorMap = new HashMap<String, GenericValueObject>(); // field value of GenericValueObject fill store
														      // Flavor Code
    private static Map<String, String> secondaryPackageMap = new HashMap<String, String>();
    private static Map<String, String> subChannelMap = new HashMap<String, String>();
    private static Map<String, String> lookupBrandMap = new HashMap<String, String>();
    private static Map<String, String> lookupFlavorMap = new HashMap<String, String>();
    private static Map<String, Integer> outletMap = new HashMap<String, Integer>();

    // private static Set<String> existedProducts = new HashSet<String>();
    private static Set<String> existedPackages = new HashSet<String>();

    enum FileTypeEnum {
	PRODUCT("PROD"), MARKET("MKT"), PERIOD("PER"), DATA("DATA"), FACT("FCT");

	private String value;

	private FileTypeEnum(String value) {
	    this.value = value;
	}

	public String getValue() {
	    return value;
	}
    }

    public static void parse() {
	ArrayList<ParseReport> reportList = new ArrayList<ParseReport>();
	ParseReport parseReport;
	JdbcConnectionBroker connectionBroker = null;
	try {
	    connectionBroker = getDbConnection(GeoCodingProcessProperties.DEFAULT_FILE_NAME);

	    String importDirectory = GeoCodingProcessProperties.getProperty("NIELSON_IMPORT_FILE_PATH");
	    String processedDataDirName = GeoCodingProcessProperties.getProperty("PROCESSED_DATA_PATH");
	    String startRowParam = GeoCodingProcessProperties.getProperty("NIELSON_START_PARSING_ROW");
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
		    if (startRow < 2) {
			startRow = 2;
		    }
		} catch (Exception e) {
		    System.err.println("START_PARSING_ROW - " + e.getMessage());
		}
	    }
	    String schemaName = GeoCodingProcessProperties.getSchema();
	    loadLookupMap(connectionBroker, schemaName);
	    initPreparedStatemets(connectionBroker, schemaName);

	    File fileDir = new File(importDirectory);
	    if (fileDir.exists()) {
		List<String> fileNames = Arrays.asList(fileDir.list());
		for (String fileName : fileNames) {
		    File importFile = new File(importDirectory, fileName);
		    if (importFile.isFile()
			    && isCorrectNielsonZipFile(importFile)) {
			parseZipFile(connectionBroker, schemaName, importFile, processedDataDirName);
			parseReport = new ParseReport(importFile.getName(), delivLineNumber, newOutletCount);
			reportList.add(parseReport);
		    }
		}
		boolean parseFlag = parseDir(connectionBroker, importDirectory, processedDataDirName, schemaName);
		if (parseFlag) {
		    parseReport = new ParseReport(importDirectory, delivLineNumber, newOutletCount);
		    reportList.add(parseReport);
		}
	    }

	    if (!reportList.isEmpty())
		ParseResultReport.report(reportList);
	    connectionBroker.commit();
	} catch (Exception e) {
	    logger.logError("Exception while reading csv file: ", e);
	} finally {
	    if (connectionBroker != null) {
		connectionBroker.cleanup();
	    }
	}
    }

    private static void parseZipFile(JdbcConnectionBroker connectionBroker, String schemaName, File importFile,
	    String processedDataDirName) throws Exception {
	InputStream is = null;
	ArchiveInputStream ais = null;
	ArchiveEntry ae;
	try {

	    logger.logInfo("Parse File: " + importFile.getName());

	    Map<String, java.sql.Date> periodMap = null;
	    Map<String, Integer> productMap = null;

	    is = new BufferedInputStream(new FileInputStream(importFile));
	    ais = new ArchiveStreamFactory().createArchiveInputStream(is);
	    while ((ae = ais.getNextEntry()) != null) {
		if (ae.getName().contains(FileTypeEnum.PERIOD.getValue())) {
		    periodMap = writePeriod(ais);
		    break;
		}
	    }
	    is.close();
	    ais.close();

	    is = new BufferedInputStream(new FileInputStream(importFile));
	    ais = new ArchiveStreamFactory().createArchiveInputStream(is);
	    while ((ae = ais.getNextEntry()) != null) {
		if (ae.getName().contains(FileTypeEnum.PRODUCT.getValue())) {
		    productMap = writeProducts(connectionBroker, schemaName, ais);
		    break;
		}
	    }
	    is.close();
	    ais.close();

	    is = new BufferedInputStream(new FileInputStream(importFile));
	    ais = new ArchiveStreamFactory().createArchiveInputStream(is);
	    while ((ae = ais.getNextEntry()) != null) {
		if (ae.getName().contains(FileTypeEnum.MARKET.getValue())) {
		    writeMarket(connectionBroker, schemaName, ais);
		    break;
		}
	    }
	    is.close();
	    ais.close();

	    is = new BufferedInputStream(new FileInputStream(importFile));
	    ais = new ArchiveStreamFactory().createArchiveInputStream(is);
	    try {
		while ((ae = ais.getNextEntry()) != null) {
		    if (ae.getName().contains(FileTypeEnum.DATA.getValue())) {
			writeData(connectionBroker, schemaName, ais, productMap, periodMap);
			break;
		    }
		}
	    } finally {
		ais.close();
		is.close();
	    }

	    moveFileToProcessedDataDir(processedDataDirName, importFile);
	} finally {
	    if (ais != null) {
		ais.close();
	    }
	    if (is != null) {
		is.close();
	    }
	}
    }

    private static boolean parseDir(JdbcConnectionBroker connectionBroker, String importDirectory,
	    String processedDataDirName, String schemaName) throws FileNotFoundException, IOException,
	    SQLException {
	boolean parseFlag = false;
	File importFileDirectory = new File(importDirectory);
	Map<FileTypeEnum, File> mapNielsonFiles = getCorrectNielsonSetFiles(importFileDirectory.list(), importFileDirectory);
	if (mapNielsonFiles != null) {
	    logger.logInfo("Parse csv files in dir: " + importDirectory);
	    InputStream is = null;
	    try {
		is = new FileInputStream(mapNielsonFiles.get(FileTypeEnum.PERIOD));
		Map<String, java.sql.Date> periodMap = writePeriod(is);
		is.close();

		is = new FileInputStream(mapNielsonFiles.get(FileTypeEnum.PRODUCT));
		Map<String, Integer> productMap = writeProducts(connectionBroker, schemaName, is);
		is.close();

		is = new FileInputStream(mapNielsonFiles.get(FileTypeEnum.MARKET));
		writeMarket(connectionBroker, schemaName, is);
		is.close();

		is = new FileInputStream(mapNielsonFiles.get(FileTypeEnum.DATA));
		writeData(connectionBroker, schemaName, is, productMap, periodMap);

		parseFlag = true;

		for (File importFile : mapNielsonFiles.values()) {
		    moveFileToProcessedDataDir(processedDataDirName, importFile);
		}
	    } finally {
		if (is != null) {
		    is.close();
		}
	    }
	}
	return parseFlag;
    }

    private static boolean isCorrectNielsonZipFile(File zipFile) throws ArchiveException, IOException {
	if (!isArchiveFile(zipFile))
	    return false;
	boolean isCorrect = false;
	InputStream is = new BufferedInputStream(new FileInputStream(zipFile));
	try {
	    ArchiveInputStream ais = new ArchiveStreamFactory().createArchiveInputStream(is);
	    try {
		ArchiveEntry ae;
		Set<FileTypeEnum> fileTypeEnums = new HashSet<FileTypeEnum>();
		while ((ae = ais.getNextEntry()) != null) {
		    for (FileTypeEnum type : FileTypeEnum.values()) {
			if (ae.getName().contains(type.getValue())
				&& !fileTypeEnums.contains(type)) {
			    fileTypeEnums.add(type);
			    break;
			}
		    }
		}
		if (fileTypeEnums.size() == FileTypeEnum.values().length) {
		    isCorrect = true;
		}
	    } finally {
		ais.close();
	    }
	} finally {
	    is.close();
	}
	return isCorrect;
    }

    private static Map<FileTypeEnum, File> getCorrectNielsonSetFiles(String[] importFilesName, File dir) {
	Map<FileTypeEnum, File> mapNielsonFiles = new HashMap<FileTypeEnum, File>();
	boolean isCorrect;
	for (FileTypeEnum type : FileTypeEnum.values()) {
	    isCorrect = false;
	    for (String importFileName : importFilesName) {
		if (importFileName.contains(type.getValue())) {
		    isCorrect = true;
		    mapNielsonFiles.put(type, new File(dir, importFileName));
		    break;
		}
	    }
	    if (!isCorrect) {
		mapNielsonFiles = null;
		break;
	    }
	}
	return mapNielsonFiles;
    }

    private static void initPreparedStatemets(JdbcConnectionBroker connectionBroker, String schemaName)
	    throws SQLException {
	productPackageSelectStmnt =
		connectionBroker.getNewPreparedStatement(GeocodingQuery.NIELSON_SELECT_PRODUCT_PKG_QUERY, schemaName);
	productPackageInsertStmnt =
		connectionBroker.getNewPreparedStatement(GeocodingQuery.NIELSON_INSERT_PRODUCT_PKG_QUERY, schemaName);
	productInsertStmnt =
		connectionBroker.getNewPreparedStatement(GeocodingQuery.NIELSON_INSERT_PRODUCT_QUERY, schemaName);
	packageSelectStmnt =
		connectionBroker.getNewPreparedStatement(GeocodingQuery.NIELSON_SELECT_PACKAGE_QUERY, schemaName);
	packageInsertStmnt =
		connectionBroker.getNewPreparedStatement(GeocodingQuery.NIELSON_INSERT_PACKAGE_QUERY, schemaName);
	outletSelectStmnt =
		connectionBroker.getNewPreparedStatement(GeocodingQuery.NIELSON_SELECT_OUTLET_QUERY, schemaName);
	outletSelectByIdStmnt =
		connectionBroker.getNewPreparedStatement(GeocodingQuery.NIELSON_SELECT_OUTLET_BY_ID_QUERY, schemaName);
	outletInsertStmnt =
		connectionBroker.getNewPreparedStatement(GeocodingQuery.NIELSON_INSERT_OUTLET_QUERY, schemaName);
	checkImproveAddrStmnt =
		connectionBroker.getNewPreparedStatement(GeocodingQuery.NIELSON_CHECK_IMPROVING_ADDRESS_QUERY, schemaName);
	improveAddrStmnt =
		connectionBroker.getNewPreparedStatement(GeocodingQuery.NIELSON_IMPROVE_ADDRESS_QUERY, schemaName);
	improveOutletAddrStmnt =
		connectionBroker.getNewPreparedStatement(GeocodingQuery.NIELSON_IMPROVE_OUTLET_ADDRESS_QUERY, schemaName);
	addrSelectStmnt =
		connectionBroker.getNewPreparedStatement(GeocodingQuery.NIELSON_SELECT_ADDRESS_QUERY, schemaName);
	addrInsertStmnt =
		connectionBroker.getNewPreparedStatement(GeocodingQuery.NIELSON_INSERT_ADDRESS_QUERY, schemaName);
	geocodedAddrInsertStmnt =
		connectionBroker.getNewPreparedStatement(GeocodingQuery.NIELSON_INSERT_GEOCODED_ADDRESS_QUERY, schemaName);

	// selectProductStmt =
	// connectionBroker.getNewPreparedStatement(GeocodingQuery.NIELSON_SELECT_PRODUCT_QUERY, schemaName);
    }

    private static Map<String, java.sql.Date> writePeriod(InputStream csvFile) throws IOException {
	Map<String, java.sql.Date> mapPeriod = new HashMap<String, java.sql.Date>();
	BufferedReader br = null;
	try {
	    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	    logger.logInfo("Parse Period File");
	    br = new BufferedReader(new InputStreamReader(csvFile, GeocodingConstants.ENCODE_UTF_8), BUFFER_SIZE);
	    String strLine = "";
	    int lineNumber = 0;
	    while ((strLine = br.readLine()) != null) {
		lineNumber++;
		if (lineNumber > 1) {
		    String[] arr = strLine.split(CSV_SPLITER);
		    String[] valueArr = arr[1].split(" ");
		    Date date = null;
		    for (String s : valueArr) {
			if (s.length() == DATE_FORMAT.length()) {
			    try {
				date = sdf.parse(s);
				break;
			    } catch (ParseException e) {
				logger.logWarning("Parse date exception");
			    }
			}
		    }
		    if (arr.length == 2
			    && arr[0] != null
			    && date != null) {
			mapPeriod.put(arr[1].trim(), new java.sql.Date(date.getTime()));
		    }
		}
	    }
	} finally {
	    if (br != null) {
		br.close();
	    }
	}
	return mapPeriod;
    }

    private static Map<String, Integer> writeProducts(JdbcConnectionBroker connectionBroker, String schemaName,
	    InputStream csvFile) throws IOException, SQLException {
	Map<String, Integer> mapProduct = new HashMap<String, Integer>();
	BufferedReader br = null;
	try {

	    initBasePreparedStatements(connectionBroker, schemaName);
	    initProductPackageIdSeq(connectionBroker, schemaName);
	    initConvertedMaps(connectionBroker, schemaName, MappingType.PRODUCT, convertedProductMap);
	    initConvertedMaps(connectionBroker, schemaName, MappingType.BRAND, convertedBrandMap);
	    initConvertedMaps(connectionBroker, schemaName, MappingType.FLAVOR, convertedFlavorMap);
	    initConvertedMaps(connectionBroker, schemaName, MappingType.PRIMARY_CONTAINER, primaryContainerMap);
	    initLookupMaps(connectionBroker, schemaName, MappingType.SECONDARY_PACKAGE, secondaryPackageMap);
	    initLookupMapsByName(connectionBroker, schemaName, MappingType.BRAND, lookupBrandMap);
	    initLookupMapsByName(connectionBroker, schemaName, MappingType.FLAVOR, lookupFlavorMap);
	    initProductBrandFlavorMap(connectionBroker, schemaName, productBrandFlavorMap);

	    logger.logInfo("Parse Product File");
	    br = new BufferedReader(new InputStreamReader(csvFile, GeocodingConstants.ENCODE_UTF_8), BUFFER_SIZE);
	    String strLine = "";
	    Product product = null;
	    int lineNumber = 0;
	    while ((strLine = br.readLine()) != null) {
		lineNumber++;
		if (lineNumber > 1) {
		    product = new Product();
		    fastProductSplit(strLine, product);
		    int productPackageId = mergeProductPackage(product);
		    if (productPackageId > 0) {
			mapProduct.put(product.getDescription(), Integer.valueOf(productPackageId));
		    }
		    if (lineNumber % BATCH_SIZE == 0) {
			logger.logInfo("Commit Product.");
			connectionBroker.commit();
			logger.logInfo("Parsing...");
		    }
		}
	    }
	    if (lineNumber > 0 && lineNumber % BATCH_SIZE != 0) {
		clearInitialMaps();
		logger.logInfo("Commit Product.");
		connectionBroker.commit();
	    }
	} finally {
	    if (br != null) {
		br.close();
	    }
	}
	return mapProduct;
    }

    private static void initProductBrandFlavorMap(JdbcConnectionBroker connectionBroker, String schemaName, Map<String, GenericValueObject> productMap)
	    throws SQLException {
	PreparedStatement stmt = connectionBroker.getNewPreparedStatement(GeocodingQuery.NIELSON_SELECT_PRODUCT_BRAND_FLAVOR_QUERY, schemaName);
	try {
	    ResultSet rs = stmt.executeQuery();
	    try {
		productMap.clear();
		while (rs.next()) {
		    productMap.put(rs.getString(1), new GenericValueObject(rs.getString(2), rs.getString(3)));
		}
	    } finally {
		closeResultSet(rs);
	    }
	} finally {
	    connectionBroker.closeStatement(stmt);
	}
    }

    private static void writeMarket(JdbcConnectionBroker connectionBroker, String schemaName,
	    InputStream csvFile) throws IOException, SQLException {
	BufferedReader br = null;
	try {
	    initOutletIdSeq(connectionBroker, schemaName);
	    initAddrIdSeq(connectionBroker, schemaName);
	    initConvertedMaps(connectionBroker, schemaName, MappingType.SUB_CHANNEL, convertedSubChannelMap);
	    initSubChannelMap(connectionBroker, schemaName);

	    logger.logInfo("Parse Market File");
	    br = new BufferedReader(new InputStreamReader(csvFile, GeocodingConstants.ENCODE_UTF_8), BUFFER_SIZE);
	    String strLine = "";
	    Outlet outlet = null;
	    int lineNumber = 0;
	    while ((strLine = br.readLine()) != null) {
		lineNumber++;
		if (lineNumber > 1) {
		    outlet = new Outlet();
		    fastOutletSplit(strLine, outlet);
		    mergeOutlet(outlet);
		    if (lineNumber % BATCH_SIZE == 0) {
			logger.logInfo("Execute Batch Outlet - Line # " + lineNumber);
			outletInsertStmnt.executeBatch();
			logger.logInfo("Commit Outlet");
			connectionBroker.commit();
			logger.logInfo("Parsing...");
		    }
		}
	    }
	    if (lineNumber > 0 && lineNumber % BATCH_SIZE != 0) {
		convertedSubChannelMap.clear();
		subChannelMap.clear();
		logger.logInfo("Execute Batch Outlet - Line # " + lineNumber);
		outletInsertStmnt.executeBatch();
		logger.logInfo("Commit Outlet");
		connectionBroker.commit();
	    }
	} finally {
	    if (br != null) {
		br.close();
	    }
	}
    }

    private static void writeData(JdbcConnectionBroker connectionBroker, String schemaName, InputStream csvFile,
	    Map<String, Integer> productMap, Map<String, java.sql.Date> periodMap) throws IOException, SQLException {
	BufferedReader br = null;
	try {

	    logger.logInfo("Parse Data File");
	    br = new BufferedReader(new InputStreamReader(csvFile, GeocodingConstants.ENCODE_UTF_8), BUFFER_SIZE);
	    String strLine = "";
	    Delivery delivery = null;
	    int lineNumber = 0;
	    while ((strLine = br.readLine()) != null) {
		lineNumber++;
		if (lineNumber > 1 && startRow <= lineNumber)
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
		    delivLineNumber++;
		    delivery = new Delivery();
		    fastDeliverySplit(arrayToProcess[c], delivery);
		    mergeDelivery(delivery, productMap, periodMap);
		    if (lineNumber % BATCH_SIZE == 0) {
			logger.logInfo("Execute Batch Data - Line # " + lineNumber);
			logger.logInfo("Commit");
			connectionBroker.commit();
			logger.logInfo("Parsing...");
		    }
		}
		if (numToProcess == 0)
		    break;
	    }
	    flushMergeDelivery(connectionBroker, schemaName);

	    if (lineNumber > 0 && lineNumber % BATCH_SIZE != 0) {
		logger.logInfo("Execute Batch Data - Line # " + lineNumber);
		logger.logInfo("Commit");
		connectionBroker.commit();
	    }
	} finally {
	    if (br != null) {
		br.close();
	    }
	}
    }

    private static void mergeDelivery(Delivery delivery, Map<String, Integer> productMap, Map<String, java.sql.Date> periodMap) throws SQLException {
	int outletId = checkOutlet(delivery.getOutletKey());
	Integer productPackageId = productMap.get(delivery.getProductPackageKey());
	if (productPackageId != null
		&& outletId > 0) {
	    java.sql.Date deliveryDate = periodMap.get(delivery.getPeriodKey());

	    addMergeDeliveryRow(deliveryDate, outletId, productPackageId.intValue());
	}
    }

    public static int checkOutlet(String outletKey) {
	if (outletKey != null && outletKey.equals(lastOutletKey)) {
	    return lastOutletId;
	}
	Integer outletId = outletMap.get(outletKey);
	if (outletId != null) {
	    lastOutletKey = outletKey;
	    lastOutletId = outletId.intValue();
	} else {
	    outletId = Integer.valueOf(0);
	}
	return outletId.intValue();
    }

    private static void mergeOutlet(Outlet outlet) throws SQLException {

	if (outlet.getChannel() == null || outlet.getChannel().isEmpty())
	    return;

	int paramIndex = 0;
	ResultSet rs = null;
	int outletId = NOT_SELECTED_VALUE;

	try {
	    outletSelectByIdStmnt.setString(++paramIndex, outlet.getDescription());
	    outletSelectByIdStmnt.setString(++paramIndex, outlet.getKey());
	    rs = outletSelectByIdStmnt.executeQuery();
	    if (rs.next()) {
		outletId = rs.getInt(1);
	    }
	    paramIndex = 0;
	    closeResultSet(rs);

	    if (outletId == NOT_SELECTED_VALUE) {
		String subChannelDesc = normalizeMapSourceValue(outlet.getChannel());
		GenericValueObject subChannel = convertedSubChannelMap.get(subChannelDesc);
		if (subChannel == null || subChannel.getCode() == null) {
		    logger.logWarning("Outlet: " + outlet.getDescription() + " Sub channel: " + outlet.getChannel() +
			    " does not exist");
		    if (outlet.getChannel() != null && !outlet.getChannel().isEmpty()) {
			subChannel = new GenericValueObject();
			subChannel.setCode(addUnmappedValue(LOOKUP_TRADE_SUB_CHANNEL, outlet.getChannel()));
			subChannel.setValue(outlet.getChannel());
			convertedSubChannelMap.put(outlet.getChannel(), subChannel);
		    }
		    return;
		}
		int subChannelCode = Integer.parseInt(subChannel.getCode());

		outletSelectStmnt.setString(++paramIndex, outlet.getTdLinxId());
		outletSelectStmnt.setString(++paramIndex, outlet.getName());
		rs = outletSelectStmnt.executeQuery();

		int businessTypeId = NOT_SELECTED_VALUE;

		while (rs.next()) {
		    if (subChannelCode == rs.getInt(3)) {
			outletId = rs.getInt(1);
			businessTypeId = rs.getInt(2);
			if (businessTypeId == BUSINESS_TYPE_TRADE_SALE_ID) {
			    break;
			}
		    }
		}

		improveAddress(outletId, outlet);

		if (outletId == NOT_SELECTED_VALUE) {
		    newOutletCount++;
		    int addrId = checkAddress(outlet);
		    String foodServiceIndicator = foodServiceMap.get(subChannelMap.get(subChannel.getCode()));

		    paramIndex = 0;
		    outletInsertStmnt.setInt(++paramIndex, ++outletIdSeq);
		    outletInsertStmnt.setString(++paramIndex, outlet.getTdLinxId());
		    outletInsertStmnt.setString(++paramIndex, outlet.getDescription());
		    outletInsertStmnt.setString(++paramIndex, outlet.getKey());
		    outletInsertStmnt.setString(++paramIndex, outlet.getName());
		    outletInsertStmnt.setString(++paramIndex, outlet.getName() + " " + outlet.getNumber());
		    outletInsertStmnt.setInt(++paramIndex, addrId);
		    outletInsertStmnt.setInt(++paramIndex, Integer.parseInt(subChannel.getCode()));
		    outletInsertStmnt.setInt(++paramIndex, BUSINESS_TYPE_TRADE_SALE_ID);
		    outletInsertStmnt.setString(++paramIndex, foodServiceIndicator == null ? NO : foodServiceIndicator);
		    if (outlet.getLatitude() == null) {
			outletInsertStmnt.setNull(++paramIndex, Types.DOUBLE);
		    } else {
			outletInsertStmnt.setDouble(++paramIndex, outlet.getLatitude().doubleValue());
		    }
		    if (outlet.getLongitude() == null) {
			outletInsertStmnt.setNull(++paramIndex, Types.DOUBLE);
		    } else {
			outletInsertStmnt.setDouble(++paramIndex, outlet.getLongitude().doubleValue());
		    }
		    outletInsertStmnt.addBatch();
		    outletId = outletIdSeq;
		}
	    }
	    outletMap.put(outlet.getDescription(), Integer.valueOf(outletId));
	} finally {
	    closeResultSet(rs);
	}
    }

    private static void improveAddress(int outletId, Outlet outlet) throws SQLException {
	if (outlet.getLatitude() == null || outlet.getLongitude() == null)
	    return;

	int paramIndex = 0;
	int addrId = 0;
	checkImproveAddrStmnt.setInt(++paramIndex, outletId);
	ResultSet rs = checkImproveAddrStmnt.executeQuery();
	try {
	    if (rs.next()) {
		addrId = rs.getInt(1);

		paramIndex = 0;
		improveAddrStmnt.setDouble(++paramIndex, outlet.getLatitude().doubleValue());
		improveAddrStmnt.setDouble(++paramIndex, outlet.getLongitude().doubleValue());
		improveAddrStmnt.setString(++paramIndex, getFormattedAddress(outlet));
		improveAddrStmnt.setInt(++paramIndex, NIELSON_GEOCODE_LEVEL);
		improveAddrStmnt.setInt(++paramIndex, addrId);
		improveAddrStmnt.executeUpdate();

		paramIndex = 0;
		improveOutletAddrStmnt.setDouble(++paramIndex, outlet.getLatitude().doubleValue());
		improveOutletAddrStmnt.setDouble(++paramIndex, outlet.getLongitude().doubleValue());
		improveOutletAddrStmnt.setInt(++paramIndex, addrId);
		improveOutletAddrStmnt.executeUpdate();
	    }
	} finally {
	    closeResultSet(rs);
	}
    }

    private static int checkAddress(Outlet outlet) throws SQLException {
	int addrId = 0;
	int paramIndex = 0;
	addrSelectStmnt.setDouble(++paramIndex, outlet.getLatitude() == null ? 0 : outlet.getLatitude().doubleValue());
	addrSelectStmnt.setDouble(++paramIndex, outlet.getLongitude() == null ? 0 : outlet.getLongitude().doubleValue());
	addrSelectStmnt.setString(++paramIndex, outlet.getStreetAddress());
	addrSelectStmnt.setString(++paramIndex, outlet.getCity());
	addrSelectStmnt.setString(++paramIndex, outlet.getZip());
	ResultSet rs = addrSelectStmnt.executeQuery();
	try {
	    if (rs.next()) {
		addrId = rs.getInt(1);
	    } else {
		paramIndex = 0;
		if (outlet.getLatitude() == null
			&& outlet.getLongitude() == null) {
		    addrInsertStmnt.setInt(++paramIndex, ++addrIdSeq);
		    addrInsertStmnt.setString(++paramIndex, outlet.getCity());
		    addrInsertStmnt.setString(++paramIndex, outlet.getZip());
		    addrInsertStmnt.setString(++paramIndex, outlet.getStreetAddress());
		    addrInsertStmnt.executeUpdate();
		} else {
		    geocodedAddrInsertStmnt.setInt(++paramIndex, ++addrIdSeq);
		    geocodedAddrInsertStmnt.setString(++paramIndex, outlet.getCity());
		    geocodedAddrInsertStmnt.setString(++paramIndex, outlet.getZip());
		    geocodedAddrInsertStmnt.setString(++paramIndex, outlet.getStreetAddress());
		    geocodedAddrInsertStmnt.setString(++paramIndex, getFormattedAddress(outlet));
		    geocodedAddrInsertStmnt.setDouble(++paramIndex, outlet.getLatitude().doubleValue());
		    geocodedAddrInsertStmnt.setDouble(++paramIndex, outlet.getLongitude().doubleValue());
		    geocodedAddrInsertStmnt.setInt(++paramIndex, NIELSON_GEOCODE_LEVEL);
		    geocodedAddrInsertStmnt.executeUpdate();
		}
		addrId = addrIdSeq;
	    }
	} finally {
	    closeResultSet(rs);
	}
	return addrId;
    }

    private static String getFormattedAddress(Outlet outlet) {
	return outlet.getStreetAddress() + ", " + outlet.getCity() + ", " + outlet.getZip();
    }

    private static int mergeProductPackage(Product product) throws SQLException {
	int productPackageId = 0;
	int paramIndex = 0;
	/* mapped value */
	String primaryContainerValue = product.getBaseSize() + " " + product.getContainer();
	String secondaryPackageValue = String.valueOf(Integer.valueOf(product.getPackageSize()));
	String genericProductDesc = normalizeNielsenProductName(product.getDescription());
	String genericBrandDesc = normalizeMapSourceValue(product.getBrand());
	String genericFlavorDesc = normalizeMapSourceValue(product.getFlavor());
	GenericValueObject genericProduct = convertedProductMap.get(genericProductDesc);

	String lookupBrandCode = lookupBrandMap.get(genericBrandDesc);
	GenericValueObject genericBrand = null;
	if (lookupBrandCode == null) {
	    genericBrand = convertedBrandMap.get(genericBrandDesc);
	} else {
	    genericBrand = new GenericValueObject();
	    genericBrand.setCode(lookupBrandCode);
	    genericBrand.setValue(genericBrandDesc);
	}

	String lookupFlavorCode = lookupFlavorMap.get(genericFlavorDesc);
	GenericValueObject genericFlavor = null;
	if (lookupFlavorCode == null) {
	    genericFlavor = convertedFlavorMap.get(genericFlavorDesc);
	} else {
	    genericFlavor = new GenericValueObject();
	    genericFlavor.setCode(lookupFlavorCode);
	    genericFlavor.setValue(genericFlavorDesc);
	}
	GenericValueObject genericPrimaryContainer = primaryContainerMap.get(primaryContainerValue);
	String genericSecondaryPackage = secondaryPackageMap.get(secondaryPackageValue);

	// Add unmapped Product
	if (genericProduct == null) {
	    logger.logWarning("Product: " + product.getKey() + " " + product.getDescription() +
		    " unmapped");
	    genericProduct = new GenericValueObject();
	    genericProduct.setCode(addUnmappedValue(LOOKUP_PRODUCT, genericProductDesc));
	    genericProduct.setValue(genericProductDesc);
	    convertedProductMap.put(genericProductDesc, genericProduct);
	}
	else {
	    GenericValueObject existentPrd = productBrandFlavorMap.get(genericProduct.getCode());
	    if (existentPrd != null) {
		if (genericBrand == null) {
		    genericBrand = new GenericValueObject();
		}
		genericBrand.setCode(existentPrd.getCode());
		if (genericFlavor == null) {
		    genericFlavor = new GenericValueObject();
		}
		genericFlavor.setCode(existentPrd.getValue()); // the field "value" stores the flavor
	    }
	}

	if ((genericBrand == null || genericFlavor == null) && product.getFlavor() != null
		&& !product.getFlavor().trim().isEmpty()) {
	    logger.logWarning("Product: " + product.getKey() + " " + product.getDescription() +
		    " Brand: " + product.getBrand() +
		    " and Flavor: " + product.getFlavor() +
		    " does not exist");
	    if (genericBrand == null) {
		genericBrand = new GenericValueObject();
		genericBrand.setCode(addUnmappedValue(LOOKUP_BRAND, product.getBrand()));
		genericBrand.setValue(genericBrandDesc);
		convertedBrandMap.put(genericBrandDesc, genericBrand);
	    }
	    if (genericFlavor == null) {
		genericFlavor = new GenericValueObject();
		genericFlavor.setCode(addUnmappedValue(LOOKUP_FLAVOR, product.getFlavor()));
		genericFlavor.setValue(genericFlavorDesc);
		convertedFlavorMap.put(genericFlavorDesc, genericFlavor);
	    }
	}

	if (genericPrimaryContainer == null || genericSecondaryPackage == null) {
	    logger.logWarning("Product: " + product.getKey() + " " + product.getDescription() +
		    " PrimaryContainer: " + primaryContainerValue +
		    " and SecondaryPackage: " + secondaryPackageValue +
		    " does not exist");
	    if (genericPrimaryContainer == null) {
		genericPrimaryContainer = new GenericValueObject();
		genericPrimaryContainer.setCode(addUnmappedValue(LOOKUP_PRIMARY_CONTAINER, primaryContainerValue));
		genericPrimaryContainer.setValue(primaryContainerValue);
		primaryContainerMap.put(primaryContainerValue, genericPrimaryContainer);
	    }
	    if (genericSecondaryPackage == null) {
		genericSecondaryPackage = addUnmappedValue(LOOKUP_SECONDARY_PACKAGE, secondaryPackageValue);
		secondaryPackageMap.put(secondaryPackageValue, genericSecondaryPackage);
	    }
	}

	if (genericBrand != null && genericFlavor != null && genericSecondaryPackage != null) {

	    checkProduct(genericProduct, genericBrand, genericFlavor);
	    checkPackage(genericPrimaryContainer.getCode(), genericSecondaryPackage, primaryContainerValue);

	    int productTypeId = primaryContainerValue.contains("BOX") ? PRODUCT_TYPE_POST_MIX_ID : PRODUCT_TYPE_BC_ID;

	    productPackageSelectStmnt.setString(++paramIndex, genericProduct.getCode());
	    productPackageSelectStmnt.setString(++paramIndex, genericPrimaryContainer.getCode());
	    productPackageSelectStmnt.setString(++paramIndex, genericSecondaryPackage);
	    productPackageSelectStmnt.setInt(++paramIndex, productTypeId);
	    ResultSet rs = productPackageSelectStmnt.executeQuery();
	    try {
		if (rs.next()) {
		    productPackageId = rs.getInt(1);
		} else {
		    paramIndex = 0;
		    productPackageInsertStmnt.setInt(++paramIndex, ++productPackageIdSeq);
		    productPackageInsertStmnt.setString(++paramIndex, genericProduct.getCode());
		    productPackageInsertStmnt.setString(++paramIndex, genericPrimaryContainer.getCode());
		    productPackageInsertStmnt.setString(++paramIndex, genericSecondaryPackage);
		    productPackageInsertStmnt.setInt(++paramIndex, productTypeId);
		    productPackageInsertStmnt.setString(++paramIndex, genericBrand.getCode());
		    productPackageInsertStmnt.setString(++paramIndex, genericFlavor.getCode());
		    productPackageInsertStmnt.executeUpdate();
		    productPackageId = productPackageIdSeq;
		}
	    } finally {
		closeResultSet(rs);
	    }
	}
	return productPackageId;
    }

    private static String normalizeNielsenProductName(String productDescription) {
	String s = normalizeMapSourceValue(productDescription);
	try {
	    s = normalizeMapSourceValue(s.replaceAll(" [0-9]{12}", " ").trim().replaceAll(" PL F ", " ").replaceAll(" PL R ", " ")
		    .replaceAll(" FC ", " ").replaceAll(" BOX ", " ").replaceAll(" PLASTIC ", " ")
		    .replaceAll(" CARTON ", " ").replaceAll(" GLASS ", " ").replaceAll(" POUCH ", " ")
		    .replaceAll(" CAN ", " ").replaceAll(" PLS ", " ").replaceAll(" ASEPTIC ", " ")
		    .replaceAll(" EASY OPEN WIDE MOUTH ", " ").replaceAll(" NON REFILLABLE BOTTLES ", " ")
		    .replaceAll(" [0-9]+P[K]* ", " ").replaceAll("([0-9]+ OZ)|([0-9]+.[0-9]+ OZ)", "").trim());
	} catch (Exception ex) {
	    logger.logError("Error normalization of the Nielsen Product Description", ex);
	}
	return s;
    }

    private static void checkPackage(String primaryContainerCode, String secondaryPackageCode,
	    String primaryContainerValue) throws SQLException {
	if (!existedPackages.contains(primaryContainerCode + secondaryPackageCode)) {

	    int paramIndex = 0;
	    packageSelectStmnt.setString(++paramIndex, primaryContainerCode);
	    packageSelectStmnt.setString(++paramIndex, secondaryPackageCode);
	    ResultSet rs = packageSelectStmnt.executeQuery();
	    try {
		if (!rs.next()) {
		    paramIndex = 0;
		    double ouncesValue = 0;
		    try {
			String ouncesValueStr = primaryContainerValue.substring(0, primaryContainerValue.indexOf("OUNCES")).trim();
			ouncesValue = Double.parseDouble(ouncesValueStr);
		    } catch (Exception e) {
			ouncesValue = 0;
		    }
		    packageInsertStmnt.setString(++paramIndex, primaryContainerCode);
		    packageInsertStmnt.setString(++paramIndex, secondaryPackageCode);
		    packageInsertStmnt.setString(++paramIndex, primaryContainerCode);
		    packageInsertStmnt.setString(++paramIndex, secondaryPackageCode);
		    packageInsertStmnt.setDouble(++paramIndex, ouncesValue);

		    packageInsertStmnt.executeUpdate();
		    existedPackages.add(primaryContainerCode + secondaryPackageCode);
		}
	    } finally {
		closeResultSet(rs);
	    }
	}
    }

    private static void checkProduct(GenericValueObject product, GenericValueObject brand, GenericValueObject flavor)
	    throws SQLException {

	GenericValueObject existentPrd = productBrandFlavorMap.get(product.getCode());
	if (existentPrd != null) {
	    return;
	}

	int paramIndex = 0;
	productInsertStmnt.setString(++paramIndex, product.getCode());
	productInsertStmnt.setString(++paramIndex, brand.getCode());
	productInsertStmnt.setString(++paramIndex, flavor.getCode());
	productInsertStmnt.executeUpdate();
	productBrandFlavorMap.put(product.getCode(), new GenericValueObject(brand.getCode(), flavor.getCode()));
    }

    private static void initLookupMaps(JdbcConnectionBroker connectionBroker, String schemaName, MappingType mappingType,
	    Map<String, String> lookupMap) throws SQLException {
	String key;
	int paramIndex = 0;
	PreparedStatement initPrimaryContainerStmnt =
		connectionBroker.getNewPreparedStatement(GeocodingQuery.GET_LOOKUP_BY_MAP_TYPE, schemaName);
	try {
	    initPrimaryContainerStmnt.setInt(++paramIndex, mappingType.getValue());
	    ResultSet rs = initPrimaryContainerStmnt.executeQuery();
	    try {
		while (rs.next()) {
		    key = rs.getString("NAME");
		    if (key != null) {
			lookupMap.put(key, rs.getString("LKP_CD"));
		    }
		}
	    } finally {
		closeResultSet(rs);
	    }
	} finally {
	    initPrimaryContainerStmnt.close();
	}
    }

    private static void initLookupMapsByName(JdbcConnectionBroker connectionBroker, String schemaName, MappingType mappingType,
	    Map<String, String> lookupMap) throws SQLException {
	String name;
	int paramIndex = 0;
	PreparedStatement initPrimaryContainerStmnt =
		connectionBroker.getNewPreparedStatement(GeocodingQuery.GET_LOOKUP_BY_MAP_TYPE, schemaName);
	try {
	    initPrimaryContainerStmnt.setInt(++paramIndex, mappingType.getValue());
	    ResultSet rs = initPrimaryContainerStmnt.executeQuery();
	    try {
		while (rs.next()) {
		    name = rs.getString("NAME");
		    if (name != null) {
			lookupMap.put(name.toUpperCase(), rs.getString("LKP_CD"));
		    }
		}
	    } finally {
		closeResultSet(rs);
	    }
	} finally {
	    initPrimaryContainerStmnt.close();
	}
    }

    private static void initSubChannelMap(JdbcConnectionBroker connectionBroker, String schemaName) throws SQLException {
	PreparedStatement initSubChannelStmnt =
		connectionBroker.getNewPreparedStatement(GeocodingQuery.GET_SUB_CHANNEL_MAPPING, schemaName);
	try {
	    ResultSet rs = initSubChannelStmnt.executeQuery();
	    try {
		while (rs.next()) {
		    subChannelMap.put(rs.getString("SB_CHNL_ID"), rs.getString("CHNL_ID"));
		}
	    } finally {
		closeResultSet(rs);
	    }
	} finally {
	    initSubChannelStmnt.close();
	}
    }

    private static void clearInitialMaps() {
	convertedProductMap.clear();
	convertedBrandMap.clear();
	convertedFlavorMap.clear();
	primaryContainerMap.clear();
	secondaryPackageMap.clear();
	lookupBrandMap.clear();
	lookupFlavorMap.clear();
    }

    private static void fastProductSplit(final String str, Product product) {
	int item = 0;
	int j = 0;
	int i;
	while ((i = str.indexOf(CSV_SPLITER, j)) >= 0) {
	    fillProductData(product, str.substring(j, i).trim(), item++);
	    j = i + 1;
	}
	if (j < str.length()) {
	    fillProductData(product, str.substring(j).trim(), item++);
	}
    }

    private static void fastOutletSplit(final String str, Outlet outlet) {
	int item = 0;
	int j = 0;
	int i;
	while ((i = str.indexOf(CSV_SPLITER, j)) >= 0) {
	    fillOutletData(outlet, str.substring(j, i).trim(), item++);
	    j = i + 1;
	}
	if (j < str.length()) {
	    fillOutletData(outlet, str.substring(j).trim(), item++);
	}
    }

    private static void fastDeliverySplit(final String str, Delivery delivery) {
	int item = 0;
	int j = 0;
	int i;
	while ((i = str.indexOf(CSV_SPLITER, j)) >= 0) {
	    fillDeliveryData(delivery, str.substring(j, i).trim(), item++);
	    j = i + 1;
	}
	if (j < str.length()) {
	    fillDeliveryData(delivery, str.substring(j).trim(), item++);
	}
    }

    private static void fillProductData(Product product, String value, int columnIndex) {

	switch (columnIndex) {
	case NielsonProperty.PRODUCT_KEY:
	    product.setKey(value);
	    break;
	case NielsonProperty.PRODUCT_DESCRIPTION:
	    product.setDescription(value);
	    break;
	case NielsonProperty.PRODUCT_BRAND:
	    product.setBrand(value);
	    break;
	case NielsonProperty.PRODUCT_CONTAINER:
	    product.setContainer(value);
	    break;
	case NielsonProperty.PRODUCT_ACNFLAVOR:
	    product.setFlavor(value == null || value.trim().isEmpty() ? UNFLAVORED : value);
	    break;
	case NielsonProperty.PRODUCT_BASE_SIZE:
	    product.setBaseSize(value);
	    break;
	case NielsonProperty.PRODUCT_PACKAGE_SIZE:
	    product.setPackageSize(value);
	    break;
	case NielsonProperty.PRODUCT_UPC:
	    product.setUniversalProductCode(value);
	    break;
	default:
	    break;
	}
    }

    private static void fillOutletData(Outlet outlet, String value, int columnIndex) {

	switch (columnIndex) {
	case NielsonProperty.OUTLET_KEY:
	    outlet.setKey(value);
	    break;
	case NielsonProperty.OUTLET_DESCRIPTION:
	    outlet.setDescription(value);
	    break;
	case NielsonProperty.OUTLET_CHANNEL:
	    outlet.setChannel(value);
	    break;
	case NielsonProperty.OUTLET_CITY:
	    outlet.setCity(value);
	    break;
	case NielsonProperty.OUTLET_LATITUDE:
	    outlet.setLatitude(value == null || value.trim().isEmpty() ? null : Double.valueOf(value));
	    break;
	case NielsonProperty.OUTLET_LONGITUDE:
	    outlet.setLongitude(value == null || value.trim().isEmpty() ? null : Double.valueOf(value));
	    break;
	case NielsonProperty.OUTLET_NAME:
	    outlet.setName(value);
	    break;
	case NielsonProperty.OUTLET_NUMBER:
	    outlet.setNumber(value);
	    break;
	case NielsonProperty.OUTLET_STREET_ADDRESS:
	    outlet.setStreetAddress(value);
	    break;
	case NielsonProperty.OUTLET_TD_LINX_ID:
	    outlet.setTdLinxId(value);
	    break;
	case NielsonProperty.OUTLET_ZIP:
	    outlet.setZip(value);
	    break;
	default:
	    break;
	}
    }

    private static void fillDeliveryData(Delivery delivery, String value, int columnIndex) {

	switch (columnIndex) {
	case NielsonProperty.DATA_OUTLET_KEY:
	    delivery.setOutletKey(value);
	    break;
	case NielsonProperty.DATA_PERIOD_KEY:
	    delivery.setPeriodKey(value);
	    break;
	case NielsonProperty.DATA_PRODUCT_PACKAGE_KEY:
	    delivery.setProductPackageKey(value);
	    break;
	default:
	    break;
	}
    }

    private static void closeResultSet(ResultSet rs) {
	try {
	    if (rs != null)
		rs.close();
	} catch (SQLException e) {
	    logger.logError(e);
	}
    }
}
