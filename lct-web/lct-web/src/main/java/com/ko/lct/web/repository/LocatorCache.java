package com.ko.lct.web.repository;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.ko.lct.common.bean.BaseDictionaryItem;
import com.ko.lct.common.bean.BeverageCategory;
import com.ko.lct.common.bean.Brand;
import com.ko.lct.common.bean.BusinessType;
import com.ko.lct.common.bean.Country;
import com.ko.lct.common.bean.Flavor;
import com.ko.lct.common.bean.Locations;
import com.ko.lct.common.bean.Package;
import com.ko.lct.common.bean.PhysicalState;
import com.ko.lct.common.bean.PrimaryContainer;
import com.ko.lct.common.bean.Prod;
import com.ko.lct.common.bean.Product;
import com.ko.lct.common.bean.ProductPackageType;
import com.ko.lct.common.bean.SecondaryPackage;
import com.ko.lct.common.bean.State;
import com.ko.lct.common.bean.SubTradeChannel;
import com.ko.lct.common.bean.TradeChannel;
import com.ko.lct.common.util.LocatorConstants;
import com.ko.lct.web.bean.DictionaryEnum;
import com.ko.lct.web.bean.DictionaryItem;

@Component
@Singleton
public class LocatorCache {

    private static final Logger logger = LoggerFactory.getLogger(LocatorCache.class);

    @Autowired
    WebServiceLocatorCache webServiceLocatorCache;

    private List<BeverageCategory> beverageCategories;
    private List<Brand> brands;
    private List<Flavor> flavors;
    private List<Prod> prods;
    private List<PrimaryContainer> primaryContainers;
    private List<PrimaryContainer> shortPrimaryContainers;
    private List<SecondaryPackage> secondaryPackages;
    private List<BaseDictionaryItem> shortSecondaryPackages;
    
    @CacheEvict(value = {"products", "brands", "flavors", "prods", "correctBrand", "beverageCategories", 
	    "keywordDictionary"}, 
	    allEntries = true)
    public void clearProductsCache() {
	logger.info("Clear products cache");
    }
    
    @CacheEvict(value = {"packages", "primaryContainers", "correctPrimaryContainer", "shortPrimaryContainers", 
	    "secondaryPackages", "shortSecondaryPackages", "keywordDictionary"}, allEntries = true)
    public void clearPackagesCache() {
	logger.info("Clear packages cache");
    }
    
    @CacheEvict(value = {"tradeChannelsAll", "tradeChannels", "subTradeChannels", "keywordDictionary"}, 
	    allEntries = true)
    public void clearTradeSubChannelsCache() {
	logger.info("Clear Trade Sub Channels cache");
    }

    @Cacheable(value = "beverageCategories")
    public List<BeverageCategory> getBeverageCategories() {
	List<Product> products = webServiceLocatorCache.getProducts();
	if (products == null) {
	    return null;
	}
	Set<BeverageCategory> tmpBeverageCategorySet = new HashSet<BeverageCategory>();

	for (Product product : products) {
	    for (BeverageCategory beverageCategory : product.getBeverageCategory()) {
		tmpBeverageCategorySet.add(beverageCategory);
	    }
	}
	this.beverageCategories = new ArrayList<BeverageCategory>(tmpBeverageCategorySet);

	return this.beverageCategories;
    }

    @Cacheable(value = "brands")
    public List<Brand> getBrands(String beverageCategoryCode) {
	List<Product> products = webServiceLocatorCache.getProducts();
	if (products == null) {
	    return null;
	}
	Collections.sort(products, new BeverageBrandComporator());
	List<Brand> tmpBrands = new ArrayList<Brand>();
	boolean isAll = LocatorConstants.SELECTED_VALUE_ALL.equals(beverageCategoryCode);
	String prevCode = null;
	for (Product product : products) {
	    if (!product.getBrand().getCode().equals(prevCode)
		    && (isAll || product.isBeverageCategoryExists(beverageCategoryCode))) {
		tmpBrands.add(product.getBrand());
		prevCode = product.getBrand().getCode();
	    }
	}
	if (isAll) {
	    this.brands = tmpBrands;
	}
	return tmpBrands;
    }

