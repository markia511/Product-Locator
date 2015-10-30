package com.ko.lct.ws.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ko.lct.common.bean.Outlet;

public class OutletWrapper extends Outlet {
    private static final long serialVersionUID = -2919325949790353450L;

    private int outletId;

    @JsonIgnore
    public int getOutletId() {
	return outletId;
    }

    public void setOutletId(int outletId) {
	this.outletId = outletId;
    }

}
