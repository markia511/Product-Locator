package com.ko.lct.job.geocodingaid.businessobjects;

import java.util.Date;

public class AidOutputDto extends AidInputDto {
    private String frmtAddr;
    private double latitude;
    private double longitude;
    private Date geoDt;
    private int geoLvl;

    public String getFrmtAddr() {
	return frmtAddr;
    }

    public void setFrmtAddr(String frmtAddr) {
	this.frmtAddr = frmtAddr;
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

    public Date getGeoDt() {
	return geoDt;
    }

    public void setGeoDt(Date geoDt) {
	this.geoDt = geoDt;
    }

    public int getGeoLvl() {
	return geoLvl;
    }

    public void setGeoLvl(int geoLvl) {
	this.geoLvl = geoLvl;
    }

}
