package com.ko.lct.job.geocodingaid.businessobjects;

public class AidInputDto {
    private int ownrshpId;
    private String btlrDlvrPntNo;
    private String btlrDlvrPntNm;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String zip;
    private String countryDesc;
    private String areaCode;
    private String phnNo;

    public int getOwnrshpId() {
	return ownrshpId;
    }

    public void setOwnrshpId(int ownrshpId) {
	this.ownrshpId = ownrshpId;
    }

    public String getBtlrDlvrPntNo() {
	return btlrDlvrPntNo;
    }

    public void setBtlrDlvrPntNo(String btlrDlvrPntNo) {
	this.btlrDlvrPntNo = btlrDlvrPntNo;
    }

    public String getBtlrDlvrPntNm() {
	return btlrDlvrPntNm;
    }

    public void setBtlrDlvrPntNm(String btlrDlvrPntNm) {
	this.btlrDlvrPntNm = btlrDlvrPntNm;
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

    public String getZip() {
	return zip;
    }

    public void setZip(String zip) {
	this.zip = zip;
    }

    public String getCountryDesc() {
	return countryDesc;
    }

    public void setCountryDesc(String countryDesc) {
	this.countryDesc = countryDesc;
    }

    public String getAreaCode() {
	return areaCode;
    }

    public void setAreaCode(String areaCode) {
	this.areaCode = areaCode;
    }

    public String getPhnNo() {
	return phnNo;
    }

    public void setPhnNo(String phnNo) {
	this.phnNo = phnNo;
    }

}
