package com.ko.lct.common.bean;

import java.io.Serializable;

public class ProductPackageType implements Serializable {
	private static final long serialVersionUID = -3601125220166405906L;
	
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
