package com.ko.lct.web.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Font;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.ko.lct.common.bean.Location;
import com.ko.lct.common.bean.Locations;

public class SearchListExcelView extends AbstractExcelView
{
    public static final String SEARCH_LIST_KEY = "SearshList";
    protected static final int OUTLET_NAME_COLUMN = 0;
    protected static final int OUTLET_ADDRESS_COLUMN = 1;
    private static final int DISTANCE_COLUMN = 2;
    private static final int TRADE_SUB_CHANEL_COLUMN = 3;
    private static final int PRODUCT_COLUMN = 4;
    private static final int FLAVOR_COLUMN = 5;
    private static final int PRIMARY_CONTAINER_COLUMN = 6;
    private static final int SECONDARY_PACKAGE_COLUMN = 7;

    public SearchListExcelView() {
	super();
	setUrl("/resources/xls/SearchResultXlsTemplate");
    }

    @Override
    protected void buildExcelDocument(Map<String, Object> model,
            HSSFWorkbook workbook,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {	    
	    //renderMergedOutputModel(model, request, response);
	    //CREATE THE SHEET
	    // workbook = getTemplateSource("/WEB-INF/SearchResultXls",request);
	/*
	    if(workbook.getNumberOfSheets() == 0){
		setUrl("/resources/xls/SearchResultXlsTemplate");
		renderMergedOutputModel(model, request, response);
	    }
	*/
	    HSSFSheet sheet = workbook.getSheetAt(0);
	    HSSFFont boldFont = workbook.createFont();
	    boldFont.setBoldweight(Font.BOLDWEIGHT_BOLD);

	    HSSFCellStyle myStyle = workbook.createCellStyle();
	    myStyle.setFont(boldFont);
	    // create a cell and asign values & style
	    //GETCELL: getCell(SHEET, ROW, COLUMN);
	    short currentRow = 0;
	    Locations searchRes = (Locations) model.get(SEARCH_LIST_KEY);
	    if(searchRes != null){
	    List<Location> searchList = searchRes.getLocation();
	    String distanceUnitName = searchRes.getDistanceUnit().getName();
	    for (Location object : searchList) {
		
		int rowspan = object.getProductPackage().size();
		
		for(int i = 0; i<rowspan; i++){
		    currentRow++;
		    HSSFRow row = sheet.createRow(currentRow);
		    if(i==0){
        		row.createCell(OUTLET_NAME_COLUMN).setCellValue(object.getOutlet().getChainName());
        		row.createCell(OUTLET_ADDRESS_COLUMN).setCellValue(object.getOutlet().getAddress().getAddressLine1() + ", " + object.getOutlet().getAddress().getCity()+ ", " + object.getOutlet().getAddress().getState()+ " " + object.getOutlet().getAddress().getPostalCode()+ ", " + object.getOutlet().getAddress().getCountryCode());
        		row.createCell(DISTANCE_COLUMN).setCellValue(new BigDecimal(object.getDistance()).setScale(1, RoundingMode.HALF_EVEN).doubleValue() + " " + distanceUnitName);
        		row.createCell(TRADE_SUB_CHANEL_COLUMN).setCellValue(object.getOutlet().getSubTradeChannel().getName());
		    }
		    row.createCell(PRODUCT_COLUMN).setCellValue(object.getProductPackage().get(i).getProduct().getProd().getName());
		    row.createCell(FLAVOR_COLUMN).setCellValue(object.getProductPackage().get(i).getProduct().getFlavor().getName());
		    row.createCell(PRIMARY_CONTAINER_COLUMN).setCellValue(object.getProductPackage().get(i).getPackage().getPrimaryContainer().getName());
		    row.createCell(SECONDARY_PACKAGE_COLUMN).setCellValue(object.getProductPackage().get(i).getPackage().getSecondaryPackage().getName());
		}
		   
	    }
	 }

    }

}
