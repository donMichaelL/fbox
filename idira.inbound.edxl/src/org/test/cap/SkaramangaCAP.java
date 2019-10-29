package org.test.cap;

import ies.edxlinbound.webservice.CheckStatus;
import ies.edxlinbound.webservice.InboundEDXLWebService;
import ies.edxlinbound.webservice.InboundEDXLWebServiceException_Exception;
import ies.edxlinbound.webservice.InboundEDXLWebServicePortType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;
import java.util.StringTokenizer;

import java.awt.List;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

public class SkaramangaCAP {

	private static final QName SERVICE_NAME = new QName("http://webservice.edxlinbound.ies", "Inbound_EDXL_WebService");
	public static Properties properties = new Properties();
	
	private static String referenceBlock = "";
	private static String creationTime = "";
	 
	private static void loadProperties(String filename) {
		try {
			properties.load(new FileInputStream(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//load the file
	
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
	
	public static String createCapAlertMsg (String sender, String location, String locationDescription, int msgNumber, String certainty, String event, String category, String urgency, String headline, String eventDesc) {
		
		// *** CAP Alert *** // 
		CapMessage capMsg = loadCapMessage();
		
		// Identifier
		SimpleDateFormat sdfId = new SimpleDateFormat(CapConstants.CAP_DATE_FORMAT_IDENTIFIER);
		String identifier = sender + "-" + sdfId.format(new Date()) + "-" + msgNumber + "-0";
		//System.out.println("Identifier: " + identifier);
		
		capMsg.setIdentifier(identifier);
		
		// Msg type
		capMsg.setMsgType("Alert");
		
		capMsg.setSender(sender);
		capMsg.setCertainty(certainty);
		capMsg.setSeverity("Severe");
		capMsg.setUrgency(urgency);
		capMsg.setCategory(category);
		capMsg.setEvent(event);
		capMsg.setHeadline(headline);
		capMsg.setDescription(eventDesc);
		
		// Sent time
		SimpleDateFormat sdfSent = new SimpleDateFormat(CapConstants.CAP_DATE_FORMAT_SENT);
		Date creation = new Date();
		String sent = sdfSent.format(creation);
		capMsg.setSent(sent);
		//System.out.println("Sent: " + sent);
		
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
		
		referenceBlock = sender + "," + identifier + "," + sent;
		creationTime = String.valueOf(creation.getTime());
		
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
		//System.out.println("Identifier: " + identifier);
		
		// Sent time of update CAP Msg
		SimpleDateFormat sdfSent = new SimpleDateFormat(CapConstants.CAP_DATE_FORMAT_SENT);
		String sent = sdfSent.format(new Date());
		capMsg.setSent(sent);
		//System.out.println("Sent: " + sent);
		
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
		//System.out.println("Identifier: " + identifier);
		
		// Sent time of cancel CAP Msg
		SimpleDateFormat sdfSent = new SimpleDateFormat(CapConstants.CAP_DATE_FORMAT_SENT);
		String sent = sdfSent.format(new Date());
		capMsg.setSent(sent);
		//System.out.println("Sent: " + sent);
		
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
		
		// Set urgency
		capMsg.setUrgency("Past");
		
		// Set severity
		capMsg.setSeverity("Severe");
		
		// Set certainty
		capMsg.setCertainty("Observed");
		
		
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
	
	public static void main(String[] args) {
		
		String configFileName = "config/cap.properties";
		String filesFolder = "shared/";
		loadProperties(configFileName);
		Scanner in = new Scanner(System.in);
		
		// MENU in order to retrieve user selection
		System.out.println("*****************    MAIN MENU    *****************\n\n");
		System.out.println("The following functionallities are provided:");
		System.out.println("--------------------------------------------");
		System.out.println("*  CAP creation      ->    Press 1");
		System.out.println("*  CAP(s) deletion   ->    Press 2");
		System.out.println("*  Terminate         ->    Press 0");
		System.out.print("\nPlease give your selection: ");
		Integer mainSelection = in.nextInt();
		
		while(mainSelection<0 || mainSelection>2) {
			System.out.println("WRONG INPUT!!! Please provide 0,1 or 2 as inputs...");
			System.out.print("Please give your selection: ");
			mainSelection = in.nextInt();
		}
		
		// This will be the input of the CAP adapter
		String wsdlURL = "http://lnx1de.iessolutions.eu:3030/axis2/services/Inbound_EDXL_WebService?wsdl";

		// At first we are going to check if this is a valid url
		URL url = null;
		try {
			url = new URL(wsdlURL);
		} catch (MalformedURLException e) {
			java.util.logging.Logger.getLogger(InboundEDXLWebService.class.getName()).log(java.util.logging.Level.WARNING, "Can not initialize the default wsdl from {0}", wsdlURL);
		}

		// Since we have a valid url we access the EDXL Inbound Service

		// Access CAP service
		InboundEDXLWebService capServiceClient = new InboundEDXLWebService(url);
		InboundEDXLWebServicePortType capService = capServiceClient.getInboundEDXLWebServiceHttpSoap11Endpoint();
		
		switch(mainSelection) {
			case 1: //CAP creation
				System.out.println("\n\n***********   CAP creation menu   ***********\n");
				System.out.println("The following functionallities are provided:");
				System.out.println("--------------------------------------------");
				System.out.println("* Fire alert        ->    Press 1");
				System.out.println("* Earthquake alert  ->    Press 2");
				System.out.println("* Terminate         ->    Press 0");
				System.out.print("\nPlease give your selection: ");
				Integer createSelection = in.nextInt();
				String prefix = "";
				
				while(createSelection<0 || createSelection>2) {
					System.out.println("WRONG INPUT!!! Please provide 0,1 or 2 as input...");
					System.out.print("Please give your selection: ");
					createSelection = in.nextInt();
				}
				
				switch(createSelection) {
					case 0: 
						System.out.println("Program Termination...");
						return;
					case 1:
						prefix = "fire";
						break;
					case 2:
						prefix = "earthquake";
						break;
				}
				
				String SKARAMANGA_POLYGON = properties.getProperty(prefix + ".location");
				String SKARAMANGA_AREA_DESC = properties.getProperty(prefix + ".description");
				String SENDER = properties.getProperty(prefix + ".sender");
				String CERTAINTY = properties.getProperty(prefix + ".certainty");
				String EVENT = properties.getProperty(prefix + ".event");
				String CATEGORY = properties.getProperty(prefix + ".category");
				String URGENCY = properties.getProperty(prefix + ".urgency");
				String HEADLINE = properties.getProperty(prefix + ".headline");
				String EVENT_DESC = properties.getProperty(prefix + ".eventdescription");
				int MSG_NUMBER = Integer.parseInt(properties.getProperty(prefix + ".msgNumber"));
				
				String xmlCap = null;
				
				xmlCap = createCapAlertMsg (SENDER, SKARAMANGA_POLYGON, SKARAMANGA_AREA_DESC, MSG_NUMBER, CERTAINTY, EVENT, CATEGORY, URGENCY, HEADLINE, EVENT_DESC);
				System.out.println(xmlCap);
				
				if(capService.isValidCAP(xmlCap)) {
					System.out.println("[Validate CAP] " + capService.validateCAP(xmlCap));
					
					try {
						Boolean reply = capService.receiveCAP(xmlCap);
						System.out.println("[Save CAP] "+reply);
						
						//Save the new file in the respective folder of the project
						PrintWriter writer = new PrintWriter(filesFolder+"cap_"+creationTime+".txt", "UTF-8");
						writer.println(referenceBlock);
						writer.close();
					} catch (InboundEDXLWebServiceException_Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.err.print("[ERROR] "+e.getMessage());
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else
					System.err.println("[Validate CAP] " + capService.validateCAP(xmlCap));
				
				System.out.println("\nCAP alert has been successfully created!!");
				System.out.println("Program Termination...");
				break;
			case 2:
				//Read the contents of the folder (only the .txt files)
				FileFilter fileFilter = new FileFilter() {

			        public boolean accept(File file) {
			            //if the file extension is .txt return true, else false
			            if (file.getName().endsWith(".txt") || file.getName().endsWith(".TXT")) {
			                return true;
			            }
			            return false;
			        }
			    };
			    
			    File folder = new File(filesFolder);
				File[] listOfFiles = folder.listFiles(fileFilter);
				String[] referenceTable = new String[listOfFiles.length]; //Keeps the contents of the files in the "shared" folder
				
				System.out.println("\n\n**********************    CAP(s) deletion menu    **********************\n");
				System.out.println("Select one of the following options for deletion:");
				System.out.println("-------------------------------------------------");
				
				if(listOfFiles.length == 0) {
					System.out.println("\nNo CAP reference files to retrieve from the respective folder!");
					System.out.println("Program Termination...");
					return;
				}
				
				for(int i=0; i<listOfFiles.length ; i++) {
					try {
						FileInputStream fstream = new FileInputStream(filesFolder+listOfFiles[i].getName());
						DataInputStream instream = new DataInputStream(fstream);
						BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
						referenceTable[i] = reader.readLine(); //Retrieve reference string
						reader.close();
						System.out.println("* "+referenceTable[i]+"\t->\tPress "+(i+2));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				System.out.println("* All\t\t\t->\tPress 1");
				System.out.println("* Terminate\t\t->\tPress 0");
				System.out.print("\nPlease give your selection: ");
				
				Integer deleteSelection = in.nextInt();
				
				while(deleteSelection<0 || deleteSelection>(referenceTable.length+1)) {
					System.out.println("WRONG INPUT!!! Please provide {0,1,..,"+(referenceTable.length+1)+"} as input...");
					System.out.print("Please give your selection: ");
					deleteSelection = in.nextInt();
				}
				
				String [] deleteList;
				String [] fileNameList;
				
				switch(deleteSelection) {
					case 0: 
						System.out.println("Program Termination...");
						return;
					case 1: //All
						deleteList = new String[referenceTable.length];
						fileNameList = new String[referenceTable.length];
						deleteList = referenceTable;
						
						for(int i=0 ; i<fileNameList.length ; i++)
							fileNameList[i] = listOfFiles[i].getName();
						
						break;
					default: //All the other selections
						deleteList = new String[1];
						fileNameList = new String[1];
						deleteList[0] = referenceTable[deleteSelection-2];
						fileNameList[0] = listOfFiles[deleteSelection-2].getName();
						break;
				}
				
				//Delete process
				for(int i=0 ; i<deleteList.length ; i++) { 
					
					StringTokenizer str = new StringTokenizer(deleteList[i],",");
					String SENDER_REF = str.nextToken();
					String ID_REF = str.nextToken();
					String SENT_REF = str.nextToken();
					
					//Which type (fire of earthquake)
					StringTokenizer type = new StringTokenizer(ID_REF,"-");
					type.nextToken();
					type.nextToken();
					
					Integer eventType = Integer.valueOf(type.nextToken());
					Integer messageNum = Integer.valueOf(type.nextToken()) + 1;
					
					String msgPrefix="";
					
					switch(eventType) {
						case 1:
							msgPrefix = "fire";
							break;
						case 2:
							msgPrefix = "earthquake";
							break;
					}
					
					String REF_AREA = properties.getProperty(msgPrefix + ".location");
					String REF_AREA_DESC = properties.getProperty(msgPrefix + ".description");
					String REF_EVENT = properties.getProperty(msgPrefix + ".event");
					String REF_CATEGORY = properties.getProperty(msgPrefix + ".category");
					
					String xmlCancelCap = null;
					xmlCancelCap = createCapCancelMsg(SENDER_REF , ID_REF , SENT_REF, messageNum, REF_AREA, REF_AREA_DESC, REF_EVENT, REF_CATEGORY);
					System.out.println(xmlCancelCap);
					
					if(capService.isValidCAP(xmlCancelCap)) {
						System.out.println("[Validate CAP] " + capService.validateCAP(xmlCancelCap));
						
						try {
							Boolean reply = capService.receiveCAP(xmlCancelCap);
							System.out.println("[Save CAP] "+reply);
							
							System.out.println("\nCAP alert \""+ deleteList[i] +"\" has been successfully deleted!!");
							
							//Also delete this file from the "shared/" folder
							File file = new File(filesFolder + fileNameList[i]);
				    		if(file.delete()){
				    			System.out.println("File \"" + file.getName() + "\" has been successfully deleted!!\n");
				    		}else{
				    			System.out.println("Delete operation is failed on file \"" + file.getName() + "\".\n");
				    		}
						} catch (InboundEDXLWebServiceException_Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							System.err.print("[ERROR] "+e.getMessage());
						} 
					}
					else
						System.err.println("[Validate CAP] " + capService.validateCAP(xmlCancelCap));
				}
			case 0:
				System.out.println("Program Termination...");
				return;
		}
	}
}