package com.ko.lct.job.geocoding.businessobjects;

public class Delivery {
    
    private String outletKey;
    private String productPackageKey;
    private String periodKey;
    
    public String getOutletKey() {
        return outletKey;
    }
    public void setOutletKey(String outletKey) {
        this.outletKey = outletKey;
    }
    public String getProductPackageKey() {
        return productPackageKey;
    }
    public void setProductPackageKey(String productPackageKey) {
        this.productPackageKey = productPackageKey;
    }
    public String getPeriodKey() {
        return periodKey;
    }
    public void setPeriodKey(String periodKey) {
        this.periodKey = periodKey;
    }
}
