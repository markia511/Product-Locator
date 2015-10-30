package com.ko.lct.web;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ko.lct.common.bean.BaseDictionaryItem;
import com.ko.lct.common.uri.WsUriTemplates;
import com.ko.lct.web.bean.GenericAndUnmappedItems;
import com.ko.lct.web.dao.MappingDao;
import com.ko.lct.web.repository.LocatorRepository;
import com.ko.lct.web.util.LocatorUtility;

/**
 * Handles requests for the application report page.
 */

@Controller
// @SessionAttributes
@Secured({"ROLE_USER", "ROLE_ADMIN"})
public class MappingController {
    private static final Logger logger = LoggerFactory.getLogger(MappingController.class);

    @Autowired
    LocatorRepository repository;

    @Autowired
    private MappingDao dao;

    @Autowired
    private LocatorUtility utility;

    @RequestMapping(value = "/mapping", method = { RequestMethod.GET, RequestMethod.POST })
    public String mappingNew(/* Locale locale, */Model model, HttpSession session) {
	String forward;
	if (this.utility.isAdminOrUserAuthority(session)) {
	    model.addAttribute("mappingTypes", this.dao.getMappingTypes());
	    MappingForm form = new MappingForm();
	    model.addAttribute("mappingForm", form);
	    forward = "mapping";
	} else {
	    forward = "redirect:";
	}
	return forward;
    }

    @RequestMapping(value = "/get_generic_list/{mappingType}", method = { RequestMethod.POST }, produces = WsUriTemplates.APP_JSON_MIME)
    public @ResponseBody
    GenericAndUnmappedItems getGenericList(@PathVariable(value = "mappingType") String mappingType) {
	GenericAndUnmappedItems retValue = new GenericAndUnmappedItems();
	List<BaseDictionaryItem> mappingsList = this.dao.getGenericItemsList(mappingType);
	retValue.getGenericItems().addAll(mappingsList);
	List<String> unmappedList = this.dao.getUnmappedItemsList(mappingType);
	retValue.getUnmappedItems().addAll(unmappedList);
	retValue.setMappingTypeCode(mappingType);
	return retValue;
    }

    @RequestMapping(value = "/get_mapped_values/{mappingType}/{genericCode}", method = { RequestMethod.POST }, produces = WsUriTemplates.APP_JSON_MIME)
    public @ResponseBody
    List<String> getMappedItemsList(
	    @PathVariable(value = "mappingType") String mappingType,
	    @PathVariable(value = "genericCode") String genericCode) {
	return this.dao.getMappedItemsList(mappingType, genericCode);
    }

    @RequestMapping(value = "/get_unmapped_values/{mappingType}", method = { RequestMethod.POST }, produces = WsUriTemplates.APP_JSON_MIME)
    public @ResponseBody
    List<String> getUnmappedItemsList(
	    @PathVariable(value = "mappingType") String mappingType) {
	return this.dao.getUnmappedItemsList(mappingType);
    }

    @RequestMapping(value = "/map_value/{mappingType}/{genericCode}/{value}", method = { RequestMethod.POST }, produces = WsUriTemplates.APP_JSON_MIME)
    public @ResponseBody
    boolean mapValue(
	    @PathVariable(value = "mappingType") Integer mappingType,
	    @PathVariable(value = "genericCode") String genericCode,
	    @PathVariable(value = "value") String value) {
	boolean updateFlag = this.dao.mapValue(mappingType, genericCode, unEscapeParam(value));
	if (updateFlag) {
	    this.utility.clearCache(mappingType.intValue());
	}
	return updateFlag;
    }

    @RequestMapping(value = "/unmap_value/{mappingType}/{genericCode}/{value}", method = { RequestMethod.POST }, produces = WsUriTemplates.APP_JSON_MIME)
    public @ResponseBody
    boolean unmapValue(
	    @PathVariable(value = "mappingType") String mappingType,
	    @PathVariable(value = "genericCode") String genericCode,
	    @PathVariable(value = "value") String value) {
	boolean updateFlag = this.dao.unmapValue(mappingType, genericCode, unEscapeParam(value));
	return updateFlag;
    }

    @RequestMapping(value = "/add_generic_value/{mappingType}/{isAutoGenerateCode}/{newCode}/{newValue}", method = { RequestMethod.POST }, produces = WsUriTemplates.APP_JSON_MIME)
    public @ResponseBody
    boolean addNewGenericValue(
	    @PathVariable(value = "mappingType") String mappingType,
	    @PathVariable(value = "isAutoGenerateCode") String isAutoGenerateCode,
	    @PathVariable(value = "newCode") String newCode,
	    @PathVariable(value = "newValue") String newValue) {
	return this.dao.addNewGenericValue(mappingType, isAutoGenerateCode, unEscapeParam(newCode), unEscapeParam(newValue));
    }

    @RequestMapping(value = "/update_generic_value/{mappingType}/{editCode}/{editValue}", method = { RequestMethod.POST }, produces = WsUriTemplates.APP_JSON_MIME)
    public @ResponseBody
    boolean updateGenericValue(
	    @PathVariable(value = "mappingType") String mappingType,
	    @PathVariable(value = "editCode") String editCode,
	    @PathVariable(value = "editValue") String editValue) {
	boolean updateFlag = this.dao.updateGenericValue(mappingType, unEscapeParam(editCode), unEscapeParam(editValue));
	if (updateFlag) {
	    this.utility.clearCache(Integer.parseInt(mappingType));
	}
	return updateFlag;
    }

    @RequestMapping(value = "/remove_generic_value/{mappingType}/{code}", method = { RequestMethod.POST }, produces = WsUriTemplates.APP_JSON_MIME)
    public @ResponseBody
    boolean removeGenericValue(
	    @PathVariable(value = "mappingType") String mappingType,
	    @PathVariable(value = "code") String code) {
	return this.dao.removeGenericValue(mappingType, unEscapeParam(code));
    }

    private static final String unEscapeParam(String value) {
	if (value != null) {
	    return value.replace("%2E", ".").replace("%5C", "\\").replace("%2F", "/").replace("%3B", ";");
	}
	return value;
    }

}
