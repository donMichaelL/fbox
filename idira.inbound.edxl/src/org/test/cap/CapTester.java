package org.test.cap;

import ies.edxlinbound.webservice.InboundEDXLWebService;
import ies.edxlinbound.webservice.InboundEDXLWebServiceException_Exception;
import ies.edxlinbound.webservice.InboundEDXLWebServicePortType;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

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
		
		String EUAB_POLYGON = "POLYGON ((48.21899,16.1532 48.18833,16.11415 48.16269,16.17938 48.13978,16.24323 48.19291,16.25147 48.20801,16.17182 48.21899,16.1532))";
		String EUAB_AREA_DESC = "Wien";
		
		
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
		
		
		//This will be the input of the CAP adapter
		String wsdlURL = "http://lnx1de.iessolutions.eu:3030/axis2/services/Inbound_EDXL_WebService?wsdl";
		
		//At first we are going to check if this is a valid url
		URL url = null;
        try {
            url = new URL(wsdlURL);
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(InboundEDXLWebService.class.getName()).log(java.util.logging.Level.WARNING, "Can not initialize the default wsdl from {0}", wsdlURL);
        }
		
        //Since we have a valid url we access the EDXL Inbound Service
        
		// Access CAP service
		InboundEDXLWebService capServiceClient = new InboundEDXLWebService(url);
		InboundEDXLWebServicePortType capService = capServiceClient.getInboundEDXLWebServiceHttpEndpoint();
		
		// Test methods
		if(capService.isValidCAP(xmlCap)) {
			System.out.println("CAP Validation: " + capService.validateCAP(xmlCap));
			
			try {
				Boolean reply = capService.receiveCAP(xmlCap);
				System.out.println("Save CAP: "+reply);
			} catch (InboundEDXLWebServiceException_Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.err.print("[ERROR] "+e.getMessage());
			}
		}
		else
			System.out.println("CAP Validation: " + capService.validateCAP(xmlCap));
		
	}

}
