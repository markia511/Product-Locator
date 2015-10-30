package com.ko.lct.common.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ProductPackageTypes implements Serializable {

    private static final long serialVersionUID = 3138481410328709789L;
    
    private List<ProductPackageType> productPackageTypes = new ArrayList<ProductPackageType>();

    public List<ProductPackageType> getProductPackageTypes() {
        return productPackageTypes;
    }

    public void setProductPackageTypes(List<ProductPackageType> productPackageTypes) {
        this.productPackageTypes = productPackageTypes;
    }
    
}
