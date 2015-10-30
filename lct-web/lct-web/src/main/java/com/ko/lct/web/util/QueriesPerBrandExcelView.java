package com.ko.lct.web.util;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.ko.lct.common.bean.Report;
import com.ko.lct.common.bean.ReportQuariesPerBrand;

public class QueriesPerBrandExcelView extends AbstractExcelView
{
    public static final String SEARCH_LIST_KEY = "SearshList";
    protected static final int BRAND_NAME_COLUMN = 0;
    protected static final int ANY_PACKAGE_COLUMN = 1;
    private static final int SPEC_PACKAGE_COLUMN = 2;
    private static final int TOTAL_COLUMN = 3;

    public QueriesPerBrandExcelView() {
	super();
	setUrl("/resources/xls/Number_of_queries_per_Brand");
    }

    @Override
    protected void buildExcelDocument(Map<String, Object> model,
	    HSSFWorkbook workbook,
	    HttpServletRequest request,
	    HttpServletResponse response) throws Exception {
	/*
	 * if (workbook.getNumberOfSheets() == 0) { setUrl("/resources/xls/Number_of_queries_per_Brand"); renderMergedOutputModel(model, request, response); } else {
	 */
	HSSFSheet sheet = workbook.getSheetAt(0);
	HSSFFont boldFont = workbook.createFont();
	boldFont.setBoldweight(Font.BOLDWEIGHT_BOLD);

	HSSFCellStyle ordinStyle = workbook.createCellStyle();
	ordinStyle.setBorderRight(CellStyle.BORDER_THIN);
	ordinStyle.setBorderLeft(CellStyle.BORDER_THIN);

	HSSFCellStyle lineStyle = workbook.createCellStyle();
	lineStyle.setBorderTop(CellStyle.BORDER_THIN);
	short currentRow = 2;
	Report report = (Report) model.get(SEARCH_LIST_KEY);
	if (report != null) {
	    List<ReportQuariesPerBrand> list = report.getReportQuariesPerBrandList();
	    HSSFRow row = sheet.getRow(0);
	    String str = "Date Range: " + report.getDateFrom() + " - " + report.getDateTo();
	    HSSFRichTextString rts = new HSSFRichTextString(str);

	    rts.applyFont(0, 11, boldFont);
	    row.getCell(0).setCellValue(rts);

	    for (ReportQuariesPerBrand object : list) {
		currentRow++;
		row = sheet.createRow(currentRow);
		HSSFCell cellClientKey = row.createCell(BRAND_NAME_COLUMN);
		cellClientKey.setCellValue(object.getBrandName());
		cellClientKey.setCellStyle(ordinStyle);

		HSSFCell cellAnyPack = row.createCell(ANY_PACKAGE_COLUMN);
		cellAnyPack.setCellValue(object.getAnyPackage());
		cellAnyPack.setCellStyle(ordinStyle);

		HSSFCell cellSpecPack = row.createCell(SPEC_PACKAGE_COLUMN);
		cellSpecPack.setCellValue(object.getSpecificPackage());
		cellSpecPack.setCellStyle(ordinStyle);

		HSSFCell cellTotal = row.createCell(TOTAL_COLUMN);
		cellTotal.setCellValue(object.getTotal());
		cellTotal.setCellStyle(ordinStyle);
	    }
	    currentRow++;
	    row = sheet.createRow(currentRow);
	    HSSFCell cellBrName = row.createCell(BRAND_NAME_COLUMN);
	    cellBrName.setCellStyle(lineStyle);
	    HSSFCell cellAnyPac = row.createCell(ANY_PACKAGE_COLUMN);
	    cellAnyPac.setCellStyle(lineStyle);
	    HSSFCell cellSpecPac = row.createCell(SPEC_PACKAGE_COLUMN);
	    cellSpecPac.setCellStyle(lineStyle);
	    HSSFCell cellTotal = row.createCell(TOTAL_COLUMN);
	    cellTotal.setCellStyle(lineStyle);
	}
	/* } */
    }

}
