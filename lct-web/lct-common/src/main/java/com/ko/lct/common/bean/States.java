package com.ko.lct.common.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class States implements Serializable {
    private static final long serialVersionUID = -8367549392328184807L;

    private List<State> state = new ArrayList<State>();

    public List<State> getState() {
	return state;
    }

    public void setState(List<State> state) {
	this.state = state;
    }

}
