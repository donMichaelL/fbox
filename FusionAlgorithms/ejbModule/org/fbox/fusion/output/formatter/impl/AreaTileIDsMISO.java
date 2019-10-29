package org.fbox.fusion.output.formatter.impl;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.NoSuchElementException;
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

@Stateless (name="areaTilesMISO")
@Remote ({IFormatter.class})
public class AreaTileIDsMISO extends AbstractFormatter<String> {

	@Override
	public void initialize(IContext state, HashMap<String, InputParameter> iparams) throws FormatterInitializationException {
		
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
							
							StringTokenizer st = new StringTokenizer(param.getValue(), ",");
							
							while(st.hasMoreTokens()) {
								allTileIDs.add(st.nextToken());
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
								if( (Double.parseDouble(param.getValue()) < 0.0) || (Double.parseDouble(param.getValue()) > 1.0) ) {
									throw new FormatterInitializationException("The value for 'threshold' parameter must be a probability that belongs to the range [0,1].\n");
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
			
			//state.setContextParameter("feedCounter", new Integer(0));
		}
	}
	
	private String createXML (String feedID, ArrayList<String> tileIDs, ArrayList<String> allTileIDs, ArrayList<Double> propValues) {
		
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
			int pos = -1;
			
			if(allTileIDs != null) {//Propability Matrix
				for(int i=0 ; i<allTileIDs.size() ; i++) {
					pos = -1;
					
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
					
					for(int j=0 ; j<tileIDs.size() ; j++) {
						if(tileIDs.get(j).equalsIgnoreCase(allTileIDs.get(i))) {
							pos = j;
							break;
						}
					}
					
					if(pos != -1)
						value.appendChild(doc.createTextNode(propValues.get(pos).toString()));
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
					value.appendChild(doc.createTextNode(propValues.get(i).toString()));
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
	public String format(IContext state, IDataElement... dataArray) throws FormatterException {
		
		String formattedMessage=null;
		
		ArrayList<String> tileIDs = new ArrayList<String>();
		ArrayList<String> allTileIDs = new ArrayList<String>();
		ArrayList<Double> propValues = new ArrayList<Double>();
		
		Boolean propMatrix = Boolean.parseBoolean(state.getContextParameter("propMatrix").toString());
		Boolean binary = false;
		Double threshold = 0.0;
		
		if(propMatrix)
			binary = Boolean.parseBoolean(state.getContextParameter("binary").toString());
		
		if(binary)
			threshold = Double.parseDouble(state.getContextParameter("threshold").toString());
		
		//Integer feedCounter = Integer.parseInt(state.getContextParameter("feedCounter").toString());
		String feedID = (String)state.getContextParameter("feedID");// + "_" + feedCounter;
		
		//Check values that come inside the formatter
		for(IDataElement argData : dataArray) {
			if(argData != null && argData.getValue() != null) {
				
				String tileID = null, number = null;
				Double propValue = null;
				
				try {
					StringTokenizer st = new StringTokenizer(argData.getValue().toString(),",");
					tileID = st.nextToken();
					number = st.nextToken();
					
					propValue = Double.parseDouble(number);
					
					if(propValue < 0.0 || propValue > 1.0) {
						throw new FormatterException("The value for 'propValue' field in the input stream must be a probability that belongs to the range [0,1].\n");
					}

					tileIDs.add(tileID);

					if(propMatrix) {
						if(binary) {
							if(propValue < threshold) {
								propValues.add(new Double(0.0));
							}
							else
								propValues.add(new Double(1.0));
						}
						else
							propValues.add(propValue);
					}
					else
						propValues.add(propValue);
				}
				catch (NoSuchElementException e1) {
					throw new FormatterException("The input of the \"" + this.getType() + "\" formatter must be of \"area_tile_x_y,propValue\" type.\n");
				}
				catch (NumberFormatException e3){
					throw new FormatterException("A non valid number(" + number + ") was specified as 'propValue' field in the input stream.\n");
				}
			}
		}
		
		//Since we' ve collected all the input data, we are going to compare the tile IDs if this is necessary
		if(propMatrix) {
			allTileIDs = (ArrayList<String>)state.getContextParameter("allTileIDs");
			
			//Comparison
			if(tileIDs.size() > allTileIDs.size()) {
				throw new FormatterException("ERROR --> The input's cardinallity must be smaller or equal compared to the 'allTileIDs' parameter's values!\n");
			}
			
			String tileErrors = "";
			for(int i=0 ; i<tileIDs.size() ; i++){
				
				if(!allTileIDs.contains(tileIDs.get(i))){
					tileErrors+="ERROR --> Area tile with ID '"+ tileIDs.get(i) + "' is not a member of the tile IDs that the 'allTileIDs' parameter defines.\n";
				}
			}
			if (!tileErrors.isEmpty()) {
				throw new FormatterException(tileErrors);
			}
		}
		else
			allTileIDs = null;
		
		formattedMessage = createXML (feedID, tileIDs, allTileIDs, propValues);
		
		//if(formattedMessage != null) //The xml document has been successfully built
		//	state.setContextParameter("feedCounter", feedCounter++);
		
		return formattedMessage;
	}
	
	@Override
	public String[] getRequiredParameters() {
		String[] params={"feedID","propMatrix"};
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
		return "areaTilesMISO";
	}

	@Override
	public boolean allowsMultipleInputs() {
		return true;
	}
	
	//Test implemented formatter
    public static void main(String args[]) {
    	
		AreaTileIDsMISO areaTilesMISO = new AreaTileIDsMISO();

		InputParameter feedID = new InputParameter("feedID","Fire@Athens");
		InputParameter propMatrix = new InputParameter("propMatrix","false");
		InputParameter allTileIDs = new InputParameter("allTileIDs","areaTile_04,areaTile_03,areaTile_07,areaTile_08,areaTile_20,areaTile_18");
		InputParameter binary = new InputParameter("binary","false");
		InputParameter threshold = new InputParameter("threshold","0.6");
		
		HashMap<String, InputParameter> iparams=new HashMap<String, InputParameter>();
		iparams.put(feedID.getName(), feedID);
		iparams.put(propMatrix.getName(), propMatrix);
		iparams.put(allTileIDs.getName(), allTileIDs);
		iparams.put(binary.getName(), binary);
		iparams.put(threshold.getName(), threshold);
		
		IAlgorithmContext context = new AlgorithmContext("1", "areaTilesMISO");
		
		try {
			areaTilesMISO.initialize(context, iparams);
		} catch (FormatterInitializationException e1) {
			e1.printStackTrace();
		}
		
		IDataElement[] data=new DataElement[3];
		
		data[0]=new DataElement("test1");
		data[0].setValue("areaTile_04,0.7");	
		data[1]=new DataElement("test2");
		data[1].setValue("areaTile_07,0.5");	
		data[2]=new DataElement("test3");
		data[2].setValue("areaTile_08,0.68");	
		
		try {
			System.out.println(areaTilesMISO.format(context, data));
		} catch (FormatterException e) {
			e.printStackTrace();
		}
	}
}
