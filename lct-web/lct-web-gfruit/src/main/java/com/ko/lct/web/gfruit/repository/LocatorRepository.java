package com.ko.lct.web.gfruit.repository;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.ko.lct.common.bean.Locations;
import com.ko.lct.common.bean.Product;

@Repository
public class LocatorRepository {

    private String googleAPIClientId;
    
    private String googleAPIChannel;
    
    @Value("${brand.code}")
    private String brandCode;
    
    @Autowired
    LocatorCache locatorCache;
    
    @Resource(name = "products")
    private List<Product> products;
    
    private String productKeysParam;
    
    @PostConstruct
    private void initProductKeysParam() {
	if(this.products != null) {
	    StringBuilder builder = new StringBuilder();
	    for(Product product: products) {
		builder.append(product.getProd().getCode());
		builder.append(",");
	    }
	    if(builder.length() > 0) {
		builder.deleteCharAt(builder.length() - 1);
	    }
	    this.productKeysParam = builder.toString();
	}
    }

    public Locations getSearchLocations(double latitude, double longitude, int distance, String distanceUnit,
	    String beverageCategoryCode, String productTypeCode, String brandCode, String flavorCode, String productCode,
	    String primaryContainerShortCode, String secondaryPackageShortCode, String businessTypeCode,
	    String physicalStateCode, String tradeChannelCode, String subTradeChannelCode, String outletName, 
	    boolean includeFoodService, boolean isKosherProduct, int pageNumber, int pageCounts, String sortColumn, 
	    String sortOrder) throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, 
	    UnsupportedEncodingException {
	return locatorCache.getSearchLocations(latitude, longitude, distance, distanceUnit,
		beverageCategoryCode, productTypeCode, brandCode, flavorCode, productCode,
		primaryContainerShortCode, secondaryPackageShortCode, businessTypeCode,
		physicalStateCode, tradeChannelCode, subTradeChannelCode, outletName, includeFoodService,  
		isKosherProduct, pageNumber, pageCounts, sortColumn, sortOrder);
    }

    public String getGoogleAPIClientId() {
        return googleAPIClientId;
    }

    public void setGoogleAPIClientId(String googleAPIClientId) {
        this.googleAPIClientId = googleAPIClientId;
    }

    public String getGoogleAPIChannel() {
        return googleAPIChannel;
    }

    public void setGoogleAPIChannel(String googleAPIChannel) {
        this.googleAPIChannel = googleAPIChannel;
    }

    public String getBrandCode() {
        return brandCode;
    }

    public void setBrandCode(String brandCode) {
        this.brandCode = brandCode;
    }

    public String getProductKeysParam() {
        return productKeysParam;
    }
    
    public List<Product> getProdsAll() {
	return Collections.unmodifiableList(products);
    }
    
}