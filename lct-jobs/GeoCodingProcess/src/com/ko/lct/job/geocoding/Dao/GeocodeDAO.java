package com.ko.lct.job.geocoding.Dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.ko.lct.job.common.businessobjects.GeocodeRequest;
import com.ko.lct.job.geocoding.logger.GeocoderLogger;
import com.ko.lct.job.geocoding.utilities.ApplicationException;
import com.ko.lct.job.geocoding.utilities.GeoCodingProcessProperties;
import com.ko.lct.job.geocoding.utilities.JdbcConnectionBroker;

public class GeocodeDAO {

    public static List<GeocodeRequest> getZipGeocodeRequestList(JdbcConnectionBroker jdbcConnectionBroker, int rowCount) {
	List<GeocodeRequest> geocodeRequests = new ArrayList<GeocodeRequest>();
	GeocodeRequest geocodeRequest;
	PreparedStatement preparedStatement = null;
	ResultSet rs = null;
	GeocoderLogger logger = GeocoderLogger.getInstance();
	try {
	    String query = getQueryWithSchema(GeocodingQuery.GET_ZIP_LIST);
	    preparedStatement = jdbcConnectionBroker.getNewPreparedStatement(query);
	    preparedStatement.setInt(1, rowCount);
	    rs = preparedStatement.executeQuery();
	    while (rs.next()) {
		geocodeRequest = new GeocodeRequest();
		geocodeRequest.setPostalCode(rs.getString("PSTL_CD"));
		geocodeRequests.add(geocodeRequest);
	    }
	} catch (Exception ex) {
	    logger.logError(ex);
	} finally {
	    JdbcConnectionBroker.closeResultSet(rs);
	    jdbcConnectionBroker.closeStatement(preparedStatement);
	}
	return geocodeRequests;
    }

    public static String getQueryWithSchema(String query) throws ApplicationException {
	return JdbcConnectionBroker.setSchemaName(query,
		GeoCodingProcessProperties.getSchema());
    }

}
