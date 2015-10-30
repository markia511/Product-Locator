package com.ko.lct.job.geocoding.geocoder;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ko.lct.job.common.businessobjects.ConvertedStepEnum;
import com.ko.lct.job.common.businessobjects.GeocodeLevelEnum;
import com.ko.lct.job.common.businessobjects.GeocodeRequest;
import com.ko.lct.job.common.businessobjects.GeocodeResponse;
import com.ko.lct.job.common.businessobjects.GeocodeStatus;
import com.ko.lct.job.common.businessobjects.GeocodeString;
import com.ko.lct.job.geocoding.utilities.GeocodingConstants;
import com.ko.lct.job.logger.AbstractLogger;

public class Geocoder extends AbstractGeocoder {

    private static final AbstractLogger logger = AbstractLogger.getInstance();

    /* counters of algorithms for statistics */
    private Map<ConvertedStepEnum, Integer> mapSuccessCount = new HashMap<ConvertedStepEnum, Integer>(ConvertedStepEnum.values().length);

    private Map<ConvertedStepEnum, Integer> mapFailureCount = new HashMap<ConvertedStepEnum, Integer>(ConvertedStepEnum.values().length);

    private Map<ConvertedStepEnum, Integer> mapSuccessRouteCount = new HashMap<ConvertedStepEnum, Integer>(ConvertedStepEnum.values().length);

    private static Map<String, String> mapZipCityVirginIslands = new HashMap<String, String>();

    private int limitGeocode;

    static {
	mapZipCityVirginIslands.put("00801", "ST THOMAS");
	mapZipCityVirginIslands.put("00802", "ST THOMAS");
	mapZipCityVirginIslands.put("00803", "ST THOMAS");
	mapZipCityVirginIslands.put("00804", "ST THOMAS");
	mapZipCityVirginIslands.put("00805", "ST THOMAS");
	mapZipCityVirginIslands.put("00820", "CHRISTIANSTED");
	mapZipCityVirginIslands.put("00821", "CHRISTIANSTED");
	mapZipCityVirginIslands.put("00822", "CHRISTIANSTED");
	mapZipCityVirginIslands.put("00823", "CHRISTIANSTED");
	mapZipCityVirginIslands.put("00824", "CHRISTIANSTED");
	mapZipCityVirginIslands.put("00830", "ST JOHN");
	mapZipCityVirginIslands.put("00831", "ST JOHN");
	mapZipCityVirginIslands.put("00840", "FREDERIKSTED");
	mapZipCityVirginIslands.put("00841", "FREDERIKSTED");
	mapZipCityVirginIslands.put("00850", "KINGSHILL");
	mapZipCityVirginIslands.put("00851", "KINGSHILL");
    }

    public Geocoder(int limitGeocode, String googleClientId, String googlePrivateKey) {
	super(googleClientId, googlePrivateKey);
	this.limitGeocode = limitGeocode;
	initStatisticMapCount(mapSuccessCount);
	initStatisticMapCount(mapFailureCount);
	initStatisticMapCount(mapSuccessRouteCount);
    }

    private static void initStatisticMapCount(Map<ConvertedStepEnum, Integer> map) {
	for (ConvertedStepEnum step : ConvertedStepEnum.values()) {
	    map.put(step, Integer.valueOf(0));
	}
    }

    private static void increaseStatisticCount(Map<ConvertedStepEnum, Integer> map, ConvertedStepEnum step) {
	map.put(step, Integer.valueOf(map.get(step).intValue() + 1));
    }

    public void geocode(GeocodeRequest request, int count)
	    throws XPathExpressionException, IOException, ParserConfigurationException,
	    SAXException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException {
	if (request.getPostalCode() != null) {
	    request.setPostalCode(getCorrectPostalCode(request.getPostalCode()));
	    if (isAddressWithStreetNumber(request.getAddressLine1())) {
		request.setAddressLineWithStreetNumber(true);
	    }
	    if (GeocodingConstants.VIRGIN_ISLANDS_STATE.equals(request.getState())) {
		String correctCity = mapZipCityVirginIslands.get(request.getPostalCode());
		if (correctCity != null) {
		    request.setCity(correctCity);
		}
	    }
	    geocode(request, null, count, FIRST_TIME, ConvertedStepEnum.ZERO_STEP);
	} else {
	    request.setGeocodeLevel(GeocodeLevelEnum.UNDEFINED);
	}
    }

    private void geocode(GeocodeRequest request, String searchAddress, int count, ConvertedStepEnum step)
	    throws XPathExpressionException, IOException, ParserConfigurationException,
	    SAXException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException {
	geocode(request, searchAddress, count, FIRST_TIME, step);
    }

