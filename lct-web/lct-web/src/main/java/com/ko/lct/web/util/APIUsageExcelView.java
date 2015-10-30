package com.ko.lct.web.util;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.ko.lct.common.bean.Report;
import com.ko.lct.common.bean.ReportAPIUsage;

public class APIUsageExcelView extends AbstractExcelView
{
    public static final String SEARCH_LIST_KEY = "SearshList";
    protected static final int BRAND_NAME_COLUMN = 0;
    protected static final int ANY_PACKAGE_COLUMN = 1;
    private static final int SPEC_PACKAGE_COLUMN = 2;

    public APIUsageExcelView() {
	super();
	setUrl("/resources/xls/Product_Locator_API_Usage_Report");
    }

    @Override
    protected void buildExcelDocument(Map<String, Object> model,
	    HSSFWorkbook workbook,
	    HttpServletRequest request,
	    HttpServletResponse response) throws Exception {
	/*
	 * if (workbook.getNumberOfSheets() == 0) { setUrl("/resources/xls/Product_Locator_API_Usage_Report"); renderMergedOutputModel(model, request, response); } else {
	 */
	HSSFSheet sheet = workbook.getSheetAt(0);
	HSSFCellStyle ordinStyle = workbook.createCellStyle();
	ordinStyle.setBorderRight(CellStyle.BORDER_THIN);
	ordinStyle.setBorderLeft(CellStyle.BORDER_THIN);

	HSSFCellStyle lineStyle = workbook.createCellStyle();
	lineStyle.setBorderTop(CellStyle.BORDER_THIN);

	short currentRow = 1;
	Report report = (Report) model.get(SEARCH_LIST_KEY);
	if (report != null) {
	    List<ReportAPIUsage> list = report.getRepotAPIUsageList();
	    HSSFRow row = sheet.getRow(0);
	    row.createCell(0).setCellValue("Date Range: " + report.getDateFrom() + " - " + report.getDateTo());
	    for (ReportAPIUsage object : list) {
		currentRow++;
		row = sheet.createRow(currentRow);
		HSSFCell cellBrName = row.createCell(BRAND_NAME_COLUMN);
		cellBrName.setCellValue(object.getClientKey());
		cellBrName.setCellStyle(ordinStyle);
		HSSFCell cellAnyPac = row.createCell(ANY_PACKAGE_COLUMN);
		cellAnyPac.setCellValue(object.getClientName());
		cellAnyPac.setCellStyle(ordinStyle);
		HSSFCell cellSpecPac = row.createCell(SPEC_PACKAGE_COLUMN);
		cellSpecPac.setCellValue(object.getCountOfQueries());
		cellSpecPac.setCellStyle(ordinStyle);

	    }
	    currentRow++;
	    row = sheet.createRow(currentRow);
	    HSSFCell cellBrName = row.createCell(BRAND_NAME_COLUMN);
	    cellBrName.setCellStyle(lineStyle);
	    HSSFCell cellAnyPac = row.createCell(ANY_PACKAGE_COLUMN);
	    cellAnyPac.setCellStyle(lineStyle);
	    HSSFCell cellSpecPac = row.createCell(SPEC_PACKAGE_COLUMN);
	    cellSpecPac.setCellStyle(lineStyle);
	}
	/* } */

    }
}
