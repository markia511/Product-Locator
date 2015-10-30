package com.ko.lct.job.common.management;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import com.ko.lct.job.common.businessobjects.ConvertedStepEnum;
import com.ko.lct.job.common.businessobjects.GeocodeLevelEnum;
import com.ko.lct.job.common.businessobjects.GeocodeRequest;
import com.ko.lct.job.geocoding.geocoder.Geocoder;
import com.ko.lct.job.geocoding.utilities.ApplicationException;
import com.ko.lct.job.geocoding.utilities.ExcelReport;
import com.ko.lct.job.geocoding.utilities.GeocodingConstants;
import com.ko.lct.job.geocoding.utilities.JdbcConnectionBroker;
import com.ko.lct.job.logger.AbstractLogger;

public abstract class AbstractGeocodeManager extends AbstractManager {
    
    private static int countSuccess = 0;
    private static int countRoute = 0;
    private static int countCityAndPostalCode = 0;
    private static int countUndefined = 0;
    private static int failureCount = 0;
    private static int duplicateCount = 0;
    private static int SPEED_LOGGING = 5000;

    public static final String FILE_SEPARATOR = AccessController
	    .doPrivileged(new PrivilegedAction<String>() {
		@Override
		public String run() {
		    return System.getProperty("file.separator");
		}
	    });   
    
    private static final String PO_BOX_REGEXP = "PO BOX(.+)";
    
    protected abstract String getLimitGeocode();
    
    protected abstract String getExportGzipFileName() throws ApplicationException;
    
    protected abstract String getGoodleClientId();
    
    protected abstract String getGooglePrivateKey();
    
    protected abstract String getEmails();
    
    protected abstract String getHost();
    
    protected abstract String getPort();
    
    protected abstract String getSmtpUserName();
    
    protected abstract String getSmtpUserPassword();
    
    protected abstract String getMailFrom();
    
    protected void updateSecondaryLocation(GeocodeRequest geocodeRequest, 
	    PreparedStatement updateSecondaryLocationStatement) throws SQLException {};
    
    protected abstract PreparedStatement getUpdateAddressStatements(JdbcConnectionBroker connectionBroker) 
            throws SQLException, ApplicationException;
    
    protected abstract PreparedStatement getUpdateSecondaryLocationStatement(JdbcConnectionBroker connectionBroker) 
	    throws SQLException, ApplicationException;
    
    protected abstract PreparedStatement getSelectGeocodedStatement(JdbcConnectionBroker connectionBroker) 
	    throws SQLException, ApplicationException;
    
    protected abstract PreparedStatement getSelectNonGeocodedStatement(JdbcConnectionBroker connectionBroker) 
	    throws SQLException, ApplicationException;
    
    protected abstract GeocodeRequest getGeocodeRequest(ResultSet rs) throws SQLException;
    