    private void geocode(GeocodeRequest request, String searchAddress, int count, int times,
	    ConvertedStepEnum step) throws IOException, XPathExpressionException,
	    ParserConfigurationException, SAXException, InterruptedException, InvalidKeyException,
	    NoSuchAlgorithmException {
	if (searchAddress == null) {
	    searchAddress = request.getFullAddress();
	}
	if (getCountGeocode() >= limitGeocode) {
	    request.setFinishLimitGeocode(true);
	    return;
	}
	GeocodeResponse response = geocodeXML(searchAddress, count, step);

	while (response.getGeocodeStatus() == GeocodeStatus.OVER_QUERY_LIMIT && times < MAX_COUNT_CHECK_OVERLIMIT) {
	    times++;
	    increaseOverQueryLimitCount();
	    Thread.sleep(GEOCODE_TIMEOUT);
	    response = geocodeXML(searchAddress, count, step);
	}

	/*
	 * if(response.getGeocodeStatus() == GeocodeStatus.OVER_QUERY_LIMIT) { // statistic count increaseOverQueryLimitCount(); if(!(step.getStep() >
	 * ConvertedStepEnum.ZERO_STEP.getStep() && request.getGeocodeLevel().getLevel() == GeocodeLevelEnum.UNDEFINED.getLevel() && request.isComplexCityName())) {
	 * if(times < MAX_COUNT_CHECK_OVERLIMIT) { Thread.sleep(GEOCODE_TIMEOUT); geocode(request, searchAddress, count, ++times, step); } else { nextStepCaller(request,
	 * count, step); } } } else
	 */
	if (response.getGeocodeStatus() == GeocodeStatus.OK) {
	    logger.logStepInfo(count, step, "Result Geocoding - " + response.getFormattedAddress() + ". Location: " +
		    response.getLatitude() + ", " + response.getLongitude());
	    boolean isCorrectAddress = false;
	    if (isCorrectPostalCode(request, response)) {
		if (step == ConvertedStepEnum.FIRST_WEIGHTY_WORD
			|| (response.getGeocodeLevel() == GeocodeLevelEnum.ESTABLISHMENT
			&& step != ConvertedStepEnum.OUTLET_IN_TOP_ADDRESS)) {
		    if (isCorrectAddress(request, response)) {
			isCorrectAddress = true;
		    }
		} else {
		    isCorrectAddress = true;
		}
	    } else if (isCorrectAddress(request, response)
		    && step != ConvertedStepEnum.WITHOUT_CITY) {
		isCorrectAddress = true;
	    }
	    if (step.getStep() >= ConvertedStepEnum.ONLY_OUTLET_ADDRESS.getStep()
		    && !isCorrectAddress) {
		isCorrectAddress = isCorrectCity(request, response);
	    }
	    if (step == ConvertedStepEnum.OUTLET_IN_TOP_ADDRESS) {
		isCorrectAddress = isCorrectEstablishment(request, response);
	    }
	    if (isHigherResponseGeocodeLevel(request, response, step)
		    && isCorrectAddress) {
		if (response.getGeocodeLevel().getLevel() > GeocodeLevelEnum.ROUTE.getLevel()) {
		    /* statistic count */
		    increaseStatisticCount(mapSuccessCount, step);
		    if (step.getStep() == ConvertedStepEnum.CHANGE_ADDRESS_LINE.getStep()) {
			changeRequestAddressLine(request);
		    }
		} else if (response.getGeocodeLevel().getLevel() == GeocodeLevelEnum.ROUTE.getLevel()
			&& request.getGeocodeLevel().getLevel() < GeocodeLevelEnum.ROUTE.getLevel()) {
		    /* statistic count */
		    increaseStatisticCount(mapSuccessRouteCount, step);
		    if (step.getStep() == ConvertedStepEnum.CHANGE_ADDRESS_LINE.getStep()) {
			changeRequestAddressLine(request);
		    }
		}
		request.setGeocodeLevel(response.getGeocodeLevel());
		request.setConvertedStepEnum(step);
		request.setResponse(response);
	    } else {
		/* statistic count */
		increaseStatisticCount(mapFailureCount, step);
	    }
	    if (!(isSuccessGeocoded(request, response, step) && isCorrectAddress)) {
		nextStepCaller(request, count, step);
	    }
	} else if (response.getGeocodeStatus() == GeocodeStatus.ZERO_RESULTS) {
	    nextStepCaller(request, count, step);
	} else if (response.getGeocodeStatus() == GeocodeStatus.OVER_QUERY_LIMIT) {
	    request.setOverQueryLimit(true);
	}
    }

