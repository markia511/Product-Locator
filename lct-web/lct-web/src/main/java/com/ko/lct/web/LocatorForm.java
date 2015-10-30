package com.ko.lct.web;

import com.ko.lct.common.util.LocatorConstants;

public class LocatorForm {

    private String beverageCategory;
    private String beverageBrand;
    private String beverageFlavor;
    private String product;
    private String physicalState;
    private String channelOrg;
    private String tradeChannel;
    private String subTradeChannel;
    private String primaryContainer;
    private String secondaryPackage;
    private String outletName;
    private int productPackageType;
    private int businessType;
    private int itemPerPage;
    private int countPage;
    private int numPage;
    private String distanceUnit;
    private String distanceUnitAdvanced;
    private String distanceUnitKeyword;
    private String state;
    private String country;
    private String shortPrimaryContainer;
    private String baseSize;
    private String shortSecondaryPackage;

    @SuppressWarnings("static-method")
    public int getMaxOutletRecordsCount() {
	return LocatorConstants.MAX_OUTLET_RECORDS_COUNT;
    }

    public String getBeverageCategory() {
	return beverageCategory;
    }

    public String getBeverageBrand() {
	return beverageBrand;
    }

    public String getBeverageFlavor() {
	return beverageFlavor;
    }

    public String getProduct() {
	return product;
    }

    public void setBeverageCategory(String beverageCategory) {
	this.beverageCategory = beverageCategory;
    }

    public void setBeverageBrand(String beverageBrand) {
	this.beverageBrand = beverageBrand;
    }

    public void setBeverageFlavor(String beverageFlavor) {
	this.beverageFlavor = beverageFlavor;
    }

    public void setProduct(String product) {
	this.product = product;
    }

    public String getPhysicalState() {
	return physicalState;
    }

    public void setPhysicalState(String physicalState) {
	this.physicalState = physicalState;
    }

    public String getChannelOrg() {
	return channelOrg;
    }

    public void setChannelOrg(String channelOrg) {
	this.channelOrg = channelOrg;
    }

    public String getTradeChannel() {
	return tradeChannel;
    }

    public void setTradeChannel(String tradeChannel) {
	this.tradeChannel = tradeChannel;
    }

    public String getSubTradeChannel() {
	return subTradeChannel;
    }

    public void setSubTradeChannel(String subTradeChannel) {
	this.subTradeChannel = subTradeChannel;
    }

    public String getPrimaryContainer() {
	return primaryContainer;
    }

    public void setPrimaryContainer(String primaryContainer) {
	this.primaryContainer = primaryContainer;
    }

    public String getSecondaryPackage() {
	return secondaryPackage;
    }

    public void setSecondaryPackage(String secondaryPackage) {
	this.secondaryPackage = secondaryPackage;
    }

    public String getOutletName() {
	return outletName;
    }

    public void setOutletName(String outletName) {
	this.outletName = outletName;
    }

    public int getProductPackageType() {
	return productPackageType;
    }

    public void setProductPackageType(int productPackageType) {
	this.productPackageType = productPackageType;
    }

    public int getBusinessType() {
	return businessType;
    }

    public void setBusinessType(int businessType) {
	this.businessType = businessType;
    }

    public int getItemPerPage() {
	return itemPerPage;
    }

    public void setItemPerPage(int itemPerPage) {
	this.itemPerPage = itemPerPage;
    }

    public int getCountPage() {
	return countPage;
    }

    public void setCountPage(int countPage) {
	this.countPage = countPage;
    }

    public int getNumPage() {
	return numPage;
    }

    public void setNumPage(int numPage) {
	this.numPage = numPage;
    }

    public String getDistanceUnit() {
	return distanceUnit;
    }

    public void setDistanceUnit(String distanceUnit) {
	this.distanceUnit = distanceUnit;
    }

    public String getDistanceUnitAdvanced() {
	return distanceUnitAdvanced;
    }

    public void setDistanceUnitAdvanced(String distanceUnitAdvanced) {
	this.distanceUnitAdvanced = distanceUnitAdvanced;
    }

    public String getDistanceUnitKeyword() {
	return distanceUnitKeyword;
    }

    public void setDistanceUnitKeyword(String distanceUnitKeyword) {
	this.distanceUnitKeyword = distanceUnitKeyword;
    }

    public String getState() {
	return state;
    }

    public void setState(String state) {
	this.state = state;
    }

    public String getCountry() {
	return country;
    }

    public void setCountry(String country) {
	this.country = country;
    }

    public String getBaseSize() {
	return baseSize;
    }

    public void setBaseSize(String baseSize) {
	this.baseSize = baseSize;
    }

    public String getShortPrimaryContainer() {
	return shortPrimaryContainer;
    }

    public void setShortPrimaryContainer(String shortPrimaryContainer) {
	this.shortPrimaryContainer = shortPrimaryContainer;
    }

    public String getShortSecondaryPackage() {
	return shortSecondaryPackage;
    }

    public void setShortSecondaryPackage(String shortSecondaryPackage) {
	this.shortSecondaryPackage = shortSecondaryPackage;
    }

}
