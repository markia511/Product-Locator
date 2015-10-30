package com.ko.lct.web.repository;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ko.lct.common.bean.BaseDictionaryItem;
import com.ko.lct.common.bean.BeverageCategory;
import com.ko.lct.common.bean.Brand;
import com.ko.lct.common.bean.BusinessType;
import com.ko.lct.common.bean.Country;
import com.ko.lct.common.bean.Flavor;
import com.ko.lct.common.bean.Locations;
import com.ko.lct.common.bean.PhysicalState;
import com.ko.lct.common.bean.PrimaryContainer;
import com.ko.lct.common.bean.Prod;
import com.ko.lct.common.bean.ProductPackageType;
import com.ko.lct.common.bean.SecondaryPackage;
import com.ko.lct.common.bean.State;
import com.ko.lct.common.bean.SubTradeChannel;
import com.ko.lct.common.bean.TradeChannel;
import com.ko.lct.common.util.LocatorConstants;
import com.ko.lct.web.bean.DictionaryItem;

@Repository
public class LocatorRepository {

    private static final Logger logger = LoggerFactory.getLogger(LocatorRepository.class);

    private String googleAPIClientId;
    
    private String googleAPIChannel;
    
    @Autowired
    LocatorCache locatorCache;

    public List<BeverageCategory> getBeverageCategories() {
	return locatorCache.getBeverageCategories();
    }

    public List<Brand> getBrandsAll() {
	return getBeverageBrands(LocatorConstants.SELECTED_VALUE_ALL);
    }

    
    public List<Brand> getBeverageBrands(String beverageCategoryCode) {
	return locatorCache.getBrands(beverageCategoryCode);
    }

    public List<Prod> getProdsAll() {
	return getProds(LocatorConstants.SELECTED_VALUE_ALL, LocatorConstants.SELECTED_VALUE_ALL, LocatorConstants.SELECTED_VALUE_ALL);
    }

    public List<Prod> getProds(String beverageCategoryCode, String brandCode, String flavorCode) {
	return locatorCache.getProds(beverageCategoryCode, brandCode, flavorCode);
    }
    
    public List<Flavor> getFlavorsAll() {
	return getFlavors(LocatorConstants.SELECTED_VALUE_ALL, LocatorConstants.SELECTED_VALUE_ALL);
    }

    public List<Flavor> getFlavors(String beverageCategoryCode, String brandCode) {
	return locatorCache.getFlavors(beverageCategoryCode, brandCode,
		locatorCache.isCorrectBrandCode(beverageCategoryCode, brandCode));
    }

    public List<PhysicalState> getPhysicalStates() {
	return locatorCache.getPhysicalStates();
    }

    public List<TradeChannel> getTradeChannels(boolean includeFoodService) {
	return locatorCache.getTradeChannels(includeFoodService);
    }
    
    public List<TradeChannel> getTradeChannelsAll() {
	return getTradeChannels(false);	
    }

    public List<SubTradeChannel> getSubTradeChannels(String tradeChannelCode, boolean includeFoodService) {
	return locatorCache.getSubTradeChannels(tradeChannelCode, includeFoodService);
    }

    public List<SubTradeChannel> getSubTradeChannelsAll() {
	return getSubTradeChannels(LocatorConstants.SELECTED_VALUE_ALL, false);
    }

    public List<DictionaryItem> getKeywordDictionary() {
	return locatorCache.getKeywordDictionary();
    }

    public List<PrimaryContainer> getPrimaryContainersAll() {
	return locatorCache.getPrimaryContainers(LocatorConstants.SELECTED_VALUE_ALL, true);
    }

    public List<PrimaryContainer> getPrimaryContainers(String beverageCategoryCode, String brandCode) {
	return locatorCache.getPrimaryContainers(brandCode, 
		locatorCache.isCorrectBrandCode(beverageCategoryCode, brandCode));
    }

