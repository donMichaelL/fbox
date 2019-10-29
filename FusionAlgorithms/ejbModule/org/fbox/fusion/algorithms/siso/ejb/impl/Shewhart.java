package org.fbox.fusion.algorithms.siso.ejb.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.fbox.common.IStructure;
import org.fbox.common.algorithms.IAlgorithm;
import org.fbox.common.data.IAlgorithmContext;
import org.fbox.common.data.IDataElement;
import org.fbox.common.data.InputParameter;
import org.fbox.common.exception.AlgorithmExecutionException;
import org.fbox.common.exception.AlgorithmInitializationException;
import org.fbox.fusion.algorithms.siso.AbstractSISOAlgorithm;

@Stateless (name="SHEWHART")
@Remote( {IAlgorithm.class, IStructure.class })
public class Shewhart extends AbstractSISOAlgorithm  {
	
	/**
     * Default constructor. 
     */
    public Shewhart() {
    }
    
    @Override
    public String[] getRequiredParameters() {
		String[] requiredParams={"alpha-factor"}; 
		return requiredParams;
    }
    
    @Override
	public void initialize(IAlgorithmContext state, HashMap<String, InputParameter> iparams) throws AlgorithmInitializationException {
		
    	//initialize execution params
    	Set<String> requiredParameters=new HashSet<String>(Arrays.asList(getRequiredParameters()));
    	Collection<InputParameter> paramValues=iparams.values();
		for (InputParameter param : paramValues) {
			if (requiredParameters.remove(param.getName())) {
				state.setContextParameter(param.getName(), Double.parseDouble((String)param.getValue()));
			} else
				System.out.println("WARNING --> Input Parameter '"+ param.getName() + "' not applicable for Algorithm " + getType() +". Will be Ignored.");
		}

		//check missing parameters
		String errorMessage="";
		for (String s : requiredParameters) {
			errorMessage+="ERROR --> Input Parameter '"+ s + "' is needed but has not been specified.\n";
		}
		if (!errorMessage.isEmpty()) {
			System.out.println(errorMessage);
			throw new AlgorithmInitializationException(errorMessage);
		}
		
		//initialize internal variables
		state.setContextParameter("k", new Double(1.0));
		state.setContextParameter("old_mean", new Double(0.0));
		state.setContextParameter("old_variance", new Double(0.0));
	}
    
    @Override
    protected Comparable<?> _update(IAlgorithmContext state, IDataElement measurement) throws AlgorithmExecutionException {
    	
    	Comparable<?> outValue = null;
    	Comparable<?> mValue = measurement.getValue();
    	
    	if(measurement != null) {
	    	if (mValue!=null && mValue instanceof Number) {
	    		
	    		Double current_mean = new Double(0.0);
	    		Double squared_variance = new Double(0.0);
	    		Double UCL = new Double(0.0);
	    		Double LCL = new Double(0.0);
	    		
	    		Double alpha = (Double)state.getContextParameter("alpha-factor");
	    				//Double.parseDouble((String)state.getContextParameter("alpha-factor"));

		    	Double old_mean = (Double)state.getContextParameter("old_mean");
		    	Double old_variance = (Double)state.getContextParameter("old_variance");
		    	Double k = (Double)state.getContextParameter("k");
		    	
		    	//System.out.println("\n[Shewhart Controller]    --------->   A-factor: "+alpha);
	    		//System.out.println("[Shewhart Controller]    --------->   K: "+k);
	    		//System.out.println("[Shewhart Controller]    --------->   Old-Mean: "+old_mean);
		    	//System.out.println("[Shewhart Controller]    --------->   Old-Variance: "+old_variance);
		    	
		    	if(k == 1) { //We have the first value from the variable we want to monitor
		    		current_mean = (Double)mValue;
		    		squared_variance = new Double(0.0);
		    	}
		    	else {
		    		current_mean = old_mean + (((Double)mValue - old_mean)/k);
		    		squared_variance = (1.0/k)*( ((k-1.0)*old_variance) + (((Double)mValue - current_mean)*((Double)mValue - old_mean)) );
		    	}
		    	
		    	UCL = current_mean + (alpha*Math.sqrt(squared_variance));
		    	LCL = current_mean - (alpha*Math.sqrt(squared_variance));
		    	
		    	//System.out.println("[Shewhart Controller]    --------->   UCL: "+UCL);
		    	//System.out.println("[Shewhart Controller]    --------->   LCL: "+LCL);
		    	//System.out.println("[Shewhart Controller]    --------->   currentValue: "+(Double)mValue);
		    	
		    	if((Double)mValue > UCL) 
		    		outValue = new Double(1.0);
		    	else
		    		if((Double)mValue < LCL)
		    			outValue = new Double(-1.0);
		    		else
		    			outValue = new Double(0.0); //Else algorithm's output remains 0
		   
		    	//System.out.println("[Shewhart Controller]    --------->   DECISION: "+outValue +"\n");
		    	
		    	k = k + 1.0; //Raise value counter by one for the next iteration
		    	
		    	//Store the new values of the "important" variables of CUSUM algorithm
		    	state.setContextParameter("old_mean", new Double(current_mean));
		    	state.setContextParameter("old_variance", new Double(squared_variance));
		    	state.setContextParameter("k", new Double(k));
		    	
	    	} else {
	        	String errorMessage="Algorithm cannot operate on a non numeric or null value";
	        	System.out.println(errorMessage);
	        	throw new AlgorithmExecutionException(errorMessage);
	        }
    	}
    	
    	return outValue;
    }
    
    @Override
    public String getType() {
		return "SHEWHART";
    }

}
