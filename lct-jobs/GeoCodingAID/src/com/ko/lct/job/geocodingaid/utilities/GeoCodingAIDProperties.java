package com.ko.lct.job.geocodingaid.utilities;

import com.ko.lct.job.geocoding.utilities.ApplicationException;
import com.ko.lct.job.geocoding.utilities.JobProperties;

public class GeoCodingAIDProperties extends JobProperties {

    public static final String DEFAULT_FILE_NAME = "GEOCODINGAID.props";

    public static String getProperty(String propertyName) {
	return getProperty(DEFAULT_FILE_NAME, propertyName);
    }
    
    public static String getDirProperty(String dirPropertyName) {
	return getDirProperty(DEFAULT_FILE_NAME, dirPropertyName);
    }
    
    public static String getNotEmptyParam(String paramName) throws ApplicationException {
	return getNotEmptyParam(DEFAULT_FILE_NAME, paramName);
    }

    public static String getDbUrlParam() throws ApplicationException {
	return JobProperties.getDbUrlParam(DEFAULT_FILE_NAME);
    }

    public static final String getSchema() throws ApplicationException {
	return JobProperties.getSchema(DEFAULT_FILE_NAME);
    }
    
    public static final String getSmtpUserName() {
	return JobProperties.getSmtpUserName(DEFAULT_FILE_NAME);
    }
    
    public static final String getSmtpUserPassword() {
	return JobProperties.getSmtpUserPassword(DEFAULT_FILE_NAME);
    }

}
