package com.ko.lct.job.geocoding.geocoder;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ko.lct.job.common.businessobjects.GeocodeResponse;
import com.ko.lct.job.common.businessobjects.GeocodeStatus;
import com.ko.lct.job.common.businessobjects.Location;
import com.ko.lct.job.geocoding.utilities.GeocodingConstants;
import com.ko.lct.job.logger.AbstractLogger;

public class ZipGeocoder extends AbstractGeocoder {
    
    private static final AbstractLogger logger = AbstractLogger.getInstance();
    
    public ZipGeocoder(String googleClientId, String googlePrivateKey) {
	super(googleClientId, googlePrivateKey);
    }

    public Location geocode(String zip, int count) throws IOException,
	    XPathExpressionException, ParserConfigurationException, SAXException, InterruptedException,
	    InvalidKeyException, NoSuchAlgorithmException {
	return geocode(zip, count, FIRST_TIME);
    }

    private Location geocode(String zip, int count, int times) throws IOException,
	    XPathExpressionException, ParserConfigurationException, SAXException, InterruptedException,
	    InvalidKeyException, NoSuchAlgorithmException {

	Location location = null;
	GeocodeResponse response = geocodeXML(zip, count);

	if (response.getGeocodeStatus() == GeocodeStatus.OVER_QUERY_LIMIT) {
	    /* statistic count */
	    increaseOverQueryLimitCount();
	    if (times < MAX_COUNT_CHECK_OVERLIMIT) {
		Thread.sleep(GEOCODE_TIMEOUT);
		location = geocode(zip, count, ++times);
	    }
	} else if (response.getGeocodeStatus() == GeocodeStatus.OK) {
	    logger.logInfo(count, "Result Geocoding - Location: " +
		    response.getLatitude() + ", " + response.getLongitude());
	    if (zip.equalsIgnoreCase(response.getPostalCode())
		    && response.getLatitude() != 0
		    && response.getLongitude() != 0
		    && (GeocodingConstants.US_SHORT_NAME.equals(response.getCountry())
		    || GeocodingConstants.CANADA_SHORT_NAME.equals(response.getCountry()))) {
		location = new Location();
		location.setLatitude(response.getLatitude());
		location.setLongitude(response.getLongitude());
	    }
	}
	return location;
    }

    private GeocodeResponse geocodeXML(String zip, int count)
	    throws IOException, XPathExpressionException, InvalidKeyException, NoSuchAlgorithmException {

	Document geocoderResultDocument = getGeocoderResultDocument(zip);

	// extract the result
	NodeList resultNodeList = null;

	GeocodeResponse response = new GeocodeResponse();
	logger.logInfo(count, "Geocoding zip...");

	resultNodeList = getResultNodeList(GEOCODE_STATUS_PATH, geocoderResultDocument);
	for (int i = 0; i < resultNodeList.getLength(); ++i) {
	    response.setGeocodeStatus(GeocodeStatus.valueOf(resultNodeList.item(i).getTextContent()));
	}

	logger.logInfo(count, "Geocode status - " + response.getGeocodeStatus());

	if (response.getGeocodeStatus() == GeocodeStatus.OK) {

	    resultNodeList = getResultNodeList(POSTAL_CODE_PATH, geocoderResultDocument);
	    for (int i = 0; i < resultNodeList.getLength() && response.getPostalCode() == null; ++i) {
		response.setPostalCode(resultNodeList.item(i).getTextContent());
	    }

	    resultNodeList = getResultNodeList(COUNTRY_PATH, geocoderResultDocument);
	    for (int i = 0; i < resultNodeList.getLength() && response.getCountry() == null; ++i) {
		response.setCountry(resultNodeList.item(i).getTextContent());
	    }

	    resultNodeList = getResultNodeList(LOCATION_PATH, geocoderResultDocument);
	    for (int i = 0; i < resultNodeList.getLength() && (response.getLatitude() == 0 || response.getLongitude() == 0); ++i) {
		Node node = resultNodeList.item(i);
		if ("lat".equals(node.getNodeName()))
		    response.setLatitude(Float.parseFloat(node.getTextContent()));
		if ("lng".equals(node.getNodeName()))
		    response.setLongitude(Float.parseFloat(node.getTextContent()));
	    }
	}
	return response;
    }

}
