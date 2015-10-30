package com.ko.lct.job.geocoding.geocoder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.ko.lct.job.geocoding.utilities.GeocodingConstants;
import com.ko.lct.job.logger.AbstractLogger;

public abstract class AbstractGeocoder {

    protected static final String GEOCODER_REQUEST_PREFIX_FOR_XML = "http://maps.googleapis.com/maps/api/geocode/xml";
    /* xpath expression for response geocode xml */
    protected static final String LOCATION_PATH = "/GeocodeResponse/result[1]/geometry/location/*";
    protected static final String COUNTRY_PATH = "/GeocodeResponse/result[1]/address_component[type/text()='country']/short_name";
    protected static final String POSTAL_CODE_PATH = "/GeocodeResponse/result[1]/address_component[type/text()='postal_code']/long_name";
    protected static final String CITY_PATH = "/GeocodeResponse/result[1]/address_component[type/text()='locality' or type/text()='sublocality']/long_name";
    protected static final String CITY2_PATH = "/GeocodeResponse/result[1]/address_component[type/text()='administrative_area_level_3' and type/text()='political']/long_name";
    protected static final String CITY3_PATH = "/GeocodeResponse/result[1]/address_component[type/text()='neighborhood' and type/text()='political']/long_name";
    protected static final String ROUTE_PATH = "/GeocodeResponse/result[1]/address_component[type/text()='route']/long_name";
    protected static final String STATE_PATH = "/GeocodeResponse/result[1]/address_component[type/text()='administrative_area_level_1' and type/text()='political']/short_name";
    protected static final String STREE_NUMBER_PATH = "/GeocodeResponse/result[1]/address_component[type/text()='street_number']/long_name";
    protected static final String ESTABLISHMENT_AND_INTEREST_PATH = "/GeocodeResponse/result[1]/address_component[type/text()='establishment' and type/text()='point_of_interest']/long_name";
    protected static final String ESTABLISHMENT_PATH = "/GeocodeResponse/result[1]/address_component[type/text()='establishment']/long_name";
    protected static final String FORMATTED_ADDRESS_PATH = "/GeocodeResponse/result[1]/formatted_address";
    protected static final String GEOCODE_STATUS_PATH = "/GeocodeResponse/status";

    protected static final String ANY_SYMBOLS_REGEXP = "(.*)";
    protected static final Pattern CORRECT_ADDR_LINE_PATTERN = Pattern.compile("([0-9]+)([^0-9]+)(.+)");
    protected static final Pattern ADDR_LINE_WITH_OZ_PATTERN = Pattern.compile("(.*)([0-9]+)((-)|( )|())OZ(()|( (.)+))");
    protected static final String DELIMETER_CROSSING_STREETS = "&";
    protected static final String DELIMETER_FORMATTED_ADDRESS = ",";
    protected static final String VENDOR_LONG_NM = "vendor";
    protected static final String VENDOR_SHRT_NM = "v#";
    protected static final int FIRST_TIME = 1;
    protected static final int MAX_COUNT_CHECK_OVERLIMIT = 3;
    protected static final int GEOCODE_TIMEOUT = 500;
    protected static final int CORRECT_DIGIT_POSTAL_CODE_LENGTH = 5;
    protected static final int INCORRECT_DIGIT_PRIVACY_POSTAL_CODE_LENGTH = 9;
    protected static final String DEFAULT_POSTAL_CODE_DIGIT = "0";

    private /* static */ int TIME_LIMIT = 200; // ms
    
    /* counters of algorithms for statistics */
    private int countGeocode = 0;
    private int overQueryLimitCount = 0;

    // Client Id for Google Maps API for Business
    private String clientId;
    // This variable stores the binary key, which is computed from the string
    // (Base64) key
    private byte[] key;

    // prepare XPath
    private XPath xpath;

