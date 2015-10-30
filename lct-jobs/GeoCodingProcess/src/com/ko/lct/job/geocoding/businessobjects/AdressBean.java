package com.ko.lct.job.geocoding.businessobjects;

public class AdressBean {
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private double latitude;
    private double longitude;

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

}