    @Cacheable(value = "prods")
    public List<Prod> getProds(String beverageCategoryCode, String brandCode, String flavorCode) {
	List<Product> products = webServiceLocatorCache.getProducts();
	if (products == null) {
	    return null;
	}
	Collections.sort(products, new BeverageProdComporator());
	List<Prod> tmpProd = new ArrayList<Prod>();
	boolean isBeverageCategoryAll = LocatorConstants.SELECTED_VALUE_ALL.equals(beverageCategoryCode);
	boolean isBrandAll = LocatorConstants.SELECTED_VALUE_ALL.equals(brandCode);
	boolean isFlavorAll = LocatorConstants.SELECTED_VALUE_ALL.equals(flavorCode);
	String prevCode = null;
	for (Product product : products) {
	    final String prodCode = product.getProd().getCode(); 
	    if (!prodCode.equals(prevCode)
		    && (isBeverageCategoryAll || product.isBeverageCategoryExists(beverageCategoryCode))
		    && (isBrandAll || brandCode.equals(product.getBrand().getCode()))
		    && (isFlavorAll || flavorCode.equals(product.getFlavor().getCode()))) {
		tmpProd.add(product.getProd());
		prevCode = prodCode;
	    }
	}
	if (isBeverageCategoryAll && isBrandAll || isFlavorAll) {
	    this.prods = tmpProd;
	}
	return tmpProd;
    }
    
    @Cacheable(value = "flavors", key = "#beverageCategoryCode + #brandCode + #isCorrectBrand")
    public List<Flavor> getFlavors(String beverageCategoryCode, String brandCode, boolean isCorrectBrand) {
	List<Product> products = webServiceLocatorCache.getProducts();
	if (products == null) {
	    return null;
	}
	Collections.sort(products, new BeverageFlavorComporator());
	List<Flavor> tmpFlavors = new ArrayList<Flavor>();
	boolean isBeverageCategoryAll = LocatorConstants.SELECTED_VALUE_ALL.equals(beverageCategoryCode);
	boolean isBrandAll = LocatorConstants.SELECTED_VALUE_ALL.equals(brandCode);
	String prevCode = null;
	if (!isCorrectBrand) {
	    isBrandAll = true;
	}
	for (Product product : products) {
	    if (!product.getFlavor().getCode().equals(prevCode)
		    && (product.isBeverageCategoryExists(beverageCategoryCode)
		    || isBeverageCategoryAll)
		    && (product.getBrand().getCode().equals(brandCode)
		    || isBrandAll)) {
		tmpFlavors.add(product.getFlavor());
		prevCode = product.getFlavor().getCode();
	    }
	}

	if (isBeverageCategoryAll && isBrandAll) {
	    this.flavors = tmpFlavors;
	}

	return tmpFlavors;
    }

    public List<DictionaryItem> getChannelTree() {
	List<TradeChannel> tradeChannels = webServiceLocatorCache.getTradeChannels();
	if (tradeChannels == null) {
	    return null;
	}
	List<DictionaryItem> retValue = new ArrayList<DictionaryItem>();
	for (TradeChannel tradeChannel : tradeChannels) {
	    DictionaryItem dictionaryItem = new DictionaryItem();
	    dictionaryItem.setCode(tradeChannel.getCode());
	    dictionaryItem.setName(tradeChannel.getName());
	    dictionaryItem.setDictionary(DictionaryEnum.CHANNEL);
	    retValue.add(dictionaryItem);
	    for (SubTradeChannel subTradeChannel : tradeChannel.getSubTradeChannel()) {
		dictionaryItem = new DictionaryItem();
		dictionaryItem.setCode(subTradeChannel.getCode());
		dictionaryItem.setName(subTradeChannel.getName());
		dictionaryItem.setDictionary(DictionaryEnum.SUB_CHANNEL);
		retValue.add(dictionaryItem);
	    }
	}
	return retValue;
    }

    public List<PhysicalState> getPhysicalStates() {
	return webServiceLocatorCache.getPhysicalStates();
    }

    @Cacheable(value = "tradeChannels")
    public List<TradeChannel> getTradeChannels(boolean includeFoodService) {
	List<TradeChannel> tradeChannels = webServiceLocatorCache.getTradeChannels();
	if (tradeChannels == null) {
	    return null;
	}
	List<TradeChannel> retValue = new ArrayList<TradeChannel>();
	for (TradeChannel tradeChannel : tradeChannels) {
	    if (includeFoodService || !tradeChannel.isFoodServiceInd()) {
		TradeChannel tradeChannelClone = new TradeChannel();
		tradeChannelClone.setCode(tradeChannel.getCode());
		tradeChannelClone.setName(tradeChannel.getName());
		retValue.add(tradeChannelClone);
	    }
	}
	Collections.sort(retValue, new ChannelComporator());
	return retValue;
    }

