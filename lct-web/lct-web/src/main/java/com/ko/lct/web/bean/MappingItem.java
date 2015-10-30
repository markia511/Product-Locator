package com.ko.lct.web.bean;

public class MappingItem {

    String originalSrcVal;
    String srcVal;
    
    String dstVal;
    String type;
    
    public String getOriginalSrcVal() {
        return originalSrcVal;
    }
    public void setOriginalSrcVal(String originalSrcVal) {
        this.originalSrcVal = originalSrcVal;
    }
    
    public String getSrcVal() {
        return srcVal;
    }
    public void setSrcVal(String srcVal) {
        this.srcVal = srcVal;
    }
    public String getDstVal() {
        return dstVal;
    }
    public void setDstVal(String dstVal) {
        this.dstVal = dstVal;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

}
