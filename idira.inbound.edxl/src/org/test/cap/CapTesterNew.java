package org.test.cap;

import ies.edxlinbound.webservice.CheckStatus;
import ies.edxlinbound.webservice.InboundEDXLWebService;
import ies.edxlinbound.webservice.InboundEDXLWebServiceException_Exception;
import ies.edxlinbound.webservice.InboundEDXLWebServicePortType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

public class CapTesterNew {

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
		//capMsg.setCircle(CapConstants.DEFAULT_CIRCLE);
		
		return capMsg;
	}
	
	public static String createCapAlertMsg (String sender, String location, String locationDescription, int msgNumber) {
		
		// *** CAP Alert *** // 
		CapMessage capMsg = loadCapMessage();
		
		// Identifier
		SimpleDateFormat sdfId = new SimpleDateFormat(CapConstants.CAP_DATE_FORMAT_IDENTIFIER);
		String identifier = sender + "-" + sdfId.format(new Date()) + "-" + msgNumber + "-0";
		System.out.println("Identifier: " + identifier);
		
		capMsg.setIdentifier(identifier);
		
		// Msg type
		capMsg.setMsgType("Alert");
		
		capMsg.setSender(sender);
		
		// Sent time
		SimpleDateFormat sdfSent = new SimpleDateFormat(CapConstants.CAP_DATE_FORMAT_SENT);
		String sent = sdfSent.format(new Date());
		capMsg.setSent(sent);
		System.out.println("Sent: " + sent);
		
		// Set location
		capMsg.setAreaDesc(locationDescription);
		
		//Check if we have to create a circle or a polygon
		StringTokenizer st = new StringTokenizer(location, " ");
		
		if(st.countTokens() > 2) { //Then we have a polygon as location 
			capMsg.setPolygon(location);
			//System.out.println("Mpike 1");
		}
		else {//We have a circle as location 
			capMsg.setCircle(location);
			//System.out.println("Mpike 2");
		}
		
		String xmlCap = capMsg.createXML();
		//System.out.println(xmlCap);
		
		return xmlCap;
	}
	
	public static String createCapUpdateMsg (String sender, String time, String oldID, int updateNum, String location, String locationDescription, String certainty) {
		
		// *** CAP Update *** // 
		CapMessage capMsg = loadCapMessage();
		
		// Msg type
		capMsg.setMsgType("Update");
		
		capMsg.setSender(sender);
		
		// Identifier of CAP Update Msg
		SimpleDateFormat sdfId = new SimpleDateFormat(CapConstants.CAP_DATE_FORMAT_IDENTIFIER);
		String identifier = sender + "-" + sdfId.format(new Date()) + "-1-" + updateNum;
		capMsg.setIdentifier(identifier);
		System.out.println("Identifier: " + identifier);
		
		// Sent time of update CAP Msg
		SimpleDateFormat sdfSent = new SimpleDateFormat(CapConstants.CAP_DATE_FORMAT_SENT);
		String sent = sdfSent.format(new Date());
		capMsg.setSent(sent);
		System.out.println("Sent: " + sent);
		
		// <sender,identifier,sent>
		String refSender = sender;
		String refIdentifier = oldID;
		String refSent = time;
		String ref = refSender + "," + refIdentifier + "," + refSent;
		capMsg.setReferences(ref);
		
		// Update certainty field of the old message
		capMsg.setCertainty(certainty);
		
		// Set location
		capMsg.setAreaDesc(locationDescription);

		//Check if we have to create a circle or a polygon
		StringTokenizer st = new StringTokenizer(location, " ");

		if(st.countTokens() > 2) { //Then we have a polygon as location 
			capMsg.setPolygon(location);
			//System.out.println("Mpike 1");
		}
		else {//We have a circle as location 
			capMsg.setCircle(location);
			//System.out.println("Mpike 2");
		}
		
		String xmlCap = capMsg.createXML();
		//System.out.println(xmlCap);
		
		return xmlCap;
	}
	
	public static String createCapCancelMsg (String sender, String oldID, String time, int updateNum, String location, String locationDescription, String event, String category) {
		
		// *** CAP Cancel *** // 
		CapMessage capMsg = loadCapMessage();

		// Msg type
		capMsg.setMsgType("Cancel");
		
		capMsg.setSender(sender);
		
		// Identifier of CAP Cancel Msg
		SimpleDateFormat sdfId = new SimpleDateFormat(CapConstants.CAP_DATE_FORMAT_IDENTIFIER);
		String identifier = sender + "-" + sdfId.format(new Date()) + "-1-" + updateNum;
		capMsg.setIdentifier(identifier);
		System.out.println("Identifier: " + identifier);
		
		// Sent time of cancel CAP Msg
		SimpleDateFormat sdfSent = new SimpleDateFormat(CapConstants.CAP_DATE_FORMAT_SENT);
		String sent = sdfSent.format(new Date());
		capMsg.setSent(sent);
		System.out.println("Sent: " + sent);
		
		// <sender,identifier,sent>
		String refSender = sender;
		String refIdentifier = oldID;
		String refSent = time;
		String ref = refSender + "," + refIdentifier + "," + refSent;
		capMsg.setReferences(ref);
		
		// Set location
		capMsg.setAreaDesc(locationDescription);
		
		// Set event
		capMsg.setEvent(event);

		// Set category
		capMsg.setCategory(category);
		
		capMsg.setDescription("Cancelation message");
		capMsg.setHeadline("Cancelation");
		
		//Check if we have to create a circle or a polygon
		StringTokenizer st = new StringTokenizer(location, " ");

		if(st.countTokens() > 2) { //Then we have a polygon as location 
			capMsg.setPolygon(location);
			//System.out.println("Mpike 1");
		}
		else {//We have a circle as location 
			capMsg.setCircle(location);
			//System.out.println("Mpike 2");
		}
		
		String xmlCap = capMsg.createXML();
		//System.out.println(xmlCap);
		
		return xmlCap;
	}
	
	private static final QName SERVICE_NAME = new QName("http://webservice.edxlinbound.ies", "Inbound_EDXL_WebService");
	
	public static void main(String[] args) {
		// [LATITUDE, LONGTITUDE CIRCLE]
		String GREECE_GYAROS = "37.616013,24.720697 1.5";
		String GREECE_AREA_DESC = "Greece";
		
		// Points in Germany
		String SLUKNOV_CIRCLE = "51.003693,14.452599 0.01";
		String SLUKNOV_AREA_DESC = "Sluknov";
		
		String WEDNIG_CIRCLE = "51.279669,12.82022 0.01";
		String WEDNIG_AREA_DESC = "Denkwitz";
		
		String RAUDEN_CIRCLE = "46.984266,15.588101 0.01";
		String RAUDEN_AREA_DESC = "Rauden";
		
		String PIRNA_HOSPITAL = "50.95860,13.95307 0.01";
		String HOSPITAL_AREA_DESC = "Hospital in Pirna";
		String HOSPITAL_POLYGON = "50.95896,13.95234 50.95893,13.95263 50.95951,13.95276 50.95948,13.95304 50.95901,13.95293 50.95897,13.95338 50.95944,13.95349 50.95941,13.95373 50.95895,13.95364 50.95891,13.95414 50.95872,13.95410 50.95875,13.95368 50.95809,13.95355 50.95813,13.95302 50.95821,13.95303 50.95821,13.95292 50.95814,13.95291 50.95817,13.95259 50.95839,13.95264 50.95843,13.95221 50.95896,13.95234";
	
		//String EUAB_POLYGON = "(E16°9.192 N48°13.1394, E16°6.849 N48°11.2998, E16°10.7628 N48°9.7614, E16°14.5938 N48°8.3868, E16°15.0882 N48°11.5746, E16°10.3092 N48°12.4806, E16°9.192 N48°13.1394)";
		//String EUAB_POLYGON = "16.1532,48.21899 16.11415,48.18833 16.17938,48.16269 16.24323,48.13978 16.25147,48.19291 16.17182,48.20801 16.1532,48.21899";
		//String EUAB_POLYGON = "16.1532,48.21899 16.11415,48.18833 16.17938,48.16269 16.24323,48.13978 16.25147,48.19291 16.17182,48.20801 16.1532,48.21899";
		String EUAB_POLYGON = "48.21899,16.1532 48.18833,16.11415 48.16269,16.17938 48.13978,16.24323 48.19291,16.25147 48.20801,16.17182 48.21899,16.1532";
		String EUAB_AREA_DESC = "Wien";
		
		String ATHENS_POLYGON = "38.15828,23.78298 38.11142,23.80770 38.12574,23.82109 38.14626,23.82315 38.15085,23.83963 38.17191,23.81800 38.15828,23.78298";
		String ATHENS_AREA_DESC = "Suburban area outside Athens";
		
		String SENDER = "SFE";
		//String TIME = "2013-02-14T18:19:44+02:00"; // (SFE1-20121118-1-1)
		//String OLDID = "SFE-20130214-1-0";
		//String SENDER = "SFE2";
		//String TIME = "2012-11-18T02:19:39+02:00"; // (SFE2-20121118-1-0)
		
		//Id order to specify if the number of an Alert CAP Msg will be 1-0, 2-0, 3-0 etc
		int msgNumber = 1;
		
		
		// Field to be updated
		String CERTAINTY = "Likely"; //Unlikely, Possible, Likely
		
		String xmlCap = null;
		
		// Uncomment the one needed!!!
		xmlCap = createCapAlertMsg (SENDER, ATHENS_POLYGON, ATHENS_AREA_DESC, msgNumber);
		//xmlCap = createCapUpdateMsg (SENDER, TIME, OLDID, updateNum, GREECE_GYAROS, GREECE_AREA_DESC, CERTAINTY);
		System.out.println(xmlCap);
		//"SFE" , "SFE1a-20130524-1-0" , "2013-05-24T16:45:40+03:00"
		// Need this in order to produce an update or a cancellation of an older CAP message
		//int updateNum = 10;
		//xmlCap = createCapCancelMsg (SENDER, "SFE1a-20130524-1-0", "2013-05-24T16:45:40+03:00", updateNum, ATHENS_POLYGON, ATHENS_AREA_DESC);
		
		
		//String SENDER = "SFE";
		String TIME = "2013-02-18T16:15:20+02:00"; // (SFE1-20121118-1-1)
		String OLDID = "SFE1c-20130218-1-0";
		
		//String HOSPITAL_AREA_DESC = "Hospital in Vienna";
		String HOSPITAL_POLYGON_1 = "15.4,18.9 0.05";
		//String HOSPITAL_POLYGON_2 = "15.4,18.9 0.05";
		//String HOSPITAL_POLYGON_3 = "15.4,18.9 0.05";
		
		String FIRE_AREA_DESC = "Suburban area outside Vienna";
		String FIRE_POLYGON_1 = "15.4,18.9 0.05";
		//String FIRE_POLYGON_2 = "15.4,18.9 0.05";
		
		String REVIEW_GAS_DESC = "People in danger due to gas leakage and a fire event in the wider area of Salerno";
		String REVIEW_GAS_POLYGON = "40.65042,14.82806 40.64896,14.82700 40.64746,14.82970 40.64896,14.83100 40.65042,14.82806";
		String GAS_EVENT = "GAS";
		String GAS_CATEGORY = "Safety";
		
		String REVIEW_FIRE_DESC = "The mountain at the east side of Salerno is in danger due to a fire event";
		String REVIEW_FIRE_POLYGON = "40.66111,14.81535 40.64965,14.83526 40.65544,14.85869 40.67992,14.85165 40.67966,14.82316 40.66957,14.82075 40.66111,14.81535";
		String FIRE_EVENT = "Fire";
		String FIRE_CATEGORY = "Fire";
		
		int updateNum = 14;
		//xmlCap = createCapCancelMsg("SFE" , "SFE2-20131113-1-0" , "2013-11-13T15:18:05+01:00", updateNum, REVIEW_GAS_POLYGON, REVIEW_GAS_DESC, GAS_EVENT, GAS_CATEGORY);
		
		//This will be the input of the CAP adapter
		//String wsdlURL = "http://lnx1de.iessolutions.eu:3030/axis2/services/Inbound_EDXL_WebService?wsdl"; // IDIRA DMZ
		String wsdlURL = "http://idira-ies.salzburgresearch.at:3030/Inbound_EDXL/services/Inbound_EDXL_WebService?wsdl"; // IDIRA MICS
		
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
		InboundEDXLWebServicePortType capService = capServiceClient.getInboundEDXLWebServiceHttpSoap11Endpoint();
		
		// Test methods
		if(capService.isValidCAP(xmlCap)) {
			System.out.println("[Validate CAP] " + capService.validateCAP(xmlCap));
			
			try {
				Boolean reply = capService.receiveCAP(xmlCap);
				System.out.println("[Save CAP] "+reply);
			} catch (InboundEDXLWebServiceException_Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.err.print("[ERROR] "+e.getMessage());
			}
		}
		else
			System.err.println("[Validate CAP] " + capService.validateCAP(xmlCap));
	}

}