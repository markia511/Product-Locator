package com.ko.lct.job.common.businessobjects;

import com.ko.lct.job.geocoding.utilities.GeocodingConstants;

public class GeocodeRequest {

    private int addressId;
    private String outletName;
    private GeocodeString outletNameConverted;
    private String addressLine1;
    private GeocodeString addressLine1Converted;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String formattedAddress;
    private double latitude;
    private double longitude;
    private GeocodeLevelEnum geocodeLevel = GeocodeLevelEnum.UNDEFINED;
    private ConvertedStepEnum convertedStepEnum = ConvertedStepEnum.ZERO_STEP;
    private boolean converted;
    private boolean addressLineWithStreetNumber;
    private boolean finishLimitGeocode = false;
    private boolean overQueryLimit = false;
    private boolean isNeedGeocodingWithoutAddrLine = true;
    private boolean isNeedGeocodingWithOutletInTop = true;
    private boolean isNeedGeocodingWithoutCity = true;

    public GeocodeRequest() {
    }

    public GeocodeRequest(String outletName, String addressLine1, String city, String state,
	    String postalCode) {
	this.outletName = outletName;
	this.addressLine1 = addressLine1;
	this.city = city;
	this.state = state;
	this.postalCode = postalCode;
    }

    public GeocodeRequest(String outletName, String addressLine1, String addressLine2, String city, String state,
	    String postalCode) {
	this.outletName = outletName;
	this.addressLine1 = addressLine1;
	this.addressLine2 = addressLine2;
	this.city = city;
	this.state = state;
	this.postalCode = postalCode;
    }

    public int getAddressId() {
	return addressId;
    }

    public void setAddressId(int addressId) {
	this.addressId = addressId;
    }

    public String getOutletName() {
	return outletName;
    }

