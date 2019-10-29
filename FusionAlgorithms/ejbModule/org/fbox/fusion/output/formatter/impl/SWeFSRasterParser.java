package org.fbox.fusion.output.formatter.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import javax.ejb.Remote;
import javax.ejb.Stateless;

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

@Stateless (name="swefsRasterParser")
@Remote ({IFormatter.class})
public class SWeFSRasterParser extends AbstractFormatter<String>{

	@Override
	public void initialize(IContext state, HashMap<String, InputParameter> iparams) throws FormatterInitializationException {
		
		// Initialize params
		String[] params=getRequiredParameters();
		if (params!=null) { //parse required params
			Set<String> requiredParameters=new HashSet<String>(Arrays.asList(params));
			
			Collection<InputParameter> paramValues=iparams.values();
			
			// Check for parameters that do not belong to the mandatory list
			for (InputParameter param : paramValues) {
				String paramName=param.getName();
				if (!requiredParameters.contains(paramName)) 
					System.out.println("WARNING --> Init Parameter '"+ paramName + "' not applicable for Formatter " + getType() +". Will be Ignored.");
			}
			
			// Check for the existence of the required params
			for (InputParameter param : paramValues) {
				String paramName=param.getName();
				if (requiredParameters.remove(paramName)) {
					if(paramName.equalsIgnoreCase("type")) {
						
						if( !param.getValue().equalsIgnoreCase("binary") && !param.getValue().equalsIgnoreCase("probabilistic") && !param.getValue().equalsIgnoreCase("both") )
							throw new FormatterInitializationException("ERROR --> Init (Required) Parameter '"+ paramName + "' for contextor '"+getType()+"' can only get the following values: {binary, probabilistic, both}\n");
						
					}
					
					state.setContextParameter(paramName, param.getValue());
				}
			}
			
			// Check missing required parameters
			String errorMessage="";
			for (String s : requiredParameters) {
				errorMessage+="ERROR --> Init (Required) Parameter '"+ s + "' is needed for Formatter " + getType() +" but has not been specified.\n";
			}
			if (!errorMessage.isEmpty()) {
				System.out.println(errorMessage);
				throw new FormatterInitializationException(errorMessage);
			}
		}
		
	}
	
	@Override
	public String format(IContext state, IDataElement... data) throws FormatterException {
		
		String returnValue = null;
		String type = (String)state.getContextParameter("type");
		String id = (String)state.getContextParameter("id");
		
		System.out.println("["+getType()+"-"+id+"] PARAMS: "+type+" "+id);
		System.out.println("["+getType()+"-"+id+"] INPUT:\n"+(String)data[0].getValue());
		
		//Check the value that comes inside the formatter
		if((data != null) && (data[0].getValue()!=null)) {
			
			if(type.equalsIgnoreCase("both")) {
				returnValue = (String)data[0].getValue();
			} 
			else {
				StringTokenizer str = new StringTokenizer((String)data[0].getValue(), "|");
				
				String binary = str.nextToken();
				String probabilistic = str.nextToken();
				
				if(type.equalsIgnoreCase("binary")) {
					returnValue = binary;
				}
				else {
					returnValue = probabilistic;
				}
			}
		}
		
		if(returnValue == null)
			System.out.println("["+getType()+"-"+id+"] OUTPUT: null");
		else
			System.out.println("["+getType()+"-"+id+"] OUTPUT: PROPABILITY_MAP FORMED!!!");
		
		return returnValue;
	}
	
	@Override
	public String[] getRequiredParameters() {
		String[] params={"type","id"};
		return params;
	}
	
	@Override
	public String getType() {
		return "swefsRasterParser";
	}

	@Override
	public boolean allowsMultipleInputs() {
		return true;
	}
	
	//Test implemented formatter
    public static void main(String args[]) {
    	
		SWeFSRasterParser parser = new SWeFSRasterParser();

		InputParameter type = new InputParameter("type","binary");
		InputParameter id = new InputParameter("id","SIMULATOR");
		
		HashMap<String, InputParameter> iparams=new HashMap<String, InputParameter>();
		iparams.put(type.getName(), type);
		iparams.put(id.getName(), id);
		
		IAlgorithmContext context = new AlgorithmContext("1", "swefsRasterParser");
		
		try {
			parser.initialize(context, iparams);
		} catch (FormatterInitializationException e1) {
			e1.printStackTrace();
		}
		
		IDataElement data;
		data = new DataElement("test");
		data.setValue("binary|probabilistic");
		
		try {
			Date start = new Date();
			System.out.println(parser.format(context, data));
			Date end = new Date();
			System.out.println("Formatter time: "+(end.getTime() - start.getTime()));

		} catch (FormatterException e) {
			e.printStackTrace();
		} 
	}
}
