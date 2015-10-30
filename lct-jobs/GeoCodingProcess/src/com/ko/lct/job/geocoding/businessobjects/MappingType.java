package com.ko.lct.job.geocoding.businessobjects;

public enum MappingType {
    BEVERAGE_CATEGORY(1), BRAND(2), FLAVOR(3), PRIMARY_CONTAINER(4), SECONDARY_PACKAGE(5), SUB_CHANNEL(6), PRODUCT(7);
	
    private int value;
	
    private MappingType(int value) {
	this.value = value;
    }

    public int getValue() {
	return value;
    }
}
