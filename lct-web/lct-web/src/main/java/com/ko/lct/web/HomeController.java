package com.ko.lct.web;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.ko.lct.common.bean.BaseDictionaryItem;
import com.ko.lct.common.bean.Brand;
import com.ko.lct.common.bean.Country;
import com.ko.lct.common.bean.DistanceUnits;
import com.ko.lct.common.bean.Flavor;
import com.ko.lct.common.bean.Locations;
import com.ko.lct.common.bean.PrimaryContainer;
import com.ko.lct.common.bean.Prod;
import com.ko.lct.common.bean.SecondaryPackage;
import com.ko.lct.common.bean.State;
import com.ko.lct.common.bean.SubTradeChannel;
import com.ko.lct.common.bean.TradeChannel;
import com.ko.lct.common.util.LocatorConstants;
import com.ko.lct.web.bean.DictionaryItem;
import com.ko.lct.web.repository.LocatorRepository;
import com.ko.lct.web.util.LocatorUtility;
import com.ko.lct.web.util.SearchListExcelView;
import com.ko.lct.web.util.SearchListPDFView;

/**
 * Handles requests for the application home page.
 */
@Controller
@SessionAttributes
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    private static final int DEFAULT_ITEM_PER_PAGE = 5;
    private static final int DEFAULT_COUNT_PAGE = 1;
    private static final int DEFAULT_NUMBER_PAGE = 1;
    private static final String LAST_SEARCHED_LOCATIONS = "lastSearchedLocations";

    @Autowired
    LocatorRepository repository;
    
    @Autowired
    private LocatorUtility utility;    

    /**
     * Simply selects the home view to render by returning its name.
     */
    @RequestMapping(value = "/", method = { RequestMethod.GET, RequestMethod.POST })
    public String home(/* Locale locale, */Model model, HttpServletRequest request, HttpSession session) {
	// logger.info("Welcome home! the client locale is " +
	// locale.toString());
	if (request.getRequestURI() != null && !request.getRequestURI().endsWith("/")) {
	    return "redirect:/";
	}
	// utility.initUser(session);

	model.addAttribute("beverageCategories", repository.getBeverageCategories());
	model.addAttribute("beverageBrands", repository.getBrandsAll());
	model.addAttribute("products", repository.getProdsAll());
	model.addAttribute("beverageFlavors", repository.getFlavorsAll());
	model.addAttribute("physicalStates", repository.getPhysicalStates());
	model.addAttribute("tradeChannels", repository.getTradeChannelsAll());
	model.addAttribute("subTradeChannels", repository.getSubTradeChannelsAll());
	
	/*
	model.addAttribute("primaryContainers", repository.getPrimaryContainersAll());
	model.addAttribute("secondaryPackages", repository.getSecondaryPackagesAll());
	*/
	
	model.addAttribute("shortPrimaryContainers", repository.getShortPrimaryContainersAll());
	model.addAttribute("shortSecondaryPackages", repository.getShortSecondaryPackagesAll());
	
	model.addAttribute("productPackageTypes", repository.getProductPackageTypes());
	model.addAttribute("businessTypes", repository.getBusinessTypes());
	model.addAttribute("states", repository.getStatesAll());
	model.addAttribute("countries", repository.getCountries());
	model.addAttribute("itemPerPage", Integer.valueOf(DEFAULT_ITEM_PER_PAGE));
	model.addAttribute("countPage", Integer.valueOf(DEFAULT_COUNT_PAGE));
	model.addAttribute("numPage", Integer.valueOf(DEFAULT_NUMBER_PAGE));
	model.addAttribute("pageList", getPageList(DEFAULT_COUNT_PAGE));
	model.addAttribute("distanceUnits", DistanceUnits.values());
	model.addAttribute("googleAPIClientId", repository.getGoogleAPIClientId());
	model.addAttribute("googleAPIChannel", repository.getGoogleAPIChannel());
	LocatorForm form = new LocatorForm();
	model.addAttribute("locatorForm", form);

	return "home";
    }

    /*
    @RequestMapping(value = "/resources/styles/PIE.htc", method = { RequestMethod.GET })
    public static AbstractUrlBasedView pieHtc(HttpServletResponse response) {
//	response.setContentType("text/x-component");
//	response.setCharacterEncoding("UTF-8");
	return new AbstractUrlBasedView() {
	    @SuppressWarnings("unchecked")
	    @Override
	    protected void renderMergedOutputModel(Map model, HttpServletRequest request,
	                                           HttpServletResponse response) throws Exception {
	        response.setContentType("text/html");
	        response.getWriter().write(json);
	    }
	};
    }
    */
    
    private static List<Integer> getPageList(int countPages) {
	List<Integer> pageList = new ArrayList<Integer>();
	for (int i = 1; i <= countPages; i++) {
	    pageList.add(Integer.valueOf(i));
	}
	return pageList;
    }

    @RequestMapping(value = "/beverageBrand/{beverageCategoryCode}", method = RequestMethod.POST)
    public @ResponseBody
    List<Brand> getBeverageBrands(@PathVariable(value = "beverageCategoryCode") String beverageCategoryCode) {
	return repository.getBeverageBrands(beverageCategoryCode);
    }

    @RequestMapping(value = "/beverageFlavor/{beverageCategoryCode}/{beverageBrandCode}", method = RequestMethod.POST)
    public @ResponseBody
    List<Flavor> getBeverageFlavors(@PathVariable(value = "beverageCategoryCode") String beverageCategoryCode,
	    @PathVariable(value = "beverageBrandCode") String beverageBrandCode) {
	return repository.getFlavors(beverageCategoryCode, beverageBrandCode);
    }

    @RequestMapping(value = "/product/{beverageCategoryCode}/{beverageBrandCode}/{beverageFlavorCode}", method = RequestMethod.POST)
    public @ResponseBody
    List<Prod> getProducts(
	    @PathVariable(value = "beverageCategoryCode") String beverageCategoryCode,
	    @PathVariable(value = "beverageBrandCode") String beverageBrandCode,
	    @PathVariable(value = "beverageFlavorCode") String beverageFlavorCode) {
	return repository.getProds(beverageCategoryCode, beverageBrandCode, beverageFlavorCode);
    }
    
    @RequestMapping(value = "/tradeChannel/{includeFoodService}", method = RequestMethod.POST)
    public @ResponseBody
    List<TradeChannel> getTradeChannels(@PathVariable(value = "includeFoodService") boolean includeFoodService) {
	return repository.getTradeChannels(includeFoodService);
    }

    @RequestMapping(value = "/subTradeChannel/{tradeChannelCode}/{includeFoodService}", method = RequestMethod.POST)
    public @ResponseBody
    List<SubTradeChannel> getSubTradeChannels(@PathVariable(value = "tradeChannelCode") String tradeChannelCode,
	    @PathVariable(value = "includeFoodService") boolean includeFoodService) {
	return repository.getSubTradeChannels(tradeChannelCode, includeFoodService);
    }

    @RequestMapping(value = "/primaryContainer/{beverageCategoryCode}/{brandCode}", method = RequestMethod.POST)
    public @ResponseBody
    List<PrimaryContainer> getPrimaryContainer(@PathVariable(value = "beverageCategoryCode") String beverageCategoryCode,
	    @PathVariable(value = "brandCode") String brandCode) {
	return repository.getPrimaryContainers(beverageCategoryCode, brandCode);
    }

    @RequestMapping(value = "/shortPrimaryContainer/{brandCode}/{productCode}", method = RequestMethod.POST)
    public @ResponseBody
    List<PrimaryContainer> getShortPrimaryContainer(
	    @PathVariable(value = "brandCode") String brandCode,
	    @PathVariable(value = "productCode") String productCode) {
	return repository.getShortPrimaryContainers(brandCode, productCode);
    }
    
    @RequestMapping(value = "/secondaryPackage/{beverageCategoryCode}/{brandCode}/{primaryContainerCode}", method = RequestMethod.POST)
    public @ResponseBody
    List<SecondaryPackage> getSecondaryPackages(@PathVariable(value = "primaryContainerCode") String primaryContainerCode,
	    @PathVariable(value = "beverageCategoryCode") String beverageCategoryCode,
	    @PathVariable(value = "brandCode") String brandCode) {
	return repository.getSecondaryPackages(beverageCategoryCode, brandCode, primaryContainerCode);
    }
    
    @RequestMapping(value = "/shortSecondaryPackage/{brandCode}/{productCode}/{primaryContainerShortCode}", method = RequestMethod.POST)
    public @ResponseBody
    List<BaseDictionaryItem> getPackageSizes(
	    @PathVariable(value = "brandCode") String brandCode,
	    @PathVariable(value = "productCode") String productCode,
	    @PathVariable(value = "primaryContainerShortCode") String primaryContainerShortCode) {
	primaryContainerShortCode = primaryContainerShortCode.replace("strange", ".");
	brandCode = brandCode.replace("strange", ".");
	return repository.getShortSecondaryPackages(brandCode, productCode, primaryContainerShortCode);
    }

    @RequestMapping(value = "/state/{countryCode}", method = RequestMethod.POST)
    public @ResponseBody
    List<State> getStates(@PathVariable(value = "countryCode") String countryCode) {
	return repository.getStates(countryCode);
    }

    @RequestMapping(value = "/country/{stateCode}", method = RequestMethod.POST)
    public @ResponseBody
    Country getCountry(@PathVariable(value = "stateCode") String stateCode) {
	return repository.getCountryByState(stateCode);
    }

    @RequestMapping(value = "/keywordDictionary", method = RequestMethod.POST)
    public @ResponseBody
    List<DictionaryItem> getKeywordDictionary() {
	return repository.getKeywordDictionary();
    }

    @RequestMapping(value = "/search/{latitude}/{longitude}/{distance}/{distanceUnit}/" +
	    "{beverageCategoryCode}/{productTypeCode}/{brandCode}/{flavorCode}/{productCode}/" +
	    "{primaryContainerShortCode}/{secondaryPackageShortCode}/{businessTypeCode}/" +
	    "{physicalStateCode}/{tradeChannelCode}/{subTradeChannelCode}/{outletName}/" +
	    "{includeFoodService}/{isKosherProduct}/{pageNumber}/{recordCount}/{sortColumn}/{sortOrder}", 
	    method = RequestMethod.POST)
    public @ResponseBody
    Locations search(
	    @PathVariable(value = "latitude") double latitude,
	    @PathVariable(value = "longitude") double longitude,
	    @PathVariable(value = "distance") int distance,
	    @PathVariable(value = "distanceUnit") String distanceUnit,
	    @PathVariable(value = "beverageCategoryCode") String beverageCategoryCode,
	    @PathVariable(value = "productTypeCode") String productTypeCode,
	    @PathVariable(value = "brandCode") String brandCode,
	    @PathVariable(value = "flavorCode") String flavorCode,
	    @PathVariable(value = "productCode") String productCode,
	    @PathVariable(value = "primaryContainerShortCode") String primaryContainerShortCode,
	    @PathVariable(value = "secondaryPackageShortCode") String secondaryPackageShortCode,
	    @PathVariable(value = "businessTypeCode") String businessTypeCode,
	    @PathVariable(value = "physicalStateCode") String physicalStateCode,
	    @PathVariable(value = "tradeChannelCode") String tradeChannelCode,
	    @PathVariable(value = "subTradeChannelCode") String subTradeChannelCode,
	    @PathVariable(value = "outletName") String outletName,
	    @PathVariable(value = "includeFoodService") boolean includeFoodService,
	    @PathVariable(value = "isKosherProduct") boolean isKosherProduct,
	    @PathVariable(value = "pageNumber") int pageNumber,
	    @PathVariable(value = "recordCount") int recordCount,
	    @PathVariable(value = "sortColumn") String sortColumn, 
	    @PathVariable(value = "sortOrder") String sortOrder,
	    HttpServletRequest request) throws InvalidKeyException, NoSuchAlgorithmException, 
	    IllegalStateException, UnsupportedEncodingException {
	
	if (LocatorConstants.MAX_OUTLET_RECORDS_COUNT < pageNumber * recordCount) {
	    recordCount = (pageNumber * recordCount) - LocatorConstants.MAX_OUTLET_RECORDS_COUNT;
	}
	Locations locations = repository.getSearchLocations(
		latitude, longitude, distance, distanceUnit, beverageCategoryCode, productTypeCode,
		brandCode, flavorCode, productCode, 
		primaryContainerShortCode, secondaryPackageShortCode, businessTypeCode,
		physicalStateCode, tradeChannelCode, subTradeChannelCode, outletName, includeFoodService,
		isKosherProduct, pageNumber, recordCount, sortColumn, sortOrder);
	setSessionSearchLocations(request, locations);
	return locations;
    }

    @RequestMapping(value = "/SearchResultXls", method = {RequestMethod.GET, RequestMethod.HEAD})
    public static ModelAndView generateXls(HttpServletRequest request, HttpServletResponse response) throws Exception {
	Map<String, Locations> model = new HashMap<String, Locations>();
	Locations locations = getSessionSearchLocations(request);
	if (locations != null) {
	    response.setHeader("Content-Disposition", "attachment; filename=\"SearchResult.xls\"");
	    model.put(SearchListExcelView.SEARCH_LIST_KEY, locations);
	    return new ModelAndView("SearchListExcel", model);
	}
	return new ModelAndView("WEB-INF/views/home", "home", null);
    }

    @RequestMapping(value = "/SearchResultPdf", method = RequestMethod.GET)
    protected static ModelAndView generatePDF(HttpServletRequest request,
	    HttpServletResponse response) throws Exception {
	Map<String, Locations> model = new HashMap<String, Locations>();
	Locations locations = getSessionSearchLocations(request);
	if (locations != null) {
	    response.setHeader("Content-Disposition", "attachment; filename=\"SearchResult.pdf\"");
	    model.put(SearchListPDFView.SEARCH_LIST_KEY, locations);
	    return new ModelAndView(new SearchListPDFView(), model);
	}
	return new ModelAndView("WEB-INF/views/home", "home", null);

    }
    
    private static void setSessionSearchLocations(HttpServletRequest request, Locations locations) {
	HttpSession session = request.getSession();
	session.setAttribute(LAST_SEARCHED_LOCATIONS, locations);
    }
    
    private static Locations getSessionSearchLocations(HttpServletRequest request) {
	HttpSession session = request.getSession();
	return (Locations) session.getAttribute(LAST_SEARCHED_LOCATIONS);
    }

}
