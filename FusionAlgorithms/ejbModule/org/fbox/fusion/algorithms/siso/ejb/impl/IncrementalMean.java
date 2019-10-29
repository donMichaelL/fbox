package org.fbox.fusion.algorithms.siso.ejb.impl;


import java.util.HashMap;

import javax.ejb.Lock;
import javax.ejb.LockType;
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
 * Session Bean implementation class IncrementalMeanValue
 */
@Stateless (name="IncrementalMean")
@Remote( {IAlgorithm.class, IStructure.class })
public class IncrementalMean extends AbstractSISOAlgorithm {  //operates only on double measurements

    public IncrementalMean() {
    }

    @Override
    @Lock(LockType.WRITE)
    //@AccessTimeout(value=10000, unit=TimeUnit.MILLISECONDS)
	protected Comparable<?> _update(IAlgorithmContext state, IDataElement measurement) throws AlgorithmExecutionException {
    	
    	Double outValue;
    	
    	Comparable<?> tmpValue=state.getData().getValue();
    		
    	if (measurement.getValue()!=null | tmpValue!=null | !(tmpValue instanceof Number)) {
    		
    		//get execution parameters from memory (state)
	    	Integer numberOfMeasurements=(Integer)state.getContextParameter("numberOfMeasurements");
	    		
	    	//re-insert to memory for future use
    		state.setContextParameter("numberOfMeasurements",++numberOfMeasurements);  //increment number of Measurements counter
	    		
    		//calculate new value and update metric  ****MUST CHECK whether  a statefull approach would be better
    		
    		outValue=(Double)tmpValue; //retrieve latest value of metric
    		outValue+= (((Double)measurement.getValue() - (Double)outValue)/numberOfMeasurements); //calculate incremental mean and set it as the new value of the metric

    		//outValue.setLocation(measurement.getLocation());
    		  	
    	} else {
    		String errorMessage="Algorithm cannot operate on a non numeric or null value";
    		System.out.println(errorMessage);
    		throw new AlgorithmExecutionException(errorMessage);
    	}
    	
    	return outValue;
		
	}

    @Override
	public void initialize(IAlgorithmContext state, HashMap<String,InputParameter> iparams) throws AlgorithmInitializationException {
    	//super.initialize(state, iparams);
		state.setContextParameter("numberOfMeasurements", new Integer(0));
	}

	@Override
	public String getType() {
		return "IncrementalMean";
	}

	@Override
	public String[] getRequiredParameters() {
		return null;
	}
    
    

}