    private GeocodeResponse geocodeXML(String searchAddress, int count, ConvertedStepEnum step)
	    throws IOException, XPathExpressionException, InvalidKeyException, NoSuchAlgorithmException {

	Document geocoderResultDocument = getGeocoderResultDocument(searchAddress);

	// extract the result
	NodeList resultNodeList = null;

	GeocodeResponse response = new GeocodeResponse();

	if (geocoderResultDocument != null) {

	    logger.logStepInfo(count, step, "Geocoding... " + searchAddress);

	    resultNodeList = getResultNodeList(GEOCODE_STATUS_PATH, geocoderResultDocument);
	    for (int i = 0; i < resultNodeList.getLength(); ++i) {
		response.setGeocodeStatus(GeocodeStatus.valueOf(resultNodeList.item(i).getTextContent()));
	    }

	    logger.logStepInfo(count, step, "Geocode status - " + response.getGeocodeStatus());

	    if (response.getGeocodeStatus() == GeocodeStatus.OK) {

		resultNodeList = getResultNodeList(FORMATTED_ADDRESS_PATH, geocoderResultDocument);
		for (int i = 0; i < resultNodeList.getLength() && response.getFormattedAddress() == null; ++i) {
		    response.setFormattedAddress(resultNodeList.item(i).getTextContent());
		}

		resultNodeList = getResultNodeList(CITY_PATH, geocoderResultDocument);
		for (int i = 0; i < resultNodeList.getLength() && response.getCity() == null; ++i) {
		    response.setCity(resultNodeList.item(i).getTextContent());
		}
		if (response.getCity() == null) {
		    resultNodeList = getResultNodeList(CITY2_PATH, geocoderResultDocument);
		    for (int i = 0; i < resultNodeList.getLength() && response.getCity() == null; ++i) {
			response.setCity(resultNodeList.item(i).getTextContent());
		    }
		}
		if (response.getCity() == null) {
		    resultNodeList = getResultNodeList(CITY3_PATH, geocoderResultDocument);
		    for (int i = 0; i < resultNodeList.getLength() && response.getCity() == null; ++i) {
			response.setCity(resultNodeList.item(i).getTextContent());
		    }
		}

		resultNodeList = getResultNodeList(STATE_PATH, geocoderResultDocument);
		for (int i = 0; i < resultNodeList.getLength() && response.getState() == null; ++i) {
		    response.setState(resultNodeList.item(i).getTextContent());
		}

		resultNodeList = getResultNodeList(POSTAL_CODE_PATH, geocoderResultDocument);
		for (int i = 0; i < resultNodeList.getLength() && response.getPostalCode() == null; ++i) {
		    response.setPostalCode(resultNodeList.item(i).getTextContent());
		}

		resultNodeList = getResultNodeList(LOCATION_PATH, geocoderResultDocument);
		for (int i = 0; i < resultNodeList.getLength() && (response.getLatitude() == 0 || response.getLongitude() == 0); ++i) {
		    Node node = resultNodeList.item(i);
		    if ("lat".equals(node.getNodeName()))
			response.setLatitude(Float.parseFloat(node.getTextContent()));
		    if ("lng".equals(node.getNodeName()))
			response.setLongitude(Float.parseFloat(node.getTextContent()));
		}

		if (step.getStep() < ConvertedStepEnum.WITHOUT_ADDRESS_LINE.getStep()) {

		    resultNodeList = getResultNodeList(STREE_NUMBER_PATH, geocoderResultDocument);
		    for (int i = 0; i < resultNodeList.getLength() && response.getStreetNumber() == null; ++i) {
			response.setStreetNumber(resultNodeList.item(i).getTextContent());
			response.setGeocodeLevel(GeocodeLevelEnum.STREET_NUMBER);
		    }

		    resultNodeList = getResultNodeList(ESTABLISHMENT_AND_INTEREST_PATH, geocoderResultDocument);
		    for (int i = 0; i < resultNodeList.getLength() && response.getEstablishment() == null; ++i) {
			if (response.getGeocodeLevel().getLevel() == GeocodeLevelEnum.STREET_NUMBER.getLevel()
				|| step == ConvertedStepEnum.OUTLET_IN_TOP_ADDRESS
				|| step == ConvertedStepEnum.ONLY_OUTLET_ADDRESS) {
			    response.setGeocodeLevel(GeocodeLevelEnum.ESTABLISHMENT);
			} else {
			    response.setEstablishmentAddress(true);
			}
			response.setEstablishment(resultNodeList.item(i).getTextContent());
		    }
		    if (resultNodeList.getLength() == 0) {
			resultNodeList = getResultNodeList(ESTABLISHMENT_PATH, geocoderResultDocument);
			for (int i = 0; i < resultNodeList.getLength() && response.getEstablishment() == null; ++i) {
			    if (response.getGeocodeLevel().getLevel() < GeocodeLevelEnum.STREET_NUMBER.getLevel()) {
				response.setGeocodeLevel(GeocodeLevelEnum.ESTABLISHMENT);
			    } else {
				response.setEstablishmentAddress(true);
			    }
			    response.setEstablishment(resultNodeList.item(i).getTextContent());
			}
		    }

		    resultNodeList = getResultNodeList(ROUTE_PATH, geocoderResultDocument);
		    for (int i = 0; i < resultNodeList.getLength() && response.getRoute() == null; ++i) {
			response.setRoute(resultNodeList.item(i).getTextContent());
			if (response.getGeocodeLevel().getLevel() < GeocodeLevelEnum.STREET_NUMBER.getLevel()) {
			    response.setGeocodeLevel(GeocodeLevelEnum.ROUTE);
			}
		    }
		}
		if (response.getGeocodeLevel().getLevel() < GeocodeLevelEnum.ROUTE.getLevel()
			&& response.getPostalCode() != null) {
		    response.setGeocodeLevel(GeocodeLevelEnum.POSTAL_CODE);
		}
		if (response.getGeocodeLevel().getLevel() < GeocodeLevelEnum.POSTAL_CODE.getLevel()
			&& (response.getCity() != null
			|| (response.getState() != null
			&& (response.getFormattedAddress().contains(GeocodingConstants.VIRGIN_ISLANDS)
			|| response.getFormattedAddress().contains(GeocodingConstants.VIRGIN_ISLANDS_STATE))))) {
		    response.setGeocodeLevel(GeocodeLevelEnum.CITY);
		}
	    }
	}
	return response;
    }

    private void nextStepCaller(GeocodeRequest request, int count, ConvertedStepEnum step)
	    throws XPathExpressionException, IOException, ParserConfigurationException, SAXException,
	    InterruptedException, InvalidKeyException, NoSuchAlgorithmException {
	if (!request.isConverted()) {
	    request.setAddressLine1Converted(new GeocodeString(request.getAddressLine1()));
	    request.setOutletNameConverted(new GeocodeString(request.getOutletName()));
	    request.setConverted(true);
	}
	if (step.getStep() <= ConvertedStepEnum.SEPARATE_FIRST_DIGIT_FROM_LETTER.getStep()
		&& !request.isAddressLineWithStreetNumber()
		&& isAddressWithStreetNumber(request.getAddressLine1())) {
	    request.setAddressLineWithStreetNumber(true);
	}
	if ((request.getAddressLine1() == null
		&& request.getAddressLine2() == null
		&& step.getStep() < ConvertedStepEnum.WITHOUT_ADDRESS_LINE.getStep())
		|| (GeocodingConstants.VIRGIN_ISLANDS_STATE.equals(request.getState())
			&& step.getStep() >= ConvertedStepEnum.SEPARATE_FIRST_DIGIT_FROM_LETTER.getStep()
			&& step.getStep() < ConvertedStepEnum.ONLY_OUTLET_ADDRESS.getStep())
		|| (GeocodingConstants.CANADA_SHORT_NAME.equals(request.getCountry())
			&& step.getStep() < ConvertedStepEnum.ONLY_OUTLET_ADDRESS.getStep()
			&& step.getStep() >= ConvertedStepEnum.WITHOUT_CITY.getStep())) {
	    step = ConvertedStepEnum.ONLY_OUTLET_ADDRESS;
	}
	switch (step) {
	case ZERO_STEP:
	    stepSeparateFirstDigit(request, count);
	    break;
	case SEPARATE_FIRST_DIGIT_FROM_LETTER:
	    stepSplitComplexCityName(request, count);
	    break;
	case SPLIT_COMPLEX_CITY_NAME:
	    stepChangeAddressLine(request, count);
	    break;
	case CHANGE_ADDRESS_LINE:
	    stepAddressFirstWeightyWords(request, count);
	    break;
	case FIRST_WEIGHTY_WORD:
	    stepAddressWithoutPostalCode(request, count);
	    break;
	case WITHOUT_POSTAL_CODE:
	    stepAddressWithoutCity(request, count);
	    break;
	case WITHOUT_CITY:
	    stepAddressWithOutletInTop(request, count);
	    break;
	case OUTLET_IN_TOP_ADDRESS:
	    stepAddressOnlyOutlet(request, count);
	    break;
	case ONLY_OUTLET_ADDRESS:
	    stepWithoutAddressLine(request, count);
	    break;
	default:
	    break;
	}
    }

