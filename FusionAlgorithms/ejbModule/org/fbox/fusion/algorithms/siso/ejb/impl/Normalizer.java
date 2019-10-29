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
 * Session Bean implementation class Normalizer
 */
@Stateless (name="Normalizer")
@Remote( {IAlgorithm.class, IStructure.class })
public class Normalizer extends AbstractSISOAlgorithm {

	/**
     * Default constructor.
     */
    public Normalizer() {
    }

    @Override
    public String[] getRequiredParameters() {
		String[] requiredParams={"maxCapacity"};
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
				System.out.println("[Norm] WARNING --> Input Parameter '"+ param.getName() + "' not applicable for Algorithm " + getType() +". Will be Ignored.");
		}

		//check missing parameters
		String errorMessage="";
		for (String s : requiredParameters) {
			errorMessage+="[Norm] ERROR --> Input Parameter '"+ s + "' is needed but has not been specified.\n";
		}
		if (!errorMessage.isEmpty()) {
			System.out.println(errorMessage);
			throw new AlgorithmInitializationException(errorMessage);
		}
    }

    @Override
    protected Comparable<?> _update(IAlgorithmContext state, IDataElement measurement) throws AlgorithmExecutionException{

    	//double denominator = Double.parseDouble((String)state.getContextParameter("max-capacity"));
    	if(measurement != null && measurement.getValue() instanceof Number) {
	    	double denominator = (Double)state.getContextParameter("maxCapacity");
	    	return (Double)measurement.getValue() / denominator;
    	} else {
    		System.out.println("[Norm] WARNING: Invalid data value detected("+measurement.getValue()+"). Value must be Double");
			return null;
    	}
    }

    /**
     * @see IAlgorithm#getType()
     */
    public String getType() {
			return "Normalizer";
    }
}
