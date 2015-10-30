package com.ko.lct.job.geocoding.businessobjects;

public class GenericValueObject {
    
    private String code;
    private String value;
    
    public GenericValueObject() {
    }
    
    public GenericValueObject(String code, String value) {
	this.code = code;
	this.value = value;
    }
    
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }    
}
