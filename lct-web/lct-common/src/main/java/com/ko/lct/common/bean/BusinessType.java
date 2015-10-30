package com.ko.lct.common.bean;

import java.io.Serializable;

public class BusinessType implements Serializable {
	
    	private static final long serialVersionUID = -9061532993708761561L;
    	
	private int id;
	private String name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
