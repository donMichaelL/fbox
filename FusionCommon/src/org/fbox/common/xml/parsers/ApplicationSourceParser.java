package org.fbox.common.xml.parsers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.xml.bind.ValidationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;

import org.fbox.common.exception.EventSourceParserException;
import org.fbox.common.xml.data.AdapterDAO;
import org.fbox.common.xml.data.AlgorithmDAO;
import org.fbox.common.xml.data.ApplicationDAO;
import org.fbox.common.xml.data.CombineType;
import org.fbox.common.xml.data.ContextorDAO;
import org.fbox.common.xml.data.ContextorType;
import org.fbox.common.xml.data.FormatterDAO;
import org.fbox.common.xml.data.OutputDAO;
import org.fbox.common.xml.data.SelectDAO;
import org.fbox.common.xml.data.SourceDAO;
import org.fbox.common.xml.data.StreamerSelectorDAO;
import org.fbox.util.XmlHelper;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class ApplicationSourceParser {
	
	private static String ROOT_ELEMENT_NAME="fusion-application";
	private static String XML_SCHEMA_URL="xml/applicationEvent.xsd"; // "http://localhost:8080/FusionWeb/xml/applicationEvent.xsd";
	
	private final String STREAM_SELECTOR_PATH=ROOT_ELEMENT_NAME +"/streamers/streamSelector";
	private final String CONTEXTOR_PATH=ROOT_ELEMENT_NAME +"/contextors/contextor";
	private final String OUTPUT_PATH=ROOT_ELEMENT_NAME +"/output/out";
	
	private static String fusionApplicationXMLSchema="C:/development/IDIRA/jboss-as-7.1.0.Final/standalone/fusion/FusionApplication.xsd";
	
	protected XmlHelper xHelper;
	
	public ApplicationSourceParser(String configUrl) throws ParserConfigurationException {
		xHelper=new XmlHelper(configUrl);
	}

	public ApplicationSourceParser(StringReader source) throws ParserConfigurationException {
		xHelper=new XmlHelper(source);
	}
	
	public LinkedHashMap<String, StreamerSelectorDAO> getStreamSelectors() throws EventSourceParserException {
		
		LinkedHashMap<String, StreamerSelectorDAO> streamSelectorsSetToReturn=null;
		
		NodeList nodes = xHelper.getAllNodes(STREAM_SELECTOR_PATH, null);
		
		if (nodes!=null) {
			streamSelectorsSetToReturn=new LinkedHashMap<String,StreamerSelectorDAO>();
			
			int numOfSelectors=nodes.getLength();
			for (int i=0;i<numOfSelectors;i++) {
				
				Element elem=(Element)nodes.item(i);
				String id=elem.getAttribute("id");
				if (id.isEmpty()) {
					throw new EventSourceParserException("'id' attribute is mandatory for streamSelector(index="+ i +")");
				}

				StreamerSelectorDAO ss=new StreamerSelectorDAO();
				ss.setId(id);

				ss.setDynamic(elem.getAttribute("dynamic").equals("true")?true:false); //get dynamic attribute
				Set<Element> selectSet=xHelper.getChildElements(elem, "select"); //get select constraints
				for (Element select : selectSet) {
					SelectDAO selectorConstraint=new SelectDAO();
					selectorConstraint.setType(select.getAttribute("type"));
					selectorConstraint.setValue(select.getTextContent());
					ss.addSelectConstraint(selectorConstraint);
				}
				
				streamSelectorsSetToReturn.put(id, ss); //add to map
			}
		}
				
		return streamSelectorsSetToReturn;
	}
	
	public LinkedHashMap<String, ContextorDAO> getContextors() throws EventSourceParserException {
		
		LinkedHashMap<String, ContextorDAO> contextorsToReturn=null;
		
		NodeList nodes = xHelper.getAllNodes(CONTEXTOR_PATH, null);
		
		if (nodes!=null) {
			contextorsToReturn=new LinkedHashMap<String,ContextorDAO>();
			
			int numOfContexters=nodes.getLength();
			for (int i=0;i<numOfContexters;i++) {	
				Element elem=(Element)nodes.item(i);
				String id=elem.getAttribute("id");
				if (id.isEmpty()) {
					throw new EventSourceParserException("'id' attribute is mandatory for 'contexter'(index="+ i +")");
				}	
		
				ContextorDAO ct=new ContextorDAO();
				ct.setId(id);

				ct.setType(ContextorType.select(elem.getAttribute("type"))); //get type attribute			
								

				ct.setAlgorithm(getAlgorithm(xHelper.getFirstChildElement(elem, "algorithm"),id));	//set algorithm
				ct.setMissingValueAlgorithm(getAlgorithm(xHelper.getFirstChildElement(elem, "missingValueAlgorithm"),id)); //setMissingValue algorithm
				
				Element sourcesElement=xHelper.getFirstChildElement(elem,"sources");
				ct.setSources(getSources(sourcesElement,id));
				
				if (sourcesElement!=null)
					ct.setSourceCombineType(CombineType.select(sourcesElement.getAttribute("combine")));
				contextorsToReturn.put(id, ct); //add to map
			}
		}
		
		return contextorsToReturn;
	}
		
	public ArrayList<SourceDAO> getSources(Element sourcesElement,  String contexterId) throws EventSourceParserException {		
		
		ArrayList<SourceDAO> listOfSources=new ArrayList<SourceDAO>();
		if (sourcesElement!=null) {
			
			NodeList sourceList=sourcesElement.getElementsByTagName("src");
			if (sourceList!=null) {
				int numOfSources=sourceList.getLength();
				for (int i=0;i<numOfSources;i++) {
					SourceDAO source=new SourceDAO();
					Element sElem=(Element)sourceList.item(i);
					String id=sElem.getAttribute("id");
					String selector=sElem.getAttribute("selector");
					if (id.isEmpty() && selector.isEmpty())
						throw new EventSourceParserException("either 'id' or 'selector' attribute should be defined for a single source [source(" +"'(index="+ i +"), Contexter:"+contexterId+"]");
					else if (!id.isEmpty() && !selector.isEmpty())
						throw new EventSourceParserException("'id' and 'selector' attribute cannot be set at the same time for a single source [source(" +"'(index="+ i +"), Contexter:"+contexterId+"]");
					else if (!id.isEmpty()) 
						source.setId(id);
					else
						source.setId(selector);
					
					String timeLimit=sElem.getAttribute("timeLimit");
					if (!timeLimit.isEmpty()) {
						source.setTimeLimit(Long.parseLong(timeLimit));
					}
					
					listOfSources.add(source);
				}
			}
		}
		return listOfSources;
	}
	
	public LinkedHashMap<String, OutputDAO> getOutputs() throws EventSourceParserException {

		LinkedHashMap<String, OutputDAO> outputsToReturn=null;
		
		NodeList nodes = xHelper.getAllNodes(OUTPUT_PATH, null);
		
		if (nodes!=null) {
			outputsToReturn=new LinkedHashMap<String,OutputDAO>();
			
			int numOfOuts=nodes.getLength();
			for (int i=0;i<numOfOuts;i++) {	
				Element elem=(Element)nodes.item(i);
				String id=elem.getAttribute("id");
				if (id.isEmpty()) {
					throw new EventSourceParserException("'id' attribute is mandatory for element 'Out'(index="+ i +")");
				}	
		
				OutputDAO out=new OutputDAO();
				out.setId(id);

				out.setFormatter(getFormatter(xHelper.getFirstChildElement(elem, "formatter"), id)); //get formatter
				out.setAdapter(getAdapter(xHelper.getFirstChildElement(elem, "adapter"), id)); //get formatter

				Element sourcesElement=xHelper.getFirstChildElement(elem,"sources");
				out.setSources(getSources(sourcesElement,id));
				
				outputsToReturn.put(id, out); //add to map
			}
		}
		
		return outputsToReturn;		
	}

	private FormatterDAO getFormatter(Element elem, String outId) throws EventSourceParserException {
		FormatterDAO formatterToReturn=null;
		
		if (elem!=null) {
			String type=elem.getAttribute("type");
			if (type.isEmpty()) {
				throw new EventSourceParserException("'type' attribute is mandatory for 'formatter' element (Out:"+outId+")");
			}		
			
			formatterToReturn=new FormatterDAO();
			formatterToReturn.setType(type);
			formatterToReturn.addParameterList(getIparamList(elem, outId));
		}
		
		return formatterToReturn;
	
	}	

	private AdapterDAO getAdapter(Element elem, String outId) throws EventSourceParserException {
		AdapterDAO adapterToReturn=null;
		
		if (elem!=null) {
			String type=elem.getAttribute("type");
			if (type.isEmpty()) {
				throw new EventSourceParserException("'type' attribute is mandatory for 'adapter' element (Out:"+outId+")");
			}		
			
			adapterToReturn=new AdapterDAO();
			adapterToReturn.setType(type);
			adapterToReturn.addParameterList(getIparamList(elem, outId));
		}
		
		return adapterToReturn;
	
	}		
	private AlgorithmDAO getAlgorithm(Element elem, String contexterId) throws EventSourceParserException {
		AlgorithmDAO algorithmToReturn=null;
		
		if (elem!=null) {
			String name=elem.getAttribute("name");
			if (name.isEmpty()) {
				throw new EventSourceParserException("'name' attribute is mandatory for 'missingValueAlgorithm'/'algorithm' element (Contexter:"+contexterId+")");
			}		
			
			algorithmToReturn=new AlgorithmDAO();
			algorithmToReturn.setName(name);
			algorithmToReturn.addParameterList(getIparamList(elem, contexterId));
		}
		
		return algorithmToReturn;
	
	}
	
	private HashMap<String, String> getIparamList(Element elem, String contexterId) throws EventSourceParserException {

		HashMap<String, String> iparamsToReturn=new HashMap<String, String>();
		//retrieve and parse iparams children
		Set<Element> iparams=xHelper.getChildElements(elem, "iparam");		
		
		for (Element iparam : iparams) {
			String name=iparam.getAttribute("name");
			if (name.isEmpty()) {
				throw new EventSourceParserException("'name' attribute is mandatory for 'iparam' element (" + elem.getAttribute("name")+ "," + ", Contexter:"+contexterId+")");				
			}
			iparamsToReturn.put(name, iparam.getTextContent());
		}	

		return iparamsToReturn;		
	}	
	
	public ApplicationDAO parse(boolean validate) throws EventSourceParserException {
		// validate = false
		boolean isValid;
		
		if (validate) {
			try {
				isValid=xHelper.validate(new StreamSource(new FileInputStream(fusionApplicationXMLSchema)));
			} catch (FileNotFoundException | ValidationException e) {
				e.printStackTrace();
				throw new EventSourceParserException(e.getMessage());
			}
		} else
			isValid=true;
		
		//isValid = true
		//continue parsing
		if (isValid) {
			ApplicationDAO application=new ApplicationDAO();
			
			Element root=(Element)xHelper.getNode(ROOT_ELEMENT_NAME, null);
			
			String applicationId=root.getAttribute("id");
			if (applicationId.isEmpty()) {
				throw new EventSourceParserException("'id' attribute is mandatory for Application element (root)");
			}
			application.setId(applicationId);
			
			String description=root.getAttribute("description");
			if (description.isEmpty())
				application.setDescription(description);
			
			application.setStreamers(getStreamSelectors());
			application.setContextors(getContextors());
			application.setOutput(getOutputs());
			return application;
		} else {
			throw new EventSourceParserException("Validation Error: Specified application script is not valid"); 
		}
	}
		
}
