package com.ko.lct.job.geocoding.logger;

import java.io.IOException;

import com.ko.lct.job.geocoding.GeoCodingProcess;
import com.ko.lct.job.geocoding.utilities.GeoCodingProcessProperties;
import com.ko.lct.job.logger.AbstractLogger;

public class GeocoderLogger extends AbstractLogger {
    public static final String LOGGER_NAME = GeoCodingProcess.class.getName();

    protected GeocoderLogger() throws IOException {
	initLogger(GeoCodingProcessProperties.getProperty("LOGFILE_PATH"), GeoCodingProcessProperties.getProperty("LOGFILE_NAME"));
    }

    @Override
    protected String getLoggerName() {
	return LOGGER_NAME;
    }

    public static synchronized GeocoderLogger getInstance() {
	if (instance == null) {
	    try {
		instance = new GeocoderLogger();
	    } catch (IOException ex) {
		throw new RuntimeException(ex);
	    }
	}
	return (GeocoderLogger) instance;
    }


}
