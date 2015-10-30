package com.ko.lct.common.bean;

public class ReportQuariesPerBrand {
    String brandName;	
    int anyPackage;
    int specificPackage;
    int total;
    public String getBrandName() {
        return brandName;
    }
    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }
    public int getAnyPackage() {
        return anyPackage;
    }
    public void setAnyPackage(int anyPackage) {
        this.anyPackage = anyPackage;
    }
    public int getSpecificPackage() {
        return specificPackage;
    }
    public void setSpecificPackage(int specificPackage) {
        this.specificPackage = specificPackage;
    }
    public int getTotal() {
        return total;
    }
    public void setTotal(int total) {
        this.total = total;
    }
    
}
