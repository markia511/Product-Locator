package com.ko.lct.job.geocoding.utilities;

import java.util.ArrayList;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.ko.lct.job.geocoding.businessobjects.ParseReport;

public class ParseResultReport {
    public static void report(ArrayList<ParseReport> reportList) throws AddressException, MessagingException {
	StringBuffer body = new StringBuffer();
	int c = 1;
	body.append("Geo_Locator file processing report. \t\n \t\n");
	for (ParseReport parseReport : reportList) {
	    body.append(c + ". File name: \t" + parseReport.getFilename() + "\t Number of processed records: \t"
		    + parseReport.getRecordCount() + "\t Number of new stores: \t" + parseReport.getNewAddressCount() + "\t\n");
	    c++;
	}
	String recipients = GeoCodingProcessProperties.getProperty("emails");
	if (recipients != null) {
	    String host = GeoCodingProcessProperties.getProperty("host");
	    String port = GeoCodingProcessProperties.getProperty("port");
	    String from = GeoCodingProcessProperties.getProperty("mail_from");
	    String smtpUserName = GeoCodingProcessProperties.getSmtpUserName();
	    String smtpUserPassword = GeoCodingProcessProperties.getSmtpUserPassword();

	    SendMail.sendTextEmail(host, port, smtpUserName, smtpUserPassword, from, recipients, "Report", body.toString());
	}
    }

}
