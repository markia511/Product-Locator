package com.ko.lct.common.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SecondaryPackages implements Serializable {
    private static final long serialVersionUID = 8881933152933416528L;

    private List<SecondaryPackage> secondaryPackage = new ArrayList<SecondaryPackage>();

    public List<SecondaryPackage> getSecondaryPackage() {
	return secondaryPackage;
    }

    public void setSecondaryPackage(List<SecondaryPackage> secondaryPackage) {
	this.secondaryPackage = secondaryPackage;
    }

}