    @Cacheable(value = "subTradeChannels", key = "#tradeChannelCode + #includeFoodService")
    public List<SubTradeChannel> getSubTradeChannels(String tradeChannelCode, boolean includeFoodService) {
	List<TradeChannel> tradeChannels = webServiceLocatorCache.getTradeChannels();
	if (tradeChannels == null) {
	    return null;
	}
	final boolean isAll = LocatorConstants.SELECTED_VALUE_ALL.equals(tradeChannelCode);
	List<SubTradeChannel> retValue = new ArrayList<SubTradeChannel>();
	for (TradeChannel tradeChannel : tradeChannels) {
	    if ((isAll || tradeChannel.getCode().equals(tradeChannelCode))
		    && (includeFoodService || !tradeChannel.isFoodServiceInd())) {
		for (SubTradeChannel subTradeChannel : tradeChannel.getSubTradeChannel()) {
		    SubTradeChannel subTradeChannelClone = new SubTradeChannel();
		    subTradeChannelClone.setCode(subTradeChannel.getCode());
		    subTradeChannelClone.setName(subTradeChannel.getName());
		    retValue.add(subTradeChannelClone);
		}
	    }
	}
	Collections.sort(retValue, new ChannelComporator());
	return retValue;
    }

    @Cacheable(value = "primaryContainers", key = "#brandCode + #isCorrectBrand")
    public List<PrimaryContainer> getPrimaryContainers(String brandCode, boolean isCorrectBrand) {
	List<Package> packages = webServiceLocatorCache.getPackages();
	if (packages == null) {
	    return null;
	}
	Collections.sort(packages, new PrimaryContainerComporator());
	List<PrimaryContainer> tmpPrimaryContainers = new ArrayList<PrimaryContainer>();
	boolean isBrandAll = LocatorConstants.SELECTED_VALUE_ALL.equals(brandCode);
	if (!isCorrectBrand) {
	    isBrandAll = true;
	}
	String prevCode = null;
	for (Package pkg : packages) {
	    if (!pkg.getPrimaryContainer().getCode().equals(prevCode)
		    && (isBrandAll || pkg.getBrandCode().equals(brandCode))) {
		tmpPrimaryContainers.add(pkg.getPrimaryContainer());
		prevCode = pkg.getPrimaryContainer().getCode();
	    }
	}
	Collections.sort(tmpPrimaryContainers, new PrimaryContainerComporatorBySize());
	if (this.primaryContainers == null) {
	    this.primaryContainers = tmpPrimaryContainers;
	}
	return tmpPrimaryContainers;
    }

    @Cacheable(value = "correctBrand", key = "#beverageCategoryCode + #brandCode")
    public boolean isCorrectBrandCode(String beverageCategoryCode, String brandCode) {
	boolean isBeverageCategoryAll = LocatorConstants.SELECTED_VALUE_ALL.equals(beverageCategoryCode);
	boolean isBrandAll = LocatorConstants.SELECTED_VALUE_ALL.equals(brandCode);
	boolean isCorrectBrand = true;
	List<Product> products = webServiceLocatorCache.getProducts();
	if (products != null
		&& !isBeverageCategoryAll
		&& !isBrandAll) {
	    isCorrectBrand = false;
	    for (Product product : products) {
		if (product.isBeverageCategoryExists(beverageCategoryCode)
			&& product.getBrand().getCode().equals(brandCode)) {
		    isCorrectBrand = true;
		    break;
		}
	    }
	}
	return isCorrectBrand;
    }

    @Cacheable(value = "correctPrimaryContainer", key = "#brandCode + #primaryContainerCode")
    public boolean isCorrectPrimaryContainer(String brandCode, String primaryContainerCode) {
	boolean isBrandAll = LocatorConstants.SELECTED_VALUE_ALL.equals(brandCode);
	boolean isPrimaryContainerAll = LocatorConstants.SELECTED_VALUE_ALL.equals(primaryContainerCode);
	boolean isCorrectPrimaryContainer = true;
	List<Package> packages = webServiceLocatorCache.getPackages();
	if (packages != null
		&& !isBrandAll
		&& !isPrimaryContainerAll) {
	    isCorrectPrimaryContainer = false;
	    for (Package pkg : packages) {
		if (pkg.getBrandCode().equals(brandCode)
			&& pkg.getPrimaryContainer().getCode().equals(primaryContainerCode)) {
		    isCorrectPrimaryContainer = true;
		    break;
		}
	    }
	    if (!isCorrectPrimaryContainer) {
		isPrimaryContainerAll = true;
	    }
	}
	return isCorrectPrimaryContainer;
    }
    
