package com.ko.lct.ws.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ko.lct.common.bean.ProductPackage;

public class ProductPackageWrapper extends ProductPackage {
    private static final long serialVersionUID = -488265038863090547L;

    private int outletId;

    @JsonIgnore
    public int getOutletId() {
	return outletId;
    }

    public void setOutletId(int outletId) {
	this.outletId = outletId;
    }

}
