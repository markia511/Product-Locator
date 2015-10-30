package com.ko.lct.web.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.ko.lct.common.bean.Location;
import com.ko.lct.common.bean.Locations;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class SearchListPDFView extends AbstractPdfView
{
    public static final String SEARCH_LIST_KEY = "searchList";

    @Override
    protected void buildPdfDocument(Map<String, Object> model,
	    Document doc,
	    PdfWriter writer,
	    HttpServletRequest req,
	    HttpServletResponse resp)
	    throws Exception {
	Locations searchRes = (Locations) model.get(SEARCH_LIST_KEY);
	String distanceUnit = searchRes.getDistanceUnit().getName();
	List<Location> searchList = searchRes.getLocation();

	int headerwidthsr[] = { 14, 25, 9, 12, 14, 7, 10, 9 }; // percentage
	PdfPTable table = new PdfPTable(8);
	table.setWidths(headerwidthsr);
	table.setWidthPercentage(100); // percentage
	table.getDefaultCell().setPadding(3);

	addCell("Outlet Name", table);

	addCell("Outlet Address", table);

	addCell("Distance", table);

	addCell("Trade Sub-Channel", table);

	addCell("Product", table);

	addCell("Flavor", table);

	addCell("Primary Container", table);

	addCell("Secondary Package", table);

	for (Location object : searchList) {

	    int rowspan = object.getProductPackage().size();

	    for (int i = 0; i < rowspan; i++) {
		if (i == 0) {
		    table.addCell(object.getOutlet().getChainName());
		    table.addCell(object.getOutlet().getAddress().getAddressLine1() + ", " + object.getOutlet().getAddress().getCity() + ", "
			    + object.getOutlet().getAddress().getState() + " " + object.getOutlet().getAddress().getPostalCode() + ", "
			    + object.getOutlet().getAddress().getCountryCode());
		    table.addCell(new BigDecimal(object.getDistance()).setScale(1, RoundingMode.HALF_EVEN).doubleValue() + " " + distanceUnit);
		    table.addCell(object.getOutlet().getSubTradeChannel().getName());
		} else {
		    table.addCell("");
		    table.addCell("");
		    table.addCell("");
		    table.addCell("");
		}
		table.addCell(object.getProductPackage().get(i).getProduct().getProd().getName());
		table.addCell(object.getProductPackage().get(i).getProduct().getFlavor().getName());
		table.addCell(object.getProductPackage().get(i).getPackage().getPrimaryContainer().getName());
		table.addCell(object.getProductPackage().get(i).getPackage().getSecondaryPackage().getName());
	    }
	}

	doc.add(table);

    }

    @Override
    protected void buildPdfMetadata(Map<String, Object> model, Document document, HttpServletRequest request) {
	// TODO Auto-generated method stub
	super.buildPdfMetadata(model, document, request);
	document.setPageSize(PageSize.A4.rotate());
    }

    private static void addCell(String text, PdfPTable table) {
	Paragraph paragraph = new Paragraph(text);
	paragraph.getFont().setStyle(Font.BOLD);
	table.addCell(paragraph);
    }
}
