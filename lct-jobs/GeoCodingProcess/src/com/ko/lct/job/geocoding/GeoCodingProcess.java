package com.ko.lct.job.geocoding;

import com.ko.lct.job.geocoding.logger.GeocoderLogger;
import com.ko.lct.job.geocoding.managment.GeocodeManager;
import com.ko.lct.job.geocoding.managment.ParseCSVManager;
import com.ko.lct.job.geocoding.managment.ParseCSVNielsonManager;
import com.ko.lct.job.geocoding.managment.ZipGeocodeManager;
import com.ko.lct.job.geocoding.utilities.EncryptionUtilities;

public class GeoCodingProcess {
    
    private static final GeocoderLogger logger = GeocoderLogger.getInstance();
    /*
    private static final String lineSeparator = AccessController
	    .doPrivileged(new PrivilegedAction<String>() {
		@Override
		public String run() {
		    return System.getProperty("line.separator");
		}
	    });
     */

    public static void main(String args[]) {

	try {
	    logger.logEntering(GeoCodingProcess.class.getName(), "main", args);
	    if (args.length < 1) {
		System.out.println("parameters passed: " + args.length);
		displayUsage();
		System.out.println("Exiting with -1");
		System.exit(-1);
	    } else {

		final String option = args[0];

		if ("geocode".equals(option)) {
		    logger.logInfo("Starting geocode process");
		    GeocodeManager geocodeManager = new GeocodeManager();
		    geocodeManager.geocode();
		} else if ("parse".equals(option)) {
		    logger.logInfo("Starting parse process");
		    ParseCSVManager.parse();
		} else if ("parsenielson".equals(option)) {
		    logger.logInfo("Starting nielson parse process");
		    ParseCSVNielsonManager.parse();
		} else if ("zipgeocode".equals(option)) {
		    logger.logInfo("Starting zip geocode process");
		    ZipGeocodeManager.geocode();
		} else if ("Decrypt".equalsIgnoreCase(option)) {
		    if (args.length < 2) {
			displayUsage();
			System.exit(-1);
		    }
		    System.out.println(EncryptionUtilities.decrypt(args[1]));

		} else if ("Encrypt".equalsIgnoreCase(option)) {
		    if (args.length < 2) {
			displayUsage();
			System.exit(-1);
		    }
		    System.out.println(EncryptionUtilities.encrypt(args[1]));
		} else {
		    logger.logInfo("Invalid process named.");
		    displayUsage();
		    System.exit(-1);
		}
	    }
	    logger.logExiting(GeoCodingProcess.class.getName(), "main");
	} catch (Exception ex) {
	    ex.printStackTrace();
	    logger.logError(ex);
	    System.exit(-1);
	}
    }

    private static void displayUsage() {
	System.out.println("Usage:");
	System.out.println("Valid processes names are:");
	System.out.println("parse");
	System.out.println("parsenielson");
	System.out.println("geocode");
	System.out.println("Decrypt <password>");
	System.out.println("Encrypt <password>");
    }
}
