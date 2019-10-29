package org.fbox.fusion.output.adapter.impl;

import java.util.ArrayList;
import java.util.Date;

import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.fbox.common.data.IContext;
import org.fbox.common.exception.OutputAdapterException;
import org.fbox.common.output.IAdapter;
import org.fbox.fusion.output.adapter.AbstractAdapter;

@Stateless(name="emailJBoss")
@Remote ({IAdapter.class})
public class EmailAdapterJBoss extends AbstractAdapter {

	// Adapter parameters
	public static final String MAIL_PARAM_FROM = "from";
	public static final String MAIL_PARAM_TO_RECIPIENTS = "to";
	public static final String MAIL_PARAM_CC_RECIPIENTS = "cc";
	public static final String MAIL_PARAM_BCC_RECIPIENTS = "bcc";
	public static final String MAIL_PARAM_SUBJECT = "subject";
	
	public static final String MAIL_RECIPIENTS_DELIMETER = ";";

	// Inject mail session
	@Resource (lookup="java:jboss/mail/fboxMail")
	private Session mailSession;
	 
	
	@Override
	public String getType() {
		return "emailJBoss";
	}

	@Override
	public String[] getRequiredParameters() {
		String[] params={MAIL_PARAM_FROM, MAIL_PARAM_TO_RECIPIENTS, MAIL_PARAM_SUBJECT};
		return params;
	}
	
	@Override
	public void dispatch(IContext state, Object data) throws OutputAdapterException {
		
		if(data == null) {
			throw new OutputAdapterException("EmailAdapter::Send: Warning: No need to send data to destination");
		}
		
		try {	
			// FROM
			Address from = new InternetAddress((String)state.getContextParameter(MAIL_PARAM_FROM));
			
			// TO 
			String recipientsStr = (String)state.getContextParameter(MAIL_PARAM_TO_RECIPIENTS);
			String[] recipients = recipientsStr.split(MAIL_RECIPIENTS_DELIMETER);
			ArrayList<Address> toRecipientsList = new ArrayList<Address>();
			for(String r : recipients) {
				toRecipientsList.add(new InternetAddress(r.trim()));
			}
			
			Address[] toRecipients = toRecipientsList.toArray(new InternetAddress[0]);
			
			
			MimeMessage m = new MimeMessage(mailSession);
			m.setFrom(from);
			m.setRecipients(Message.RecipientType.TO, toRecipients);
			m.setSubject((String)state.getContextParameter(MAIL_PARAM_SUBJECT));
			m.setSentDate(new Date());
			
			if(data instanceof Multipart) {
				m.setContent((Multipart) data);
			} else 
				throw new OutputAdapterException("[EmailAdapter] WARNING: Invalid input data detected! Input must be an instance of Multipart object!");
			
			Transport.send(m);
			System.out.println("[INFO]: EmailAdapter: Mail Sent Successfully.");
		} catch (MessagingException e) {
			e.printStackTrace();
			System.err.println("Error in Sending Mail");
		}
	}

}
