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

/**
 * Session Bean implementation class Cumsum
 * 
 * In this implementation of CUMSUM the approach for output is as follows:
 * 
 * - Two Side Detection (detection-type: both)
 *   1.	   Output = 0  --> No detection
 *   2.    Output = 1  --> Detection that we are above the "ha" threshold
 *   3.    Output = -1 --> Detection that we are below the "hb" threshold
 *   
 * - One Side Detection - UP (detection-type: above)
 * 	 1.	   Output = 0  --> No detection   
 * 	 2.    Output = 1  --> Detection that we are above the "ha" threshold
 * 
 * - One Side Detection - DOWN (detection-type: below)
 * 	 1.	   Output = 0  --> No detection   
 * 	 2.    Output = 1  --> Detection that we are below the "hb" threshold
 */
@Stateless (name="CUMSUM")
@Remote( {IAlgorithm.class, IStructure.class })
public class Cumsum  extends AbstractSISOAlgorithm {

    /**
     * Default constructor. 
     */
    public Cumsum() {
    }

    @Override
    public String[] getRequiredParameters() {
		String[] requiredParams={"target-value", "above-tolerance", "below-tolerance", "above-threshold", "below-threshold", "detection-type"}; 
		return requiredParams;
    }

    @Override
    public void initialize(IAlgorithmContext state, HashMap<String,InputParameter> iparams) throws AlgorithmInitializationException {
    		
    	//initialize execution params
    	Set<String> requiredParameters=new HashSet<String>(Arrays.asList(getRequiredParameters()));
    	Collection<InputParameter> paramValues=iparams.values();
		for (InputParameter param : paramValues) {
			if (requiredParameters.remove(param.getName())) {
				if(param.getName().equalsIgnoreCase("detection-type")) { //The only "string" parameter
					state.setContextParameter(param.getName(), (String)param.getValue());
				} else
					state.setContextParameter(param.getName(), Double.parseDouble((String)param.getValue()));
			} else
				System.out.println("[CUMSUM] WARNING: Input Parameter '"+ param.getName() + "' not applicable for Algorithm " + getType() +". Will be Ignored.");
		}
		
		//check missing parameters
		String errorMessage="";
		for (String s : requiredParameters) {
			errorMessage+="[CUMSUM] ERROR: Input Parameter '"+ s + "' is needed but has not been specified.\n";
		}
		if (!errorMessage.isEmpty()) {
			System.out.println(errorMessage);
			throw new AlgorithmInitializationException(errorMessage);
		}
    	
		//initialize internal variables
		state.setContextParameter("R", new Double(0.0));
		state.setContextParameter("Q", new Double(0.0));
		state.setContextParameter("sAbove", new Integer(0));
		state.setContextParameter("sBelow", new Integer(0));
    }

    @Override
    protected Comparable<?> _update(IAlgorithmContext state, IDataElement measurement) throws AlgorithmExecutionException {
    	
    	Comparable<?> outValue = null;
    	Comparable<?> mValue = measurement.getValue();
    	
    	if(measurement != null) {
	    	if (mValue!=null && mValue instanceof Number) {
	    		
	    		Double m = (Double)state.getContextParameter("target-value");
		    	Double ka = (Double)state.getContextParameter("above-tolerance");   	
		    	Double kb = (Double)state.getContextParameter("below-tolerance");
		    	Double ha = (Double)state.getContextParameter("above-threshold");   	
		    	Double hb = (Double)state.getContextParameter("below-threshold");  
		    	String type = (String)state.getContextParameter("detection-type"); //Values: "above","below","both"
		    			
		    	Double r=(Double)state.getContextParameter("R");
		    	Double q=(Double)state.getContextParameter("Q");
		    	Integer sa=(Integer)state.getContextParameter("sAbove");
		    	Integer sb=(Integer)state.getContextParameter("sBelow");
		    	
		    	//If the last detected value has been an "outlier"
		    	if(!type.equalsIgnoreCase("below") && (sa == 1)) { 
		    		sa = 0;
		    		r = 0.0;
		    	}
		    	
		    	//If the last detected value has been an "outlier"
		    	if(!type.equalsIgnoreCase("above") && (sb == 1)) { 
		    		sb = 0;
		    		q = 0.0;
		    	}
		    		
		    	if(!type.equalsIgnoreCase("below")) //We do not have to detect change in this direction
		    		r = Math.max(0, (Double)mValue - (m + ka) + r);
		    	
		    	if(!type.equalsIgnoreCase("above")) //We do not have to detect change in this direction
		    		q = Math.min(0, (Double)mValue - (m - kb) + q);
		    		
		    	if (!type.equalsIgnoreCase("below") && (r>ha)) {
		    		sa = 1;
		    		outValue = new Double(sa);  
		    	}
	
		    	if (!type.equalsIgnoreCase("above") && (q<-hb)) {
		    		sb = 1;
		    		
		    		if(type.equalsIgnoreCase("both"))
		    			outValue = new Double(-sb);  //We must have value equal to -1
		    		else //type: below
		    			outValue = new Double(sb); 
		    	}
		    	
		    	if(outValue == null) //No detection
		    		outValue = new Double(0.0);
		    	
		    	//Store the new values of the "important" variables of CUSUM algorithm
		    	state.setContextParameter("R", new Double(r));
		    	state.setContextParameter("Q", new Double(q));
		    	state.setContextParameter("sAbove", new Integer(sa));
		    	state.setContextParameter("sBelow", new Integer(sb));
	    	} else {
	    		//throw new AlgorithmExecutionException("[CUMSUM] ERROR: Algorithm cannot operate on a non numeric or null value");
	        	System.out.println("[CUMSUM] ERROR: Algorithm cannot operate on a non numeric or null value");
	        	return null;
	        }
    	}
    	//System.out.println("[CUMSUM] OUTPUT: "+outValue.toString());
    	return outValue;
    }

    @Override
    public String getType() {
		return "CUMSUM";
    }

}
