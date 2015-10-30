package com.ko.lct.common.bean;

public enum SortColumnEnum {
    
    OUTLET_NAME("OUTLET_NAME"),
    DISTANCE("DISTANCE"),
    TRADE_CHANNEL("TRADE_CHANNEL"),
    TRADE_SUB_CHANNEL("TRADE_SUB_CHANNEL");
    
    private String columnName;

    private SortColumnEnum(String columnName) {
	this.columnName = columnName;
    }
    
    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
     
}
