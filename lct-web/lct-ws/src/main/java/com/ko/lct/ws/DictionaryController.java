package com.ko.lct.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ko.lct.common.bean.BeverageCategories;
import com.ko.lct.common.bean.Brands;
import com.ko.lct.common.bean.BusinessTypes;
import com.ko.lct.common.bean.Countries;
import com.ko.lct.common.bean.Flavors;
import com.ko.lct.common.bean.Packages;
import com.ko.lct.common.bean.PhysicalStates;
import com.ko.lct.common.bean.PrimaryContainers;
import com.ko.lct.common.bean.ProductPackageTypes;
import com.ko.lct.common.bean.Products;
import com.ko.lct.common.bean.SecondaryPackages;
import com.ko.lct.common.bean.States;
import com.ko.lct.common.bean.TradeChannels;
import com.ko.lct.common.uri.WsUriTemplates;
import com.ko.lct.ws.bean.ServiceTimer;
import com.ko.lct.ws.dao.LocatorDao;

/**
 * Handles requests for the application home page.
 */
@RestController
public class DictionaryController {

    private static final Logger logger = LoggerFactory.getLogger(DictionaryController.class);

    @Autowired
    private LocatorDao dao;
    
    private static final String FORMAT_XML = "xml";
    private static final String FORMAT_JSON = "json";
    
    @RequestMapping(value = WsUriTemplates.COUNTRIES_JSON, produces = WsUriTemplates.APP_JSON_MIME)
    public @ResponseBody
    Countries getCountriesJson(@PathVariable("clientId") String clientId, @PathVariable("locale") String locale) {
	ServiceTimer timer = new ServiceTimer();
	Countries countries = dao.getCountries();
	logDictionary(WsUriTemplates.COUNTRIES, timer.getCurrentDurationTime(), FORMAT_JSON);
	return countries;
    }

    @RequestMapping(value = WsUriTemplates.COUNTRIES_XML, produces = WsUriTemplates.APP_XML_MIME)
    public @ResponseBody
    Countries getCountriesXml(@PathVariable("clientId") String clientId, @PathVariable("locale") String locale) {
	ServiceTimer timer = new ServiceTimer();
	Countries countries = dao.getCountries();
	logDictionary(WsUriTemplates.COUNTRIES, timer.getCurrentDurationTime(), FORMAT_XML);
	return countries;
    }

    @RequestMapping(value = WsUriTemplates.PRODUCTS_JSON, produces = WsUriTemplates.APP_JSON_MIME)
    public @ResponseBody
    Products getProductsJson(@PathVariable("clientId") String clientId, @PathVariable("locale") String locale) {
	ServiceTimer timer = new ServiceTimer();
	Products products = dao.getProducts();
	logDictionary(WsUriTemplates.PRODUCT, timer.getCurrentDurationTime(), FORMAT_JSON);
	return products;
    }

    @RequestMapping(value = WsUriTemplates.PRODUCTS_XML, produces = WsUriTemplates.APP_XML_MIME)
    public @ResponseBody
    Products getProductsXml(@PathVariable("clientId") String clientId, @PathVariable("locale") String locale) {
	ServiceTimer timer = new ServiceTimer();
	Products products = dao.getProducts();
	logDictionary(WsUriTemplates.PRODUCT, timer.getCurrentDurationTime(), FORMAT_XML);
	return products;
    }

    @RequestMapping(value = WsUriTemplates.BEVERAGE_CATEGORIES_JSON, produces = WsUriTemplates.APP_JSON_MIME)
    public @ResponseBody
    BeverageCategories getBeverageCategoriesJson(@PathVariable("clientId") String clientId, @PathVariable("locale") String locale) {
	ServiceTimer timer = new ServiceTimer();
	BeverageCategories beverageCategories = dao.getBeverageCategories();
	logDictionary(WsUriTemplates.BEVERAGE_CATEGORIES, timer.getCurrentDurationTime(), FORMAT_JSON);
	return beverageCategories;
    }