    private static boolean isSuccessGeocoded(GeocodeRequest request, GeocodeResponse response, ConvertedStepEnum step) {
	if (response != null
		&& (response.getGeocodeLevel().getLevel() > GeocodeLevelEnum.ROUTE.getLevel()
		|| (response.getGeocodeLevel().getLevel() >= GeocodeLevelEnum.CITY.getLevel()
		&& step.getStep() >= ConvertedStepEnum.ONLY_OUTLET_ADDRESS.getStep()))) {
	    return true;
	}
	return false;
    }

    private static boolean isHigherResponseGeocodeLevel(GeocodeRequest request, GeocodeResponse response, ConvertedStepEnum step) {
	if (response != null
		&& response.getGeocodeLevel().getLevel() > request.getGeocodeLevel().getLevel()
		&& (response.getGeocodeLevel().getLevel() >= GeocodeLevelEnum.ROUTE.getLevel()
		|| (response.getGeocodeLevel().getLevel() > GeocodeLevelEnum.UNDEFINED.getLevel()
		&& (step.getStep() >= ConvertedStepEnum.ONLY_OUTLET_ADDRESS.getStep()
		|| (request.getAddressLine1() == null && request.getAddressLine2() == null))))) {
	    return true;
	}
	return false;
    }

    private static boolean isCorrectPostalCode(GeocodeRequest request, GeocodeResponse response) {
	if (response != null
		&& response.getPostalCode() != null
		&& request.getState() != null
		&& (response.getPostalCode().toLowerCase().contains(request.getPostalCode().toLowerCase().trim())
		|| request.getPostalCode().toLowerCase().contains(response.getPostalCode().toLowerCase().trim()))
		&& response.getFormattedAddress().toLowerCase().contains(request.getState().toLowerCase())) {
	    return true;
	}
	return false;
    }

    private static boolean isCorrectAddress(GeocodeRequest request, GeocodeResponse response) {
	if (response.getGeocodeLevel().getLevel() >= GeocodeLevelEnum.ROUTE.getLevel()
		&& isMatcherAddress(request, response)) {
	    /*
	     * TODO: what do you do if Geocode Level = Establishment (not Point Of Interest)
	     */
	    // if(response.getGeocodeLevel().getLevel() >
	    // GeocodeLevelEnum.ROUTE.getLevel()) {
	    if (response.getStreetNumber() != null) {
		request.setAddressLineWithStreetNumber(true);
	    }
	    return true;
	}
	return false;
    }

    private static boolean isCorrectEstablishment(GeocodeRequest request, GeocodeResponse response) {
	if (response != null
		&& response.getEstablishment() != null) {
	    String mainEstablishmentWord = getMainWord(response.getEstablishment());
	    if (request.getOutletName().toLowerCase().contains(mainEstablishmentWord.toLowerCase())) {
		return true;
	    }
	}
	return false;
    }

    private static boolean isMatchCity(String requestCity, String responseCity) {
	requestCity = requestCity.toLowerCase();
	responseCity = responseCity.toLowerCase();
	if (requestCity.equals(responseCity)
		|| requestCity.contains(responseCity)) {
	    return true;
	} else if (requestCity.contains("saint ")) {
	    if (responseCity.contains("st ")) {
		return requestCity.equals(responseCity.replace("st ", "saint "));
	    } else if (responseCity.contains("st. ")) {
		return requestCity.equals(responseCity.replace("st. ", "saint "));
	    } else {
		return false;
	    }
	} else {
	    return false;
	}
    }

