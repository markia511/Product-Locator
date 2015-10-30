package com.ko.lct.job.caching.bean;

import java.io.Serializable;

public class SearchRequest implements Serializable {
    private static final long serialVersionUID = 4336387787581987401L;

    private String clientId;
    private String signature;
    private String locale;
    private double latitude;
    private double longitude;
    private int distance;
    private String distanceUnit;
    private String beverageCategoryCode;
    private String productTypeCode;
    private String brandCode;
    private String flavorCode;
    private String primaryContainerCode;
    private String secondaryPackageCode;
    private String businessTypeCode;
    private String physicalStateCode;
    private String tradeChannelCode;
    private String subTradeChannelCode;
    private String outletName;
    private boolean includeFoodService;
    private boolean kosherProductOnly;
    private int pageNumber;
    private int recordsCount;
    private String sortColumn;
    private String sortOrder;

    private DistanceUnits distanceUnits;
    private SortColumnEnum sortColumnEnum;

    public String getDistanceUnit() {
	return distanceUnit;
    }

    public void setDistanceUnit(String distanceUnit) {
	this.distanceUnit = distanceUnit;
	distanceUnits = (distanceUnit != null && (distanceUnit.startsWith("k") || distanceUnit.startsWith("K"))) ? DistanceUnits.km : DistanceUnits.mi;
    }

    public String getClientId() {
	return clientId;
    }

    public void setClientId(String clientId) {
	this.clientId = clientId;
    }

    public String getSignature() {
	return signature;
    }

    public void setSignature(String signature) {
	this.signature = signature;
    }

    public String getLocale() {
	return locale;
    }

    public void setLocale(String locale) {
	this.locale = locale;
    }

    public double getLatitude() {
	return latitude;
    }

    public void setLatitude(double latitude) {
	this.latitude = latitude;
    }

    public double getLongitude() {
	return longitude;
    }

    public void setLongitude(double longitude) {
	this.longitude = longitude;
    }

    public int getDistance() {
	return distance;
    }

    public void setDistance(int distance) {
	this.distance = distance;
    }

    public String getBeverageCategoryCode() {
	return beverageCategoryCode;
    }

    public void setBeverageCategoryCode(String beverageCategoryCode) {
	this.beverageCategoryCode = beverageCategoryCode;
    }

    public String getProductTypeCode() {
	return productTypeCode;
    }

    public void setProductTypeCode(String productTypeCode) {
	this.productTypeCode = productTypeCode;
    }

    public String getBrandCode() {
	return brandCode;
    }

    public void setBrandCode(String brandCode) {
	this.brandCode = brandCode;
    }

    public String getFlavorCode() {
	return flavorCode;
    }

    public void setFlavorCode(String flavorCode) {
	this.flavorCode = flavorCode;
    }

    public String getPrimaryContainerCode() {
	return primaryContainerCode;
    }

    public void setPrimaryContainerCode(String primaryContainerCode) {
	this.primaryContainerCode = primaryContainerCode;
    }

    public String getSecondaryPackageCode() {
	return secondaryPackageCode;
    }

    public void setSecondaryPackageCode(String secondaryPackageCode) {
	this.secondaryPackageCode = secondaryPackageCode;
    }

    public String getBusinessTypeCode() {
	return businessTypeCode;
    }

    public void setBusinessTypeCode(String businessTypeCode) {
	this.businessTypeCode = businessTypeCode;
    }

    public String getPhysicalStateCode() {
	return physicalStateCode;
    }

    public void setPhysicalStateCode(String physicalStateCode) {
	this.physicalStateCode = physicalStateCode;
    }

    public String getTradeChannelCode() {
	return tradeChannelCode;
    }

    public void setTradeChannelCode(String tradeChannelCode) {
	this.tradeChannelCode = tradeChannelCode;
    }

    public String getSubTradeChannelCode() {
	return subTradeChannelCode;
    }

    public void setSubTradeChannelCode(String subTradeChannelCode) {
	this.subTradeChannelCode = subTradeChannelCode;
    }

    public String getOutletName() {
	return outletName;
    }

    public void setOutletName(String outletName) {
	this.outletName = outletName;
    }

    public boolean isIncludeFoodService() {
	return includeFoodService;
    }

    public void setIncludeFoodService(boolean includeFoodService) {
	this.includeFoodService = includeFoodService;
    }

    public boolean isKosherProductOnly() {
	return kosherProductOnly;
    }

    public void setKosherProductOnly(boolean kosherProductOnly) {
	this.kosherProductOnly = kosherProductOnly;
    }

    public int getPageNumber() {
	return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
	this.pageNumber = pageNumber;
    }

    public int getRecordsCount() {
	return recordsCount;
    }

    public void setRecordsCount(int recordsCount) {
	this.recordsCount = recordsCount;
    }

    public DistanceUnits getDistanceUnits() {
	return distanceUnits;
    }

    public void setDistanceUnits(DistanceUnits distanceUnits) {
	this.distanceUnits = distanceUnits;
    }

    public String getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
        this.sortColumnEnum = (this.sortColumn == null || "*".equals(this.sortColumn)) ? null : SortColumnEnum.valueOf(sortColumn);
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public SortColumnEnum getSortColumnEnum() {
        return sortColumnEnum;
    }

    public void setSortColumnEnum(SortColumnEnum sortColumnEnum) {
        this.sortColumnEnum = sortColumnEnum;
    }
}
