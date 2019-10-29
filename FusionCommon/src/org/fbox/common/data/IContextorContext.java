package org.fbox.common.data;

import java.io.Serializable;

import org.fbox.common.algorithms.IAlgorithm;
import org.fbox.common.exception.AlgorithmExecutionException;

public interface IContextorContext extends Serializable {

	public String getId();
	public IAlgorithm getBaseAlgorithm();
	public void setBaseAlgorithm(IAlgorithm algorithm);
	public IAlgorithm getMissingAlgorithm();
	public void setMissingAlgorithm(IAlgorithm algorithm);	
	public IDataElement updateContext(IDataElement... data) throws AlgorithmExecutionException;
	public IDataElement getValidData(long timeLimit);
	public IAlgorithmContext getBaseAlgorithmContext();
	public IAlgorithmContext getMissingAlgorithmContext();
}
