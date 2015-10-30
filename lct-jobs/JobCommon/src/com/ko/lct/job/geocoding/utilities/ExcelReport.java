package com.ko.lct.job.geocoding.utilities;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Font;

import com.ko.lct.job.common.businessobjects.GeocodeRequest;
import com.ko.lct.job.common.management.AbstractManager;
import com.ko.lct.job.geocoding.geocoder.Geocoder;
import com.ko.lct.job.logger.AbstractLogger;

public class ExcelReport {

    /**
     * @param errorRequestList
     * @param logger
     * @param args
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws IOException
     * @throws MessagingException 
     */
    public static void report(Geocoder geocoder, int sizeList, int countSuccess, int countRoute, int countCityAndPostalCode,
	    int countUndefined, int duplicateCount, int failureCount, List<GeocodeRequest> errorRequestList, 
	    String recipients, String host, String port, String smtpUserName, String smtpUserPassword, String from, AbstractLogger logger) 
	    throws ClassNotFoundException, IOException, MessagingException {
	Report report = new Report();
	report.createWorkbook("GeoCode", "Report");

	int countAll = geocoder.getCountGeocode();
	int overQueryLimitCount = geocoder.getOverQueryLimitCount();
	DecimalFormat format = new DecimalFormat(GeocodingConstants.PERCENT_FORMAT);

	report.createRow("Statistics: ");
	report.createRow("Size addresses: ", sizeList);
	report.createRow("Duplicate address: ", duplicateCount);
	report.createRow("Failure address: ", failureCount);
	report.createRow("All count geocoding: ", countAll, AbstractManager.getPercent(countAll, sizeList, format));
	report.createRow("Over query limit count geocoding: ", overQueryLimitCount);
	countAll -= overQueryLimitCount;
	report.createRow("All count geocoding without over query limit: ", countAll, AbstractManager.getPercent(countAll, sizeList, format));

	report.createRow("Results: ");
	report.createRow("Success: ", countSuccess, AbstractManager.getPercent(countSuccess, sizeList, format));
	report.createRow("Route: ", countRoute, AbstractManager.getPercent(countRoute, sizeList, format));
	report.createRow("City and Zip: ", countCityAndPostalCode, AbstractManager.getPercent(countCityAndPostalCode, sizeList, format));
	report.createRow("Undefined: ", countUndefined, AbstractManager.getPercent(countUndefined, sizeList, format));
	report.createRow("");
	report.createRow("Error Address: ");
	if (errorRequestList != null && errorRequestList.size() != 0) {
	    // create header of error addresses table
	    report.createRow("Country", "State", "City", "Address", "Postal Code");

	    for (GeocodeRequest geocodeRequest : errorRequestList) {
		report.createRow(geocodeRequest.getCountry(), geocodeRequest.getState(), geocodeRequest.getCity(), ((geocodeRequest.getAddressLine1() == null) ? ""
			: geocodeRequest.getAddressLine1()) + " " + ((geocodeRequest.getAddressLine2() == null) ? "" : geocodeRequest.getAddressLine2()),
			geocodeRequest.getPostalCode());
	    }
	}

	String fileName = report.saveAndCloseWorkbook();

	if (recipients != null) {
	    SendMail.sendEmailWithAttachment(host, port, smtpUserName, smtpUserPassword, from, recipients, "Report", "See attachment", fileName, logger);
	}

    }


    private static class Report {
	private HSSFWorkbook workbook = null;
	private HSSFSheet sheet = null;
	private HSSFCellStyle style = null;
	private HSSFFont font = null;
	private int rowNum = 1;
	private String wbName = null;

	public Report() {
	}

	public void createWorkbook(String prefix, String svpr) {
	    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
	    String dt = sf.format(new Date());
	    wbName = prefix + '-' + svpr + '-' + dt;
	    workbook = new HSSFWorkbook();
	    sheet = workbook.createSheet(wbName);
	    rowNum = 1;
	    style = workbook.createCellStyle();
	    font = workbook.createFont();
	    font.setBoldweight(Font.BOLDWEIGHT_BOLD);
	    style.setFont(font);
	}

	public String saveAndCloseWorkbook() throws IOException {
	    String fileName = wbName + ".xls";
	    for (int i = 0; i < rowNum; i++) {
		sheet.autoSizeColumn(i);
	    }
	    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileName));
	    try {
		workbook.write(bos);
	    } finally {
		bos.close();
	    }
	    font = null;
	    style = null;
	    sheet = null;
	    workbook = null;
	    wbName = null;
	    return fileName;
	}

	void createRow(String label) {
	    HSSFRow row = sheet.createRow(rowNum);
	    HSSFCell cell = row.createCell(0);
	    cell.setCellValue(label);
	    cell.setCellStyle(style);
	    rowNum++;
	}

	void createRow(String country, String state, String city, String address, String zip) {
	    HSSFRow row = sheet.createRow(rowNum);
	    HSSFCell cellcn = row.createCell(0);
	    cellcn.setCellValue(country);
	    cellcn.setCellStyle(style);
	    HSSFCell cellst = row.createCell(1);
	    cellst.setCellValue(state);
	    cellst.setCellStyle(style);
	    HSSFCell cellci = row.createCell(2);
	    cellci.setCellValue(city);
	    cellci.setCellStyle(style);
	    HSSFCell celladdr = row.createCell(3);
	    celladdr.setCellValue(address);
	    celladdr.setCellStyle(style);
	    HSSFCell cellzip = row.createCell(4);
	    cellzip.setCellValue(zip);
	    cellzip.setCellStyle(style);
	    rowNum++;
	}

	void createRow(String label, int value) {
	    HSSFRow row = sheet.createRow(rowNum);
	    HSSFCell cell = row.createCell(0);
	    cell.setCellValue(label);
	    cell.setCellStyle(style);
	    HSSFCell cellv = row.createCell(1);
	    cellv.setCellValue(value);
	    cellv.setCellStyle(style);
	    rowNum++;
	}

	void createRow(String label, int value, String details) {
	    HSSFRow row = sheet.createRow(rowNum);
	    HSSFCell cell = row.createCell(0);
	    cell.setCellValue(label);
	    cell.setCellStyle(style);
	    HSSFCell cellv = row.createCell(1);
	    cellv.setCellValue(value);
	    cellv.setCellStyle(style);
	    HSSFCell celld = row.createCell(2);
	    celld.setCellValue(details);
	    celld.setCellStyle(style);
	    rowNum++;
	}
    }
}
