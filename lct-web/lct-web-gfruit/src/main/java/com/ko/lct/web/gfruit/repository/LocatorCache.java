package com.ko.lct.web.gfruit.repository;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.inject.Singleton;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ko.lct.common.bean.Locations;

@Component
@Singleton
public class LocatorCache {

    @Autowired
    WebServiceLocatorCache webServiceLocatorCache;

    public Locations getSearchLocations(double latitude, double longitude, int distance, String distanceUnit,
	    String beverageCategoryCode, String productTypeCode, String brandCode, String flavorCode, String productCode,
	    String primaryContainerShortCode, String secondaryPackageShortCode, String businessTypeCode,
	    String physicalStateCode, String tradeChannelCode, String subTradeChannelCode, String outletName,
	    boolean includeFoodService, boolean isKosherProduct, int pageNumber, int pageCounts, String sortColumn,
	    String sortOrder) throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException,
	    UnsupportedEncodingException {
	return webServiceLocatorCache.getSearchLocations(
		latitude, longitude, distance, distanceUnit,
		beverageCategoryCode, productTypeCode,
		brandCode, flavorCode, productCode,
		primaryContainerShortCode,
		secondaryPackageShortCode,
		businessTypeCode,
		physicalStateCode, tradeChannelCode, subTradeChannelCode, outletName,
		includeFoodService, isKosherProduct, pageNumber, pageCounts, sortColumn, sortOrder);
    }

}