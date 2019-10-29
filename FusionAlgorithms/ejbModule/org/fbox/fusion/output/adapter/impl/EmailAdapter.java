package org.fbox.fusion.output.adapter.impl;

import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import org.fbox.common.data.IContext;
import org.fbox.common.exception.OutputAdapterException;
import org.fbox.common.output.IAdapter;
import org.fbox.fusion.output.adapter.AbstractAdapter;


@Stateless (name="email")
@Remote ({IAdapter.class})
public class EmailAdapter extends AbstractAdapter {
	
	// Adapter parameters
	public static final String EMAIL_DEFAUL_MAILER = "IDIRA-Mailer";
		
	// Adapter user defined parameters
	public static final String EMAIL_PARAM_TO = "to";
	public static final String EMAIL_PARAM_SUBJECT = "subject";
	public static final String EMAIL_PARAM_CC = "cc";
	public static final String EMAIL_PARAM_BCC = "bcc";
	
	// Formatting...
	public static String EMAIL_DEFAULT_REPORT_TEMPLATE = 
			"<\\!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\"><html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><title>IDIRA Situation Report</title></head><body><p><img alt=\"http://wand.di.uoa.gr/idira/templates/idira_logo_large.png\" height=\"119\" src=\"http://www.idira.eu/images/images/idira_logo_neu.png\" width=\"255\"></p><h1>IDIRA Situation Report</h1><p>##DATA##</p></body></html>";

	
	private static Properties getDefaultEmailConfigurationProperties() {
		Properties configProperties=new Properties();
		
		configProperties.setProperty("mail.smtp.starttls.enable", "true");
		configProperties.setProperty("mail.smtp.auth", "true");
		configProperties.setProperty("mail.smtp.port", "587");
		configProperties.setProperty("mail.from", "idira.nkua@gmail.com");
		configProperties.setProperty("mail.smtp.host", "smtp.gmail.com");
		configProperties.setProperty("mail.password", "wsdf18i36n");
		configProperties.setProperty("mail.username", "idira.nkua");
		
		return configProperties;
	}	
	
	@Override
	public String[] getRequiredParameters() {
		String[] params={EMAIL_PARAM_TO, EMAIL_PARAM_SUBJECT, EMAIL_PARAM_CC, EMAIL_PARAM_BCC};
		return params;
	}

	@Override
	public String getType() {
		return "email";
	}


	@Override
	public void dispatch(IContext state, Object data) throws OutputAdapterException {
		
		final Properties configProps=getDefaultEmailConfigurationProperties();
		
		String mailer = EMAIL_DEFAUL_MAILER;
		Message msg = null;
		
		String mailTo = null, mailSubject = null, mailCC = null, mailBCC = null;
		
		mailTo = (String)state.getContextParameter(EMAIL_PARAM_TO);
		mailSubject = (String)state.getContextParameter(EMAIL_PARAM_SUBJECT);
		mailCC = (String)state.getContextParameter(EMAIL_PARAM_CC);
		mailBCC = (String)state.getContextParameter(EMAIL_PARAM_BCC);
		
		if (mailTo == null)
			throw new OutputAdapterException("EmailExporter::Send: Destination not specified.");
		
		try {
			
		    Session session = Session.getInstance(configProps, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(configProps.getProperty("mail.username"), configProps.getProperty("mail.password"));
				}
			  });
		    
		    msg = new MimeMessage(session); 
			
		    // From
		    msg.setFrom(new InternetAddress(configProps.getProperty("mail.from")));
			
			// Recipients
			msg.setRecipients(Message.RecipientType.TO,	InternetAddress.parse(mailTo, false));
			
			if (mailCC != null)
				msg.setRecipients(Message.RecipientType.CC,	InternetAddress.parse(mailCC, false));
			
			if (mailBCC != null)
				msg.setRecipients(Message.RecipientType.BCC,	InternetAddress.parse(mailBCC, false));
			
			// Subject
			msg.setSubject(mailSubject);
			
			String messageToExport = EMAIL_DEFAULT_REPORT_TEMPLATE.replaceFirst("##DATA##", (String)data); 
						
			msg.setDataHandler(new DataHandler(new ByteArrayDataSource(messageToExport, "text/html")));
			msg.setHeader("X-Mailer", mailer);
			msg.setSentDate(new Date());
		} 
		catch (Throwable e)
		{
			e.printStackTrace();
			throw new OutputAdapterException("EmailExporter::Send: Error in email construction.", e);
		}

		try {
			Transport.send(msg);
		}
		catch (MessagingException e)
		{
			throw new OutputAdapterException("EmailExporter::Send: Cannot send email.", e);
		}
		
	}
	
}
