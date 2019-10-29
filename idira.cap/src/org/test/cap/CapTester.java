package org.test.cap;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.frequentis.cap.CAPInbound;
import com.frequentis.cap.ICAPInbound;

public class CapTester {

	public static CapMessage loadDefaultCapMessage() {
		
		CapMessage capMsg = new CapMessage();
		
		capMsg.setIdentifier(CapConstants.DEFAULT_IDENTIFIER); 
		capMsg.setMsgType(CapConstants.DEFAULT_MSGTYPE); 
		capMsg.setSender(CapConstants.DEFAULT_SENDER);
		capMsg.setScope(CapConstants.DEFAULT_SCOPE);
		capMsg.setSent(CapConstants.DEFAULT_SENT);
		capMsg.setStatus(CapConstants.DEFAULT_STATUS);
		capMsg.setSource(CapConstants.DEFAULT_SOURCE);
		capMsg.setAddresses(CapConstants.DEFAULT_ADDREESSES);
		
		capMsg.setCategory(CapConstants.DEFAULT_CATEGORY);
		capMsg.setCertainty(CapConstants.DEFAULT_CERTAINTY);
		capMsg.setDescription(CapConstants.DEFAULT_DESCRIPTION);
		capMsg.setEvent(CapConstants.DEFAULT_EVENT);
		capMsg.setHeadline(CapConstants.DEFAULT_HEADLINE);
		capMsg.setLanguage(CapConstants.DEFAULT_LANGUAGE);
		capMsg.setSenderName(CapConstants.DEFAULT_SENDERNAME);
		capMsg.setSeverity(CapConstants.DEFAULT_SEVERITY);
		capMsg.setUrgency(CapConstants.DEFAULT_URGENCY);
		capMsg.setAreaDesc(CapConstants.DEFAULT_AREADESC);
		capMsg.setCircle(CapConstants.DEFAULT_CIRCLE);
		
		return capMsg;
	}
	
	
	public static CapMessage loadCapMessage() {
		CapMessage capMsg = new CapMessage();
		
		capMsg.setIdentifier(CapConstants.DEFAULT_IDENTIFIER);  // <sender-date-id-update>
		capMsg.setMsgType(CapConstants.DEFAULT_MSGTYPE);		// [alert]
		capMsg.setSender(CapConstants.DEFAULT_SENDER);			// (SFE)
		capMsg.setScope(CapConstants.DEFAULT_SCOPE);			// [Private]
		capMsg.setSent(CapConstants.DEFAULT_SENT);				
		capMsg.setStatus(CapConstants.DEFAULT_STATUS);
		capMsg.setSource(CapConstants.DEFAULT_SOURCE);
		capMsg.setAddresses(CapConstants.DEFAULT_ADDREESSES);
		
		capMsg.setCategory(CapConstants.DEFAULT_CATEGORY);
		capMsg.setCertainty(CapConstants.DEFAULT_CERTAINTY);
		capMsg.setDescription(CapConstants.DEFAULT_DESCRIPTION);
		capMsg.setEvent(CapConstants.DEFAULT_EVENT);
		capMsg.setHeadline(CapConstants.DEFAULT_HEADLINE);
		capMsg.setLanguage(CapConstants.DEFAULT_LANGUAGE);
		capMsg.setSenderName(CapConstants.DEFAULT_SENDERNAME);
		capMsg.setSeverity(CapConstants.DEFAULT_SEVERITY);
		capMsg.setUrgency(CapConstants.DEFAULT_URGENCY);
		capMsg.setAreaDesc(CapConstants.DEFAULT_AREADESC);
		capMsg.setCircle(CapConstants.DEFAULT_CIRCLE);
		
		return capMsg;
		
	}
	
	public static void main(String[] args) {
		// [LATITUDE, LONGTITUDE CIRCLE]
		String GREECE_GYAROS = "37.616013,24.720697 0.01";
		String GREECE_AREA_DESC = "Greece";
		
		// Points in Germany
		String SLUKNOV_CIRCLE = "51.003693,14.452599 0.01";
		String SLUKNOV_AREA_DESC = "Sluknov";
		
		String WEDNIG_CIRCLE = "51.279669,12.82022 0.01";
		String WEDNIG_AREA_DESC = "Denkwitz";
		
		String RAUDEN_CIRCLE = "46.984266,15.588101 0.01";
		String RAUDEN_AREA_DESC = "Rauden";
		
		
		// *** CAP Alert *** // 
		CapMessage capMsg = loadCapMessage();
		
		// Identifier
		SimpleDateFormat sdfId = new SimpleDateFormat(CapConstants.CAP_DATE_FORMAT_IDENTIFIER);
		String identifier =  CapConstants.DEFAULT_SENDER + "-" 
				+ sdfId.format(new Date()) + "-"
				+ "1" + "-" 
				+ "0";
		
		System.out.println("Identifier: " + identifier);
		capMsg.setIdentifier(identifier);
		
		// Sent
		SimpleDateFormat sdfSent = new SimpleDateFormat(CapConstants.CAP_DATE_FORMAT_SENT);
		String sent = sdfSent.format(new Date());
		capMsg.setSent(sent);
		System.out.println("Sent: " + sent);
		
		// Circle - AreaDesc
		capMsg.setCircle(SLUKNOV_CIRCLE);
		capMsg.setAreaDesc(SLUKNOV_AREA_DESC);
		
		String xmlCap = capMsg.createXML();
		
		// *** CAP Update **** //
		capMsg.setMsgType("Update");
		
		int updateNum = 3;
		identifier =  CapConstants.DEFAULT_SENDER + "-" 
				+ sdfId.format(new Date()) + "-"
				+ "2" + "-" 
				+ updateNum;
		capMsg.setIdentifier(identifier);
		
		// <sender,identifier,sent>
		String refSender = CapConstants.DEFAULT_SENDER;
		String refIdentifier = "SFE-20120916-2-3";
		String refSent = "2012-09-16T21:43:15+03:00";
		String ref = refSender + "," + refIdentifier + "," + refSent;
		capMsg.setReferences(ref);
		
		// *** CAP Cancel *** //
		capMsg.setMsgType("Cancel");
	
		// Cretae and display CAP
		xmlCap = capMsg.createXML();
		System.out.println(xmlCap);
		
		// String wsdlURL = "http://idira-services.frequentis.com:3001/?wsdl";
		
		// Access CAP service
		CAPInbound capServiceClient = new CAPInbound();
		ICAPInbound capService = capServiceClient.getBasicHttpBindingICAPInbound();
		
		// Test methods
		System.out.println("Supported CAP Version: " + capService.supportedCAPVersion());
		// System.out.println(capService.supportedCAPSchema());
		System.out.println("Validation: " + capService.validateCAP(xmlCap));
		System.out.println("SaveCAP: " + capService.saveCAP(xmlCap));
	}

}
