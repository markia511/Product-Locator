package com.ko.lct.job.geocodingaid.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ko.lct.job.geocoding.utilities.ApplicationException;
import com.ko.lct.job.geocoding.utilities.JdbcConnectionBroker;
import com.ko.lct.job.geocodingaid.businessobjects.AidDbDto;
import com.ko.lct.job.geocodingaid.businessobjects.AidInputDto;
import com.ko.lct.job.geocodingaid.logger.GeocoderAidLogger;
import com.ko.lct.job.geocodingaid.utilities.GeoCodingAIDProperties;

public class GeocoderAidDAO {

    private static final int ADDRESS_BATCH_SIZE = 500;

    private static final GeocoderAidLogger logger = GeocoderAidLogger.getInstance();

    public static void updatePrivacyAddressByMainAddress(JdbcConnectionBroker jdbcConnectionBroker) 
	    throws SQLException, ApplicationException {
	int batchSize = ADDRESS_BATCH_SIZE;
	int updateCount = batchSize;
	int sumUpdateCount = 0;
	String updateQuery = getQueryWithSchema(GeocoderAidQuery.UPDATE_GEOCODING_ADDRESS_BY_MAIN_ADDRESS);
	PreparedStatement preparedStatement = jdbcConnectionBroker.getNewPreparedStatement(updateQuery);
	preparedStatement.setInt(1, batchSize);
	try {
	    while (updateCount == batchSize) {
		updateCount = preparedStatement.executeUpdate();
		sumUpdateCount += updateCount;
		jdbcConnectionBroker.commit();
		logger.logInfo("Update privacy address: " + sumUpdateCount + " items.");
	    }
	} catch (SQLException ex) {
	    logger.logError(ex);
	    throw ex;
	} finally {
	    jdbcConnectionBroker.closeStatement(preparedStatement);
	}
    }

    public static void updatePrivacyAddressByPrivacyAddress(JdbcConnectionBroker jdbcConnectionBroker) 
	    throws SQLException, ApplicationException {
	int batchSize = ADDRESS_BATCH_SIZE;
	int updateCount = batchSize;
	int sumUpdateCount = 0;
	String updateQuery = getQueryWithSchema(GeocoderAidQuery.UPDATE_GEOCODING_ADDRESS_BY_PRIVACY_ADDRESS);
	PreparedStatement preparedStatement = jdbcConnectionBroker.getNewPreparedStatement(updateQuery);
	preparedStatement.setInt(1, batchSize);
	try {
	    while (updateCount == batchSize) {
		updateCount = preparedStatement.executeUpdate();
		sumUpdateCount += updateCount;
		jdbcConnectionBroker.commit();
		logger.logInfo("Update privacy address: " + sumUpdateCount + " items.");
	    }
	} catch (SQLException ex) {
	    logger.logError(ex);
	    throw ex;
	} finally {
	    jdbcConnectionBroker.closeStatement(preparedStatement);
	}
    }

    public static String getQueryWithSchema(String query) throws ApplicationException {
	return JdbcConnectionBroker.setSchemaName(query, GeoCodingAIDProperties.getSchema());
    }

    public static int getNewAddrPrvId(JdbcConnectionBroker connectionBroker) throws SQLException, ApplicationException {
	int retValue = 1;
	PreparedStatement stmt =
		connectionBroker.getNewPreparedStatement(getQueryWithSchema(GeocoderAidQuery.GET_NEW_ADDR_PRV_ID));
	try {
	    ResultSet rs = stmt.executeQuery();
	    try {
		if (rs.next()) {
		    retValue = rs.getInt(1);
		}
	    } finally {
		JdbcConnectionBroker.closeResultSet(rs);
	    }
	} finally {
	    connectionBroker.closeStatement(stmt);
	}
	return retValue;
    }

    public static AidDbDto findAddress(PreparedStatement stmt, AidInputDto dto) throws SQLException {
	AidDbDto dbDto = null;
	int paramIndex = 0;
	stmt.setInt(++paramIndex, dto.getOwnrshpId());
	stmt.setString(++paramIndex, dto.getBtlrDlvrPntNo());
	stmt.setString(++paramIndex, dto.getState());
	stmt.setString(++paramIndex, dto.getCity());
	stmt.setString(++paramIndex, dto.getAddressLine1());
	ResultSet rs = stmt.executeQuery();
	try {
	    if (rs.next()) {
		dbDto = new AidDbDto();
		dbDto.setAddrPrvId(rs.getInt(1));
		dbDto.setBtlrDlvrPntNm(rs.getString(2));
		dbDto.setAddressLine2(rs.getString(3));
		dbDto.setZip(rs.getString(4));
		dbDto.setCountryDesc(rs.getString(5));
		dbDto.setAreaCode(rs.getString(6));
		dbDto.setPhnNo(rs.getString(7));
	    }

	} finally {
	    JdbcConnectionBroker.closeResultSet(rs);
	}
	return dbDto;
    }

    public static boolean isExistUnloadGeocodedAddress(JdbcConnectionBroker connectionBroker)
	    throws SQLException, ApplicationException {
	boolean isExist = false;
	PreparedStatement stmt =
		connectionBroker.getNewPreparedStatement(getQueryWithSchema(GeocoderAidQuery.IS_EXIST_UNLOAD_GEOCODED_ADDRESS));
	try {
	    ResultSet rs = stmt.executeQuery();
	    try {
		if (rs.next()) {
		    isExist = true;
		}
	    } finally {
		JdbcConnectionBroker.closeResultSet(rs);
	    }
	} finally {
	    connectionBroker.closeStatement(stmt);
	}
	return isExist;
    }

    public static void updateUnloadGeocodedAddress(JdbcConnectionBroker connectionBroker)
	    throws SQLException, ApplicationException {
	int batchSize = ADDRESS_BATCH_SIZE;
	int updateCount = batchSize;
	int sumUpdateCount = 0;
	PreparedStatement preparedStatement = 
		connectionBroker.getNewPreparedStatement(getQueryWithSchema(GeocoderAidQuery.UPDATE_UNLOAD_GEOCODED_ADDRESS));
	preparedStatement.setInt(1, batchSize);
	try {
	    while (updateCount == batchSize) {
		updateCount = preparedStatement.executeUpdate();
		sumUpdateCount += updateCount;
		connectionBroker.commit();
		logger.logInfo("Update unloaded address: " + sumUpdateCount + " items.");
	    }
	} catch (SQLException ex) {
	    logger.logError(ex);
	    throw ex;
	} finally {
	    connectionBroker.closeStatement(preparedStatement);
	}
    }
}
