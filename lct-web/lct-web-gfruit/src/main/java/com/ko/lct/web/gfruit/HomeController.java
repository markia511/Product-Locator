package com.ko.lct.web.gfruit;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ko.lct.common.bean.DistanceUnits;
import com.ko.lct.common.bean.Locations;
import com.ko.lct.web.gfruit.repository.LocatorRepository;

@Controller
public class HomeController {

    private static final int RECORD_COUNT = 25;
    private static final int PAGE_NUMBER = 1;
    private static final String SEARCH_SELECTED_VALUE_ALL = "*";
    private static final int ERROR_404 = 404;
    
    @Autowired
    LocatorRepository repository;
    
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Model model, HttpServletRequest request) {
	model.addAttribute("products", repository.getProdsAll());
	model.addAttribute("googleAPIClientId", repository.getGoogleAPIClientId());
	model.addAttribute("googleAPIChannel", repository.getGoogleAPIChannel());
	return "home";
    }
    
    @RequestMapping(value = "/search", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    Locations search(LocatorForm form, HttpServletRequest request, HttpServletResponse response) throws InvalidKeyException, NoSuchAlgorithmException, 
	    IllegalStateException, IOException {
	String productCode = this.securityProductCodeFilter(form.getProductCode());
	Locations locations = null;
	if(productCode != null) {
	    form.setProductCode(productCode);
	    locations = repository.getSearchLocations(
		form.getLatitude(), form.getLongitude(), form.getDistance(), DistanceUnits.mi.name().toLowerCase(), SEARCH_SELECTED_VALUE_ALL, SEARCH_SELECTED_VALUE_ALL,
		repository.getBrandCode(), form.getFlavorCode(), form.getProductCode(), SEARCH_SELECTED_VALUE_ALL,
		SEARCH_SELECTED_VALUE_ALL, SEARCH_SELECTED_VALUE_ALL,
		SEARCH_SELECTED_VALUE_ALL, SEARCH_SELECTED_VALUE_ALL,
		SEARCH_SELECTED_VALUE_ALL, SEARCH_SELECTED_VALUE_ALL, 
		false, false, PAGE_NUMBER, RECORD_COUNT, form.getSortColumn(), form.getSortOrder());
	} else {
	    response.sendError(ERROR_404);
	}
	return locations;
    }
 
    private String securityProductCodeFilter(String productCode) {
	if(productCode != null) {
	    if(productCode.equals(SEARCH_SELECTED_VALUE_ALL)) {
		productCode = repository.getProductKeysParam();
	    } else {
		String[] keys = productCode.split(",");
		String validProductKeys = repository.getProductKeysParam();
		boolean isValidProductCode = true;
		for(int i = 0; i < keys.length && isValidProductCode; i++) {
		    if(!validProductKeys.contains(keys[i])) {
			isValidProductCode = false;
			productCode = validProductKeys;
		    } 
		}
	    }
	}
	return productCode;
    }
    
}