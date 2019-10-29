package org.fbox.fusion.output.formatter.impl;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.fbox.common.application.data.DataElement;
import org.fbox.common.data.AlgorithmContext;
import org.fbox.common.data.IAlgorithmContext;
import org.fbox.common.data.IContext;
import org.fbox.common.data.IDataElement;
import org.fbox.common.data.InputParameter;
import org.fbox.common.exception.FormatterException;
import org.fbox.common.exception.FormatterInitializationException;
import org.fbox.common.output.IFormatter;
import org.fbox.fusion.output.formatter.AbstractFormatter;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Stateless (name="areaTilesSISO")
@Remote ({IFormatter.class})
public class AreaTileIDsSISO extends AbstractFormatter<String> {

	@Override
	public void initialize(IContext state, HashMap<String, InputParameter> iparams) throws FormatterInitializationException {
		
		ArrayList<String> tileIDs = new ArrayList<String>();
		ArrayList<String> allTileIDs = new ArrayList<String>();
		
		//initialize params
		String[] params=getRequiredParameters();
		if (params!=null) { //parse required params

			Set<String> requiredParameters=new HashSet<String>(Arrays.asList(params));
			Set<String> optionalParameters=new HashSet<String>(Arrays.asList(getOptionalParameters()));
			Set<String> subOptionalParameters=new HashSet<String>(Arrays.asList(getSubOptionalParameters()));
			
			Collection<InputParameter> paramValues=iparams.values();
			
			//Check for parameters that do not belong to the three lists
			for (InputParameter param : paramValues) {
				String paramName=param.getName();
				if (!requiredParameters.contains(paramName) && !optionalParameters.contains(paramName) && !subOptionalParameters.contains(paramName)) 
					System.out.println("WARNING --> Init Parameter '"+ paramName + "' not applicable for Formatter " + getType() +". Will be Ignored.");
			}
			
			//Check first for the required parameters
			for (InputParameter param : paramValues) {
				String paramName=param.getName();
				if (requiredParameters.remove(paramName)) {
					
					if(paramName.equalsIgnoreCase("tileIDs")) {
						
						if(param.getValue() == null || param.getValue().isEmpty()) {
							throw new FormatterInitializationException("The expected format for the \""+paramName+"\" parameter is: \"area_tile_0_12,area_tile_2_3,...,area_tile_4_90\".\n");
						}
						
						//Parse the tile IDs and load them to an ArrayList
						StringTokenizer st1 = new StringTokenizer(param.getValue(), ",");
						
						while(st1.hasMoreTokens()) {
							tileIDs.add(st1.nextToken());
						}
						
						state.setContextParameter(paramName, tileIDs);
					}
					else
						state.setContextParameter(paramName, param.getValue());
					
					//Check if we have a boolean value
					if(paramName.equalsIgnoreCase("propMatrix")) {
						if(!param.getValue().equalsIgnoreCase("true") && !param.getValue().equalsIgnoreCase("false"))
							throw new FormatterInitializationException("ERROR --> Init (Required) Parameter '"+ paramName + "' for Formatter " + getType() +" can only take 'true' or 'false' as value.\n");
					}
				} 
			}

			//check missing required parameters
			String errorMessage="";
			for (String s : requiredParameters) {
				errorMessage+="ERROR --> Init (Required) Parameter '"+ s + "' is needed for Formatter " + getType() +" but has not been specified.\n";
			}
			if (!errorMessage.isEmpty()) {
				System.out.println(errorMessage);
				throw new FormatterInitializationException(errorMessage);
			}		
			
			//If there is no required parameter missing then we check the value of the "propMatrix" parameter
			//If this value is true then we have to collect the optional parameters
			if(Boolean.parseBoolean(state.getContextParameter("propMatrix").toString())) {
				
				//We have to create a probability matrix, hence the rest parameters must be loaded
				for (InputParameter param : paramValues) {
					String paramName=param.getName();
					if (optionalParameters.remove(paramName)) {
						state.setContextParameter(paramName, param.getValue());
						
						if(paramName.equalsIgnoreCase("allTileIDs")) {
							//Parse the tile IDs and load them to an ArrayList
							
							StringTokenizer st2 = new StringTokenizer(param.getValue(), ",");
							
							while(st2.hasMoreTokens()) {
								allTileIDs.add(st2.nextToken());
							}
							
							state.setContextParameter(paramName, allTileIDs);
						}
						else
							state.setContextParameter(paramName, param.getValue());
						
						//Check if we have a boolean value
						if(paramName.equalsIgnoreCase("binary")) {
							if(!param.getValue().equalsIgnoreCase("true") && !param.getValue().equalsIgnoreCase("false"))
								throw new FormatterInitializationException("ERROR --> Init (Optional) Parameter '"+ paramName + "' for Formatter " + getType() +" can only take 'true' or 'false' as value.\n");
						}
					} 
				}

				//check missing optional parameters
				for (String s : optionalParameters) {
					errorMessage+="ERROR --> Init (Optional) Parameter '"+ s + "' is needed for Formatter " + getType() +" but has not been specified.\n";
				}
				if (!errorMessage.isEmpty()) {
					System.out.println(errorMessage);
					throw new FormatterInitializationException(errorMessage);
				}
				
				//Since we' ve collected the optional parameters, the next step is to compare the "tileIDs" and the "allTileIDs" lists
				//All the tileID names that belong to the tileIDs list, must also belong to the allTileIDs list
				if(tileIDs.size() > allTileIDs.size()) {
					throw new FormatterInitializationException("ERROR --> The 'tileIDs' parameter's values must be of smaller or equal cardinallity compared to the 'allTileIDs' parameter's values!\n");
				}
				
				String tileErrors = "";
				for(int i=0 ; i<tileIDs.size() ; i++){
					
					if(!allTileIDs.contains(tileIDs.get(i))){
						tileErrors+="ERROR --> Area tile with ID '"+ tileIDs.get(i) + "' is not a member of the tile IDs that the 'allTileIDs' parameter defines.\n";
					}
				}
				if (!tileErrors.isEmpty()) {
					throw new FormatterInitializationException(tileErrors);
				}
				
				//Since all the optional parameters have been provided, we have to check the value of the "binary" parameter
				//If this value is true then we have to collect the suboptional parameter
				if(Boolean.parseBoolean(state.getContextParameter("binary").toString())) {
					
					//We have to define a cutoff threshold, hence the threshold parameter must be loaded
					for (InputParameter param : paramValues) {
						String paramName=param.getName();
						if (subOptionalParameters.remove(paramName)) {
							state.setContextParameter(paramName, param.getValue());
							
							//Check if the cutoff value belong in the interval [0,1]
							if(paramName.equalsIgnoreCase("threshold")) {
								try {
									if(Double.parseDouble(param.getValue()) < 0.0 || Double.parseDouble(param.getValue()) > 1.0) {
										throw new FormatterInitializationException("The value for 'threshold' parameter must be a probability that belongs to the range [0,1].\n");
									}
								}
								catch (NumberFormatException e){
									throw new FormatterInitializationException("A non valid number(" + param.getValue() + ") was specified as '"+paramName+"'.\n");
								}
							}
						} 
					}

					//check missing optional parameters
					for (String s : subOptionalParameters) {
						errorMessage+="ERROR --> Init (Sub-Optional) Parameter '"+ s + "' is needed for Formatter " + getType() +" but has not been specified.\n";
					}
					if (!errorMessage.isEmpty()) {
						System.out.println(errorMessage);
						throw new FormatterInitializationException(errorMessage);
					}
				}
			}
			
			state.setContextParameter("feedCounter", new Integer(0));
		}
	}
	
