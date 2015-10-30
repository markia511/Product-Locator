package com.ko.lct.common.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Product implements Serializable {
	private static final long serialVersionUID = 8709540824091021627L;
	
	private Prod prod;
	private Brand brand;
	private Flavor flavor;
	private List<BeverageCategory> beverageCategory = new ArrayList<BeverageCategory>();
	
	public boolean isBeverageCategoryExists(String beverageCategoryCode) {
	    if (beverageCategoryCode == null) {
		return false;
	    }
	    
	    for (BeverageCategory bevCat : this.beverageCategory) {
		if (beverageCategoryCode.equals(bevCat.getCode())) {
		    return true;
		}
	    }
	    return false;
	}
	
	public Prod getProd() {
	    return prod;
	}

	public void setProd(Prod prod) {
	    this.prod = prod;
	}

	public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}

	public Flavor getFlavor() {
		return flavor;
	}

	public void setFlavor(Flavor flavor) {
		this.flavor = flavor;
	}

	public List<BeverageCategory> getBeverageCategory() {
	    return beverageCategory;
	}

	public void setBeverageCategory(List<BeverageCategory> beverageCategory) {
	    this.beverageCategory = beverageCategory;
	}

}
