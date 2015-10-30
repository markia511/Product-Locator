package com.ko.lct.common.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PrimaryContainers implements Serializable {
    private static final long serialVersionUID = 519193843705653885L;
    private List<PrimaryContainer> primaryContainer = new ArrayList<PrimaryContainer>();

    public List<PrimaryContainer> getPrimaryContainer() {
	return primaryContainer;
    }

    public void setPrimaryContainer(List<PrimaryContainer> primaryContainer) {
	this.primaryContainer = primaryContainer;
    }

}