    private static boolean isMatcherAddress(GeocodeRequest request, GeocodeResponse response) {
	if (response.getFormattedAddress() != null
		&& request.getAddressLine1() != null
		&& request.getAddressLine1().trim().length() > 0) {
	    if (response.getFormattedAddress().toLowerCase()
		    .contains(request.getCity().toLowerCase().trim())
		    && response.getFormattedAddress().toLowerCase()
			    .contains(request.getAddressLine1().toLowerCase().trim())) {
		return true;
	    } else if (response.getCity() != null
		    && request.getCity() != null
		    && request.getState() != null
		    && (request.getState().equalsIgnoreCase(response.getState())
		    /* TODO: Virgin Islands Hardcode */
		    || (request.getState().equals(GeocodingConstants.VIRGIN_ISLANDS_STATE)
		    && (response.getFormattedAddress().contains(GeocodingConstants.VIRGIN_ISLANDS_STATE)
		    || response.getFormattedAddress().contains(GeocodingConstants.VIRGIN_ISLANDS))))) {

		String mainResponseRoute = getMainWord(response.getRoute());
		/*
		 * TODO: maybe remove equal Establishment, because it's very strange logic
		 */
		if (mainResponseRoute == null
			|| ((response.getGeocodeLevel() == GeocodeLevelEnum.ESTABLISHMENT
			|| response.isEstablishmentAddress())
			&& !request.isAddressLineWithStreetNumber())) {
		    mainResponseRoute = getMainWord(response.getEstablishment());
		}

		if (mainResponseRoute != null
			&& request.getAddressLine1().toLowerCase().contains(mainResponseRoute.toLowerCase())
			&& ((response.getStreetNumber() != null
			&& request.getAddressLine1().toLowerCase().contains(response.getStreetNumber()))
			/*
			 * TODO: maybe remove equal Establishment, because it's very strange logic
			 */
			|| ((response.getGeocodeLevel() == GeocodeLevelEnum.ESTABLISHMENT || response.isEstablishmentAddress())
			&& isCorrectCity(request, response)))) {
		    return true;
		} else if (response.getGeocodeLevel() == GeocodeLevelEnum.ESTABLISHMENT
			|| response.isEstablishmentAddress()) {
		    mainResponseRoute = getMainWord(request.getOutletName());
		    if (mainResponseRoute != null
			    && response.getFormattedAddress().toLowerCase().contains(mainResponseRoute.toLowerCase())
			    && isCorrectCity(request, response)) {
			return true;
		    }
		}
		if (
		/* TODO: maybe remove? */
		// (request.getGeocodeLevel().getLevel() ==
		// GeocodeLevelEnum.ROUTE.getLevel()
		// && !request.isAddressLineWithStreetNumber())
		// ||
		(response.getGeocodeLevel().getLevel() == GeocodeLevelEnum.ROUTE.getLevel()
		&& !isCorrectCity(request, response))) {
		    return false;
		}

		String requestAddress = request.getAddressLine1().toLowerCase().trim();
		String responseAddress = (response.getStreetNumber() == null
			? "" : (response.getStreetNumber() + " ") +
				response.getRoute()).toLowerCase();
		if (responseAddress.length() == 0) {
		    responseAddress =
			    response.getFormattedAddress().toLowerCase().split(DELIMETER_FORMATTED_ADDRESS)[0];
		}
		if (responseAddress.length() > 0) {

		    responseAddress = getImprovingResponseAddress(responseAddress, requestAddress,
			    GeocodingConstants.CANADA_SHORT_NAME.equals(request.getCountry()));
		    requestAddress = getImprovingRequestAddress(responseAddress, requestAddress);

		    if (isEqualAddress(requestAddress, responseAddress)
			    || isEqualAddress(responseAddress, requestAddress)) {
			return true;
		    }
		    /* TODO: maybe remove if unnecessary */
		    int indexReqAmp = requestAddress.indexOf(DELIMETER_CROSSING_STREETS);
		    int indexResAmp = responseAddress.indexOf(DELIMETER_CROSSING_STREETS);
		    if (indexReqAmp != -1
			    && indexResAmp != -1) {
			String requestAddress1 = indexReqAmp > 0 ? requestAddress.substring(0, indexReqAmp - 1).toLowerCase().trim() : "";
			String requestAddress2 = requestAddress.substring(indexReqAmp + 1).toLowerCase().trim();
			String responseAddress1 = indexResAmp > 0 ? responseAddress.substring(0, indexResAmp - 1).toLowerCase().trim() : "";
			String responseAddress2 = responseAddress.substring(indexResAmp + 1).toLowerCase().trim();
			if ((response.getFormattedAddress().toLowerCase().contains(requestAddress1)
				&& response.getFormattedAddress().toLowerCase().contains(requestAddress2))
				|| (isEqualAddress(requestAddress1, responseAddress2)
				&& isEqualAddress(requestAddress2, responseAddress1))) {
			    System.out.println("!!!SUCCESS CHANGING ROUTE WITH &!!!");
			    return true;
			}
		    }

		}

	    }
	}
	return false;
    }

    private static String getImprovingResponseAddress(String responseAddress, String requestAddress, boolean isCanada) {
	if (requestAddress.contains(" trl")) {
	    responseAddress = responseAddress.replace(" trail", " trl");
	}
	if (requestAddress.contains(" plz")) {
	    responseAddress = responseAddress.replace(" plaza", " plz");
	}
	responseAddress = responseAddress.replace(" loop", " ");
	if (responseAddress.contains("boulevard")) {
	    if (requestAddress.contains(" blvd")) {
		responseAddress = responseAddress.replace("boulevard", "blvd");
	    } else if (requestAddress.contains(" bl")) {
		responseAddress = responseAddress.replace("boulevard", "bl");
	    }
	}
	if (responseAddress.contains(" u.s. ")) {
	    if (requestAddress.contains(" us ")) {
		responseAddress = responseAddress.replace(" u.s. ", " us ");
	    } else if (requestAddress.contains(" hwy ")) {
		responseAddress = responseAddress.replace(" u.s. ", " hwy ");
	    } else if (requestAddress.contains(" highway ")) {
		responseAddress = responseAddress.replace(" u.s. ", " highway ");
	    }
	}
	if (responseAddress.contains("highway")
		&& requestAddress.contains("hwy")) {
	    responseAddress = responseAddress.replace("highway", "hwy");
	}
	if (responseAddress.contains(" state route")
		&& requestAddress.contains(" sr")) {
	    responseAddress = responseAddress.replace(" state route", " sr");
	}
	if (responseAddress.contains(" west ")
		&& requestAddress.contains(" w ")) {
	    responseAddress = responseAddress.replace(" west ", " w ");
	}
	if (responseAddress.contains("parkway")
		&& requestAddress.contains("pkwy")) {
	    responseAddress = responseAddress.replace("parkway", "pkwy");
	}
	if (responseAddress.contains("saint")
		&& requestAddress.contains(" st ")) {
	    responseAddress = requestAddress.replace(" saint ", " st ");
	}
	if (isCanada) {
	    responseAddress = getImprovedFrenchWord(responseAddress, requestAddress);
	}
	return responseAddress;
    }

