package com.ko.lct.job.geocoding.businessobjects;

public class DataRow
{
    private static final int OUTLET_NAME = 0;
    private static final int OUTLET_CODE_TDLINX_CODE = 1;
    private static final int OUTLET_CODE_MKTG_GRP = 2;
    private static final int DISTR_OUTLET_NAME_DESC = 5;
    private static final int DISTR_OUTLET_NAME_ID1 = 3;
    private static final int DISTR_OUTLET_NAME_ID2 = 4;
    private static final int DISTR_OUTLET_ADDRESS = 6;
    private static final int DISTR_OUTLET_ADDRESS_2 = 7;
    private static final int DISTR_OUTLET_CITY = 8;
    private static final int DISTR_OUTLET_STATE = 9;
    private static final int DISTR_OUTLET_ZIP = 10;
    private static final int COUNTRY = 12;
    private static final int CBS_BUSINESS_TYPE_DESC = 14;
    private static final int CBS_BUSINESS_TYPE_ID = 13;
    private static final int TDL_AREA_CODE = 15;
    private static final int TDL_PHONE_NUMBER = 16;
    private static final int DELIVERY_DATE = 17;
    private static final int BPP_CODE = 21;
    private static final int BPP_DESC = 22;
    private static final int PHYSICAL_STATE_NAME = 24;
    private static final int PHYSICAL_STATE_CODE = 23;
    private static final int PRODUCT_TYPE_DESC = 26;
    private static final int PRODUCT_TYPE_ID = 25;
    private static final int BEVERAGE_CATEGORY_NM = 28;
    private static final int BEVERAGE_CATEGORY_CODE = 27;
    private static final int BRAND_NM = 30;
    private static final int BRAND_CODE = 29;
    private static final int FLAVOR_NM = 32;
    private static final int FLAVOR_CODE = 31;
    private static final int PACKAGE_CATEGORY_NAME = 34;
    private static final int PRIMARY_CONTAINER_NM = 36;
    private static final int PRIMARY_CONTAINER_CODE = 35;
    private static final int SECONDARY_PACKAGE_DESC = 38;
    private static final int SECONDARY_PACKAGE_CODE = 37;
    private static final int CHANNEL_ORG_DESC = 40;
    private static final int CHANNEL_ORG_ID = 39;
    private static final int TRADE_CHANNEL_NAME = 42;
    private static final int TRADE_CHANNEL_CODE = 41;
    private static final int SUB_TRADE_CHANNEL_NAME = 44;
    private static final int SUB_TRADE_CHANNEL_CODE = 43;
    private static final int DISTR_UPC = 45;

    private String outletName;
    private String outletTdlinxCode;
    private String outletMktgGrpCode;
    private String outletNameDesc;
    private String outletNameId1;
    private String outletNameId2;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String cbsBusinessTypeDesc;
    private String cbsBusinessTypeId;
    private String tdlAreaCode;
    private String tdlPhoneNumber;
    private String deliveryDate;
    private String productTypeDesc;
    private String productTypeId;
    private String beverageCategoryName;
    private String beverageCategoryCode;
    private String brandName;
    private String brandCode;
    private String flavorName;
    private String flavorCode;
    private String primaryContainerName;
    private String primaryContainerCode;
    private String secondaryPackageDesc;
    private String secondaryPackageCode;
    private String channelOrgDesc;
    private String channelOrgId;
    private String tradeChannelName;
    private String tradeChannelCode;
    private String subTradeChannelName;
    private String subTradeChannelCode;
    private String upc;
    private String bppCode;
    private String bppName;
    private String physicalStateCode;
    private String physicalStateName;
    private String packageCategoryName;

