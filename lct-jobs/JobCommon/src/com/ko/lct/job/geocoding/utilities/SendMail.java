package com.ko.lct.job.geocoding.utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.ko.lct.job.logger.AbstractLogger;

public class SendMail {

    private static Session createMailSession(String host, String port, boolean isAuthenticate) {
	// Create a Properties object to contain connection configuration information.
	Properties props = System.getProperties();
	props.put("mail.transport.protocol", "smtp");
	props.put("mail.smtp.host", host);
	props.put("mail.smtp.port", port);

	// Set properties indicating that we want to use STARTTLS to encrypt the connection.
	// The SMTP session will begin on an unencrypted connection, and then the client
	// will issue a STARTTLS command to upgrade to an encrypted connection.
	if (isAuthenticate) {
	    props.put("mail.smtp.auth", "true");
	}
	props.put("mail.smtp.starttls.enable", "true");
	props.put("mail.smtp.starttls.required", "true");

	// Create a Session object to represent a mail session with the specified properties.
	Session session = Session.getDefaultInstance(props);
	return session;
    }

    private static Transport createMailTransport(Session session, String host, String smtpUserName, String smtpUserPassword) throws MessagingException {
	// Create a transport.
	Transport transport = session.getTransport();
	if (smtpUserName == null) {
	    transport.connect();
	}
	else {
	    transport.connect(host, smtpUserName, smtpUserPassword);
	}
	return transport;
    }

    public static void sendTextEmail(String host, String port, String smtpUserName, String smtpUserPassword, String from, String recipients, String subject, String body)
	    throws MessagingException {

	if (recipients == null || recipients.trim().isEmpty()) {
	    throw new MessagingException("Empty recipients");
	}

	if (subject == null || subject.trim().isEmpty()) {
	    throw new MessagingException("Empty Subject");
	}

	if (body == null || body.trim().isEmpty()) {
	    throw new MessagingException("Empty Body");
	}

	Session session = createMailSession(host, port, smtpUserName != null);
	// create a message
	MimeMessage msg = new MimeMessage(session);
	msg.setFrom(new InternetAddress(from));

	ArrayList<String> recipientsArray = new ArrayList<String>();
	StringTokenizer stringTokenizer = new StringTokenizer(recipients, ",");

	while (stringTokenizer.hasMoreTokens()) {
	    recipientsArray.add(stringTokenizer.nextToken());
	}
	int sizeTo = recipientsArray.size();
	InternetAddress[] addressTo = new InternetAddress[sizeTo];
	for (int i = 0; i < sizeTo; i++) {
	    addressTo[i] = new InternetAddress(recipientsArray.get(i).toString());
	}
	msg.setRecipients(Message.RecipientType.TO, addressTo);
	
	if (host != null && host.endsWith(".amazonaws.com")) {
	    subject += " (Amazon)";
	}
	msg.setSubject(subject);
	// add message
	msg.setText(body);
	// set the Date: header
	msg.setSentDate(new Date());
	// send the message

	Transport transport = createMailTransport(session, host, smtpUserName, smtpUserPassword);
	try {
	    transport.sendMessage(msg, msg.getAllRecipients());
	} finally {
	    transport.close();
	}
    }

    public static void sendEmailWithAttachment(String host, String port, String smtpUserName, String smtpUserPassword, String from, String recipients,
	    String subject, String body, String attachmentFileName, AbstractLogger logger) throws MessagingException {

	File reportFile = new File(attachmentFileName);
	if (recipients == null || recipients.isEmpty()) {
	    logger.logError("Report Recipients is empty");
	}
	else if (subject == null || subject.isEmpty()) {
	    logger.logError("Report Subject is empty");
	}
	else if (body == null || body.isEmpty()) {
	    logger.logError("Report Body is empty");
	}
	else if (attachmentFileName == null || attachmentFileName.isEmpty()) {
	    logger.logError("Report Attachment File Name is empty");
	}
	else if (reportFile.length() == 0) {
	    logger.logError("Report is empty");
	}
	else {
	    Session session = createMailSession(host, port, smtpUserName != null);
	    try
	    {
		// create a message
		MimeMessage msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(from));

		ArrayList<String> recipientsArray = new ArrayList<String>();
		StringTokenizer stringTokenizer = new StringTokenizer(recipients, ",");

		while (stringTokenizer.hasMoreTokens()) {
		    recipientsArray.add(stringTokenizer.nextToken());
		}
		int sizeTo = recipientsArray.size();
		InternetAddress[] addressTo = new InternetAddress[sizeTo];
		for (int i = 0; i < sizeTo; i++) {
		    addressTo[i] = new InternetAddress(recipientsArray.get(i).toString());
		}
		msg.setRecipients(Message.RecipientType.TO, addressTo);

		if (host != null && host.endsWith(".amazonaws.com")) {
		    subject += " (Amazon)";
		}
		msg.setSubject(subject);

		// create and fill the first message part
		MimeBodyPart mbp1 = new MimeBodyPart();
		mbp1.setText(body);

		// create the second message part
		MimeBodyPart mbp2 = new MimeBodyPart();

		// attach the file to the message
		FileDataSource fds = new FileDataSource(attachmentFileName);
		mbp2.setDataHandler(new DataHandler(fds));
		mbp2.setFileName(fds.getName());

		// create the Multipart and add its parts to it
		Multipart mp = new MimeMultipart();
		mp.addBodyPart(mbp1);
		mp.addBodyPart(mbp2);

		// add the Multipart to the message
		msg.setContent(mp);

		// set the Date: header
		msg.setSentDate(new Date());

		// send the message
		Transport transport = createMailTransport(session, host, smtpUserName, smtpUserPassword);
		try {
		    transport.sendMessage(msg, msg.getAllRecipients());
		} finally {
		    transport.close();
		}

	    } catch (MessagingException mex) {
		logger.logError("Error sending mail", mex);
		mex.printStackTrace();
		throw mex;
	    }
	}
    }

}
