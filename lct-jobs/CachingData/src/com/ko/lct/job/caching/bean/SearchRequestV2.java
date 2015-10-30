package com.ko.lct.job.caching.bean;

public class SearchRequestV2 extends SearchRequest {
    private static final long serialVersionUID = 7694053294520006929L;

    private String productCode;

    public String getProductCode() {
	return productCode;
    }

    public void setProductCode(String productCode) {
	this.productCode = productCode;
    }

}