    private static String getImproveCity(String responseCity, String requestCity) {
	String resultCity = responseCity;
	if (resultCity.contains("saint")
		&& !requestCity.contains("saint")
		&& requestCity.contains("st")) {
	    resultCity = resultCity.replace("saint", "st");
	}
	if (resultCity.contains("sainte")
		&& !requestCity.contains("sainte")
		&& requestCity.contains("ste")) {
	    resultCity = resultCity.replace("sainte", "ste");
	}
	resultCity = getImprovedFrenchWord(resultCity, requestCity);
	return resultCity;
    }

    private static String getImprovedFrenchWord(String frenchWord, String englishWord) {
	String resultWord = getReplacementWord(frenchWord, englishWord, "à", "a");
	resultWord = getReplacementWord(resultWord, englishWord, "â", "a");
	resultWord = getReplacementWord(resultWord, englishWord, "é", "e");
	resultWord = getReplacementWord(resultWord, englishWord, "è", "e");
	resultWord = getReplacementWord(resultWord, englishWord, "ê", "e");
	resultWord = getReplacementWord(resultWord, englishWord, "ë", "e");
	resultWord = getReplacementWord(resultWord, englishWord, "î", "i");
	resultWord = getReplacementWord(resultWord, englishWord, "ï", "i");
	resultWord = getReplacementWord(resultWord, englishWord, "ô", "o");
	resultWord = getReplacementWord(resultWord, englishWord, "ù", "u");
	resultWord = getReplacementWord(resultWord, englishWord, "û", "u");
	resultWord = getReplacementWord(resultWord, englishWord, "ü", "u");
	resultWord = getReplacementWord(resultWord, englishWord, "ÿ", "y");
	return resultWord;
    }

    private static String getReplacementWord(String replacementWord, String checkedWord,
	    String oldSign, String newSign) {
	if (replacementWord.contains(oldSign)
		&& !checkedWord.contains(oldSign)) {
	    return replacementWord.replace(oldSign, newSign);
	} else {
	    return replacementWord;
	}
    }

    private static String getImprovingRequestAddress(String responseAddress, String requestAddress) {
	if (responseAddress.indexOf(DELIMETER_FORMATTED_ADDRESS) != -1
		&& requestAddress.indexOf(DELIMETER_FORMATTED_ADDRESS) == -1
		&& requestAddress.indexOf(" and ") != -1) {
	    requestAddress = requestAddress.replace(" and ", " " + DELIMETER_FORMATTED_ADDRESS + " ");
	}
	if (requestAddress.contains(" highway ")
		&& responseAddress.contains(" hwy ")) {
	    requestAddress = requestAddress.replace(" highway ", " hwy ");
	}
	if (!responseAddress.contains("'")
		&& requestAddress.contains("'")) {
	    requestAddress = requestAddress.replace("'", "");
	}
	if (requestAddress.contains(" rte ")
		&& responseAddress.contains(" route ")) {
	    requestAddress = requestAddress.replace(" rte ", " route ");
	}
	return requestAddress;
    }

    private static String getMainWord(String sentence) {
	String mainWord = sentence;
	if (sentence != null) {
	    String[] words = sentence.split(" ");
	    if (words.length > 1) {
		/* TODO: hardcode for geocode airport */
		if (sentence.toLowerCase().contains("airport")) {
		    return "airport";
		} else {
		    mainWord = words[0];
		    for (String word : words) {
			if (mainWord.length() < word.length()
				&& !word.equalsIgnoreCase("Boulevard")
				&& !word.equalsIgnoreCase("St")
				&& !word.equalsIgnoreCase("Loop")
				&& !word.equalsIgnoreCase("Trail")
				&& !word.equalsIgnoreCase("Park")) {
			    mainWord = word;
			}
		    }
		}
	    }
	}
	return mainWord;
    }

    private static boolean isEqualAddress(String address1, String address2) {
	String[] routeArr = address1.replace("*", "").split(" ");
	try {
	    if (routeArr.length > 0) {
		String routeRegexp = ANY_SYMBOLS_REGEXP;
		for (int i = 0; i < routeArr.length; i++) {
		    routeRegexp += (routeArr[i] + ANY_SYMBOLS_REGEXP);
		}
		System.out.println(routeRegexp);
		System.out.println(address2);
		Pattern pattern = Pattern.compile(routeRegexp);
		Matcher matcher = pattern.matcher(address2);
		System.out.println("MATCHES - " + matcher.matches());
		if (matcher.matches()) {
		    return true;
		}
	    }
	} catch (PatternSyntaxException ex) {
	    logger.logError(ex.getMessage(), ex);
	}
	return false;
    }

    private static boolean isCorrectCity(GeocodeRequest request, GeocodeResponse response) {
	/* add country equal */
	if (response.getFormattedAddress() != null
		&& request.getCity() != null
		&& request.getState() != null) {
	    if (response.getFormattedAddress().toLowerCase()
		    .contains(request.getCity().toLowerCase().trim())
		    && response.getFormattedAddress().toLowerCase()
			    .contains(request.getState().toLowerCase().trim())) {
		return true;
	    } else if (response.getCity() != null
		    && request.getCountry().equals(GeocodingConstants.CANADA_SHORT_NAME)
		    && response.getFormattedAddress().toLowerCase()
			    .contains(request.getState().toLowerCase().trim())) {
		String requestCity = request.getCity().toLowerCase().trim();
		String improvedFrenchWord = getImproveCity(response.getCity().toLowerCase(), requestCity);
		if (isEqualAddress(requestCity, improvedFrenchWord)
			|| isEqualAddress(improvedFrenchWord, requestCity)) {
		    return true;
		} else {
		    return false;
		}
	    } else if (response.getCity() != null
		    && isMatchCity(request.getCity(), response.getCity())
		    && response.getFormattedAddress().toLowerCase()
			    .contains(request.getState().toLowerCase().trim())) {
		return true;
		/* TODO: Virgin Islands Hardcode */
	    } else if ((response.getCity() != null || response.getState() != null)
		    && GeocodingConstants.VIRGIN_ISLANDS_STATE.equals(request.getState())
		    && (response.getFormattedAddress().contains(GeocodingConstants.VIRGIN_ISLANDS)
		    || response.getFormattedAddress().contains(GeocodingConstants.VIRGIN_ISLANDS_STATE))
		    && isMatchCity(request.getCity(), response.getState())) {
		return true;
	    } else {
		return false;
	    }
	}
	return false;
    }

