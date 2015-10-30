package com.ko.lct.common.bean;

import java.util.List;

public class Report {
    List<ReportAPIUsage> repotAPIUsageList;
    List<ReportQuariesPerBrand> reportQuariesPerBrandList;
    String dateFrom;
    String dateTo;
    public List<ReportAPIUsage> getRepotAPIUsageList() {
        return repotAPIUsageList;
    }
    public void setRepotAPIUsageList(List<ReportAPIUsage> repotAPIUsageList) {
        this.repotAPIUsageList = repotAPIUsageList;
    }
    public Report(List<ReportAPIUsage> repotAPIUsageList, List<ReportQuariesPerBrand> reportQuariesPerBrandList, String dateFrom, String dateTo) {
	super();
	this.repotAPIUsageList = repotAPIUsageList;
	this.reportQuariesPerBrandList = reportQuariesPerBrandList;
	this.dateFrom = dateFrom;
	this.dateTo = dateTo;
    }
    public List<ReportQuariesPerBrand> getReportQuariesPerBrandList() {
        return reportQuariesPerBrandList;
    }
    public void setReportQuariesPerBrandList(List<ReportQuariesPerBrand> reportQuariesPerBrandList) {
        this.reportQuariesPerBrandList = reportQuariesPerBrandList;
    }
    public String getDateFrom() {
        return dateFrom;
    }
    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }
    public String getDateTo() {
        return dateTo;
    }
    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

}
