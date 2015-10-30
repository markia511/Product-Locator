package com.ko.lct.job.common.businessobjects;

public enum GeocodeLevelEnum {

    EXACT_STREET_NUMBER(6), ESTABLISHMENT(5), STREET_NUMBER(4), ROUTE(3), POSTAL_CODE(2), CITY(1), UNDEFINED(0);

    private int level;

    private GeocodeLevelEnum(int level) {
	this.level = level;
    }

    public int getLevel() {
	return level;
    }
}