    public static String getCorrectPostalCode(String postalCode) {
	if (postalCode != null) {
	    if (postalCode.length() < CORRECT_DIGIT_POSTAL_CODE_LENGTH) {
		Matcher matcher = GeocodeString.DIGIT_PATTERN.matcher(postalCode);
		if (matcher.matches()) {
		    for (int i = 0; i < (CORRECT_DIGIT_POSTAL_CODE_LENGTH - postalCode.length()); i++) {
			postalCode = (DEFAULT_POSTAL_CODE_DIGIT + postalCode);
		    }
		}
	    } else if (postalCode.trim().contains(" ")) {
		postalCode = postalCode.trim().split(" ")[0];
	    } else if (postalCode.trim().length() == INCORRECT_DIGIT_PRIVACY_POSTAL_CODE_LENGTH) {
		postalCode = postalCode.substring(0, 5);
	    }
	}
	return postalCode;
    }

    private static boolean isAddressWithStreetNumber(String addressLine) {
	if (addressLine != null) {
	    Matcher matcher = CORRECT_ADDR_LINE_PATTERN.matcher(addressLine);
	    if (matcher.matches()) {
		return true;
	    }
	}
	return false;
    }

    private static boolean isAddressWithoutPackage(String addressLine) {
	if (addressLine != null) {
	    Matcher matcher = ADDR_LINE_WITH_OZ_PATTERN.matcher(addressLine);
	    if (!matcher.matches()) {
		return true;
	    } else {
		return false;
	    }
	} else {
	    return true;
	}
    }

    /* statistic count */
    public Map<ConvertedStepEnum, Integer> getMapSuccessCount() {
	return mapSuccessCount;
    }

    public Map<ConvertedStepEnum, Integer> getMapFailureCount() {
	return mapFailureCount;
    }

    public Map<ConvertedStepEnum, Integer> getMapSuccessRouteCount() {
	return mapSuccessRouteCount;
    }

    private static String getImprovingAddressLine2(String addressLine2) {
	int index = -1;
	if (addressLine2 != null
		&& ((index = addressLine2.toLowerCase().indexOf(VENDOR_LONG_NM)) != -1
		|| (index = addressLine2.toLowerCase().indexOf(VENDOR_SHRT_NM)) != -1)) {
	    return addressLine2.substring(0, index).trim();
	}
	return addressLine2;
    }

    private void stepSeparateFirstDigit(GeocodeRequest convertedRequest, int i)
	    throws XPathExpressionException, IOException, ParserConfigurationException, SAXException,
	    InterruptedException, InvalidKeyException, NoSuchAlgorithmException {
	String searchAddr = convertedRequest.getSeparatedFirstDigitAddress();
	if (searchAddr != null) {
	    convertedRequest.setAddressLine1Converted(new GeocodeString(searchAddr));
	    convertedRequest.setAddressLine1(searchAddr);
	    searchAddr += convertedRequest.getAddressWithoutAddrLine();
	    geocode(convertedRequest, searchAddr, i, ConvertedStepEnum.SEPARATE_FIRST_DIGIT_FROM_LETTER);
	} else {
	    nextStepCaller(convertedRequest, i, ConvertedStepEnum.SEPARATE_FIRST_DIGIT_FROM_LETTER);
	}
    }

    private void stepSplitComplexCityName(GeocodeRequest convertedRequest, int i)
	    throws XPathExpressionException, IOException, ParserConfigurationException, SAXException,
	    InterruptedException, InvalidKeyException, NoSuchAlgorithmException {
	if (convertedRequest.getGeocodeLevel().getLevel() == GeocodeLevelEnum.UNDEFINED.getLevel()
		&& convertedRequest.isComplexCityName()) {
	    geocode(convertedRequest, convertedRequest.getCutComplexCityName(), i, ConvertedStepEnum.SPLIT_COMPLEX_CITY_NAME);
	} else {
	    nextStepCaller(convertedRequest, i, ConvertedStepEnum.SPLIT_COMPLEX_CITY_NAME);
	}
    }

    private void stepChangeAddressLine(GeocodeRequest convertedRequest, int i)
	    throws XPathExpressionException, IOException, ParserConfigurationException, SAXException,
	    InterruptedException, InvalidKeyException, NoSuchAlgorithmException {
	if (!convertedRequest.isAddressLineWithStreetNumber()
		&& isAddressWithStreetNumber(convertedRequest.getAddressLine2())
		&& isAddressWithoutPackage(convertedRequest.getAddressLine2())) {
	    convertedRequest.setAddressLine2(getImprovingAddressLine2(convertedRequest.getAddressLine2()));
	    String searchAddress = convertedRequest.getAddressLine2() + " " + convertedRequest.getAddressWithoutAddrLine();
	    geocode(convertedRequest, searchAddress, i, ConvertedStepEnum.CHANGE_ADDRESS_LINE);
	} else {
	    nextStepCaller(convertedRequest, i, ConvertedStepEnum.CHANGE_ADDRESS_LINE);
	}
    }

    private static void changeRequestAddressLine(GeocodeRequest convertedRequest) {
	convertedRequest.setAddressLineWithStreetNumber(true);
	convertedRequest.setAddressLine1(convertedRequest.getAddressLine2());
	convertedRequest.setAddressLine1Converted(new GeocodeString(convertedRequest.getAddressLine2()));
    }

