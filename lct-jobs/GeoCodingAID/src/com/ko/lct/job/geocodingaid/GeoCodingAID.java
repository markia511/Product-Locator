package com.ko.lct.job.geocodingaid;

import java.security.AccessController;
import java.security.PrivilegedAction;

import com.ko.lct.job.geocodingaid.logger.GeocoderAidLogger;
import com.ko.lct.job.geocodingaid.managment.GeocodeManager;
import com.ko.lct.job.geocodingaid.managment.ParseGeocodedManager;
import com.ko.lct.job.geocodingaid.managment.ParseManager;
import com.ko.lct.job.geocodingaid.managment.UnloadManager;
import com.ko.lct.job.geocodingaid.managment.UpdateAddressManager;

public class GeoCodingAID {

    private static final GeocoderAidLogger logger = GeocoderAidLogger.getInstance();

    public static final String FILE_SEPARATOR = AccessController
	    .doPrivileged(new PrivilegedAction<String>() {
		@Override
		public String run() {
		    return System.getProperty("file.separator");
		}
	    });

    /*
     * private static final String lineSeparator = AccessController .doPrivileged(new PrivilegedAction<String>() {
     * 
     * @Override public String run() { return System.getProperty("line.separator"); } });
     */

    public static void main(String args[]) {

	try {
	    logger.logEntering(GeoCodingAID.class.getName(), "main", args);
	    if (args.length > 0 && "loadgeocoded".equalsIgnoreCase(args[0])) {
		if (args.length == 1) {
		    logger.logError("USAGE: java com.ko.lct.job.geocodingaid.GeoCodingAID loadgeocoded filename.gz");
		}
		else {
		    ParseGeocodedManager.parse(args[1]);
		}
	    }
	    else {

		ParseManager.parse();
		
		UpdateAddressManager.update();

		GeocodeManager geocodeManager = new GeocodeManager();
		geocodeManager.geocode();

		UnloadManager.unload();
	    }
	    logger.logExiting(GeoCodingAID.class.getName(), "main");
	} catch (Exception ex) {
	    ex.printStackTrace();
	    logger.logError(ex);
	    System.exit(-1);
	}
    }

}