    @RequestMapping(value = WsUriTemplates.BEVERAGE_CATEGORIES_XML, produces = WsUriTemplates.APP_XML_MIME)
    public @ResponseBody
    BeverageCategories getBeverageCategoriesXml(@PathVariable("clientId") String clientId, @PathVariable("locale") String locale) {
	ServiceTimer timer = new ServiceTimer();
	BeverageCategories beverageCategories = dao.getBeverageCategories();
	logDictionary(WsUriTemplates.BEVERAGE_CATEGORIES, timer.getCurrentDurationTime(), FORMAT_XML);
	return beverageCategories;
    }

    @RequestMapping(value = WsUriTemplates.BRANDS_JSON, produces = WsUriTemplates.APP_JSON_MIME)
    public @ResponseBody
    Brands getBeverageBrandsJson(@PathVariable("clientId") String clientId, @PathVariable("locale") String locale,
	    @PathVariable("beverageCategoryCode") String beverageCategoryCode) {
	ServiceTimer timer = new ServiceTimer();
	Brands brands = dao.getBrands(beverageCategoryCode);
	logDictionary(WsUriTemplates.BRANDS, timer.getCurrentDurationTime(), FORMAT_JSON);
	return brands;
    }

    @RequestMapping(value = WsUriTemplates.BRANDS_XML, produces = WsUriTemplates.APP_XML_MIME)
    public @ResponseBody
    Brands getBeverageBrandsXml(@PathVariable("clientId") String clientId, @PathVariable("locale") String locale,
	    @PathVariable("beverageCategoryCode") String beverageCategoryCode) {
	ServiceTimer timer = new ServiceTimer();
	Brands brands = dao.getBrands(beverageCategoryCode);
	logDictionary(WsUriTemplates.BRANDS, timer.getCurrentDurationTime(), FORMAT_XML);
	return brands;
    }

    @RequestMapping(value = WsUriTemplates.FLAVORS_JSON, method = RequestMethod.GET, produces = WsUriTemplates.APP_JSON_MIME)
    public @ResponseBody
    Flavors getFlavorsJson(@PathVariable("clientId") String clientId, @PathVariable("locale") String locale,
	    @PathVariable("beverageCategoryCode") String beverageCategoryCode, @PathVariable("brandCode") String brandCode) {
	ServiceTimer timer = new ServiceTimer();
	Flavors flavors = dao.getFlavors(beverageCategoryCode, brandCode);
	logDictionary(WsUriTemplates.FLAVORS, timer.getCurrentDurationTime(), FORMAT_JSON);
	return flavors;
    }

    @RequestMapping(value = WsUriTemplates.FLAVORS_XML, method = RequestMethod.GET, produces = WsUriTemplates.APP_XML_MIME)
    public @ResponseBody()
    Flavors getFlavorsXml(@PathVariable("clientId") String clientId, @PathVariable("locale") String locale,
	    @PathVariable("beverageCategoryCode") String beverageCategoryCode, @PathVariable("brandCode") String brandCode) {
	ServiceTimer timer = new ServiceTimer();
	Flavors flavors = dao.getFlavors(beverageCategoryCode, brandCode);
	logDictionary(WsUriTemplates.FLAVORS, timer.getCurrentDurationTime(), FORMAT_XML);
	return flavors;
    }

    @RequestMapping(value = WsUriTemplates.PHYSICAL_STATES_JSON, method = RequestMethod.GET, produces = WsUriTemplates.APP_JSON_MIME)
    public @ResponseBody
    PhysicalStates getPhysicalStatesJson(@PathVariable("clientId") String clientId, @PathVariable("locale") String locale) {
	ServiceTimer timer = new ServiceTimer();
	PhysicalStates physicalStates = dao.getPhysicalStates();
	logDictionary(WsUriTemplates.PHYSICAL_STATES, timer.getCurrentDurationTime(), FORMAT_JSON);
	return physicalStates;
    }

