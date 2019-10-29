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
import org.fbox.common.exception.AlgorithmInitializationException;
import org.fbox.fusion.algorithms.miso.AbstractMISOAlgorithm;

/**
 * Session Bean implementation class SimpleVoting
 */
@Stateless(name="SimpleVoting")
@Remote( { IAlgorithm.class, IStructure.class })
public class SimpleVoting extends AbstractMISOAlgorithm {

    /**
     * Default constructor. 
     */
    public SimpleVoting() {
    }

	/**
     * @see IDetect#getRequiredParameters()
     */
    public String[] getRequiredParameters() {
		String[] requiredParams={"z-threshold"}; //a Single initialization parameter("z-threshold") is needed
		return requiredParams;
	}

    @Override
    public void initialize(IAlgorithmContext state, HashMap<String, InputParameter> iparams) throws AlgorithmInitializationException {
		//initialize metric
		//state.getData().setValue(new Double(0.0)); //initialize metric as double
		
		Set<String> requiredParameters=new HashSet<String>(Arrays.asList(getRequiredParameters()));
		
		//intialize execution params
		Collection<InputParameter> paramValues=iparams.values();
		for (InputParameter param : paramValues) {
			switch (param.getName()) { 
			case "z-threshold":
				try {
					state.setContextParameter("z-threshold", Double.parseDouble((String)param.getValue()));  //set the z voting threshold
				} catch (NumberFormatException ex) {
					throw new AlgorithmInitializationException("A non valid number(" + param.getValue() + ") was specified as z-threshold");						
				}
				requiredParameters.remove(param.getName());
				break;
			default:
				System.out.println("WARNING --> Input Parameter '"+ param.getName() + "' not applicable for Detection Algorithm " + getType() +". Will be Ignored.");
			}
		}
		
		for (String s : requiredParameters) {
			System.out.println("ERROR --> Input Parameter '"+ s + "' is needed but has not been specified.");
		}		
    }

    @Override
    protected Comparable<?> _update(IAlgorithmContext state, IDataElement[] measurementList) {
    	Double outValue;
    	
    	int m=measurementList.length;
    	Double zThreshold=(Double)state.getContextParameter("z-threshold");
    	//System.out.println("%%%%%%%%%%%%%%%%%%%%%zThreshold=" + zThreshold);
    	//System.out.println("!!!!!!!!!!!!!!!!!!!"+state);
    	Double sumOfMeasurements=0.0;
    	//int i=0;
    	for (IDataElement mm : measurementList) {
    		Comparable<?> value=mm.getValue();
    		//System.out.println("value["+(i++)+"]="+value);
    		if (value!=null && value instanceof Number)
    			sumOfMeasurements+=(Double)value;
    		else
    			System.out.println("Invalid value detected. Values must be Double");
    	}
    	
    	Double votingResult= sumOfMeasurements/m;
    	// System.out.println("Sum=" + sumOfMeasurements);
    	// System.out.println("m=" + m);
    	//System.out.println("Voting Result=" + votingResult);
    	
    	zThreshold=0.5;
    	if (votingResult<zThreshold) 
    		outValue= 0.0;
    	else
    		outValue=1.0;
    	
    	return outValue;
    }

    @Override
    public String getType() {
      return "SimpleVoting";
    }

}
