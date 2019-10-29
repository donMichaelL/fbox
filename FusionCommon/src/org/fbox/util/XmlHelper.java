package org.fbox.util;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.bind.ValidationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlHelper {
		
	private Document xmlDocument;
	private XPath xPath; 
	
	public XmlHelper(StringReader configSource) throws ParserConfigurationException {
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		XPathFactory xpf=XPathFactory.newInstance();
		xPath=xpf.newXPath();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringBuffer sb=new StringBuffer();
			this.xmlDocument = db.parse(new InputSource(configSource));
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new ParserConfigurationException("Unable to process file: " + e.getMessage());
		}				
	}
	
	public XmlHelper(String configUrl) throws ParserConfigurationException {
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		XPathFactory xpf=XPathFactory.newInstance();
		xPath=xpf.newXPath();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			this.xmlDocument = db.parse(configUrl);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new ParserConfigurationException("Unable to process file: " + e.getMessage());
		}				
	}	
	

	public boolean validate(Source xmlSchemaSource) throws ValidationException {
		boolean isValid=true;
		
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema;
		try {
			System.out.println(xmlDocument.getTextContent());
			schema = schemaFactory.newSchema(xmlSchemaSource);
			Validator validator=schema.newValidator();
			validator.validate(new DOMSource(xmlDocument));
		} catch (SAXException | IOException e) {
			isValid=false;
			throw new ValidationException(e.getMessage());
		} 		
		return isValid;
	}
		
	public Node getNode(String xpath, Document xmlDocument) {
		//xmlDocument = null
		Node nodeToReturn=null;

		try {
			XPathExpression xPathExpression = xPath.compile(xpath);
			nodeToReturn = (Node)xPathExpression.evaluate(xmlDocument==null?this.xmlDocument:xmlDocument,XPathConstants.NODE);			
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return  nodeToReturn;	
	}

	public NodeList getAllNodes(String xpath, Document xmlDocument) {
		
		NodeList nodeToReturn=null;
		try {
			XPathExpression xPathExpression = xPath.compile(xpath);
			Object obj= xPathExpression.evaluate(xmlDocument==null?this.xmlDocument:xmlDocument,XPathConstants.NODESET);
			if (obj!=null) {
				nodeToReturn=(NodeList)obj;
			}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return  nodeToReturn;	
	}	
	
	
	public Set<Element> getChildElements(Node node, String nodeName) {
		Set<Element> setOfChildren=new HashSet<Element>();
		NodeList children=node.getChildNodes();
		int numOfChildren=children.getLength();
		for (int i=0;i<numOfChildren;i++) {
			Node child=children.item(i);
			if (child.getNodeType()==Document.ELEMENT_NODE && child.getNodeName().equals(nodeName)) {						
				setOfChildren.add((Element)child);
			} 
		}
		return setOfChildren;
	}

	public Element getFirstChildElement(Node node, String nodeName) {
		Element childToReturn=null;
		NodeList children=node.getChildNodes();
		int numOfChildren=children.getLength();
		for (int i=0;i<numOfChildren;i++) {
			Node child=children.item(i);			
			if (child.getNodeType()==Document.ELEMENT_NODE && child.getNodeName().equals(nodeName)) {						
				childToReturn=(Element)child;
				break;
			} 
		}
		return childToReturn;	
	}
		
}
