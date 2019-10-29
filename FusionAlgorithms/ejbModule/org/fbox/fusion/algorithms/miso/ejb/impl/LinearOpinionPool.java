package org.fbox.fusion.algorithms.miso.ejb.impl;

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
import org.fbox.fusion.algorithms.miso.AbstractMISOAlgorithm;

/**
 * Session Bean implementation class SimpleVoting
 */
@Stateless(name="LinearOpinionPool")
@Remote( { IAlgorithm.class, IStructure.class })
public class LinearOpinionPool extends AbstractMISOAlgorithm {

    /**
     * Default constructor. 
     */
    public LinearOpinionPool() {
    }

    @Override
    public String[] getRequiredParameters() {
		String[] requiredParams={"weight-list"}; //a Single initialization parameter("z-threshold") is needed
		return requiredParams;
	}

    @Override
    public void initialize(IAlgorithmContext state, HashMap<String, InputParameter> iparams) throws AlgorithmInitializationException {
		
		//initialize metric
		//state.getData().setValue(new Double(0.0)); //initialize metric as double
		
		//intialize execution params
		Set<String> requiredParameters=new HashSet<String>(Arrays.asList(getRequiredParameters()));
		Collection<InputParameter> paramValues=iparams.values();
		for (InputParameter param : paramValues) {
			switch (param.getName()) { 
			case "weight-list":
				String[] strListOfWeights=((String)param.getValue()).split(";");
				if (strListOfWeights.length>0) {
					Double[] dblListOfWeights=new Double[strListOfWeights.length];
					double sumOfWeights=0;
					for (int i=0;i<strListOfWeights.length;i++) {
						try {
							dblListOfWeights[i]=Double.parseDouble(strListOfWeights[i]);
						} catch (NumberFormatException e) {
							throw new AlgorithmInitializationException("A non valid number(" + strListOfWeights[i] + ") was specified as weight");							
						}
						sumOfWeights+=dblListOfWeights[i];
						//System.out.println("----------------------------------------------------" + dblListOfWeights[i]);
						if (sumOfWeights>1) {
							throw new AlgorithmInitializationException("The sum of linear weights exceeds 1.0");
						}
					}
					if (sumOfWeights>1) {
						throw new AlgorithmInitializationException("The sum of linear weights does not equal 1.0");
					}
					state.setContextParameter("weight-list", dblListOfWeights);  //set the weights list execution parameter
				}
				requiredParameters.remove(param.getName());
				break;
			default:
				System.out.println("WARNING --> Input Parameter '"+ param.getName() + "' not applicable for Detection Algorithm " + getType() +". Will be Ignored.");
			}
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

    }

    @Override
    protected Comparable<?> _update(IAlgorithmContext state, IDataElement[] measurementList) throws AlgorithmExecutionException {
    	
    	Double[] listOfWeights=(Double[])state.getContextParameter("weight-list");
    	
    	Double linearProduct=0.0;
    	int i=0;
    	for (IDataElement mm : measurementList) {
    		Comparable<?> value=mm.getValue();
    		if (value!=null && value instanceof Number) {
    			try {
    				//System.out.println("00000000000000000000000000000000  " + listOfWeights[i]);
    				//System.out.println("11111111111111111111111111111111  " + mm.getDoubleValue());
    				linearProduct += listOfWeights[i++]*(Double)value;
    				//System.out.println("22222222222222222222222222222222  " + linearProduct);
    			} catch (ArrayIndexOutOfBoundsException ex) {
    				throw new AlgorithmExecutionException("The list size of provided weights does not match the number of measurements");
    			}
    		} else {
    			if (value==null) // if null value then proceed to next value/weight assuming product is zero;
    				i++;
    			else
    				throw new AlgorithmExecutionException("The algorithm cannot operate on non mumeric values");
    		}
    	}
    	
    	//System.out.println("Linear Opinion Value=" + linearProduct);
    	
    	//set the metric to the calculated value
    	return linearProduct;
    }

    @Override
    public String getType() {
      return "LinearOpinionPool";
    }


    
}