    @Cacheable(value = "shortPrimaryContainers", key = "#brandCode + #productCode")
    public List<PrimaryContainer> getShortPrimaryContainers(String brandCode, String productCode) {
	List<Package> packages = webServiceLocatorCache.getPackages();
	if (packages == null) {
	    return null;
	}
	Collections.sort(packages, new PrimaryContainerComporator());
	List<PrimaryContainer> tmpShortPrimaryContainers = new ArrayList<PrimaryContainer>();
	boolean isBrandAll = LocatorConstants.SELECTED_VALUE_ALL.equals(brandCode);
	boolean isProductAll = LocatorConstants.SELECTED_VALUE_ALL.equals(productCode);
	String prevCode = null;
	for (Package pkg : packages) {
	    String primaryContainerShortCode = pkg.getPrimaryContainer().getCode(); 
	    if (primaryContainerShortCode != null
		    && !primaryContainerShortCode.equals(prevCode)
		    && (isBrandAll || pkg.getBrandCode().equals(brandCode))
		    && (isProductAll || pkg.getProductCode().equals(productCode))) {
		tmpShortPrimaryContainers.add(pkg.getPrimaryContainer());
		prevCode = primaryContainerShortCode;
	    }
	}
	Collections.sort(tmpShortPrimaryContainers, new PrimaryContainerComporatorBySize());
	if (isBrandAll && isProductAll) {
	    this.shortPrimaryContainers = tmpShortPrimaryContainers;
	}
	return tmpShortPrimaryContainers;
    }
    
    @Cacheable(value = "secondaryPackages", key = "#brandCode + #primaryContainerCode + #isCorrectBrand + #isCorrectPrimaryContainer")
    public List<SecondaryPackage> getSecondaryPackages(String brandCode, String primaryContainerCode,
	    boolean isCorrectBrand, boolean isCorrectPrimaryContainer) {
	List<Package> packages = webServiceLocatorCache.getPackages();
	if (packages == null) {
	    return null;
	}
	Collections.sort(packages, new SecondaryPackageComporator());
	List<SecondaryPackage> tmpSecondaryPackages = new ArrayList<SecondaryPackage>();
	boolean isBrandAll = LocatorConstants.SELECTED_VALUE_ALL.equals(brandCode);
	boolean isPrimaryContainerAll = LocatorConstants.SELECTED_VALUE_ALL.equals(primaryContainerCode);
	if (!isCorrectBrand) {
	    isBrandAll = true;
	}
	if (!isCorrectPrimaryContainer) {
	    isPrimaryContainerAll = true;
	}
	String prevCode = null;
	for (Package pkg : packages) {
	    if (!pkg.getSecondaryPackage().getCode().equals(prevCode)
		    && (isPrimaryContainerAll || pkg.getPrimaryContainer().getCode().equals(primaryContainerCode))
		    && (isBrandAll || pkg.getBrandCode().equals(brandCode))) {
		tmpSecondaryPackages.add(pkg.getSecondaryPackage());
		prevCode = pkg.getSecondaryPackage().getCode();
	    }
	}
	if (this.secondaryPackages == null) {
	    this.secondaryPackages = tmpSecondaryPackages;
	}
	return tmpSecondaryPackages;
    }

