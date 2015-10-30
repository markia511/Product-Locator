package com.ko.lct.web.bean;

import com.ko.lct.common.bean.BaseDictionaryItem;

public class DictionaryItem extends BaseDictionaryItem {

    private static final long serialVersionUID = -2511700488184675060L;

    private DictionaryEnum dictionary;

    public DictionaryItem() {
    }

    public DictionaryItem(String code, String name, DictionaryEnum dictionary) {
	setCode(code);
	setName(name);
	setDictionary(dictionary);
    }

    public DictionaryEnum getDictionary() {
	return dictionary;
    }

    public void setDictionary(DictionaryEnum dictionary) {
	this.dictionary = dictionary;
    }
}
