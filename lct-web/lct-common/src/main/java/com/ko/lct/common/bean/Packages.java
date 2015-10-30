package com.ko.lct.common.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Packages implements Serializable {
    
    private static final long serialVersionUID = 5707721868278882994L;
    
    private List<Package> packages = new ArrayList<Package>();

    public List<Package> getPackage() {
        return packages;
    }

    public void setPackage(List<Package> packages) {
        this.packages = packages;
    }
    
}
