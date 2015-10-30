package com.ko.lct.job.geocoding.managment;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.ko.lct.job.common.businessobjects.GeocodeRequest;
import com.ko.lct.job.common.management.AbstractGeocodeManager;
import com.ko.lct.job.geocoding.Dao.GeocodingQuery;
import com.ko.lct.job.geocoding.logger.GeocoderLogger;
import com.ko.lct.job.geocoding.utilities.ApplicationException;
import com.ko.lct.job.geocoding.utilities.GeoCodingProcessProperties;
import com.ko.lct.job.geocoding.utilities.JdbcConnectionBroker;

public class GeocodeManager extends AbstractGeocodeManager {

    private static final GeocoderLogger logger = GeocoderLogger.getInstance();

    public void geocode() {

	JdbcConnectionBroker connectionBroker = null;
	try {
	    connectionBroker = getDbConnection(GeoCodingProcessProperties.DEFAULT_FILE_NAME);
	    geocodeProcess(connectionBroker);
	} catch (SQLException e) {
	    logger.logError(e.getMessage(), e);
	} catch (ApplicationException e) {
	    logger.logError(e.getMessage(), e);
	} catch (Exception e) {
	    logger.logError(e.getMessage(), e);
	} finally {
	    if (connectionBroker != null) {
		connectionBroker.cleanup();
	    }
	}
    }

    @Override
    protected void updateSecondaryLocation(GeocodeRequest geocodeRequest,
	    PreparedStatement updateOutletLocationStatement) throws SQLException {
	updateOutletLocationStatement.setDouble(1, geocodeRequest.getLatitude());
	updateOutletLocationStatement.setDouble(2, geocodeRequest.getLongitude());
	updateOutletLocationStatement.setInt(3, geocodeRequest.getAddressId());
	updateOutletLocationStatement.executeUpdate();
    }

    @Override
    protected String getLimitGeocode() {
	return String.valueOf(GeoCodingProcessProperties.getProperty("limitGeocode"));
    }

    @Override
    protected String getExportGzipFileName() throws ApplicationException {
	return GeoCodingProcessProperties.getNotEmptyParam("EXPORT_PATH") + FILE_SEPARATOR +
		(new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")).format(Calendar.getInstance().getTime()) + ".txt.gz";
    }

    @Override
    protected String getGoodleClientId() {
	return GeoCodingProcessProperties.getProperty("googleClientId");
    }

    @Override
    protected String getGooglePrivateKey() {
	return GeoCodingProcessProperties.getProperty("googlePrivateKey");
    }

    @Override
    protected String getEmails() {
	return GeoCodingProcessProperties.getProperty("emails");
    }

    @Override
    protected String getHost() {
	return GeoCodingProcessProperties.getProperty("host");
    }

    @Override
    protected String getPort() {
	return GeoCodingProcessProperties.getProperty("port");
    }
    
    @Override
    protected String getSmtpUserName() {
	return GeoCodingProcessProperties.getSmtpUserName();
    }

    @Override
    protected String getSmtpUserPassword() {
	return GeoCodingProcessProperties.getSmtpUserPassword();
    }

    @Override
    protected String getMailFrom() {
	return GeoCodingProcessProperties.getProperty("mail_from");
    }

    @Override
    protected PreparedStatement getUpdateAddressStatements(JdbcConnectionBroker connectionBroker)
	    throws SQLException, ApplicationException {
	return connectionBroker.getNewPreparedStatement(
		JdbcConnectionBroker.setSchemaName(GeocodingQuery.UPDATE_ADDRESS_GEOCODE,
			GeoCodingProcessProperties.getSchema()));
    }

    @Override
    protected PreparedStatement getUpdateSecondaryLocationStatement(JdbcConnectionBroker connectionBroker)
	    throws SQLException, ApplicationException {
	return connectionBroker.getNewPreparedStatement(
		JdbcConnectionBroker.setSchemaName(GeocodingQuery.UPDATE_OUTLET_LOCATION,
			GeoCodingProcessProperties.getSchema()));
    }

    @Override
    protected PreparedStatement getSelectGeocodedStatement(JdbcConnectionBroker connectionBroker)
	    throws SQLException, ApplicationException {
	return connectionBroker.getNewPreparedStatement(GeocodingQuery.SELECT_GEOCODED_SQL,
		GeoCodingProcessProperties.getSchema());
    }

    @Override
    protected PreparedStatement getSelectNonGeocodedStatement(JdbcConnectionBroker connectionBroker)
	    throws SQLException, ApplicationException {
	return connectionBroker.getNewPreparedStatement(GeocodingQuery.GET_ADDRESS_LIST,
		GeoCodingProcessProperties.getSchema());
    }

    @Override
    protected GeocodeRequest getGeocodeRequest(ResultSet rs) throws SQLException {
	GeocodeRequest geocodeRequest = new GeocodeRequest();
	geocodeRequest.setAddressId(rs.getInt("ADDR_ID"));
	geocodeRequest.setCountry(rs.getString("CTRY_CD"));
	geocodeRequest.setState(rs.getString("STATE"));
	geocodeRequest.setCity(rs.getString("CITY"));
	geocodeRequest.setPostalCode(rs.getString("PSTL_CD"));
	geocodeRequest.setAddressLine1(rs.getString("ADDR_LINE_1"));
	geocodeRequest.setAddressLine2(rs.getString("ADDR_LINE_2"));
	geocodeRequest.setOutletName(rs.getString("OUTLET_NAME"));
	return geocodeRequest;
    }

}
