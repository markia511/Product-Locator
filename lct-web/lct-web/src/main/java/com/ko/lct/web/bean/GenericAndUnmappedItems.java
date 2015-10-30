package com.ko.lct.web.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.ko.lct.common.bean.BaseDictionaryItem;

public class GenericAndUnmappedItems implements Serializable {
    private static final long serialVersionUID = -680342283649755826L;

    private String mappingTypeCode;
    private List<BaseDictionaryItem> genericItems = new ArrayList<BaseDictionaryItem>();
    private List<String> unmappedItems = new ArrayList<String>();

    public String getMappingTypeCode() {
	return mappingTypeCode;
    }

    public void setMappingTypeCode(String mappingTypeCode) {
	this.mappingTypeCode = mappingTypeCode;
    }

    public List<BaseDictionaryItem> getGenericItems() {
	return genericItems;
    }

    public void setGenericItems(List<BaseDictionaryItem> genericItems) {
	this.genericItems = genericItems;
    }

    public List<String> getUnmappedItems() {
	return unmappedItems;
    }

    public void setUnmappedItems(List<String> unmappedItems) {
	this.unmappedItems = unmappedItems;
    }

}
