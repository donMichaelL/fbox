package org.sos.adapter;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.sos.exception.SOSException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SimpleSOSHttpAdapter {

	private String postServer;
	private HttpClient client;
	private HttpContext context;
	
	public SimpleSOSHttpAdapter (HttpClient httpClient, String url) {
		this.postServer = url;
		this.client = httpClient;
		this.context = new BasicHttpContext();
	}
	
	public String postXML(String strXML) throws SOSException  {
		
		HttpPost httppost = new HttpPost(this.postServer);
		HttpEntity entity = null;
		HttpResponse response = null;
		
		String message = null;
		
		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			
	        nameValuePairs.add(new BasicNameValuePair("request", strXML));
	        
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        
	        // Executes the HTTP request and gets the appropriate response
	        response = this.client.execute(httppost, this.context);
	        
        	entity = response.getEntity();
        	int code = response.getStatusLine().getStatusCode();
        	
        	if (code != 200) {
        		EntityUtils.consume(entity); //In order to clear underlying stream
        		throw new SOSException("Server replied with HTTP error code: " + code);
        	}
        	
        	message = examineResponse(entity);
        	
        	//In order to clear underlying stream
        	EntityUtils.consume(entity);
        } catch (IOException e) { //For exception in httppost
        	try {
        		httppost.abort();
        		EntityUtils.consume(entity);	
        	} catch (IOException e1) {
        		e1.printStackTrace();
        	}
        	throw new SOSException("Cannot execute post: " + e.getMessage());
		}
        
        return message;
	}
	
	private String examineResponse(HttpEntity entity) throws SOSException {
		
		String message = null;
		
		try {
			if(entity != null) {
				String xmlString = EntityUtils.toString(entity);

				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = factory.newDocumentBuilder();
				InputSource inStream = new InputSource();
				inStream.setCharacterStream(new StringReader(xmlString));
				Document doc = db.parse(inStream);

				String value;

				//This covers the occasion of having an exception from SOS
				NodeList nlExc = doc.getElementsByTagName("ows:ExceptionText");
				for (int i = 0; i < nlExc.getLength(); i++) {
					if (nlExc.item(i).getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
						org.w3c.dom.Element nameElement = (org.w3c.dom.Element) nlExc.item(i);
						value = nameElement.getFirstChild().getNodeValue().trim();
						
						StringTokenizer tokenizer = new StringTokenizer(value);
						
						if(tokenizer.nextToken(" ").equals("Sensor"))
							message = "{INFO} "+value;
						else
							throw new SOSException(value, "Error in response from SOS service.");
					}
				}
				
				//To inform if a sensor has been registered successfully in SOS
				NodeList nlSucReg = doc.getElementsByTagName("sos:AssignedSensorId");
				if(nlSucReg.getLength() != 0)
					message = "{INFO} Sensor registered successfully in SOS!";
				
				//To inform if an observation has been inserted successfully in SOS
				NodeList nlSucIns = doc.getElementsByTagName("sos:AssignedObservationId");
				if(nlSucIns.getLength() != 0)
					message = "{INFO} Observation inserted successfully in SOS!";
			} //end if statement
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (DOMException e){
			e.printStackTrace();
		} catch (ParserConfigurationException e){
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		} catch (SAXException e){
			e.printStackTrace();
		}
		
		return message;
	}
}