	private String createXML (String feedID, ArrayList<String> tileIDs, ArrayList<String> allTileIDs, Double propValue) {
		
		String xmlDoc = null;
		String dateFormatTime = "yyyy-MM-dd'T'HH:mm:ssZ";
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormatTime);

		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("FM_feed");
			doc.appendChild(rootElement);
			
			// feedID element
			Element fID = doc.createElement("feedID");
			rootElement.appendChild(fID);
	 
			// set attribute to feedID element
			Attr attr = doc.createAttribute("id");
			attr.setValue(feedID);
			fID.setAttributeNode(attr);
			
			// areaTiles element
			Element areaTiles = doc.createElement("areaTiles");
			fID.appendChild(areaTiles);
			
			Element areaTileID = null;
			Element fireProbability = null;
			Element value = null;
			Attr attr2 = null;
			
			if(allTileIDs != null) {//Propability Matrix
				for(int i=0 ; i<allTileIDs.size() ; i++) {
					// areaTileID element
					areaTileID = doc.createElement("areaTileID");
					areaTiles.appendChild(areaTileID);
					
					// set attribute to areaTileID element
					attr2 = doc.createAttribute("id");
					attr2.setValue(allTileIDs.get(i));
					areaTileID.setAttributeNode(attr2);
					
					// fireProbability element
					fireProbability = doc.createElement("fireProbability");
					areaTileID.appendChild(fireProbability);
					
					// value element
					value = doc.createElement("value");
					if(tileIDs.contains(allTileIDs.get(i)))
						value.appendChild(doc.createTextNode(propValue.toString()));
					else
						value.appendChild(doc.createTextNode("0.0"));
					fireProbability.appendChild(value);
				}
			}
			else {
				for(int i=0 ; i<tileIDs.size() ; i++) {
					// areaTileID element
					areaTileID = doc.createElement("areaTileID");
					areaTiles.appendChild(areaTileID);
					
					// set attribute to areaTileID element
					attr2 = doc.createAttribute("id");
					attr2.setValue(tileIDs.get(i));
					areaTileID.setAttributeNode(attr2);
					
					// fireProbability element
					fireProbability = doc.createElement("fireProbability");
					areaTileID.appendChild(fireProbability);
					
					// value element
					value = doc.createElement("value");
					value.appendChild(doc.createTextNode(propValue.toString()));
					fireProbability.appendChild(value);
				}
			}
			
			Date currentTime = new Date();
			String dateTime = sdf.format(currentTime);
			