    @RequestMapping(value = WsUriTemplates.PHYSICAL_STATES_XML, method = RequestMethod.GET, produces = WsUriTemplates.APP_XML_MIME)
    public @ResponseBody
    PhysicalStates getPhysicalStatesXml(@PathVariable("clientId") String clientId, @PathVariable("locale") String locale) {
	ServiceTimer timer = new ServiceTimer();
	PhysicalStates physicalStates = dao.getPhysicalStates();
	logDictionary(WsUriTemplates.PHYSICAL_STATES, timer.getCurrentDurationTime(), FORMAT_XML);
	return physicalStates;
    }

    @RequestMapping(value = WsUriTemplates.TRADE_CHANNELS_JSON, method = RequestMethod.GET, produces = WsUriTemplates.APP_JSON_MIME)
    public @ResponseBody
    TradeChannels getTradeChannelsJson(@PathVariable("clientId") String clientId, @PathVariable("locale") String locale) {
	ServiceTimer timer = new ServiceTimer();
	TradeChannels tradeChannels = dao.getTradeChannels();
	logDictionary(WsUriTemplates.TRADE_CHANNELS, timer.getCurrentDurationTime(), FORMAT_JSON);
	return tradeChannels;
    }

    @RequestMapping(value = WsUriTemplates.TRADE_CHANNELS_XML, method = RequestMethod.GET, produces = WsUriTemplates.APP_XML_MIME)
    public @ResponseBody
    TradeChannels getTradeChannelsXml(@PathVariable("clientId") String clientId, @PathVariable("locale") String locale) {
	ServiceTimer timer = new ServiceTimer();
	TradeChannels tradeChannels = dao.getTradeChannels();
	logDictionary(WsUriTemplates.TRADE_CHANNELS, timer.getCurrentDurationTime(), FORMAT_XML);
	return tradeChannels;
    }

    @RequestMapping(value = WsUriTemplates.PACKAGES_JSON, method = RequestMethod.GET, produces = WsUriTemplates.APP_JSON_MIME)
    public @ResponseBody
    Packages getPackagesJson(@PathVariable("clientId") String clientId, @PathVariable("locale") String locale) {
	ServiceTimer timer = new ServiceTimer();
	Packages packages = dao.getPackages();
	logDictionary(WsUriTemplates.PACKAGES, timer.getCurrentDurationTime(), FORMAT_JSON);
	return packages;
    }

    @RequestMapping(value = WsUriTemplates.PACKAGES_XML, method = RequestMethod.GET, produces = WsUriTemplates.APP_XML_MIME)
    public @ResponseBody
    Packages getPackagesXml(@PathVariable("clientId") String clientId, @PathVariable("locale") String locale) {
	ServiceTimer timer = new ServiceTimer();
	Packages packages = dao.getPackages();
	logDictionary(WsUriTemplates.PACKAGES, timer.getCurrentDurationTime(), FORMAT_XML);
	return packages;
    }

    @RequestMapping(value = WsUriTemplates.PRIMARY_CONTAINERS_JSON, method = RequestMethod.GET, produces = WsUriTemplates.APP_JSON_MIME)
    public @ResponseBody
    PrimaryContainers getPrimaryContainersJson(@PathVariable("clientId") String clientId, @PathVariable("locale") String locale,
	    @PathVariable("brandCode") String brandCode, @PathVariable("flavorCode") String flavorCode) {
	ServiceTimer timer = new ServiceTimer();
	PrimaryContainers primaryContainers = dao.getPrimaryContainers(brandCode, flavorCode);
	logDictionary(WsUriTemplates.PRIMARY_CONTAINERS, timer.getCurrentDurationTime(), FORMAT_JSON);
	return primaryContainers;
    }

