package org.fbox.fusion.output.formatter.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.fbox.common.data.IContext;
import org.fbox.common.data.IDataElement;
import org.fbox.common.data.InputParameter;
import org.fbox.common.exception.FormatterException;
import org.fbox.common.exception.FormatterInitializationException;
import org.fbox.common.output.IFormatter;
import org.fbox.fusion.output.formatter.AbstractFormatter;

@Stateless (name="rdfForm")
@Remote ({IFormatter.class})
public class RDFFormatter extends AbstractFormatter<String> {
	
	public static final String POLYGON_LOCATION = "location";
	public static final String APPLICATION_ID = "application-id";
	
	private static String rdf_prefix = "PREFIX swefs: <http://swefs.di.uoa.gr/ontology#>\n" +
									   "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
									   "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
									   "PREFIX strdf: <http://strdf.di.uoa.gr/ontology#>\n\n";
	
	@Override
	public String format(IContext state, IDataElement... data) throws FormatterException {
		
		String formattedMessage=null;
		
		//Check the value that comes inside the formatter
		if(data != null) {

			if (data[0].getValue() instanceof Number) {
				
				Boolean firstTime=(Boolean)state.getContextParameter("firstTime");
				
				if(firstTime) {
					formattedMessage = rdf_prefix +  registerRDFstring(state);   //Registration
					state.setContextParameter("firstTime", false);
					
					formattedMessage = formattedMessage + "\n\n;\n\n" + insertRDFstring(state, data[0]);//First message insertion
				}
				else {
					try{
						formattedMessage = rdf_prefix + insertRDFstring(state, data[0]);
					} catch (FormatterException e) {
						System.out.println("WARNING---->RDF FORMATTER: "+e.getMessage());
						return null;
					}
				}
			} else
				throw new FormatterException("Invalid data value detected("+data[0].getValue()+"). Value must be Double");
		}
		
		return formattedMessage;
	}
	
	public String registerRDFstring(IContext state) {

		String rdfString;
		String appl_id = (String)state.getContextParameter(APPLICATION_ID); //Get value from xml
		String location = (String)state.getContextParameter(POLYGON_LOCATION);
		String dateFormat = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

		Date currentTime = new Date();
		String timestamp = sdf.format(currentTime);
		String rdf_id = "swefs:SFE" + appl_id + "_" + timestamp + " rdf:type swefs:SensorFusionEngine .\n";
		String rdf_geom = "swefs:SFE" + appl_id + "_" + timestamp + " swefs:hasGeometry \"POLYGON((" + location + "))\"^^strdf:WKT .\n";

		rdfString = "INSERT DATA {\n" + rdf_id + rdf_geom + "}";
		
		//Insert the application ID in state memory
		state.setContextParameter("applID", "SFE" + appl_id + "_" + timestamp);

		return rdfString;
    }
	
	public String insertRDFstring(IContext state, IDataElement data) throws FormatterException {
        
		String rdfString;
		String appl_id = (String)state.getContextParameter("applID"); //Get value from registry
		String dateFormat = "yyyy-MM-dd'T'HH:mm:ssZ";
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		int counter = Integer.parseInt((String)state.getContextParameter("measNo"));

		if(data.getValue()!=null) {
			
			String rdf_meas = "swefs:" + appl_id + " swefs:measured swefs:meas" + Integer.toString(counter) + " .\n";
			String rdf_fireProb = "swefs:meas" + Integer.toString(counter) + " swefs:fireProbability \"" + data.getValue() + "\"^^xsd:double .\n";  
			
			Date currentTime = new Date();
			String timestamp = sdf.format(currentTime);
			String firstPart = timestamp.substring(0, (timestamp.length()-1) - 1);
			String secondPart = timestamp.substring((timestamp.length()-1) - 1, timestamp.length());
			timestamp = firstPart + ":" + secondPart;
			String rdf_time = "swefs:meas" + Integer.toString(counter) + " swefs:time \"" + timestamp + "\"^^xsd:dateTime .\n";
			
			rdfString = "INSERT DATA {\n" + rdf_meas + rdf_fireProb + rdf_time + "}";
			
			counter++;
			state.setContextParameter("measNo", Integer.toString(counter));
	        
	        return rdfString;
		}
		else
			throw new FormatterException("No need to format RDF triple");
    }
	
	@Override
	public void initialize(IContext state, HashMap<String, InputParameter> iparams) throws FormatterInitializationException {
		super.initialize(state, iparams);
		state.setContextParameter("firstTime", true);
		state.setContextParameter("measNo", "0");
		state.setContextParameter("applID", null);
	}
	
	@Override
	public String[] getRequiredParameters() {
		String[] params={POLYGON_LOCATION, APPLICATION_ID};
		return params;
	}
	
	@Override
	public String getType() {
		return "rdfForm";
	}

	@Override
	public boolean allowsMultipleInputs() {
		return false;
	}
}
