package com.ko.lct.web.bean;

import java.io.Serializable;

public class MappingType implements Serializable{
    private static final long serialVersionUID = -77589861662586806L;
    String id;
    String name;
    public String getCode() {
        return id;
    }
    public void setCode(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    

}