    @RequestMapping(value = WsUriTemplates.PRIMARY_CONTAINERS_XML, method = RequestMethod.GET, produces = WsUriTemplates.APP_XML_MIME)
    public @ResponseBody
    PrimaryContainers getPrimaryContainersXml(@PathVariable("clientId") String clientId, @PathVariable("locale") String locale,
	    @PathVariable("brandCode") String brandCode, @PathVariable("flavorCode") String flavorCode) {
	ServiceTimer timer = new ServiceTimer();
	PrimaryContainers primaryContainers = dao.getPrimaryContainers(brandCode, flavorCode);
	logDictionary(WsUriTemplates.PRIMARY_CONTAINERS, timer.getCurrentDurationTime(), FORMAT_XML);
	return primaryContainers;
    }

    @RequestMapping(value = WsUriTemplates.PRIMARY_CONTAINERS_V2_JSON, method = RequestMethod.GET, produces = WsUriTemplates.APP_JSON_MIME)
    public @ResponseBody
    PrimaryContainers getPrimaryContainersJson(@PathVariable("clientId") String clientId, @PathVariable("locale") String locale,
	    @PathVariable("brandCode") String brandCode, @PathVariable("flavorCode") String flavorCode, @PathVariable("productCode") String productCode) {
	ServiceTimer timer = new ServiceTimer();
	PrimaryContainers primaryContainers = dao.getPrimaryContainers(brandCode, flavorCode, productCode);
	logDictionary(WsUriTemplates.PRIMARY_CONTAINERS, timer.getCurrentDurationTime(), FORMAT_JSON);
	return primaryContainers;
    }

    @RequestMapping(value = WsUriTemplates.PRIMARY_CONTAINERS_V2_XML, method = RequestMethod.GET, produces = WsUriTemplates.APP_XML_MIME)
    public @ResponseBody
    PrimaryContainers getPrimaryContainersXml(@PathVariable("clientId") String clientId, @PathVariable("locale") String locale,
	    @PathVariable("brandCode") String brandCode, @PathVariable("flavorCode") String flavorCode, @PathVariable("productCode") String productCode) {
	ServiceTimer timer = new ServiceTimer();
	PrimaryContainers primaryContainers = dao.getPrimaryContainers(brandCode, flavorCode, productCode);
	logDictionary(WsUriTemplates.PRIMARY_CONTAINERS, timer.getCurrentDurationTime(), FORMAT_XML);
	return primaryContainers;
    }
    
    @RequestMapping(value = WsUriTemplates.SECONDARY_PACKAGES_JSON, method = RequestMethod.GET, produces = WsUriTemplates.APP_JSON_MIME)
    public @ResponseBody
    SecondaryPackages getSecondaryPackagesJson(@PathVariable("clientId") String clientId, @PathVariable("locale") String locale,
	    @PathVariable("brandCode") String brandCode, @PathVariable("flavorCode") String flavorCode, @PathVariable("primaryContainerCode") String primaryContainerCode) {
	ServiceTimer timer = new ServiceTimer();
	SecondaryPackages secondaryPackages = dao.getSecondaryPackages(brandCode, flavorCode, primaryContainerCode);
	logDictionary(WsUriTemplates.SECONDARY_PACKAGES, timer.getCurrentDurationTime(), FORMAT_JSON);
	return secondaryPackages;
    }

    @RequestMapping(value = WsUriTemplates.SECONDARY_PACKAGES_XML, method = RequestMethod.GET, produces = WsUriTemplates.APP_XML_MIME)
    public @ResponseBody
    SecondaryPackages getSecondaryPackagesXml(@PathVariable("clientId") String clientId, @PathVariable("locale") String locale,
	    @PathVariable("brandCode") String brandCode, @PathVariable("flavorCode") String flavorCode, @PathVariable("primaryContainerCode") String primaryContainerCode) {
	ServiceTimer timer = new ServiceTimer();
	SecondaryPackages secondaryPackages = dao.getSecondaryPackages(brandCode, flavorCode, primaryContainerCode);
	logDictionary(WsUriTemplates.SECONDARY_PACKAGES, timer.getCurrentDurationTime(), FORMAT_XML);
	return secondaryPackages;
    }