    @Cacheable(value = "shortSecondaryPackages", key = "#brandCode + #productCode + #primaryContainerShortCode")
    public List<BaseDictionaryItem> getShortSecondaryPackages(String brandCode, String productCode, String primaryContainerShortCode) {
	List<Package> packages = webServiceLocatorCache.getPackages();
	if (packages == null) {
	    return null;
	}
	Collections.sort(packages, new PackageSizeComporator());
	List<BaseDictionaryItem> tmpPackageSizes = new ArrayList<BaseDictionaryItem>();
	
	boolean isBrandAll = LocatorConstants.SELECTED_VALUE_ALL.equals(brandCode);
	boolean isProductAll = LocatorConstants.SELECTED_VALUE_ALL.equals(productCode);
	boolean isPrimaryContainerAll = LocatorConstants.SELECTED_VALUE_ALL.equals(primaryContainerShortCode);
	BaseDictionaryItem tempItem;
	String prevCode = null;
	for (Package pkg : packages) {
	    String secondaryPackageShortCode = pkg.getSecondaryPackage().getCode();
	    if (secondaryPackageShortCode != null
		    && !secondaryPackageShortCode.equals(prevCode)
		    && (isBrandAll || brandCode.equals(pkg.getBrandCode()))
		    && (isProductAll || productCode.equals(pkg.getProductCode()))
		    && (isPrimaryContainerAll || primaryContainerShortCode.equals(pkg.getPrimaryContainer().getCode()))) {
		tempItem = new BaseDictionaryItem();
		tempItem.setCode(secondaryPackageShortCode);
		tempItem.setName(pkg.getSecondaryPackage().getName());
		tmpPackageSizes.add(tempItem);
		prevCode = secondaryPackageShortCode;
	    }
	}
	this.shortSecondaryPackages = tmpPackageSizes;
	return tmpPackageSizes;
    }

    public List<ProductPackageType> getProductPackageTypes() {
	return webServiceLocatorCache.getProductPackageTypes();
    }

    public List<BusinessType> getBusinessTypes() {
	return webServiceLocatorCache.getBusinessTypes();
    }

    @Cacheable(value = "states")
    public List<State> getStates(String countryCode) {
	List<State> states = webServiceLocatorCache.getStates();
	if (states == null) {
	    return null;
	} else if (LocatorConstants.SELECTED_VALUE_ALL.equals(countryCode)) {
	    return states;
	} else {
	    List<State> resultStates = new ArrayList<State>();
	    for (State state : states) {
		if (state.getCountry().getCode().equals(countryCode)) {
		    resultStates.add(state);
		}
	    }
	    return resultStates;
	}
    }

    public List<Country> getCountries() {
	return webServiceLocatorCache.getCountries();
    }

    @Cacheable(value = "keywordDictionary")
    public List<DictionaryItem> getKeywordDictionary() {
	List<DictionaryItem> keywordDictionary = new ArrayList<DictionaryItem>();
	if (this.beverageCategories != null) {
	    for (BeverageCategory beverageCategory : this.beverageCategories) {
		keywordDictionary.add(new DictionaryItem(beverageCategory.getCode(),
			beverageCategory.getName(), DictionaryEnum.BEV_CAT));
	    }
	}
	if (this.brands != null) {
	    for (Brand brand : this.brands) {
		keywordDictionary.add(new DictionaryItem(brand.getCode(), brand.getName(), DictionaryEnum.BEV_BRAND));
	    }
	}
	if (this.flavors != null) {
	    for (Flavor flavor : this.flavors) {
		keywordDictionary.add(new DictionaryItem(flavor.getCode(), flavor.getName(), DictionaryEnum.BEV_FLAVOR));
	    }
	}
	if (this.prods != null) {
	    for (Prod prod : this.prods) {
		keywordDictionary.add(new DictionaryItem(prod.getCode(), prod.getName(), DictionaryEnum.PROD));
	    }
	}
	
	/*
	 * if (this.primaryContainers != null) { for (PrimaryContainer pContainer : this.primaryContainers) { keywordDictionary.add(new
	 * DictionaryItem(pContainer.getCode(), pContainer.getName(), DictionaryEnum.PR_CONT)); } } if (this.secondaryPackages != null) { for (SecondaryPackage sPackage :
	 * this.secondaryPackages) { keywordDictionary.add(new DictionaryItem(sPackage.getCode(), sPackage.getName(), DictionaryEnum.SEC_PKG)); } }
	 */
	if (this.shortPrimaryContainers != null) {
	    for (BaseDictionaryItem item : this.shortPrimaryContainers) {
		keywordDictionary.add(new DictionaryItem(item.getCode(), item.getName(), DictionaryEnum.CONT_TYPE));
	    }
	}
	if (this.shortSecondaryPackages != null) {
	    for (BaseDictionaryItem item : this.shortSecondaryPackages) {
		keywordDictionary.add(new DictionaryItem(item.getCode(), item.getName(), DictionaryEnum.PACK_SIZE));
	    }
	}
	List<DictionaryItem> channelTree = getChannelTree();
	if (channelTree != null) {
	    keywordDictionary.addAll(channelTree);
	}
	if (webServiceLocatorCache.getBusinessTypes() != null) {
	    for (BusinessType businessType : webServiceLocatorCache.getBusinessTypes()) {
		keywordDictionary.add(new DictionaryItem(String.valueOf(businessType.getId()),
			businessType.getName(), DictionaryEnum.BUS_TYPE));
	    }
	}
	if (webServiceLocatorCache.getProductPackageTypes() != null) {
	    for (ProductPackageType productPackageType : webServiceLocatorCache.getProductPackageTypes()) {
		keywordDictionary.add(new DictionaryItem(String.valueOf(productPackageType.getId()),
			productPackageType.getName(), DictionaryEnum.PR_PKG_TYPE));
	    }
	}
	if (webServiceLocatorCache.getPhysicalStates() != null) {
	    for (PhysicalState physicalState : webServiceLocatorCache.getPhysicalStates()) {
		keywordDictionary.add(new DictionaryItem(physicalState.getCode(),
			physicalState.getName(), DictionaryEnum.PHYS_STATE));
	    }
	}
	return keywordDictionary;
    }

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

