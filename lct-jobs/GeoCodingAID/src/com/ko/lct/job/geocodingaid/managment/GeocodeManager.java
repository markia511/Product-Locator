package com.ko.lct.job.geocodingaid.managment;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.ko.lct.job.common.businessobjects.GeocodeRequest;
import com.ko.lct.job.common.management.AbstractGeocodeManager;
import com.ko.lct.job.geocoding.utilities.ApplicationException;
import com.ko.lct.job.geocoding.utilities.JdbcConnectionBroker;
import com.ko.lct.job.geocodingaid.dao.GeocoderAidQuery;
import com.ko.lct.job.geocodingaid.logger.GeocoderAidLogger;
import com.ko.lct.job.geocodingaid.utilities.GeoCodingAIDProperties;

public class GeocodeManager extends AbstractGeocodeManager {

    private static final GeocoderAidLogger logger = GeocoderAidLogger.getInstance();

    public void geocode() throws Exception {
	JdbcConnectionBroker connectionBroker = null;
	try {
	    logger.logInfo("Starting geocode process");
	    connectionBroker = getDbConnection(GeoCodingAIDProperties.DEFAULT_FILE_NAME);
	    geocodeProcess(connectionBroker);
	} finally {
	    if (connectionBroker != null) {
		connectionBroker.cleanup();
	    }
	}
    }

    @Override
    protected String getLimitGeocode() {
	return String.valueOf(GeoCodingAIDProperties.getProperty("LIMIT_GEOCODE"));
    }

    @Override
    protected String getExportGzipFileName() throws ApplicationException {
	return GeoCodingAIDProperties.getNotEmptyParam("EXPORT_PATH") + FILE_SEPARATOR +
		(new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")).format(Calendar.getInstance().getTime()) + ".txt.gz";
    }

    @Override
    protected String getGoodleClientId() {
	return GeoCodingAIDProperties.getProperty("googleClientId");
    }

    @Override
    protected String getGooglePrivateKey() {
	return GeoCodingAIDProperties.getProperty("googlePrivateKey");
    }

    @Override
    protected String getEmails() {
	return GeoCodingAIDProperties.getProperty("emails");
    }

    @Override
    protected String getHost() {
	return GeoCodingAIDProperties.getProperty("host");
    }

    @Override
    protected String getPort() {
	return GeoCodingAIDProperties.getProperty("port");
    }

    @Override
    protected String getSmtpUserName() {
	String smtpUserName = GeoCodingAIDProperties.getProperty("smtpUserName");
	if (smtpUserName != null && smtpUserName.isEmpty()) {
	    return null;
	}
	return smtpUserName;
    }

    @Override
    protected String getSmtpUserPassword() {
	return GeoCodingAIDProperties.getProperty("smtpUserPassword");
    }

    @Override
    protected String getMailFrom() {
	return GeoCodingAIDProperties.getProperty("mail_from");
    }

    @Override
    protected PreparedStatement getUpdateAddressStatements(JdbcConnectionBroker connectionBroker)
	    throws SQLException, ApplicationException {
	return connectionBroker.getNewPreparedStatement(
		JdbcConnectionBroker.setSchemaName(GeocoderAidQuery.UPDATE_GEOCODED_PRIVACY_ADDRESS,
			GeoCodingAIDProperties.getSchema()));
    }

    @Override
    protected PreparedStatement getUpdateSecondaryLocationStatement(JdbcConnectionBroker connectionBroker)
	    throws SQLException, ApplicationException {
	return null;
    }

    @Override
    protected PreparedStatement getSelectGeocodedStatement(JdbcConnectionBroker connectionBroker)
	    throws SQLException, ApplicationException {
	return connectionBroker.getNewPreparedStatement(GeocoderAidQuery.SELECT_GEOCODED_SQL,
		GeoCodingAIDProperties.getSchema());
    }

    @Override
    protected PreparedStatement getSelectNonGeocodedStatement(JdbcConnectionBroker connectionBroker)
	    throws SQLException, ApplicationException {
	return connectionBroker.getNewPreparedStatement(GeocoderAidQuery.GET_NON_GEOCODED_PRIVACY_ADDRESS_LIST,
		GeoCodingAIDProperties.getSchema());
    }

    @Override
    protected GeocodeRequest getGeocodeRequest(ResultSet rs) throws SQLException {
	GeocodeRequest geocodeRequest = new GeocodeRequest();
	geocodeRequest.setAddressId(rs.getInt("ADDR_PRV_ID"));
	geocodeRequest.setCountry(rs.getString("CTRY_CD"));
	geocodeRequest.setState(rs.getString("STATE"));
	geocodeRequest.setCity(rs.getString("CITY"));
	geocodeRequest.setPostalCode(rs.getString("PSTL_CD"));
	geocodeRequest.setAddressLine1(rs.getString("ADDR_LINE_1"));
	geocodeRequest.setAddressLine2(rs.getString("ADDR_LINE_2"));
	geocodeRequest.setOutletName(rs.getString("BTLR_DLVR_PNT_NM"));
	geocodeRequest.setNeedGeocodingWithoutCity(false);
	geocodeRequest.setNeedGeocodingWithOutletInTop(false);
	geocodeRequest.setNeedGeocodingWithoutAddrLine(false);
	return geocodeRequest;
    }

}
