package org.fbox.fusion.algorithms.siso;


import org.fbox.common.data.IAlgorithmContext;
import org.fbox.common.data.IDataElement;
import org.fbox.common.exception.AlgorithmExecutionException;
import org.fbox.fusion.algorithms.AbstractAlgorithm;

public abstract class AbstractSISOAlgorithm extends AbstractAlgorithm {

	@Override
	public IDataElement update(IAlgorithmContext state, IDataElement... measurement) throws AlgorithmExecutionException {
		if (measurement==null)
			throw new AlgorithmExecutionException("No input data specified");
		else if (measurement.length!=1)
			throw new AlgorithmExecutionException("A Single Input is required for a SISO algorithm and currently has "+measurement.length);
		else {
			Comparable<?> calculatedValue=_update(state, measurement[0]);
			//if (calculatedValue!=null) { 				
				state.getData().setValue(calculatedValue);
			//}
		}
		
		return state.getData();
	}

	protected abstract Comparable<?> _update(IAlgorithmContext state, IDataElement measurement) throws AlgorithmExecutionException;

	@Override
	public boolean allowsMultipleInputs() {
		return false;
	}
	
}