    public List<PrimaryContainer> getShortPrimaryContainersAll() {
	return getShortPrimaryContainers(LocatorConstants.SELECTED_VALUE_ALL, LocatorConstants.SELECTED_VALUE_ALL);
    }
    
    public List<PrimaryContainer> getShortPrimaryContainers(String brandCode, String productCode) {
	return locatorCache.getShortPrimaryContainers(brandCode, productCode); 
    }
    
    public List<SecondaryPackage> getSecondaryPackagesAll() {
	return locatorCache.getSecondaryPackages(LocatorConstants.SELECTED_VALUE_ALL, LocatorConstants.SELECTED_VALUE_ALL, 
		true, true);
    }

    public List<SecondaryPackage> getSecondaryPackages(String beverageCategoryCode, String brandCode, 
	    String primaryContainerCode) {
	return locatorCache.getSecondaryPackages(brandCode, primaryContainerCode,
		locatorCache.isCorrectBrandCode(beverageCategoryCode, brandCode), 
		locatorCache.isCorrectPrimaryContainer(brandCode, primaryContainerCode));
    }

    public List<BaseDictionaryItem> getShortSecondaryPackagesAll() {
	return getShortSecondaryPackages(LocatorConstants.SELECTED_VALUE_ALL, LocatorConstants.SELECTED_VALUE_ALL, LocatorConstants.SELECTED_VALUE_ALL);
    }
    
    public List<BaseDictionaryItem> getShortSecondaryPackages(String brandCode, String productCode, String primaryContainerShortCode) {
	return locatorCache.getShortSecondaryPackages(brandCode, productCode, primaryContainerShortCode);
    }

    public List<ProductPackageType> getProductPackageTypes() {
	return locatorCache.getProductPackageTypes();
    }

    public List<BusinessType> getBusinessTypes() {
	return locatorCache.getBusinessTypes();
    }
    
    public List<State> getStatesAll() {
	return locatorCache.getStates(LocatorConstants.SELECTED_VALUE_ALL);
    }
    
    public List<State> getStates(String countryCode) {
	return locatorCache.getStates(countryCode);
    }
    
    public Country getCountryByState(String stateCode) {
	for(State state : locatorCache.getStates(LocatorConstants.SELECTED_VALUE_ALL)) {
	    if(state.getCode().equals(stateCode)) {
		return state.getCountry();
	    }
	}
	return null;
    }
    
    public List<Country> getCountries() {
	return locatorCache.getCountries();
    }

    public Locations getSearchLocations(double latitude, double longitude, int distance, String distanceUnit,
	    String beverageCategoryCode, String productTypeCode, String brandCode, String flavorCode, String productCode,
	    String primaryContainerShortCode, String secondaryPackageShortCode, String businessTypeCode,
	    String physicalStateCode, String tradeChannelCode, String subTradeChannelCode, String outletName, 
	    boolean includeFoodService, boolean isKosherProduct, int pageNumber, int pageCounts, String sortColumn, 
	    String sortOrder) throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, 
	    UnsupportedEncodingException {
	return locatorCache.getSearchLocations(latitude, longitude, distance, distanceUnit,
		beverageCategoryCode, productTypeCode, brandCode, flavorCode, productCode,
		primaryContainerShortCode, secondaryPackageShortCode, businessTypeCode,
		physicalStateCode, tradeChannelCode, subTradeChannelCode, outletName, includeFoodService,  
		isKosherProduct, pageNumber, pageCounts, sortColumn, sortOrder);
    }

    public String getGoogleAPIClientId() {
        return googleAPIClientId;
    }

    public void setGoogleAPIClientId(String googleAPIClientId) {
        this.googleAPIClientId = googleAPIClientId;
    }

    public String getGoogleAPIChannel() {
        return googleAPIChannel;
    }

    public void setGoogleAPIChannel(String googleAPIChannel) {
        this.googleAPIChannel = googleAPIChannel;
    }

}
