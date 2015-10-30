package com.ko.lct.common.bean;

public class State extends BaseDictionaryItem {
    private static final long serialVersionUID = -4489807352438627383L;

    private Country country;

    public Country getCountry() {
	return country;
    }

    public void setCountry(Country country) {
	this.country = country;
    }

}
