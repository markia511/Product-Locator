package com.ko.lct.web.gfruit.repository;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.ko.lct.common.bean.Locations;
import com.ko.lct.common.uri.WsUriTemplates;
import com.ko.lct.common.util.LocatorCrypt;

@Singleton
public class WebServiceLocatorCache {

    private static final Logger logger = LoggerFactory.getLogger(WebServiceLocatorCache.class);

    /* TODO: change clientId from T_ORG */
    private static final String DEFAULT_CLIENT_ID = "9";
    private static final String DEFAULT_CLIENT_KEY = "bc891e92-7050-474d-82ad-796f575c6580";
    private static final String DEFAULT_LOCALE = "en";
    
    private String webServiceUrl;
    private RestTemplate restTemplate;

    @Autowired
    public WebServiceLocatorCache(String webServiceUrl, RestTemplate restTemplate) {
	this.webServiceUrl = webServiceUrl;
	this.restTemplate = restTemplate;
    }

    public Locations getSearchLocations(double latitude, double longitude, int distance, String distanceUnit,
	    String beverageCategoryCode, String productTypeCode, String brandCode, String flavorCode, String productCode,
	    String primaryContainerShortCode, String secondaryPackageShortCode, String businessTypeCode,
	    String physicalStateCode, String tradeChannelCode, String subTradeChannelCode, String outletName,
	    boolean includeFoodService, boolean isKosherProduct, int pageNumber, int pageCounts, String sortColumn,
	    String sortOrder) throws InvalidKeyException, NoSuchAlgorithmException,
	    IllegalStateException, UnsupportedEncodingException {
	logger.debug("Get Web Service Cache Search Locations");
	if (outletName != null && !outletName.isEmpty()) {
	    outletName = outletName.replace(';', ',');
	}
	final String signature = LocatorCrypt.getSignature(DEFAULT_CLIENT_ID, DEFAULT_CLIENT_KEY, latitude, longitude, distance);
	Locations locations = null;
	try {
	     locations = restTemplate.getForObject(
		getWsUrlSearch(), Locations.class, DEFAULT_CLIENT_ID, signature, DEFAULT_LOCALE,
		Double.valueOf(latitude), Double.valueOf(longitude), Integer.valueOf(distance), distanceUnit,
		beverageCategoryCode, productTypeCode, brandCode, flavorCode, productCode,
		primaryContainerShortCode, /* "*", */ 
		secondaryPackageShortCode, /* "*", */
		businessTypeCode, physicalStateCode, tradeChannelCode, subTradeChannelCode, 
		outletName,
		includeFoodService ? "1" : "0",
		isKosherProduct ? "1" : "0",
		Integer.valueOf(pageNumber),
		Integer.valueOf(pageCounts), sortColumn, sortOrder);
	} catch(RestClientException e) {
	    logger.error("Get locations from the server" + e.getMessage());
	}
	return locations;
    }

    public String getWebServiceUrl() {
	return webServiceUrl;
    }

    private String getWsUrlSearch() {
	return webServiceUrl + WsUriTemplates.SEARCH_V2_JSON;
    }
    
}