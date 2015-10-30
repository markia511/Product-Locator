package com.ko.lct.job.common.businessobjects;

public class Location {

    private double latitude;
    private double longitude;
    private boolean incorrect = false;

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

    public boolean isIncorrect() {
	return incorrect;
    }

    public void setIncorrect(boolean incorrect) {
	this.incorrect = incorrect;
    }
}
