package com.ko.lct.common.bean;

import java.io.Serializable;

public class ProductPackage implements Serializable {
    private static final long serialVersionUID = 1945067584683525558L;

    private Product product;
    private Package pkg;
    private String bppCode;
    private String bppName;
    private String physicalStateCode;
    private String physicalStateName;

    public Product getProduct() {
	return product;
    }

    public void setProduct(Product product) {
	this.product = product;
    }

    public Package getPackage() {
	return pkg;
    }

    public void setPackage(Package pkg) {
	this.pkg = pkg;
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

}
