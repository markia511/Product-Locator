package com.ko.lct.web.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.ko.lct.common.bean.BaseDictionaryItem;
import com.ko.lct.web.bean.DictionaryItem;

@XmlRootElement
public class DictionaryItems implements Serializable {

	private static final long serialVersionUID = -3681852435794460467L;

	private List<DictionaryItem> dictionaryItems = new ArrayList<DictionaryItem>();

	@XmlElement(name = "dictionaryItem")
	public List<DictionaryItem> getDictionaryItems() {
		return dictionaryItems;
	}

	public void setDictionaryItems(List<DictionaryItem> dictionaryItems) {
		this.dictionaryItems = dictionaryItems;
	}
	
	public static final List<DictionaryItem> listToDictionaryItemList(List<? extends BaseDictionaryItem> baseDictionaryItemList, DictionaryEnum dictionaryType) {
		List<DictionaryItem> dictionaryItemList = new ArrayList<DictionaryItem>();
		for (BaseDictionaryItem baseDisctionaryItem : baseDictionaryItemList) {
			DictionaryItem dictionaryItem = new DictionaryItem();
			dictionaryItem.setCode(baseDisctionaryItem.getCode());
			dictionaryItem.setName(baseDisctionaryItem.getName());
			dictionaryItem.setDictionary(dictionaryType);
			dictionaryItemList.add(dictionaryItem);
		}
		return dictionaryItemList;
	}
}
