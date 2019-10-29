package org.fbox.fusion.algorithms.miso.ejb.impl;

import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.fbox.common.IStructure;
import org.fbox.common.algorithms.IAlgorithm;
import org.fbox.common.data.IAlgorithmContext;
import org.fbox.common.data.IDataElement;
import org.fbox.fusion.algorithms.miso.AbstractMISOAlgorithm;

/**
 * Session Bean implementation class AggregateEqually
 */
@Stateless(name="AggregateEqually")
@Remote( { IAlgorithm.class, IStructure.class })
public class AggregateEqually extends AbstractMISOAlgorithm {

	/**
     * Default constructor. 
     */
    public AggregateEqually() {
    }
    
    public String[] getRequiredParameters() {
		return null; //No parameters needed
	}
    
    @Override
    protected Comparable<?> _update(IAlgorithmContext state, IDataElement[] measurementList) {
    	
    	//Count the input values
    	double inputs = measurementList.length;
    	int i=0;
    	double return_value = 0.0;
    	
    	for (IDataElement d : measurementList) {
    		Comparable<?> value=d.getValue();
    		if (value!=null) {
    			if (value instanceof Number) {
    				if((Double)value == 0.0) {
    	    			return 0.0;
    	    		} else    	    		
    	    			return_value = return_value + (Double)d.getValue() * ( 1.0 / inputs );    				
    			} else {
    				System.out.println("Invalid value detected. Values must be Double");
    				return 0.0;
    			}    			
    		} else {
    			return 0.0;
    		}
    	}
    	
   		return return_value;
    }

    
    @Override
    public String getType() {
      return "AggregateEqually";
    }
}
