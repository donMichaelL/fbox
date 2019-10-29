package org.fbox.fusion.output.formatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.fbox.common.IStructure;
import org.fbox.common.data.IContext;
import org.fbox.common.data.InputParameter;
import org.fbox.common.exception.FormatterInitializationException;
import org.fbox.common.output.IFormatter;

public abstract class AbstractFormatter<T extends Object> implements IFormatter<T>{

	@Override
	public void initialize(IContext state, HashMap<String, InputParameter> iparams) throws FormatterInitializationException {
	   	//initialize params
		String[] params=getRequiredParameters();
		if (params!=null) { //parse required params
			
	    	Set<String> requiredParameters=new HashSet<String>(Arrays.asList(params));
			Set<String> optionalParameters=new HashSet<String>(Arrays.asList(getOptionalParameters()));
			Collection<InputParameter> paramValues=iparams.values();
			for (InputParameter param : paramValues) {
				String paramName=param.getName();
				if (requiredParameters.remove(paramName)) {
					state.setContextParameter(paramName, param.getValue());
				} else if (optionalParameters.remove(paramName)) {
					state.setContextParameter(paramName, param.getValue());
				} else
					System.out.println("WARNING --> Init Parameter '"+ paramName + "' not applicable for Formatter " + getType() +". Will be Ignored.");
			}
			
			//check missing parameters
			String errorMessage="";
			for (String s : requiredParameters) {
				errorMessage+="ERROR --> Init Parameter '"+ s + "' is needed for Formatter " + getType() +" but has not been specified.\n";
			}
			if (!errorMessage.isEmpty()) {
				System.out.println(errorMessage);
				throw new FormatterInitializationException(errorMessage);
			}				
		}
	}
	
	
	@Override	
	public String[] getOptionalParameters() {
		String[] params={};
		return params;
	}

}
