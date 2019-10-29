package org.fbox.fusion.algorithms.miso.ejb.impl;


import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.fbox.common.IStructure;
import org.fbox.common.algorithms.IAlgorithm;
import org.fbox.common.data.IAlgorithmContext;
import org.fbox.common.data.IDataElement;
import org.fbox.fusion.algorithms.miso.AbstractMISOAlgorithm;

/**
 * Session Bean implementation class SimpleVoting
 */
@Stateless(name="Min")
@Remote( { IAlgorithm.class, IStructure.class })
public class Min extends AbstractMISOAlgorithm {

    /**
     * Default constructor. 
     */
    public Min() {
    }

	/**
     * @see IDetect#getRequiredParameters()
     */
    public String[] getRequiredParameters() {
		String[] requiredParams={}; //a Single initialization parameter("z-threshold") is needed
		return requiredParams;
	}

    @Override
    protected Comparable<?> _update(IAlgorithmContext state, IDataElement[] measurementList) {
    	    	
    	int i=0;
    	
    	Double minValue=Double.MAX_VALUE;
    	for (IDataElement mm : measurementList) {
    		Comparable<?> value=mm.getValue();
    		if (value!=null && value instanceof Number)
    			minValue=Math.min((Double)value, minValue);
    		else
    			System.out.println("Invalid value detected. Values must be Double");
    	}
    	
    	//System.out.println("Min value Result=" + minValue);
    	
   		return minValue==Double.MAX_VALUE?null:minValue;
    }

    @Override
    public String getType() {
      return "Min";
    }

}
