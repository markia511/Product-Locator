package com.ko.lct.common.bean;

import java.io.Serializable;

public class Country implements Serializable {
	private static final long serialVersionUID = 408773189573999342L;
	
	private String code;
	private String name;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