    public AbstractGeocoder(String googleClientId, String googlePrivateKey) {

	xpath = XPathFactory.newInstance().newXPath();
	String tmpClientId = googleClientId;
	String signature = googlePrivateKey;

	if (!isEmpty(tmpClientId)
		&& !isEmpty(signature)) {
	    // Convert the key from 'web safe' base 64 to binary
	    // signature = signature.replace('-', '+');
	    // signature = signature.replace('_', '/');
	    this.key = Base64.decodeBase64(signature);
	    this.clientId = tmpClientId;
	    TIME_LIMIT = 150; // 110;
	}
    }

    private String getSignUrl(String path, String query) throws
	    NoSuchAlgorithmException, InvalidKeyException {

	String resource = path + "?" + query + "&client=" + this.clientId;

	// Get an HMAC-SHA1 signing key from the raw key bytes
	SecretKeySpec sha1Key = new SecretKeySpec(key, "HmacSHA1");

	// Get an HMAC-SHA1 Mac instance and initialize it with the
	// HMAC-SHA1 key
	Mac mac = Mac.getInstance("HmacSHA1");
	mac.init(sha1Key);

	// compute the binary signature for the request
	byte[] sigBytes = mac.doFinal(resource.getBytes());

	// base 64 encode the binary signature
	String signature = Base64.encodeBase64URLSafeString(sigBytes);

	// convert the signature to 'web safe' base 64
	// signature = signature.replace('+', '-');
	// signature = signature.replace('/', '_');

	return resource + "&signature=" + signature;
    }

    protected long previousAttemptTime = 0;
    protected Document getGeocoderResultDocument(String searchAddress)
	    throws InvalidKeyException, NoSuchAlgorithmException, IOException {
	AbstractLogger logger = AbstractLogger.getInstance();
	try {	
	    long curTimeTime = System.currentTimeMillis();
	    if (curTimeTime < previousAttemptTime + TIME_LIMIT) {
	    	 long waitInterval = previousAttemptTime + TIME_LIMIT - curTimeTime;
	    	 logger.logInfo("Geocoding too fast - wait: " + Long.toString(waitInterval));
	    	 Thread.sleep(waitInterval);
	    }
	}
	catch(InterruptedException e) {
	    logger.logError(e.getMessage(), e);
	}

	// prepare a URL to the geocoder
	String urlString = GEOCODER_REQUEST_PREFIX_FOR_XML + "?address=" +
		URLEncoder.encode(searchAddress, GeocodingConstants.ENCODE_UTF_8) + "&sensor=false";
	URL url = new URL(urlString);
	if (!isEmpty(this.clientId)) {
	    String signRequest = getSignUrl(url.getPath(), url.getQuery());
	    url = new URL(url.getProtocol() + "://" + url.getHost() + signRequest);
	}

	// prepare an HTTP connection to the geocoder
	HttpURLConnection conn = (HttpURLConnection) url.openConnection();

	Document geocoderResultDocument = null;
	try {
	    // open the connection and get results as InputSource.
	    conn.connect();
	    InputSource geocoderResultInputSource = new InputSource(conn.getInputStream());
	    increaseCountGeocode();
	    // read result and parse into XML Document
	    geocoderResultDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(geocoderResultInputSource);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.logError(e);
	} finally {
	    conn.disconnect();
	}

	previousAttemptTime = System.currentTimeMillis();

	return geocoderResultDocument;
    }

    public int getCountGeocode() {
	return countGeocode;
    }

    public void increaseCountGeocode() {
	countGeocode++;
    }

    public int getOverQueryLimitCount() {
	return overQueryLimitCount;
    }

    public void increaseOverQueryLimitCount() {
	overQueryLimitCount++;
    }

    protected NodeList getResultNodeList(String expression, Document geocoderResultDocument)
	    throws XPathExpressionException {
	return (NodeList) xpath.evaluate(expression, geocoderResultDocument, XPathConstants.NODESET);
    }

    private static boolean isEmpty(String str) {
	return str == null || str.equals("");
    }
}
