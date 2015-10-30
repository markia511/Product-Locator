package com.ko.lct.ws;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ko.lct.common.bean.Locations;
import com.ko.lct.common.bean.SearchRequest;
import com.ko.lct.common.bean.SearchRequestV2;
import com.ko.lct.common.uri.WsUriTemplates;
import com.ko.lct.ws.bean.ServiceTimer;
import com.ko.lct.ws.dao.LocatorDao;
import com.ko.lct.ws.exception.InvalidSignatureException;

@RestController
public class SearchController {
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private LocatorDao dao;

    @RequestMapping(value = WsUriTemplates.SEARCH_JSON, produces = { WsUriTemplates.APP_JSON_MIME })
    public @ResponseBody
    Locations getLocationsJson(HttpServletResponse response, SearchRequest searchRequest, Model model) {
	try {
	    ServiceTimer timer = new ServiceTimer();
	    Locations locations = this.dao.getLocation(searchRequest);
	    logger.info("Getting locations by json completed in " + timer.getCurrentDurationTime() + " ms, " +
	    		"signature - " + searchRequest.getSignature());
	    return locations;
	} catch (InvalidSignatureException e) {
	    response.setStatus(403);
	    return null;
	}
    }

    @RequestMapping(value = WsUriTemplates.SEARCH_XML, produces = { WsUriTemplates.APP_XML_MIME })
    public @ResponseBody
    Locations getLocationsXml(HttpServletResponse response, SearchRequest searchRequest, Model model) {
	try {
	    ServiceTimer timer = new ServiceTimer();
	    Locations locations = this.dao.getLocation(searchRequest);
	    logger.info("Getting locations by xml completed in " + timer.getCurrentDurationTime() + " ms, " +
	    		"signature - " + searchRequest.getSignature());
	    return locations;
	} catch (InvalidSignatureException e) {
	    response.setStatus(403);
	    return null;
	}
    }
    
    
    @RequestMapping(value = WsUriTemplates.SEARCH_V2_JSON, produces = { WsUriTemplates.APP_JSON_MIME })
    public @ResponseBody
    Locations getLocationsJson(HttpServletResponse response, SearchRequestV2 searchRequest) {
	try {
	    ServiceTimer timer = new ServiceTimer();
	    Locations locations = this.dao.getLocation(searchRequest);
	    logger.info("Getting locations by json completed in " + timer.getCurrentDurationTime() + " ms, " +
	    		"signature - " + searchRequest.getSignature());
	    return locations;
	} catch (InvalidSignatureException e) {
	    response.setStatus(403);
	    return null;
	}
    }

    @RequestMapping(value = WsUriTemplates.SEARCH_V2_XML, produces = { WsUriTemplates.APP_XML_MIME })
    public @ResponseBody
    Locations getLocationsXml(HttpServletResponse response, SearchRequestV2 searchRequest) {
	try {
	    ServiceTimer timer = new ServiceTimer();
	    Locations locations = this.dao.getLocation(searchRequest);
	    logger.info("Getting locations by xml completed in " + timer.getCurrentDurationTime() + " ms, " +
	    		"signature - " + searchRequest.getSignature());
	    return locations;
	} catch (InvalidSignatureException e) {
	    response.setStatus(403);
	    return null;
	}
    }
    
}