    private void stepAddressFirstWeightyWords(GeocodeRequest convertedRequest, int i)
	    throws XPathExpressionException, IOException, ParserConfigurationException, SAXException,
	    InterruptedException, InvalidKeyException, NoSuchAlgorithmException {
	String searchAddr = null;
	if (convertedRequest.getGeocodeLevel().getLevel() < GeocodeLevelEnum.ROUTE.getLevel()) {
	    searchAddr = convertedRequest.getAddressFirstWeightyWord();
	}
	if (searchAddr != null) {
	    geocode(convertedRequest, searchAddr, i, ConvertedStepEnum.FIRST_WEIGHTY_WORD);
	} else {
	    nextStepCaller(convertedRequest, i, ConvertedStepEnum.FIRST_WEIGHTY_WORD);
	}
    }

    private void stepAddressWithoutPostalCode(GeocodeRequest convertedRequest, int i)
	    throws XPathExpressionException, IOException, ParserConfigurationException, SAXException,
	    InterruptedException, InvalidKeyException, NoSuchAlgorithmException {
	if (convertedRequest.getGeocodeLevel().getLevel() <= GeocodeLevelEnum.ROUTE.getLevel()
		&& convertedRequest.isAddressLineWithStreetNumber()) {
	    geocode(convertedRequest, convertedRequest.getAddressWithoutPostalCode(), i, ConvertedStepEnum.WITHOUT_POSTAL_CODE);
	} else {
	    nextStepCaller(convertedRequest, i, ConvertedStepEnum.WITHOUT_POSTAL_CODE);
	}
    }

    private void stepAddressWithoutCity(GeocodeRequest convertedRequest, int i)
	    throws XPathExpressionException, IOException, ParserConfigurationException, SAXException,
	    InterruptedException, InvalidKeyException, NoSuchAlgorithmException {
	/* TODO: necessary? */
	if (convertedRequest.getGeocodeLevel().getLevel() < GeocodeLevelEnum.ROUTE.getLevel()
		&& convertedRequest.isAddressLineWithStreetNumber()
		&& convertedRequest.isNeedGeocodingWithoutCity()) {
	    geocode(convertedRequest, convertedRequest.getAddressWithoutCity(), i, ConvertedStepEnum.WITHOUT_CITY);
	} else {
	    nextStepCaller(convertedRequest, i, ConvertedStepEnum.WITHOUT_CITY);
	}
    }

    private void stepAddressWithOutletInTop(GeocodeRequest convertedRequest, int i)
	    throws XPathExpressionException, IOException, ParserConfigurationException, SAXException,
	    InterruptedException, InvalidKeyException, NoSuchAlgorithmException {
	String searchAddr = convertedRequest.getAddressWithOutletInTop();
	if (searchAddr != null
		&& convertedRequest.getGeocodeLevel().getLevel() < GeocodeLevelEnum.STREET_NUMBER.getLevel()
		&& convertedRequest.isNeedGeocodingWithOutletInTop()) {
	    geocode(convertedRequest, searchAddr, i, ConvertedStepEnum.OUTLET_IN_TOP_ADDRESS);
	} else {
	    nextStepCaller(convertedRequest, i, ConvertedStepEnum.OUTLET_IN_TOP_ADDRESS);
	}
    }

    private void stepAddressOnlyOutlet(GeocodeRequest convertedRequest, int i)
	    throws XPathExpressionException, IOException, ParserConfigurationException, SAXException,
	    InterruptedException, InvalidKeyException, NoSuchAlgorithmException {
	if (convertedRequest.getGeocodeLevel().getLevel() < GeocodeLevelEnum.ROUTE.getLevel()) {
	    String searchAddr = convertedRequest.getAddressWithOnlyOutlet();
	    if (searchAddr != null) {
		geocode(convertedRequest, searchAddr, i, ConvertedStepEnum.ONLY_OUTLET_ADDRESS);
	    } else {
		nextStepCaller(convertedRequest, i, ConvertedStepEnum.ONLY_OUTLET_ADDRESS);
	    }
	}
    }

    private void stepWithoutAddressLine(GeocodeRequest convertedRequest, int i)
	    throws XPathExpressionException, IOException, ParserConfigurationException, SAXException,
	    InterruptedException, InvalidKeyException, NoSuchAlgorithmException {
	if (convertedRequest.getGeocodeLevel().getLevel() == GeocodeLevelEnum.UNDEFINED.getLevel()
		&& convertedRequest.isNeedGeocodingWithoutAddrLine()) {
	    String searchAddr = convertedRequest.getAddressWithoutAddrLine();
	    if (searchAddr != null) {
		geocode(convertedRequest, searchAddr, i, ConvertedStepEnum.WITHOUT_ADDRESS_LINE);
	    }
	}
    }

    /*
     * private static void logStepInfo(int count, ConvertedStepEnum step, String message) { logger.info(count + (step.getStep() == 0 ? "" : (" Step " + step.getStep())) +
     * " " + message); }
     */

    // public static void main(String[] args) throws InvalidKeyException,
    // XPathExpressionException, NoSuchAlgorithmException, IOException,
    // ParserConfigurationException, SAXException, InterruptedException {
    // GeocodeRequest geocodeRequest = new GeocodeRequest();
    // geocodeRequest.setAddressId(null); geocodeRequest.setCountry("CA");
    // geocodeRequest.setState("VI"); geocodeRequest.setCity("C STED SAINT CROI");
    // geocodeRequest.setPostalCode("00820");
    // geocodeRequest.setAddressLine1("1 HOPE EST");
    // geocodeRequest.setAddressLine2(null);
    // geocodeRequest.setOutletName("Walmart"); (new
    // Geocoder(100)).geocode(geocodeRequest,1); }

}