			// timestamp element
			Element timestamp = doc.createElement("timestamp");
			timestamp.appendChild(doc.createTextNode(dateTime));
			rootElement.appendChild(timestamp);
			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			
			StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            DOMSource source = new DOMSource(doc);
            transformer.transform(source, result);
            //System.out.println(writer.toString());
            
            xmlDoc = writer.toString();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
		
		return xmlDoc;
	}
	
	@Override
	public String format(IContext state, IDataElement... data) throws FormatterException {
		
		String formattedMessage=null;
		String feedID = null;
		ArrayList<String> tileIDs = new ArrayList<String>();
		ArrayList<String> allTileIDs = new ArrayList<String>();
		Double propValue = 0.0;
		
		//Check the value that comes inside the formatter
		if(data != null) {
			if (data[0].getValue() instanceof Number) {
				
				if((Double)data[0].getValue() < 0.0 || (Double)data[0].getValue() > 1.0) {
					throw new FormatterException("The input value of the \""+this.getType()+"\" formatter must be a probability that belongs to the range [0,1].\n");
				}
				
				//Here the creation of the xml file will take place
				tileIDs = (ArrayList<String>)state.getContextParameter("tileIDs");
				feedID = state.getContextParameter("feedID") + "_" + Integer.parseInt(state.getContextParameter("feedCounter").toString());
				
				if(Boolean.parseBoolean(state.getContextParameter("propMatrix").toString())) {
					allTileIDs = (ArrayList<String>)state.getContextParameter("allTileIDs");
				
					if(Boolean.parseBoolean(state.getContextParameter("binary").toString())) {
						if((Double)data[0].getValue() < Double.parseDouble(state.getContextParameter("threshold").toString())) {
							propValue = 0.0;
						}
						else
							propValue = 1.0;
					}
					propValue = (Double)data[0].getValue();
				}
				else {
					allTileIDs = null;
					propValue = (Double)data[0].getValue();
				}
				
				formattedMessage = createXML (feedID, tileIDs, allTileIDs, propValue);
			} else {
				//System.out.println("Invalid data value detected("+data[0].getValue()+"). Value must be Double");
				//return null;
				throw new FormatterException("Invalid data value detected("+data[0].getValue()+"). Value must be Double");
			}
		}
		
		if(formattedMessage != null) //The xml document has been successfully built
			state.setContextParameter("feedCounter", Integer.parseInt(state.getContextParameter("feedCounter").toString()) + 1 );
			
		return formattedMessage;
	}
	
	@Override
	public String[] getRequiredParameters() {
		String[] params={"tileIDs","feedID","propMatrix"};
		return params;
	}
	
	//Only if "propMatrix = true"
	@Override
	public String[] getOptionalParameters() {
		String[] params={"allTileIDs","binary"};
		return params;
	}
	
	//Only if "binary = true"
	public String[] getSubOptionalParameters() {
		String[] params={"threshold"};
		return params;
	}
	
	@Override
	public String getType() {
		return "areaTilesSISO";
	}

	@Override
	public boolean allowsMultipleInputs() {
		return true;
	}
	
	//Test implemented formatter
    public static void main(String args[]) {
    	
		AreaTileIDsSISO areaTilesSISO = new AreaTileIDsSISO();

		InputParameter tileIDs = new InputParameter("tileIDs","areaTile_04,areaTile_03,areaTile_07,areaTile_08,areaTile_18");
		InputParameter feedID = new InputParameter("feedID","Fire@Athens");
		InputParameter propMatrix = new InputParameter("propMatrix","true");
		InputParameter allTileIDs = new InputParameter("allTileIDs","areaTile_04,areaTile_03,areaTile_07,areaTile_08,areaTile_20,areaTile_18");
		InputParameter binary = new InputParameter("binary","true");
		InputParameter threshold = new InputParameter("threshold","0.6");
		
		HashMap<String, InputParameter> iparams=new HashMap<String, InputParameter>();
		iparams.put(tileIDs.getName(), tileIDs);
		iparams.put(feedID.getName(), feedID);
		iparams.put(propMatrix.getName(), propMatrix);
		iparams.put(allTileIDs.getName(), allTileIDs);
		iparams.put(binary.getName(), binary);
		iparams.put(threshold.getName(), threshold);
		
		IAlgorithmContext context = new AlgorithmContext("1", "areaTilesSISO");
		
		try {
			areaTilesSISO.initialize(context, iparams);
		} catch (FormatterInitializationException e1) {
			e1.printStackTrace();
		}
		/*
		IDataElement data;
		
		double measurement = 0.53;
		System.out.println("Value to encapsulate -->" + measurement);
		data = new DataElement("test");
		data.setValue(measurement);	
		*/
		IDataElement[] data=new DataElement[2];
		
		data[0]=new DataElement("test1");
		data[0].setValue(new Double(0.7));	
		data[1]=new DataElement("test2");
		data[1].setValue(new Double(0.5));	
		
		try {
			System.out.println(areaTilesSISO.format(context, data));
			//System.out.println(areaTilesSISO.format(context, data));
		} catch (FormatterException e) {
			e.printStackTrace();
		}
	}
}
