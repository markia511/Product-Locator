package com.ko.lct.common.bean;

import java.io.Serializable;

public class Package implements Serializable {
    private static final long serialVersionUID = -895462885072071681L;

    private String packageCategory;
    private PrimaryContainer primaryContainer;
    private SecondaryPackage secondaryPackage;
    private String brandCode;
    private String productCode;

    public String getPackageCategory() {
	return packageCategory;
    }

    public void setPackageCategory(String packageCategory) {
	this.packageCategory = packageCategory;
    }

    public PrimaryContainer getPrimaryContainer() {
	return primaryContainer;
    }

    public void setPrimaryContainer(PrimaryContainer primaryContainer) {
	this.primaryContainer = primaryContainer;
    }

    public SecondaryPackage getSecondaryPackage() {
	return secondaryPackage;
    }

    public void setSecondaryPackage(SecondaryPackage secondaryPackage) {
	this.secondaryPackage = secondaryPackage;
    }

    public String getBrandCode() {
        return brandCode;
    }

    public void setBrandCode(String brandCode) {
        this.brandCode = brandCode;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
    
}
