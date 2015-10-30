package com.ko.lct.job.caching.bean;

public enum DistanceUnits {
	mi("Miles"), km("Km");
	
	private DistanceUnits(String name) {
	    this.name = name;
	}
	
	private String name;

	public String getName() {
	    return name;
	}

	public void setName(String name) {
	    this.name = name;
	}
}
