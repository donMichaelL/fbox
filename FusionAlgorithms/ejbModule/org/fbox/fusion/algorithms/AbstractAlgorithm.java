package org.fbox.fusion.algorithms;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.fbox.common.IStructure;
import org.fbox.common.algorithms.IAlgorithm;
import org.fbox.common.data.IAlgorithmContext;
import org.fbox.common.data.InputParameter;
import org.fbox.common.exception.AlgorithmInitializationException;


public abstract class AbstractAlgorithm implements IAlgorithm, IStructure {

	@Override
	public String[] getOptionalParameters() {
		String[] params={};
		return params;
	}
	
	
	@Override
	public void initialize(IAlgorithmContext state, HashMap<String, InputParameter> iparams) throws AlgorithmInitializationException {
    	//initialize execution params
		String[] params=getRequiredParameters();
		Set<String> optionalParameters=new HashSet<String>(Arrays.asList(getOptionalParameters()));		
		if (params!=null) { //parse required params
	    	Set<String> requiredParameters=new HashSet<String>(Arrays.asList(params));
	    	Collection<InputParameter> paramValues=iparams.values();
			for (InputParameter param : paramValues) {
				String paramName=param.getName();
				if (requiredParameters.remove(paramName)) {
					state.setContextParameter(paramName, param.getValue());
				} else if (optionalParameters.remove(paramName)) {
					state.setContextParameter(paramName, param.getValue());
				} else
					System.out.println("WARNING --> Init Parameter '"+ paramName + "' not applicable for Output Adapter " + getType() +". Will be Ignored.");
			}
			
			//check missing parameters
			String errorMessage="";
			for (String s : requiredParameters) {
				errorMessage+="ERROR --> Init Parameter '"+ s + "' is needed for Output Adapter " + getType() +" but has not been specified.\n";
			}
			if (!errorMessage.isEmpty()) {
				System.out.println(errorMessage);
				throw new AlgorithmInitializationException(errorMessage);
			}				
		}
	}

}
