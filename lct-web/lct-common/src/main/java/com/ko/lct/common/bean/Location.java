package com.ko.lct.common.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Location implements Serializable {
	private static final long serialVersionUID = -6423894977654533040L;
	
	private Outlet outlet;
	private double distance;
	private List<ProductPackage> productPackage = new ArrayList<ProductPackage>();

	public Outlet getOutlet() {
		return outlet;
	}

	public void setOutlet(Outlet outlet) {
		this.outlet = outlet;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public List<ProductPackage> getProductPackage() {
		return productPackage;
	}

	public void setProductPackage(List<ProductPackage> productPackage) {
		this.productPackage = productPackage;
	}

}