    public void setValue(int index, String value) {
	switch (index) {
	case BEVERAGE_CATEGORY_CODE:
	    beverageCategoryCode = value;
	    break;
	case BEVERAGE_CATEGORY_NM:
	    beverageCategoryName = value;
	    break;
	case BRAND_CODE:
	    brandCode = value;
	    break;
	case BRAND_NM:
	    brandName = value;
	    break;
	case CBS_BUSINESS_TYPE_DESC:
	    cbsBusinessTypeDesc = value;
	    break;
	case CBS_BUSINESS_TYPE_ID:
	    cbsBusinessTypeId = value;
	    break;
	case CHANNEL_ORG_DESC:
	    channelOrgDesc = value;
	    break;
	case CHANNEL_ORG_ID:
	    channelOrgId = value;
	    break;
	case COUNTRY:
	    country = value.toUpperCase();
	    break;
	case DELIVERY_DATE:
	    deliveryDate = value;
	    break;
	case DISTR_OUTLET_ADDRESS:
	    addressLine1 = value;
	    break;
	case DISTR_OUTLET_ADDRESS_2:
	    addressLine2 = value;
	    if (addressLine1 == null || addressLine1.isEmpty()) {
		addressLine1 = value;
	    }
	    break;
	case DISTR_OUTLET_CITY:
	    city = value;
	    break;
	case DISTR_OUTLET_NAME_DESC:
	    outletNameDesc = value;
	    break;
	case DISTR_OUTLET_NAME_ID1:
	    outletNameId1 = value;
	    break;
	case DISTR_OUTLET_NAME_ID2:
	    outletNameId2 = value;
	    break;
	case DISTR_OUTLET_STATE:
	    state = value.toUpperCase();
	    break;
	case DISTR_OUTLET_ZIP:
	    postalCode = value;
	    break;
	case DISTR_UPC:
	    upc = value;
	    break;
	case FLAVOR_CODE:
	    flavorCode = value;
	    break;
	case FLAVOR_NM:
	    flavorName = value;
	    break;
	case OUTLET_CODE_MKTG_GRP:
	    outletMktgGrpCode = value;
	    break;
	case OUTLET_CODE_TDLINX_CODE:
	    outletTdlinxCode = value;
	    break;
	case OUTLET_NAME:
	    outletName = value;
	    break;
	case BPP_CODE:
	    bppCode = value;
	    break;
	case BPP_DESC:
	    bppName = value;
	    break;
	case PHYSICAL_STATE_CODE:
	    physicalStateCode = value;
	    break;
	case PHYSICAL_STATE_NAME:
	    physicalStateName = value;
	    break;
	case PACKAGE_CATEGORY_NAME:
	    packageCategoryName = value;
	    break;
	case PRIMARY_CONTAINER_CODE:
	    primaryContainerCode = value;
	    break;
	case PRIMARY_CONTAINER_NM:
	    primaryContainerName = value;
	    break;
	case PRODUCT_TYPE_DESC:
	    productTypeDesc = value;
	    break;
	case PRODUCT_TYPE_ID:
	    productTypeId = value;
	    break;
	case SECONDARY_PACKAGE_CODE:
	    secondaryPackageCode = value;
	    break;
	case SECONDARY_PACKAGE_DESC:
	    secondaryPackageDesc = value;
	    break;
	case SUB_TRADE_CHANNEL_CODE:
	    subTradeChannelCode = value;
	    break;
	case SUB_TRADE_CHANNEL_NAME:
	    subTradeChannelName = value;
	    break;
	case TDL_AREA_CODE:
	    tdlAreaCode = value;
	    break;
	case TDL_PHONE_NUMBER:
	    tdlPhoneNumber = value;
	    break;
	case TRADE_CHANNEL_CODE:
	    tradeChannelCode = value;
	    break;
	case TRADE_CHANNEL_NAME:
	    tradeChannelName = value;
	    break;
	default:
	    break;
	}
    }

    public String getOutletName() {
	return outletName;
    }

    public void setOutletName(String outletName) {
	this.outletName = outletName;
    }

    public String getOutletTdlinxCode() {
	return outletTdlinxCode;
    }

    public void setOutletTdlinxCode(String outletTdlinxCode) {
	this.outletTdlinxCode = outletTdlinxCode;
    }

    public String getOutletMktgGrpCode() {
	return outletMktgGrpCode;
    }

    public void setOutletMktgGrpCode(String outletMktgGrpCode) {
	this.outletMktgGrpCode = outletMktgGrpCode;
    }

    public String getOutletNameDesc() {
	return outletNameDesc;
    }

    public void setOutletNameDesc(String outletNameDesc) {
	this.outletNameDesc = outletNameDesc;
    }

    public String getOutletNameId1() {
	return outletNameId1;
    }

    public void setOutletNameId1(String outletNameId1) {
	this.outletNameId1 = outletNameId1;
    }

    public String getOutletNameId2() {
	return outletNameId2;
    }

    public void setOutletNameId2(String outletNameId2) {
	this.outletNameId2 = outletNameId2;
    }

    public String getAddressLine1() {
	return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
	this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
	return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
	this.addressLine2 = addressLine2;
    }

    public String getCity() {
	return city;
    }

    public void setCity(String city) {
	this.city = city;
    }

    public String getState() {
	return state;
    }

    public void setState(String state) {
	this.state = state;
    }

    public String getPostalCode() {
	return postalCode;
    }

    public void setPostalCode(String postalCode) {
	this.postalCode = postalCode;
    }

    public String getCountry() {
	return country;
    }

    public void setCountry(String country) {
	this.country = country;
    }

    public String getCbsBusinessTypeDesc() {
	return cbsBusinessTypeDesc;
    }

    public void setCbsBusinessTypeDesc(String cbsBusinessTypeDesc) {
	this.cbsBusinessTypeDesc = cbsBusinessTypeDesc;
    }

    public String getCbsBusinessTypeId() {
	return cbsBusinessTypeId;
    }

    public void setCbsBusinessTypeId(String cbsBusinessTypeId) {
	this.cbsBusinessTypeId = cbsBusinessTypeId;
    }

    public String getTdlAreaCode() {
	return tdlAreaCode;
    }

    public void setTdlAreaCode(String tdlAreaCode) {
	this.tdlAreaCode = tdlAreaCode;
    }

