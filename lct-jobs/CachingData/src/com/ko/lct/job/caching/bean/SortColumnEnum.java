package com.ko.lct.job.caching.bean;

public enum SortColumnEnum {
    
    OUTLET_NAME("outlet.CHN_NM"), DISTANCE("DIST"), TRADE_CHANNEL("CHNL_NM"), 
    TRADE_SUB_CHANNEL("SB_CHNL_NM");
    
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
