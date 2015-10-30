package com.ko.lct.job.caching.utilities;

import com.ko.lct.job.geocoding.utilities.ApplicationException;
import com.ko.lct.job.geocoding.utilities.EncryptionUtilities;
import com.ko.lct.job.geocoding.utilities.JobProperties;

public class CachingDataProperties extends JobProperties {
    private static final String DEFAULT_FILE_NAME = "CachingData.properties";
    private static final String DB_URL_PARAM = "url";
    private static final String SCHEMA_PARAM = "schema";
    private static final String USER_NAME_PARAM = "user";
    private static final String PASSWORD_PARAM = "password";
    private static final String PAUSE_TIME_PARAM = "pauseTime_ms";
    
    public static String getProperty(String propertyName) {
	return getProperty(DEFAULT_FILE_NAME, propertyName);
    }
    
    public static String getNotEmptyParam(String paramName) throws ApplicationException {
	return getNotEmptyParam(DEFAULT_FILE_NAME, paramName);
    }
    
    public static String getDbUrlParam() throws ApplicationException {
	return getNotEmptyParam(DEFAULT_FILE_NAME, DB_URL_PARAM);
    }

    public static final String getSchema() throws ApplicationException {
	return getNotEmptyParam(DEFAULT_FILE_NAME, SCHEMA_PARAM);
    }

    public static final String getUserName() throws ApplicationException {
	return getNotEmptyParam(DEFAULT_FILE_NAME, USER_NAME_PARAM);
    }

    public static final long getCachingTime() throws ApplicationException {
	String param = getNotEmptyParam(DEFAULT_FILE_NAME, PAUSE_TIME_PARAM);
	long value = 0;
	try {
	    value = Long.parseLong(param);
	} catch (Exception e) {
	    value = 0;
	}
	return value;
    }

    public static final String getPassword() throws ApplicationException {
	return EncryptionUtilities.decrypt(getNotEmptyParam(DEFAULT_FILE_NAME, 
		PASSWORD_PARAM));
    }

}
