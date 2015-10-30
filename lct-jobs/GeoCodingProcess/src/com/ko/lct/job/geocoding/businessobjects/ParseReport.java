package com.ko.lct.job.geocoding.businessobjects;

public class ParseReport {
    
    String filename;
    int recordCount;
    int newAddressCount;
    
    public ParseReport(String filename, int recordCount, int newAddressCount) {
	super();
	this.filename = filename;
	this.recordCount = recordCount;
	this.newAddressCount = newAddressCount;
    }
    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }
    public int getRecordCount() {
        return recordCount;
    }
    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }
    public int getNewAddressCount() {
        return newAddressCount;
    }
    public void setNewAddressCount(int newAddressCount) {
        this.newAddressCount = newAddressCount;
    }

}