    protected void geocodeProcess(JdbcConnectionBroker connectionBroker) 
	    throws ApplicationException, SQLException, Exception {

	int count = 0;
	AbstractLogger logger = AbstractLogger.getInstance();		
	List<GeocodeRequest> errorRequestList = new ArrayList<GeocodeRequest>();
	String limitGeocode = getLimitGeocode();
	int rowsCount = limitGeocode == null ? 0 : Integer.valueOf(limitGeocode).intValue();
	    
	    final String exportGzipFileName = getExportGzipFileName();
	    
	    String googleClientId = getGoodleClientId(); 
	    String googlePrivateKey = getGooglePrivateKey();
	    Geocoder geocoder = new Geocoder(rowsCount, googleClientId, googlePrivateKey);
	    long startTime = System.currentTimeMillis();

	    PreparedStatement updateAddressStatement = null;
	    PreparedStatement updateSecondaryLocationStatement = null;
	    PreparedStatement selectGeocodedStmt = null;
	    PreparedStatement selectNonGeocodedStmt = null;
	    ResultSet rs = null;
	    FileOutputStream exportFileOutputStream = null;
	    BufferedOutputStream exportBufferedOutputStream = null;
	    CompressorOutputStream gzippedOut = null;

	    try {
		boolean isExportFileCreated = false;
		
		Pattern pattern = Pattern.compile(PO_BOX_REGEXP);
		
		updateAddressStatement = getUpdateAddressStatements(connectionBroker);
		updateSecondaryLocationStatement = getUpdateSecondaryLocationStatement(connectionBroker);
		selectGeocodedStmt = getSelectGeocodedStatement(connectionBroker);
		selectNonGeocodedStmt = getSelectNonGeocodedStatement(connectionBroker);
		
		if(selectNonGeocodedStmt == null) 
		    return;
			
		selectNonGeocodedStmt.setInt(1, rowsCount);
		rs = selectNonGeocodedStmt.executeQuery();
		DecimalFormat format = new DecimalFormat(GeocodingConstants.PERCENT_FORMAT);
		long startCurTime = System.currentTimeMillis();
		int prevCountAll = 0;
		String prevGeocodeRequestKey = null;
		GeocodeRequest prevGeocodedRequest = null;
		boolean isFailure;
		while (rs.next()) {
		    GeocodeRequest geocodeRequest = getGeocodeRequest(rs);
		    isFailure = false;
		    String geocodeRequestKey = getGeocodeRequestKey(geocodeRequest);
		    if(geocodeRequestKey != null 
			    && prevGeocodedRequest != null
			    && geocodeRequestKey.equals(prevGeocodeRequestKey)) {
			geocodeRequest.setAddressLine1(prevGeocodedRequest.getAddressLine1());
			geocodeRequest.setAddressLineWithStreetNumber(prevGeocodedRequest.isAddressLineWithStreetNumber());
			geocodeRequest.setConvertedStepEnum(prevGeocodedRequest.getConvertedStepEnum());
			geocodeRequest.setGeocodeLevel(prevGeocodedRequest.getGeocodeLevel());
			geocodeRequest.setFormattedAddress(prevGeocodedRequest.getFormattedAddress());
			geocodeRequest.setLatitude(prevGeocodedRequest.getLatitude());
			geocodeRequest.setLongitude(prevGeocodedRequest.getLongitude());
			geocodeRequest.setOverQueryLimit(prevGeocodedRequest.isOverQueryLimit());
			logger.logInfo("Duplicate address: " + geocodeRequest.getFullAddress());
			duplicateCount++;
			isFailure = true;
		    } else {
			if (geocodeRequest.getAddressLine1() == null && geocodeRequest.getAddressLine2() == null) {
			    failureCount++;
			    isFailure = true;
			    logger.logInfo("Empty Address " + geocodeRequest.getFullAddress());
			}
			else {
			    if(!(isPoBox(pattern, geocodeRequest.getAddressLine1()) && geocodeRequest.getAddressLine2() == null)) {
				geocoder.geocode(geocodeRequest, ++count);
			    } else {
				failureCount++;
				isFailure = true;
				logger.logInfo("Address with PO BOX: " + geocodeRequest.getFullAddress());
			    }
			}
		    }
		    prevGeocodeRequestKey = geocodeRequestKey;
		    prevGeocodedRequest = geocodeRequest;

		    if(geocodeRequest.isFinishLimitGeocode()) {
			break;
		    }
		    if(!geocodeRequest.isOverQueryLimit()) {
			
			updateAddress(geocodeRequest, count, updateAddressStatement);
			updateSecondaryLocation(geocodeRequest, updateSecondaryLocationStatement);
			
			if (!isExportFileCreated) {
			    exportFileOutputStream = new FileOutputStream(exportGzipFileName);
			    exportBufferedOutputStream = new BufferedOutputStream(exportFileOutputStream);
			    gzippedOut = new CompressorStreamFactory().createCompressorOutputStream(CompressorStreamFactory.GZIP, exportBufferedOutputStream);
			}
			exportGeocodedData(geocodeRequest, selectGeocodedStmt, gzippedOut, !isExportFileCreated);
			isExportFileCreated = true;
		    }
			    
		    if (geocoder.getOverQueryLimitCount() > GeocodingConstants.MAX_OVER_QUERY_LIMIT)
		    	break;

		    if (count % 100 == 0) {
			connectionBroker.commit();
		    }
		    // statistic count
		    if(!isFailure) {
			if (geocodeRequest.getGeocodeLevel().getLevel() > GeocodeLevelEnum.ROUTE.getLevel()) {
			    countSuccess++;
			} else if (geocodeRequest.getGeocodeLevel().getLevel() == GeocodeLevelEnum.ROUTE.getLevel()) {
			    countRoute++;
			} else if (geocodeRequest.getGeocodeLevel().getLevel() > GeocodeLevelEnum.UNDEFINED.getLevel()) {
			    countCityAndPostalCode++;
			    errorRequestList.add(geocodeRequest);
			} else if (!geocodeRequest.isOverQueryLimit()) {
			    countUndefined++;
			    errorRequestList.add(geocodeRequest);
			}
		    }
		    if(count%SPEED_LOGGING == 0 && !isFailure) {
			int countAll = geocoder.getCountGeocode();
			logger.logInfo("Current count geocoding: " + countAll + 
							" (" + getPercent(countAll, count, format) + ")");
			double finishCurTime = System.currentTimeMillis();
			double speedGeocoding = SPEED_LOGGING * 1000 / (finishCurTime - startCurTime);
			double fullSpeedGeocoding = (countAll - prevCountAll) * 1000 / (finishCurTime - startCurTime);
			logger.logInfo("Current speed geocoding: " + speedGeocoding + " items/sec, " +
							fullSpeedGeocoding + " geocoding calls/sec");
			startCurTime = System.currentTimeMillis();
			prevCountAll = countAll;
		    }
		}

	   } finally {
		JdbcConnectionBroker.closeResultSet(rs);
		connectionBroker.closeStatement(selectNonGeocodedStmt);
		connectionBroker.closeStatement(updateSecondaryLocationStatement);
		connectionBroker.closeStatement(updateAddressStatement);
		connectionBroker.closeStatement(selectGeocodedStmt);
		
		if (gzippedOut != null) {
		    gzippedOut.close();
		}
		if (exportBufferedOutputStream != null) {
		    exportBufferedOutputStream.close();    
		}
		if (exportFileOutputStream != null) {
		    exportFileOutputStream.close();
		}
	    }

	    connectionBroker.commit();

	    long endTime = System.currentTimeMillis();
	    logger.logInfo("Size of addresses - " + count +
		    ", time geocoding - " + (endTime - startTime) + " mc" +
		    ", count geocoding - " + geocoder.getCountGeocode());

	    logStatistics(geocoder, count, countSuccess, countRoute, countCityAndPostalCode, countUndefined, duplicateCount,
		    failureCount);

	    String recipients = getEmails();
	    String host = getHost();
	    String port = getPort();
	    String smtpUserName = getSmtpUserName();
	    String smtpUserPassword = getSmtpUserPassword();
	    String from = getMailFrom();
	    ExcelReport.report(geocoder, count, countSuccess, countRoute, countCityAndPostalCode, countUndefined, 
		    duplicateCount, failureCount, errorRequestList, recipients, host, port, smtpUserName, smtpUserPassword, from, logger);

    }
    