    public void setOutletName(String outletName) {
	this.outletName = outletName;
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
	if (this.addressLine1 == null) {
	    this.addressLine1 = addressLine2;
	}
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

    public String getFormattedAddress() {
	return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
	this.formattedAddress = formattedAddress;
    }

    public GeocodeLevelEnum getGeocodeLevel() {
	return geocodeLevel;
    }

    public void setGeocodeLevel(GeocodeLevelEnum geocodeLevel) {
	this.geocodeLevel = geocodeLevel;
    }

    public GeocodeString getOutletNameConverted() {
	return outletNameConverted;
    }

    public void setOutletNameConverted(GeocodeString outletNameConverted) {
	this.outletNameConverted = outletNameConverted;
    }

    public GeocodeString getAddressLine1Converted() {
	return addressLine1Converted;
    }

    public void setAddressLine1Converted(GeocodeString addressLine1Converted) {
	this.addressLine1Converted = addressLine1Converted;
    }

    public ConvertedStepEnum getConvertedStepEnum() {
	return convertedStepEnum;
    }

    public void setConvertedStepEnum(ConvertedStepEnum convertedStepEnum) {
	this.convertedStepEnum = convertedStepEnum;
    }

    public boolean isConverted() {
	return converted;
    }

    public void setConverted(boolean converted) {
	this.converted = converted;
    }

    public boolean isAddressLineWithStreetNumber() {
	return addressLineWithStreetNumber;
    }

    public void setAddressLineWithStreetNumber(boolean addressLineWithStreetNumber) {
	this.addressLineWithStreetNumber = addressLineWithStreetNumber;
    }

    public boolean isFinishLimitGeocode() {
        return finishLimitGeocode;
    }

    public void setFinishLimitGeocode(boolean finishLimitGeocode) {
        this.finishLimitGeocode = finishLimitGeocode;
    }

    public boolean isOverQueryLimit() {
        return overQueryLimit;
    }

    public void setOverQueryLimit(boolean overQueryLimit) {
        this.overQueryLimit = overQueryLimit;
    }

    public boolean isNeedGeocodingWithoutAddrLine() {
        return isNeedGeocodingWithoutAddrLine;
    }

    public void setNeedGeocodingWithoutAddrLine(boolean isNeedGeocodingWithoutAddrLine) {
        this.isNeedGeocodingWithoutAddrLine = isNeedGeocodingWithoutAddrLine;
    }

    public boolean isNeedGeocodingWithOutletInTop() {
        return isNeedGeocodingWithOutletInTop;
    }

    public void setNeedGeocodingWithOutletInTop(boolean isNeedGeocodingWithOutletInTop) {
        this.isNeedGeocodingWithOutletInTop = isNeedGeocodingWithOutletInTop;
    }

    public boolean isNeedGeocodingWithoutCity() {
        return isNeedGeocodingWithoutCity;
    }

    public void setNeedGeocodingWithoutCity(boolean isNeedGeocodingWithoutCity) {
        this.isNeedGeocodingWithoutCity = isNeedGeocodingWithoutCity;
    }

    public boolean isGeocoded() {
	if (this.formattedAddress != null
		&& this.formattedAddress.trim().length() > 0) {
	    return true;
	}
	return false;
    }

    public String getFullAddress() {
	/* TODO: Virgin Islands Hardcode */
	if(!GeocodingConstants.VIRGIN_ISLANDS_STATE.equals(this.state)) {
	    return getAddressLine1WithSpace() +
    		   city + " " +
    		   (state == null ? "" : state + " ") +
    	           postalCode +
    		   (country == null ? "" : " " + country);
	}
	return getAddressLine1WithSpace() +
		   city + " " +
	    	   GeocodingConstants.VIRGIN_ISLANDS_GEOCODE_REQUEST_ADDRESS;
    }

    public String getAddressWithoutAddrLine() {
	if(!GeocodingConstants.VIRGIN_ISLANDS_STATE.equals(this.state)) {
	    return city + " " +
		   (state == null ? "" : state + " ") +
		   postalCode +
    		   (country == null ? "" : " " + country);
	}
	return city + " " + GeocodingConstants.VIRGIN_ISLANDS_GEOCODE_REQUEST_ADDRESS;
    }

    public String getAddressWithoutPostalCode() {
	if(!GeocodingConstants.VIRGIN_ISLANDS_STATE.equals(this.state)) {
	    return getAddressLine1WithSpace() +
		   city + " " +
		   (state == null ? "" : state + " ") +
    		   (country == null ? "" : " " + country);
	}
	return getAddressLine1WithSpace() +
		   city + " " + 
		   GeocodingConstants.VIRGIN_ISLANDS_GEOCODE_REQUEST_ADDRESS;
    }

    public String getAddressWithoutCity() {
	if(!GeocodingConstants.VIRGIN_ISLANDS_STATE.equals(this.state)) {
	    return getAddressLine1WithSpace() +
		   (state == null ? "" : state + " ") +
        	   postalCode + 
    		   (country == null ? "" : " " + country);
	}
	return getAddressLine1WithSpace() + 
		   GeocodingConstants.VIRGIN_ISLANDS_GEOCODE_REQUEST_ADDRESS;
    }

    public void setResponse(GeocodeResponse response) {
	if (response != null) {
	    this.formattedAddress = response.getFormattedAddress();
	    this.latitude = response.getLatitude();
	    this.longitude = response.getLongitude();
	    this.geocodeLevel = response.getGeocodeLevel();
	}
    }

    public String getSeparatedFirstDigitAddress() {
	return getAddressLine1Converted().separateFirstDigitsFromLetters();
    }

    public String getAddressWithOutletInTop() {
	int position = -1;
	if (getAddressLine1Converted().isStartFromDigits()) {
	    position = 0;
	}
	String result = this.addressLine1Converted.injectWords(getOutletNameConverted().getFirstOnlyWeightyWords(),
		position);
	if (result != null) {
	    result += (" " + getAddressWithoutAddrLine());
	}
	return result;
    }

    public String getAddressFirstWeightyWord() {
	String result = getAddressLine1Converted().getFirstWeightyWord();
	if (result != null) {
	    result += (" " + getAddressWithoutAddrLine());
	}
	return result;
    }

    public String getAddressWithOnlyOutlet() {
	String result = getOutletNameConverted().getFirstOnlyWeightyWords();
	if (result != null) {
	    result += (" " + getAddressWithoutAddrLine());
	}
	return result;
    }

    public boolean isComplexCityName() {
	if (this.city.indexOf(GeocodeString.COMPLEX_CITY_SIGN) > -1
		|| this.city.indexOf(" ") != this.city.lastIndexOf(" ")) {
	    return true;
	}
	return false;
    }

    public String getCutComplexCityName() {
	String cutComplexCityName = this.city;
	String[] complexCityNameArr = null;
	String complexCityDelimeter = "";
	if (this.city.indexOf(GeocodeString.COMPLEX_CITY_SIGN) != this.city.lastIndexOf(GeocodeString.COMPLEX_CITY_SIGN)) {
	    complexCityNameArr = this.city.split(GeocodeString.COMPLEX_CITY_SIGN);
	    complexCityDelimeter = GeocodeString.COMPLEX_CITY_SIGN;
	} else if (this.city.indexOf(" ") != this.city.lastIndexOf(" ")) {
	    complexCityNameArr = this.city.split(" ");
	    complexCityDelimeter = " ";
	} else if(this.city.indexOf(GeocodeString.COMPLEX_CITY_SIGN) > -1) {
	    cutComplexCityName = this.city.replace(GeocodeString.COMPLEX_CITY_SIGN, " ");
	}
	
	if (complexCityNameArr != null
		&& complexCityNameArr.length >= GeocodeString.COUNT_WORD_CUT_COMPLEX_CITY) {
	    cutComplexCityName = complexCityNameArr[0] + complexCityDelimeter + complexCityNameArr[1];
	}
	if(!GeocodingConstants.VIRGIN_ISLANDS_STATE.equals(this.state)) {
	    return getAddressLine1WithSpace() +
        	   cutComplexCityName + " " +
        	   (state == null ? "" : state + " ") +
        	   postalCode;
	}
	return getAddressLine1WithSpace() +
	           cutComplexCityName + " " +
	           GeocodingConstants.VIRGIN_ISLANDS_GEOCODE_REQUEST_ADDRESS;    
    }
    
    public String getAddressLine1WithSpace() {
	return addressLine1 == null ? "" : (addressLine1 + " ");
    }
}
