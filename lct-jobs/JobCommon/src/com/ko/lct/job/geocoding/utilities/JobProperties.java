package com.ko.lct.job.geocoding.utilities;

import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;

public class JobProperties {
    private static final String DB_URL_PARAM = "Database";
    private static final String SCHEMA_PARAM = "Schema";
    private static final String USER_NAME_PARAM = "UserID";
    private static final String PASSWORD_PARAM = "Password";
    private static final String SMTP_USER_NAME = "smtpUserName";
    private static final String SMTP_USER_PASSWORD = "smtpUserPassword";

    private static final String FILE_SEPARATOR = AccessController
	    .doPrivileged(new PrivilegedAction<String>() {
		@Override
		public String run() {
		    return System.getProperty("file.separator");
		}
	    });
    
    private static JobProperties propertiesObject = null;
    private static boolean isInitialized = false;
    private Properties properties = null;
    
    public JobProperties() {
	properties = new Properties();
    }

    public static String getProperty(String propertyFileName, String propertyName) {
	if (!isInitialized) {
	    initialize(propertyFileName);
	}
	return propertiesObject.properties.getProperty(propertyName);
    }

    public static String getDirProperty(final String propertyFileName, final String dirPropertyName) {
	if (!isInitialized) {
	    initialize(propertyFileName);
	}
	String directoryName = propertiesObject.properties.getProperty(dirPropertyName);
	if (directoryName != null
		&& !directoryName.isEmpty()
		&& !directoryName.endsWith(FILE_SEPARATOR) 
		&& !directoryName.endsWith("\\")
		&& !directoryName.endsWith("/")) {
	    directoryName = directoryName + FILE_SEPARATOR;
	}
	return directoryName;
    }
    
    public static void initialize(String defaultFileName) {
	if (propertiesObject == null) {
	    propertiesObject = new JobProperties();
	}
	InputStream propertiesFile = null;
	try {
	    propertiesFile = ClassLoader.getSystemResourceAsStream(defaultFileName);
	    propertiesObject.properties.load(propertiesFile);
	    isInitialized = true;
	} catch (Exception e) {
	    System.out.println("*ERROR* Can not initialize properties.  Check the file and path ");
	    System.out.println("The property file was expected at: " + defaultFileName);
	    e.printStackTrace();
	    System.exit(-1);
	} finally {
	    if (propertiesFile != null) {
		try {
		    propertiesFile.close();
		} catch (Exception ex) { /* do nothing */
		}
	    }
	}
    }

    protected static String getNotEmptyParam(String propertyFileName, String paramName) throws ApplicationException {
	String retValue = getProperty(propertyFileName, paramName);
	if (retValue == null || retValue.trim().isEmpty()) {
	    throw new ApplicationException(paramName + " is not specified in the properties file");
	}
	return retValue;
    }
    
    public static String getDbUrlParam(String propertyFileName) throws ApplicationException {
	return getNotEmptyParam(propertyFileName, DB_URL_PARAM);
    }

    public static final String getSchema(String propertyFileName) throws ApplicationException {
	return getNotEmptyParam(propertyFileName, SCHEMA_PARAM);
    }

    public static final String getUserName(String propertyFileName) throws ApplicationException {
	return getNotEmptyParam(propertyFileName, USER_NAME_PARAM);
    }

    public static final String getPassword(String propertyFileName) throws ApplicationException {
	return EncryptionUtilities.decrypt(getNotEmptyParam(propertyFileName, PASSWORD_PARAM));
    }

    public static final String getSmtpUserName(String propertyFileName) {
	String smtpUserName =getProperty(propertyFileName, SMTP_USER_NAME);
	if (smtpUserName != null && smtpUserName.isEmpty()) {
	    return null;
	}
	return smtpUserName;
    }

    public static final String getSmtpUserPassword(String propertyFileName) {
	String smtpUserPassword =getProperty(propertyFileName, SMTP_USER_PASSWORD);
	if (smtpUserPassword != null && smtpUserPassword.isEmpty()) {
	    return null;
	}
	return smtpUserPassword;
    }
    
}
