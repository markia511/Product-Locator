package com.ko.lct.common.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Flavors implements Serializable {
	private static final long serialVersionUID = -4792230013042482216L;
	
	private List<Flavor> flavor = new ArrayList<Flavor>();

	public List<Flavor> getFlavor() {
		return flavor;
	}

	public void setFlavor(List<Flavor> flavor) {
		this.flavor = flavor;
	}

}