    public String getTdlPhoneNumber() {
	return tdlPhoneNumber;
    }

    public void setTdlPhoneNumber(String tdlPhoneNumber) {
	this.tdlPhoneNumber = tdlPhoneNumber;
    }

    public String getDeliveryDate() {
	return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
	this.deliveryDate = deliveryDate;
    }

    public String getProductTypeDesc() {
	return productTypeDesc;
    }

    public void setProductTypeDesc(String productTypeDesc) {
	this.productTypeDesc = productTypeDesc;
    }

    public String getProductTypeId() {
	return productTypeId;
    }

    public void setProductTypeId(String productTypeId) {
	this.productTypeId = productTypeId;
    }

    public String getBeverageCategoryName() {
	return beverageCategoryName;
    }

    public void setBeverageCategoryName(String beverageCategoryName) {
	this.beverageCategoryName = beverageCategoryName;
    }

    public String getBeverageCategoryCode() {
	return beverageCategoryCode;
    }

    public void setBeverageCategoryCode(String beverageCategoryCode) {
	this.beverageCategoryCode = beverageCategoryCode;
    }

    public String getProductCode() {
	if (bppCode == null) {
	    return null;
	}
	int i = bppCode.indexOf('-');
	if (i >= 0) {
	    return bppCode.substring(0,i);
	}
	return bppCode;
    }
    
    public String getBrandName() {
	return brandName;
    }

    public void setBrandName(String brandName) {
	this.brandName = brandName;
    }

    public String getBrandCode() {
	return brandCode;
    }

    public void setBrandCode(String brandCode) {
	this.brandCode = brandCode;
    }

    public String getFlavorName() {
	return flavorName;
    }

    public void setFlavorName(String flavorName) {
	this.flavorName = flavorName;
    }

    public String getFlavorCode() {
	return flavorCode;
    }

    public void setFlavorCode(String flavorCode) {
	this.flavorCode = flavorCode;
    }

    public String getPrimaryContainerName() {
	return primaryContainerName;
    }

    public void setPrimaryContainerName(String primaryContainerName) {
	this.primaryContainerName = primaryContainerName;
    }

    public String getPrimaryContainerCode() {
	return primaryContainerCode;
    }

    public void setPrimaryContainerCode(String primaryContainerCode) {
	this.primaryContainerCode = primaryContainerCode;
    }

    public String getSecondaryPackageDesc() {
	return secondaryPackageDesc;
    }

    public void setSecondaryPackageDesc(String secondaryPackageDesc) {
	this.secondaryPackageDesc = secondaryPackageDesc;
    }

    public String getSecondaryPackageCode() {
	return secondaryPackageCode;
    }

    public void setSecondaryPackageCode(String secondaryPackageCode) {
	this.secondaryPackageCode = secondaryPackageCode;
    }

    public String getChannelOrgDesc() {
	return channelOrgDesc;
    }

    public void setChannelOrgDesc(String channelOrgDesc) {
	this.channelOrgDesc = channelOrgDesc;
    }

    public String getChannelOrgId() {
	return channelOrgId;
    }

    public void setChannelOrgId(String channelOrgId) {
	this.channelOrgId = channelOrgId;
    }

    public String getTradeChannelName() {
	return tradeChannelName;
    }

    public void setTradeChannelName(String tradeChannelName) {
	this.tradeChannelName = tradeChannelName;
    }

    public String getTradeChannelCode() {
	return tradeChannelCode;
    }

    public void setTradeChannelCode(String tradeChannelCode) {
	this.tradeChannelCode = tradeChannelCode;
    }

    public String getSubTradeChannelName() {
	return subTradeChannelName;
    }

    public void setSubTradeChannelName(String subTradeChannelName) {
	this.subTradeChannelName = subTradeChannelName;
    }

    public String getSubTradeChannelCode() {
	return subTradeChannelCode;
    }

    public void setSubTradeChannelCode(String subTradeChannelCode) {
	this.subTradeChannelCode = subTradeChannelCode;
    }

    public String getUpc() {
	return upc;
    }

    public void setUpc(String upc) {
	this.upc = upc;
    }

    public String getBppCode() {
	return bppCode;
    }

    public void setBppCode(String bppCode) {
	this.bppCode = bppCode;
    }

    public String getBppName() {
	return bppName;
    }

    public void setBppName(String bppName) {
	this.bppName = bppName;
    }

    public String getPhysicalStateCode() {
	return physicalStateCode;
    }

    public void setPhysicalStateCode(String physicalStateCode) {
	this.physicalStateCode = physicalStateCode;
    }

    public String getPhysicalStateName() {
	return physicalStateName;
    }

    public void setPhysicalStateName(String physicalStateName) {
	this.physicalStateName = physicalStateName;
    }

    public String getPackageCategoryName() {
	return packageCategoryName;
    }

    public void setPackageCategoryName(String packageCategoryName) {
	this.packageCategoryName = packageCategoryName;
    }

}
