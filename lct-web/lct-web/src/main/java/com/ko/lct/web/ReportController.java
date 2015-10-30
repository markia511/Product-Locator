package com.ko.lct.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.ko.lct.common.bean.Report;
import com.ko.lct.common.bean.ReportAPIUsage;
import com.ko.lct.common.bean.ReportQuariesPerBrand;
import com.ko.lct.web.dao.ReportDao;
import com.ko.lct.web.util.LocatorUtility;
import com.ko.lct.web.util.SearchListExcelView;

/**
 * Handles requests for the application report page.
 */
@Controller
// @SessionAttributes
@Secured({"ROLE_USER", "ROLE_ADMIN"})
public class ReportController {

    @Autowired
    private ReportDao dao;
    public static final String DATEFROM = "datefrom";
    public static final String DATETO = "dateto";

    @Autowired
    private LocatorUtility utility;

    /**
     * Simply selects the home view to render by returning its name.
     */
    @RequestMapping(value = "/report", method = { RequestMethod.GET, RequestMethod.POST })
    public String report(/* Locale locale, */Model model, HttpSession session) {
	String forward;
	if (this.utility.isAdminOrUserAuthority(session)) {
	    ReportForm form = new ReportForm();
	    model.addAttribute("reportForm", form);
	    forward = "report";
	} else {
	    forward = "redirect:";
	}
	return forward;
    }

    @RequestMapping(value = "/runReport", method = RequestMethod.GET, produces = "application/octet-stream")
    public ModelAndView generateXls(HttpServletRequest request, HttpServletResponse response) throws Exception {

	String dtFrom = request.getParameter("dateFrom");
	DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
	Date dateFrom = df.parse(dtFrom);
	String dtTo = request.getParameter("dateTo");
	Date dateTo = df.parse(dtTo);
	String type = request.getParameter("reportType");
	Map<String, Report> model = new HashMap<String, Report>();
	if ("1".equals(type)) {
	    response.setHeader("Content-Disposition", "attachment;filename=\"BrandQueryReport.xls\"");
	    List<ReportQuariesPerBrand> list2 = this.dao.getReportQuariesPerBrand(dateFrom.getTime(), dateTo.getTime());
	    model.put(SearchListExcelView.SEARCH_LIST_KEY, new Report(null, list2, dtFrom, dtTo));
	    return new ModelAndView("ReportQueriesPerBrandExcel", model);
	} else {
	    response.setHeader("Content-Disposition", "attachment;filename=\"APIUsageReport.xls\"");
	    List<ReportAPIUsage> list = this.dao.getReportAPIUsage(dateFrom.getTime(), dateTo.getTime());
	    model.put(SearchListExcelView.SEARCH_LIST_KEY, new Report(list, null, dtFrom, dtTo));
	    return new ModelAndView("ReportAPIUsageExcel", model);
	}

    }
}
