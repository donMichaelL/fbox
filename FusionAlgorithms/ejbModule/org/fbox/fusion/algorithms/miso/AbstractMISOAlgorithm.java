package org.fbox.fusion.algorithms.miso;

import org.fbox.common.data.IAlgorithmContext;
import org.fbox.common.data.IDataElement;
import org.fbox.common.exception.AlgorithmExecutionException;
import org.fbox.fusion.algorithms.AbstractAlgorithm;


public abstract class AbstractMISOAlgorithm extends AbstractAlgorithm {

	@Override
	public IDataElement update(IAlgorithmContext state, IDataElement... measurement) throws AlgorithmExecutionException {
		if (measurement==null)
			throw new AlgorithmExecutionException("No input data specified");
/*		else if (measurement.length==0)
			throw new AlgorithmExecutionException("No input specified for a MISO algorithm");
		else if (getNumberOfInputs()!=null && (measurement.length<getNumberOfInputs() || measurement.length>getNumberOfInputs()))
			throw new AlgorithmExecutionException("A MISO algorithm with "+ getNumberOfInputs() +" cannot accept" + measurement.length + " inputs!!!" );
 */		else {
		 	Comparable<?> calculatedValue=_update(state, measurement);
		 	if (calculatedValue!=null) { 				
		 		state.getData().setValue(calculatedValue);
		 	}
		}
	 	return state.getData();

	}

	protected abstract Comparable<?> _update(IAlgorithmContext state, IDataElement[] measurement) throws AlgorithmExecutionException;

	@Override
	public boolean allowsMultipleInputs() {
		return true;
	}
	
}