    @RequestMapping(value = WsUriTemplates.PRODUCT_PACKAGE_TYPES_JSON, method = RequestMethod.GET, produces = WsUriTemplates.APP_JSON_MIME)
    public @ResponseBody
    ProductPackageTypes getProductPackageTypesJson(@PathVariable("clientId") String clientId, @PathVariable("locale") String locale) {
	ServiceTimer timer = new ServiceTimer();
	ProductPackageTypes productPackageTypes = dao.getProductPackageTypes();
	logDictionary(WsUriTemplates.PRODUCT_PACKAGE_TYPES, timer.getCurrentDurationTime(), FORMAT_JSON);
	return productPackageTypes;
    }

    @RequestMapping(value = WsUriTemplates.PRODUCT_PACKAGE_TYPES_XML, method = RequestMethod.GET, produces = WsUriTemplates.APP_XML_MIME)
    public @ResponseBody
    ProductPackageTypes getProductPackageTypesXml(@PathVariable("clientId") String clientId, @PathVariable("locale") String locale) {
	ServiceTimer timer = new ServiceTimer();
	ProductPackageTypes productPackageTypes = dao.getProductPackageTypes();
	logDictionary(WsUriTemplates.PRODUCT_PACKAGE_TYPES, timer.getCurrentDurationTime(), FORMAT_XML);
	return productPackageTypes;
    }

    @RequestMapping(value = WsUriTemplates.BUSINESS_TYPES_JSON, method = RequestMethod.GET, produces = WsUriTemplates.APP_JSON_MIME)
    public @ResponseBody
    BusinessTypes getBusinessTypesJson(@PathVariable("clientId") String clientId, @PathVariable("locale") String locale) {
	ServiceTimer timer = new ServiceTimer();
	BusinessTypes businessTypes = dao.getBusinessTypes();
	logDictionary(WsUriTemplates.BUSINESS_TYPES, timer.getCurrentDurationTime(), FORMAT_JSON);
	return businessTypes;
    }

    @RequestMapping(value = WsUriTemplates.BUSINESS_TYPES_XML, method = RequestMethod.GET, produces = WsUriTemplates.APP_XML_MIME)
    public @ResponseBody
    BusinessTypes getBusinessTypesXml(@PathVariable("clientId") String clientId, @PathVariable("locale") String locale) {
	ServiceTimer timer = new ServiceTimer();
	BusinessTypes businessTypes = dao.getBusinessTypes();
	logDictionary(WsUriTemplates.BUSINESS_TYPES, timer.getCurrentDurationTime(), FORMAT_XML);
	return businessTypes;
    }

    @RequestMapping(value = WsUriTemplates.STATES_JSON, method = RequestMethod.GET, produces = WsUriTemplates.APP_JSON_MIME)
    public @ResponseBody
    States getStatesJson(@PathVariable("clientId") String clientId, @PathVariable("locale") String locale) {
	ServiceTimer timer = new ServiceTimer();
	States states = dao.getStates();
	logDictionary(WsUriTemplates.STATES, timer.getCurrentDurationTime(), FORMAT_JSON);
	return states;
    }

    @RequestMapping(value = WsUriTemplates.STATES_XML, method = RequestMethod.GET, produces = WsUriTemplates.APP_XML_MIME)
    public @ResponseBody
    States getStatesXml(@PathVariable("clientId") String clientId, @PathVariable("locale") String locale) {
	ServiceTimer timer = new ServiceTimer();
	States states = dao.getStates();
	logDictionary(WsUriTemplates.STATES, timer.getCurrentDurationTime(), FORMAT_XML);
	return states;
    }
    
    private static void logDictionary(String dictionary, long time, String responseFormat) {
	logger.info("Getting " + dictionary + " by " + responseFormat + " completed in " + time + " ms");
    }
}