    abstract class AbstractProductPackageComparator<T> implements Comparator<T> {
	protected int compare(final String s1, final String s2) {
	    if (s1 == null && s2 == null) {
		return 0;
	    }
	    if (s1 == null) {
		return -1;
	    }
	    if (s2 == null) {
		return 1;
	    }
	    return s1.compareTo(s2);
	}
    }

    class BeverageBrandComporator extends AbstractProductPackageComparator<Product> {
	@Override
	public int compare(Product o1, Product o2) {
	    return compare(o1.getBrand().getName(), o2.getBrand().getName());
	}
    }
    
    class BeverageProdComporator extends AbstractProductPackageComparator<Product> {
	@Override
	public int compare(Product o1, Product o2) {
	    return compare(o1.getProd().getName(), o2.getProd().getName());
	}
    }

    class BeverageFlavorComporator extends AbstractProductPackageComparator<Product> {
	@Override
	public int compare(Product o1, Product o2) {
	    return compare(o1.getFlavor().getName(), o2.getFlavor().getName());
	}
    }

    class PrimaryContainerComporator extends AbstractProductPackageComparator<Package> {
	@Override
	public int compare(Package o1, Package o2) {
	    return compare(o1.getPrimaryContainer().getName(), o2.getPrimaryContainer().getName());
	}
    }

    class PrimaryContainerComporatorBySize extends AbstractProductPackageComparator<PrimaryContainer> {
	@Override
	public int compare(PrimaryContainer o1, PrimaryContainer o2) {
	    if(o1.getSize() == null) 
		return 1;
	    if(o2.getSize() == null) 
		return -1;	    
	    if (o1.getSize().doubleValue() > o2.getSize().doubleValue())
		return 1;
	    else if (o1.getSize().doubleValue() < o2.getSize().doubleValue())
		return -1;
	    else 
		return 0;
	}
    }

    class SecondaryPackageComporator extends AbstractProductPackageComparator<Package> {
	@Override
	public int compare(Package o1, Package o2) {
	    return compare(o1.getSecondaryPackage().getName(), o2.getSecondaryPackage().getName());
	}
    }

    class PackageSizeComporator implements Comparator<Package> {
	@Override
	public int compare(Package o1, Package o2) {
	    int s1;
	    int s2;
	    try {
		s1 = o1.getSecondaryPackage().getName() == null ? 0 : Integer.parseInt(o1.getSecondaryPackage().getName());
	    } catch (NumberFormatException ex) {
		s1 = 0;
	    }
	    
	    try {
		s2 = o2.getSecondaryPackage().getName() == null ? 0 : Integer.parseInt(o2.getSecondaryPackage().getName());
	    } catch (NumberFormatException ex) {
		s2 = 0;
	    }

	    if (s1 == s2)
		return 0;
	    else if (s1 > s2)
		return 1;
	    else
		return -1;
	}
    }

    class ChannelComporator implements Comparator<BaseDictionaryItem> {
	@Override
	public int compare(BaseDictionaryItem o1, BaseDictionaryItem o2) {
	    return o1.getName().compareTo(o2.getName());
	}
    }

}
