package com.ko.lct.job.geocodingaid.logger;

import java.io.IOException;

import com.ko.lct.job.geocodingaid.GeoCodingAID;
import com.ko.lct.job.geocodingaid.utilities.GeoCodingAIDProperties;
import com.ko.lct.job.logger.AbstractLogger;

public class GeocoderAidLogger extends AbstractLogger {
    public static final String LOGGER_NAME = GeoCodingAID.class.getName();
    
    protected GeocoderAidLogger() throws IOException {
	initLogger(GeoCodingAIDProperties.getProperty("LOGFILE_PATH"), GeoCodingAIDProperties.getProperty("LOGFILE_NAME"));
    }

    @Override
    protected String getLoggerName() {
	return LOGGER_NAME;
    }

    public static synchronized GeocoderAidLogger getInstance() {
	if (instance == null) {
	    try {
		instance = new GeocoderAidLogger();
	    } catch (IOException ex) {
		throw new RuntimeException(ex);
	    }
	}
	return (GeocoderAidLogger) instance;
    }

}
