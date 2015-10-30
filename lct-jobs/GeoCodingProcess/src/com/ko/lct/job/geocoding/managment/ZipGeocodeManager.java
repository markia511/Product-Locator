package com.ko.lct.job.geocoding.managment;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;

import com.ko.lct.job.common.management.AbstractManager;
import com.ko.lct.job.geocoding.Dao.GeocodeDAO;
import com.ko.lct.job.geocoding.Dao.GeocodingQuery;
import com.ko.lct.job.common.businessobjects.GeocodeRequest;
import com.ko.lct.job.geocoding.logger.GeocoderLogger;
import com.ko.lct.job.geocoding.utilities.ApplicationException;
import com.ko.lct.job.geocoding.utilities.GeoCodingProcessProperties;
import com.ko.lct.job.geocoding.utilities.GeocodingConstants;
import com.ko.lct.job.geocoding.utilities.JdbcConnectionBroker;

public class ZipGeocodeManager extends AbstractManager {

    private static final GeocoderLogger logger = GeocoderLogger.getInstance();

    private static int countSuccess = 0;
    private static int countUndefined = 0;
    private static final String ZIP_FILE = "zips.csv";

    public static void geocode() {
	int count = 0;
	JdbcConnectionBroker connectionBroker = null;
	PreparedStatement updateAddressStatement = null;
	try {
	    connectionBroker = getDbConnection(GeoCodingProcessProperties.DEFAULT_FILE_NAME);
	    List<GeocodeRequest> requestList = GeocodeDAO.getZipGeocodeRequestList(connectionBroker,
		    GeocodingConstants.MAX_GEOCODE_ADDRESSES);
	    updateAddressStatement = connectionBroker.getNewPreparedStatement(
		    JdbcConnectionBroker.setSchemaName(GeocodingQuery.UPDATE_LOCATION_BY_ZIP,
			    GeoCodingProcessProperties.getSchema()));

	    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(ZIP_FILE), GeocodingConstants.ENCODE_UTF_8), 1024 * 1024);
	    try {
		String[] curr = null;
		String strLine = "";
		while ((strLine = br.readLine()) != null) {
		    count++;
		    if (count == 1)
			continue;

		    curr = strLine.split(",");
		    if (curr.length < 6 || curr[0].length() <= 2 || curr[1].length() <= 2 || curr[2].length() <= 2 || curr[3].length() <= 2)
			continue;

		    for (int i = 0; i < 4; i++) {
			curr[i] = curr[i].trim();
			curr[i] = curr[i].substring(1, curr[i].length() - 1).trim();
		    }
		    try {

			int updated = updateAddress(curr, updateAddressStatement);
			logger.logInfo("Zip " + curr[0] + " updated: " + updated);
		    } catch (Exception e) {
			countUndefined++;
			e.printStackTrace();
			logger.logError(e);
		    }
		    if (count % 100 == 0) {
			connectionBroker.commit();
		    }
		}
		connectionBroker.commit();
	    } finally {
		br.close();
	    }

	    DecimalFormat format = new DecimalFormat(GeocodingConstants.PERCENT_FORMAT);
	    logger.logInfo("Results: ");
	    logger.logInfo("Success - \t\t" + countSuccess + " (" + getPercent(countSuccess, requestList.size(), format) + ")");
	    logger.logInfo("Undefined - \t" + countUndefined + " (" + getPercent(countUndefined, requestList.size(), format) + ")");
	} catch (SQLException e) {
	    logger.logError(SQLException.class.getName(), e);
	} catch (ApplicationException e) {
	    logger.logError(ApplicationException.class.getName(), e);
	} catch (Exception e) {
	    logger.logError(Exception.class.getName(), e);
	} finally {
	    if (connectionBroker != null) {
		connectionBroker.cleanup();
	    }
	}
    }

    private static int updateAddress(String[] params, PreparedStatement statement)
	    throws SQLException {
	if (statement != null) {
	    int paramIndex = 0;

	    statement.setString(++paramIndex, params[2]);
	    statement.setString(++paramIndex, params[3]);
	    statement.setString(++paramIndex, params[0]);
	    statement.setString(++paramIndex, params[1]);
	    return statement.executeUpdate();
	}
	logger.logInfo("SAVED DATA - Zip: " + params[0] + " " + params[2] + " " + params[3]);
	return 0;
    }

}
