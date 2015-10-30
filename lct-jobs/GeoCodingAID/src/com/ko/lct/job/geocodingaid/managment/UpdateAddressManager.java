package com.ko.lct.job.geocodingaid.managment;

import com.ko.lct.job.common.management.AbstractManager;
import com.ko.lct.job.geocoding.utilities.JdbcConnectionBroker;
import com.ko.lct.job.geocodingaid.dao.GeocoderAidDAO;
import com.ko.lct.job.geocodingaid.logger.GeocoderAidLogger;
import com.ko.lct.job.geocodingaid.utilities.GeoCodingAIDProperties;

public class UpdateAddressManager extends AbstractManager {
    
    private static final GeocoderAidLogger logger = GeocoderAidLogger.getInstance();
    
    public static void update() throws Exception {
    
	JdbcConnectionBroker connectionBroker = null;
	try {
	    connectionBroker = getDbConnection(GeoCodingAIDProperties.DEFAULT_FILE_NAME);
	    
	    logger.logInfo("Update privacy addresses by main addresses.");
	    GeocoderAidDAO.updatePrivacyAddressByMainAddress(connectionBroker);
	    
	    logger.logInfo("Update privacy addresses by privacy addresses.");
	    GeocoderAidDAO.updatePrivacyAddressByPrivacyAddress(connectionBroker);
	    
	    logger.logInfo("Finish updating privacy addresses.");
	    
	} finally {
	    if (connectionBroker != null) {
		connectionBroker.cleanup();
	    }
	}
    }

}
