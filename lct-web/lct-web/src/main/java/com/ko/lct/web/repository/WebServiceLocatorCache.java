package com.ko.lct.web.repository;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.ko.lct.common.bean.BusinessType;
import com.ko.lct.common.bean.BusinessTypes;
import com.ko.lct.common.bean.Countries;
import com.ko.lct.common.bean.Country;
import com.ko.lct.common.bean.Locations;
import com.ko.lct.common.bean.Package;
import com.ko.lct.common.bean.Packages;
import com.ko.lct.common.bean.PhysicalState;
import com.ko.lct.common.bean.PhysicalStates;
import com.ko.lct.common.bean.Product;
import com.ko.lct.common.bean.ProductPackageType;
import com.ko.lct.common.bean.ProductPackageTypes;
import com.ko.lct.common.bean.Products;
import com.ko.lct.common.bean.State;
import com.ko.lct.common.bean.States;
import com.ko.lct.common.bean.TradeChannel;
import com.ko.lct.common.bean.TradeChannels;
import com.ko.lct.common.uri.WsUriTemplates;
import com.ko.lct.common.util.LocatorCrypt;

// @Component
@Singleton
public class WebServiceLocatorCache {

    private static final Logger logger = LoggerFactory.getLogger(WebServiceLocatorCache.class);

    /* TODO: change clientId from T_ORG */
    private static final String DEFAULT_CLIENT_ID = "1";
    private static final String DEFAULT_CLIENT_KEY = "2510146b-8c21-4444-ab9e-6c2409960f85";
    private static final String DEFAULT_LOCALE = "en";
    
    private String webServiceUrl;
    private RestTemplate restTemplate;

    @Autowired
    public WebServiceLocatorCache(String webServiceUrl, RestTemplate restTemplate) {
	this.webServiceUrl = webServiceUrl;
	this.restTemplate = restTemplate;
    }
    
    @Cacheable(value = "products")
    public List<Product> getProducts() {
	try {
	    logger.debug("Get Web Service Cache Products");
	    Products products = restTemplate.getForObject(getWsUrlProducts(), Products.class, DEFAULT_CLIENT_ID, DEFAULT_LOCALE);
	    return products.getProduct();
	} catch (RestClientException ex) {
	    logger.error("Failed to get Products", ex);
	    return null;
	}
    }

    @Cacheable(value = "physicalStates")
    public List<PhysicalState> getPhysicalStates() {
	try {
	    logger.debug("Get Web Service Cache Physical States");
	    PhysicalStates physicalStates = restTemplate.getForObject(getWsUrlPhysicalStates(), PhysicalStates.class,
		    DEFAULT_CLIENT_ID, DEFAULT_LOCALE);
	    return physicalStates.getPhysicalState();
	} catch (RestClientException ex) {
	    logger.error("Failed to get PhysicalStates", ex);
	    return null;
	}
    }

    @Cacheable(value = "tradeChannelsAll")
    public List<TradeChannel> getTradeChannels() {
	try {
	    logger.debug("Get Web Service Cache Trade Channels");
	    TradeChannels tradeChannels = restTemplate.getForObject(getWsUrlTradeChannels(), TradeChannels.class,
		    DEFAULT_CLIENT_ID, DEFAULT_LOCALE);
	    return tradeChannels.getTradeChannels();
	} catch (RestClientException ex) {
	    logger.error("Failed to get TradeChannels", ex);
	    return null;
	}
    }

    @Cacheable(value = "packages")
    public List<Package> getPackages() {
	try {
	    logger.debug("Get Web Service Cache Packages");
	    Packages packages = restTemplate.getForObject(getWsUrlPackages(), Packages.class,
		    DEFAULT_CLIENT_ID, DEFAULT_LOCALE);
	    return packages.getPackage();
	} catch (RestClientException ex) {
	    logger.error("Failed to get Packages", ex);
	    return null;
	}
    }

    @Cacheable(value = "productPackageTypes")
    public List<ProductPackageType> getProductPackageTypes() {
	try {
	    logger.debug("Get Web Service Cache Product Package Types");
	    ProductPackageTypes productPackageTypes = restTemplate.getForObject(getWsUrlProductTypes(), ProductPackageTypes.class,
		    DEFAULT_CLIENT_ID, DEFAULT_LOCALE);
	    return productPackageTypes.getProductPackageTypes();
	} catch (RestClientException ex) {
	    logger.error("Failed to get ProductPackageTypes", ex);
	    return null;
	}
    }

    @Cacheable(value = "businessTypes")
    public List<BusinessType> getBusinessTypes() {
	try {
	    logger.debug("Get Web Service Cache Business Types");
	    BusinessTypes businessTypes = restTemplate.getForObject(getWsUrlBusinessTypes(), BusinessTypes.class,
		    DEFAULT_CLIENT_ID, DEFAULT_LOCALE);
	    return businessTypes.getBusinessType();
	} catch (RestClientException ex) {
	    logger.error("Failed to get BusinessTypes", ex);
	    return null;
	}
    }

    @Cacheable(value = "statesAll")
    public List<State> getStates() {
	try {
	    logger.debug("Get Web Service Cache States");
	    States states = restTemplate.getForObject(getWsUrlStates(), States.class,
		    DEFAULT_CLIENT_ID, DEFAULT_LOCALE);
	    return states.getState();
	} catch (RestClientException ex) {
	    logger.error("Failed to get States", ex);
	    return null;
	}
    }

    @Cacheable(value = "countries")
    public List<Country> getCountries() {
	try {
	    logger.debug("Get Web Service Cache Countries");
	    Countries countries = restTemplate.getForObject(getWsUrlCountries(), Countries.class,
		    DEFAULT_CLIENT_ID, DEFAULT_LOCALE);
	    return countries.getCountryList();
	} catch (RestClientException ex) {
	    logger.error("Failed to get Countries", ex);
	    return null;
	}
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
	Locations locations = restTemplate.getForObject(
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
	return locations;
    }

    public String getWebServiceUrl() {
	return webServiceUrl;
    }

    private String getWsUrlProducts() {
	return webServiceUrl + WsUriTemplates.PRODUCTS_JSON;
    }

    private String getWsUrlPhysicalStates() {
	return webServiceUrl + WsUriTemplates.PHYSICAL_STATES_JSON;
    }

    private String getWsUrlTradeChannels() {
	return webServiceUrl + WsUriTemplates.TRADE_CHANNELS_JSON;
    }

    private String getWsUrlPackages() {
	return webServiceUrl + WsUriTemplates.PACKAGES_JSON;
    }
    
    private String getWsUrlProductTypes() {
	return webServiceUrl + WsUriTemplates.PRODUCT_PACKAGE_TYPES_JSON;
    }

    private String getWsUrlBusinessTypes() {
	return webServiceUrl + WsUriTemplates.BUSINESS_TYPES_JSON;
    }

    private String getWsUrlStates() {
	return webServiceUrl + WsUriTemplates.STATES_JSON;
    }

    private String getWsUrlCountries() {
	return webServiceUrl + WsUriTemplates.COUNTRIES_JSON;
    }

    private String getWsUrlSearch() {
	return webServiceUrl + WsUriTemplates.SEARCH_V2_JSON;
    }
    
}
