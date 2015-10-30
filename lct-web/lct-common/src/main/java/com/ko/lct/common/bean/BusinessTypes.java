package com.ko.lct.common.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BusinessTypes implements Serializable {
    private static final long serialVersionUID = -361682270176512033L;

    private List<BusinessType> businessType = new ArrayList<BusinessType>();

    public List<BusinessType> getBusinessType() {
	return businessType;
    }

    public void setBusinessType(List<BusinessType> businessType) {
	this.businessType = businessType;
    }

}
