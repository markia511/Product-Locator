package com.ko.lct.common.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Locations implements Serializable {
    private static final long serialVersionUID = 5241484157629905137L;

    private List<Location> location = new ArrayList<Location>();
    private DistanceUnits distanceUnit = DistanceUnits.mi;
    private String distanceUnitName = DistanceUnits.mi.getName();
    private int pageNumber;
    private int recordsCount;

    public List<Location> getLocation() {
	return location;
    }

    public void setLocation(List<Location> location) {
	this.location = location;
    }

    public DistanceUnits getDistanceUnit() {
	return distanceUnit;
    }

    public String getDistanceUnitName() {
	return distanceUnitName;
    }

    public void setDistanceUnit(DistanceUnits distanceUnit) {
	this.distanceUnit = distanceUnit;
	this.distanceUnitName = distanceUnit.getName();
    }

    public int getPageNumber() {
	return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
	this.pageNumber = pageNumber;
    }

    public int getRecordsCount() {
	return recordsCount;
    }

    public void setRecordsCount(int recordsCount) {
	this.recordsCount = recordsCount;
    }
}
