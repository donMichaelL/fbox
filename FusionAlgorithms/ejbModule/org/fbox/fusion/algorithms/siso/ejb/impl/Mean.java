package org.fbox.fusion.algorithms.siso.ejb.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.ejb.LocalBean;
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
 * Session Bean implementation class MeanValue
 */
@Stateless (name="Mean")
@Remote( {IAlgorithm.class, IStructure.class })
@LocalBean
public class Mean extends AbstractSISOAlgorithm {

    /**
     * Default constructor. 
     */
    public Mean() {
    }

	@Override
	protected Comparable<?> _update(IAlgorithmContext state, IDataElement measurement) throws AlgorithmExecutionException { //operates on double measurements
		Double outValue;
		Comparable<?> mValue=measurement.getValue();
		if (mValue!=null && mValue instanceof Number) { //if a double value has come
	    	//get buffer from memory (state)
	    	Double[] buffer=(Double[])state.getContextParameter("buffer");
	    	Integer maxBufferSize=(Integer)state.getContextParameter("maxBufferSize");
	    	Integer currentBufferSize=(Integer)state.getContextParameter("currentBufferSize");
	    	    		
//	    	System.out.println("^^^^^^^^^^^^^^^^^^^^"+ maxBufferSize +", "+currentBufferSize);
	    	if (currentBufferSize<maxBufferSize) {
	    		state.setContextParameter("currentBufferSize",++currentBufferSize);    			
	    	}
		   		
	    	//shift elements in buffer
	    	if (currentBufferSize>0)
	    		System.arraycopy(buffer, 0, buffer, 1, currentBufferSize-1);
	    	
	    	//add the new Measurement at position 0
	    	buffer[0]=(Double)measurement.getValue();
	    	
	    	//re-insert buffer to memory
	    	//state.setContextParameter("buffer", buffer);
	    		//calculate new value and update metric data   		
    		outValue=sum(buffer,currentBufferSize)/currentBufferSize;    		
    	} else {
    		String errorMessage="Algorithm cannot operate on a non numeric or null value";
    		System.out.println(errorMessage);
    		throw new AlgorithmExecutionException(errorMessage);
    	}
		
		//System.out.println("[BufMean] OUTPUT: "+outValue);
		return outValue;
	}
	
	private Double sum(Double[] buffer, int currentBufferSize) {
		Double result=0.0;
		for (int i=0;i<currentBufferSize && i<buffer.length;i++) {
			result+=buffer[i];
		}
		return result;
	}
	
	@Override
	public void initialize(IAlgorithmContext state, HashMap<String,InputParameter> iparams) throws AlgorithmInitializationException {
		
		//initialize execution params
		Set<String> requiredParameters=new HashSet<String>(Arrays.asList(getRequiredParameters()));		
		Collection<InputParameter> paramValues=iparams.values();
		for (InputParameter param : paramValues) {
			switch (param.getName()) { 
			case "buffer":				
				state.setContextParameter("buffer", new Double[Integer.parseInt((String)param.getValue())]);  //create a buffer with the specified size
				state.setContextParameter("maxBufferSize", Integer.parseInt((String)param.getValue())); //keep the max size of the buffer
				state.setContextParameter("currentBufferSize", new Integer(0)); //the current bufferSize 
				requiredParameters.remove(param.getName());
				break;
			default:
				System.out.println("WARNING --> Input Parameter '"+ param.getName() + "' not applicable for Algorithm " + getType() +". Will be Ignored.");
			}
		}
		
		String errorMessage="";
		for (String s : requiredParameters) {
			errorMessage+="ERROR --> Input Parameter '"+ s + "' is needed but has not been specified.\n";
		}
		if (!errorMessage.isEmpty()) {
			System.out.println(errorMessage);
			throw new AlgorithmInitializationException(errorMessage);
		}
	}

	@Override
	public String getType() {
		return  "Mean";
	}

	public String[] getRequiredParameters() {
		String[] requiredParams={"buffer"}; //a Single initialization parameter("buffer") is needed
		return requiredParams;
	}
}
