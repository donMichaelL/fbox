package org.fbox.fusion.output.adapter.impl;

import ies.edxlinbound.webservice.InboundEDXLWebService;
import ies.edxlinbound.webservice.InboundEDXLWebServiceException_Exception;
import ies.edxlinbound.webservice.InboundEDXLWebServicePortType;

import java.net.MalformedURLException;
import java.net.URL;

import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.fbox.common.data.IContext;
import org.fbox.common.exception.OutputAdapterException;
import org.fbox.common.output.IAdapter;
import org.fbox.fusion.output.adapter.AbstractAdapter;

import javax.xml.parsers.*;
import org.xml.sax.InputSource;
import org.w3c.dom.*;
import java.io.*;

@Stateless(name="capHttp")
@Remote ({IAdapter.class})
public class CapAdapter extends AbstractAdapter {

	@Override
	public String[] getRequiredParameters() {
		String[] params={"wsdl"};
		return params; //No parameters for this adapter for now
	}
	
	@Override
	public String getType() {
		return "capHttp";
	}
	
	@Override
	public void dispatch(IContext state, Object data) throws OutputAdapterException {
	
		//Handle input from CapFormatter
		if(data == null) {
			throw new OutputAdapterException("CapAdapter::Send: Warning: No need to post xml to destination");
		}
		
		String wsdlURL = (String)state.getContextParameter("wsdl");
		
		//At first we are going to check if this is a valid url
		URL url = null;
		try {
			url = new URL(wsdlURL);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new OutputAdapterException(e.getMessage());
		}
		
		//Since we have a valid URL we access the EDXL Inbound Service

		// Access CAP service
		InboundEDXLWebService capServiceClient = new InboundEDXLWebService(url);
		InboundEDXLWebServicePortType capService = capServiceClient.getInboundEDXLWebServiceHttpSoap11Endpoint();

		String xmlCAP = data.toString();
		
		// Check if the formatted CAP message is valid
		Boolean valid = capService.isValidCAP(xmlCAP);
		String validate  = capService.validateCAP(xmlCAP); //Validate XML that is going to be sent 
		
		if(!valid)
			throw new OutputAdapterException("CapExporter::Send: Validation Error: "+validate);
		else
			System.out.println("VALIDATE-----------------------------> "+validate);
		
		System.out.println("Sending CAP---------------------------->\n"+xmlCAP);
		
		Boolean reply = false;
		try {
			reply = capService.receiveCAP(xmlCAP);
			System.out.println("CAP REPLY-----------------------------> "+reply);
			//Keep a logging in order to easily delete CAP messages from COP
			parseXML(xmlCAP);
		} catch (InboundEDXLWebServiceException_Exception e) {
			throw new OutputAdapterException("CapExporter::Send: Save Error: "+reply);
		}
	}
	
	public static void parseXML (String xml) {
		try {
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        InputSource is = new InputSource();
	        is.setCharacterStream(new StringReader(xml));

	        Document doc = db.parse(is);
	        NodeList nodes1 = doc.getElementsByTagName("sender");
	        Element sender = (Element) nodes1.item(0);
	        
	        NodeList nodes2 = doc.getElementsByTagName("identifier");
	        Element identifier = (Element) nodes2.item(0);
	        
	        NodeList nodes3 = doc.getElementsByTagName("sent");
	        Element sent = (Element) nodes3.item(0);
	        
	        System.out.println("[CAP_ADAPTER]  --->   \""+sender.getTextContent()+"\" , \""+identifier.getTextContent()+"\" , \""+sent.getTextContent()+"\"");
	    }
	    catch (Exception e) {
	        e.printStackTrace();
	    }
	}
}
