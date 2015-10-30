package com.ko.lct.common.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PhysicalStates implements Serializable {
    private static final long serialVersionUID = 2623183967224147665L;

    private List<PhysicalState> physicalState = new ArrayList<PhysicalState>();

    public List<PhysicalState> getPhysicalState() {
	return physicalState;
    }

    public void setPhysicalState(List<PhysicalState> physicalState) {
	this.physicalState = physicalState;
    }

}