    private static boolean isPoBox(Pattern pattern, String address) {
	if (address == null) return false;
	try {
	    Matcher matcher = pattern.matcher(address);
	    return matcher.matches();
	}
	catch (Exception ex) {
	    return false;
	}
    }
    
    protected static void exportGeocodedData(GeocodeRequest geocodeRequest, PreparedStatement selectGeocodedStmt, 
	    OutputStream outputStream, boolean isNeedOutputHeader) throws SQLException, IOException {
	selectGeocodedStmt.setInt(1, geocodeRequest.getAddressId());
	ResultSet rs = selectGeocodedStmt.executeQuery();
	try {
	    ResultSetMetaData metaData = rs.getMetaData();
	    if (isNeedOutputHeader) {
		writeHeader(outputStream, metaData);
	    }

	    while (rs.next()) {
		writeData(outputStream, rs, metaData);
	    }
	}
	finally {
	    try {rs.close();} catch (SQLException ex) {/* ignore */}
	}
    }
    
    protected static void logStatistics(Geocoder geocoder, int sizeList, int countSuccess, int countRoute,
	    int countCityAndPostalCode, int countUndefined, int duplicateCount, int failureCount) {
	AbstractLogger logger = AbstractLogger.getInstance();
	Map<ConvertedStepEnum, Integer> mapSuccessCount = geocoder.getMapSuccessCount();
	Map<ConvertedStepEnum, Integer> mapSuccessRouteCount = geocoder.getMapSuccessRouteCount();
	Map<ConvertedStepEnum, Integer> mapSuccessFailureCount = geocoder.getMapFailureCount();
	int countAll = geocoder.getCountGeocode();
	int overQueryLimitCount = geocoder.getOverQueryLimitCount();
	DecimalFormat format = new DecimalFormat(GeocodingConstants.PERCENT_FORMAT);
	logger.logInfo("Statistics: ");
	logger.logInfo("Size addresses: " + sizeList);
	logger.logInfo("Duplicate address: " + duplicateCount);
	logger.logInfo("Failure address: " + failureCount);
	logger.logInfo("All count geocoding: " + countAll + " (" + getPercent(countAll, sizeList, format) + ")");
	logger.logInfo("Over query limit count geocoding: " + overQueryLimitCount);
	countAll -= overQueryLimitCount;
	logger.logInfo("All count geocoding without over query limit: " + countAll + " (" + getPercent(countAll, sizeList, format) + ")");

	logger.logInfo("Results: ");
	logger.logInfo("Success - \t\t\t" + countSuccess + " (" + getPercent(countSuccess, sizeList, format) + ")");
	logger.logInfo("Route - \t\t\t" + countRoute + " (" + getPercent(countRoute, sizeList, format) + ")");
	logger.logInfo("City and Zip - \t" + countCityAndPostalCode + " (" + getPercent(countCityAndPostalCode, sizeList, format) + ")");
	logger.logInfo("Undefined - \t\t" + countUndefined + " (" + getPercent(countUndefined, sizeList, format) + ")");

	logger.logInfo("Algorithms name: ");
	for (ConvertedStepEnum step : ConvertedStepEnum.values()) {
	    logger.logInfo(step.getStep() + " - " + step);
	}
	logger.logInfo("Algorithms: ");
	Integer sCount, sRCount, fCount;
	for (ConvertedStepEnum step : ConvertedStepEnum.values()) {
	    sCount = mapSuccessCount.get(step);
	    sRCount = mapSuccessRouteCount.get(step);
	    fCount = mapSuccessFailureCount.get(step);
	    logger.logInfo(step.getStep() + ": success - " + sCount + " (" + getPercent(sCount.intValue(), countAll, format) + ") ;\t " +
		    "success route - " + sRCount + " (" + getPercent(sRCount.intValue(), countAll, format) + ") ;\t " +
		    "failure - " + fCount + " (" + getPercent(fCount.intValue(), countAll, format) + ") .");
	}

    }

