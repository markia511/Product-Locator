package com.ko.lct.job.geocoding.utilities;

public class GeoCodingProcessProperties extends JobProperties {

    public static final String DEFAULT_FILE_NAME = "GEOCODINGProcess.props";

    public static String getProperty(String propertyName) {
	return getProperty(DEFAULT_FILE_NAME, propertyName);
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

    public static final String getUserName() throws ApplicationException {
	return JobProperties.getUserName(DEFAULT_FILE_NAME);
    }

    public static final String getPassword() throws ApplicationException {
	return JobProperties.getPassword(DEFAULT_FILE_NAME);
    }
    
    public static final String getSmtpUserName() {
	return JobProperties.getSmtpUserName(DEFAULT_FILE_NAME);
    }
    
    public static final String getSmtpUserPassword() {
	return JobProperties.getSmtpUserPassword(DEFAULT_FILE_NAME);
    }
}
