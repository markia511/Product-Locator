package com.ko.lct.common.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BeverageCategories implements Serializable {
	
	private static final long serialVersionUID = -6100619948866403089L;
	
	private List<BeverageCategory> beverageCategory = new ArrayList<BeverageCategory>();

	public List<BeverageCategory> getBeverageCategory() {
		return beverageCategory;
	}

	public void setBeverageCategory(List<BeverageCategory> beverageCategory) {
		this.beverageCategory = beverageCategory;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}


}
