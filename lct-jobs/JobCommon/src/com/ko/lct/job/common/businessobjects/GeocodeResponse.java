package com.ko.lct.job.common.businessobjects;

public class GeocodeResponse {

    private String city;
    private String postalCode;
    private String route;
    private String streetNumber;
    private String state;
    private String country;
    private String formattedAddress;
    private String establishment;
    private double latitude;
    private double longitude;
    private GeocodeLevelEnum geocodeLevel = GeocodeLevelEnum.UNDEFINED;
    private GeocodeStatus geocodeStatus = GeocodeStatus.ZERO_RESULTS;
    private boolean establishmentAddress;

    public String getCity() {
	return city;
    }

    public void setCity(String city) {
	this.city = city;
    }

    public String getPostalCode() {
	return postalCode;
    }

    public void setPostalCode(String postalCode) {
	this.postalCode = postalCode;
    }

    public String getFormattedAddress() {
	return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
	this.formattedAddress = formattedAddress;
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

    public GeocodeLevelEnum getGeocodeLevel() {
	return geocodeLevel;
    }

    public void setGeocodeLevel(GeocodeLevelEnum geocodeLevel) {
	this.geocodeLevel = geocodeLevel;
    }

    public String getRoute() {
	return route;
    }

    public void setRoute(String route) {
	this.route = route;
    }

    public String getStreetNumber() {
	return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
	this.streetNumber = streetNumber;
    }

    public GeocodeStatus getGeocodeStatus() {
	return geocodeStatus;
    }

    public void setGeocodeStatus(GeocodeStatus geocodeStatus) {
	this.geocodeStatus = geocodeStatus;
    }

    public String getState() {
	return state;
    }

    public void setState(String state) {
	this.state = state;
    }

    public String getEstablishment() {
	return establishment;
    }

    public void setEstablishment(String establishment) {
	this.establishment = establishment;
    }

    public String getCountry() {
	return country;
    }

    public void setCountry(String country) {
	this.country = country;
    }

    public boolean isEstablishmentAddress() {
        return establishmentAddress;
    }

    public void setEstablishmentAddress(boolean establishmentAddress) {
        this.establishmentAddress = establishmentAddress;
    }
}
