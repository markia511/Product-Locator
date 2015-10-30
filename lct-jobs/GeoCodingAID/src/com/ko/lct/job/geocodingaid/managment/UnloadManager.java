package com.ko.lct.job.geocodingaid.managment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import com.ko.lct.job.common.management.AbstractManager;
import com.ko.lct.job.geocoding.utilities.ApplicationException;
import com.ko.lct.job.geocoding.utilities.JdbcConnectionBroker;
import com.ko.lct.job.geocodingaid.dao.GeocoderAidDAO;
import com.ko.lct.job.geocodingaid.dao.GeocoderAidQuery;
import com.ko.lct.job.geocodingaid.logger.GeocoderAidLogger;
import com.ko.lct.job.geocodingaid.utilities.GeoCodingAIDProperties;

public class UnloadManager extends AbstractManager {

    private static final GeocoderAidLogger logger = GeocoderAidLogger.getInstance();

    public static void unload() throws Exception {

	JdbcConnectionBroker connectionBroker = null;
	FileOutputStream exportFileOutputStream = null;
	BufferedOutputStream exportBufferedOutputStream = null;
	CompressorOutputStream gzippedOut = null;

	try {
	    logger.logInfo("Unload geocoded addresses.");
	    connectionBroker = getDbConnection(GeoCodingAIDProperties.DEFAULT_FILE_NAME);

	    String outputDirectory = GeoCodingAIDProperties.getProperty("OUTPUT_DATA_PATH");
	    String outputFilePrefix = GeoCodingAIDProperties.getProperty("INPUT_FILE_PREFIX");
	    String outputFileSuffix = GeoCodingAIDProperties.getProperty("OUTPUT_FILE_SUFFIX");
	    String dataFileSuffix = GeoCodingAIDProperties.getProperty("DATA_FILE_SUFFIX");
	    String doneFileSuffix = GeoCodingAIDProperties.getProperty("DONE_FILE_SUFFIX");
	    String time = (new SimpleDateFormat("yyyyMMdd")).format(Calendar.getInstance().getTime());
	    String outputFileName = outputFilePrefix + time + outputFileSuffix;

	    if (GeocoderAidDAO.isExistUnloadGeocodedAddress(connectionBroker)) {

		File outputDataFile = new File(outputDirectory, outputFileName + dataFileSuffix + /*".txt*/ ".gz");
		exportFileOutputStream = new FileOutputStream(outputDataFile);
		exportBufferedOutputStream = new BufferedOutputStream(exportFileOutputStream);

		gzippedOut = new CompressorStreamFactory().createCompressorOutputStream(CompressorStreamFactory.GZIP,
			exportBufferedOutputStream);

		fillOutputStream(connectionBroker, gzippedOut);
		
		gzippedOut.close();
		gzippedOut = null;
		
		exportBufferedOutputStream.close();
		exportBufferedOutputStream = null;
		
		exportFileOutputStream.close();
		exportFileOutputStream = null;

		File outputDoneFile = new File(outputDirectory, outputFileName + doneFileSuffix);
		outputDoneFile.createNewFile();

		logger.logInfo("Update unloaded addresses.");
		GeocoderAidDAO.updateUnloadGeocodedAddress(connectionBroker);
		connectionBroker.commit();
		logger.logInfo("Finish unloading geocoded addresses.");
	    } else {
		logger.logInfo("Unloaded geocoded addresses are not exist.");
	    }

	} finally {
	    if (connectionBroker != null) {
		connectionBroker.cleanup();
	    }
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
    }

    private static boolean fillOutputStream(JdbcConnectionBroker connectionBroker, OutputStream outputStream)
	    throws SQLException, IOException, ApplicationException {
	boolean isHaveItems = false;
	ResultSet rs = null;
	PreparedStatement stmt = connectionBroker.getNewPreparedStatement(
		GeocoderAidQuery.GET_UNLOAD_GEOCODED_ADDRESS, GeoCodingAIDProperties.getSchema());
	try {
	    rs = stmt.executeQuery();
	    try {
		ResultSetMetaData metaData = rs.getMetaData();
		
		int count = 0;
		while (rs.next()) {
		    isHaveItems = true;
		    writeData(outputStream, rs, metaData);
		    count++;
		    if(count%10000 == 0) {
			logger.logInfo("Unloading geocoded addresses: " + count);
		    }
		}
		if(count > 0 && count%10000 != 0) {
		    logger.logInfo("Unloading geocoded addresses: " + count);		    
		}
	    } finally {
		try {
		    rs.close();
		} catch (Exception ex) {/* ignore */
		}
	    }
	} finally {
	    connectionBroker.closeStatement(stmt);
	}
	return isHaveItems;
    }
}