    protected static void updateAddress(GeocodeRequest request, int i, PreparedStatement statement)
	    throws SQLException {
	if (request.getGeocodeLevel() == GeocodeLevelEnum.STREET_NUMBER
		&& request.isAddressLineWithStreetNumber()) {
	    request.setGeocodeLevel(GeocodeLevelEnum.EXACT_STREET_NUMBER);
	}
	if(request.getFormattedAddress() != null) {
	    request.setFormattedAddress(request.getFormattedAddress().replace("|", "/"));
	}
	AbstractLogger logger = AbstractLogger.getInstance();
	logger.logStepInfo(i, "Geocoding Level - " + request.getGeocodeLevel() +
		", Step saving - " + request.getConvertedStepEnum());
	int paramIndex = 0;
	statement.setString(++paramIndex, request.getFormattedAddress());
	statement.setDouble(++paramIndex, request.getLatitude());
	statement.setDouble(++paramIndex, request.getLongitude());
	statement.setInt(++paramIndex, request.getGeocodeLevel().getLevel());
	statement.setLong(++paramIndex, request.getAddressId());
	statement.executeUpdate();
	logger.logStepInfo(i, "SAVED DATA - Address: " + request.getFormattedAddress() + "; Location: " +
		request.getLatitude() + " " + request.getLongitude());
    }
    
    private static String getGeocodeRequestKey(GeocodeRequest request) {
	return request.getAddressLine1() == null ? "null" : request.getAddressLine1() + "+" + 
		request.getCity() +  "+" + 
		request.getState();
    }
}
