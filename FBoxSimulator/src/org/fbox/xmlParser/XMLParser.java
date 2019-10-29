package org.fbox.xmlParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLParser {
	
	private String xml;
	private Boolean register;
	
	public XMLParser (String xmlToBeParsed) {
		this.xml = xmlToBeParsed;
		this.register = this.xml.contains("RegisterSensor");
	}

	public String parseElement () {
		
		DocumentBuilder db;
		NodeList document;
		String output = null;
		
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(this.xml.getBytes("UTF-8")));
		    doc.getDocumentElement().normalize();
		    Element line, linetime; 
		    
		    //Identify if we have a registration or an insertion
		    if(this.register == false) {
		    	document = doc.getElementsByTagName("InsertObservation");
		   
			    for (int i = 0; i < document.getLength(); i++) {
			    	Element element = (Element) document.item(i);
			    	
			    	NodeList name = element.getElementsByTagName("AssignedSensorId");
			        line = (Element) name.item(0);
			        
			        NodeList time = element.getElementsByTagName("gml:timePosition");
			        linetime = (Element) time.item(0);
			        
			        output = "[" + getCharFromElement(line) + ", " + getCharFromElement(linetime) + "]";
			    } 
		    }
		    else {
		    	document = doc.getElementsByTagName("sml:IdentifierList");
		    	
		    	for (int i = 0; i < document.getLength(); i++) {
			    	Element element = (Element) document.item(i);
			    	
			    	NodeList name = element.getElementsByTagName("sml:value");
			        line = (Element) name.item(0);
			        
			        output = "[" + getCharFromElement(line) + "]";
			    }
		    }
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return output;
	}
	
	public static String getCharFromElement(Element e)
	{
		Node child = e.getFirstChild();
		if (child instanceof CharacterData)
		{
			CharacterData cd = (CharacterData) child;
			return cd.getData().trim();
		}
		return "";
	}
}
