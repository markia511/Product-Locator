package com.ko.lct.web.gfruit;

import java.io.Serializable;

public class LocatorForm implements Serializable {

    private static final long serialVersionUID = 3660162138334649654L;
    private static final int MAX_DISTANCE = 100;
    
    private double latitude;
    private double longitude;
    private int distance;
    private String flavorCode;
    private String productCode;
    private String sortColumn; 
    private String sortOrder;
    
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
    public int getDistance() {
        return distance;
    }
    public void setDistance(int distance) {
	if(distance > MAX_DISTANCE) {
	    this.distance = MAX_DISTANCE;
	} else {
	    this.distance = distance;
	}
    }
    public String getFlavorCode() {
        return flavorCode;
    }
    public void setFlavorCode(String flavorCode) {
        this.flavorCode = flavorCode;
    }
    public String getProductCode() {
        return productCode;
    }
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
    public String getSortColumn() {
        return sortColumn;
    }
    public void setSortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
    }
    public String getSortOrder() {
        return sortOrder;
    }
    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
    
}