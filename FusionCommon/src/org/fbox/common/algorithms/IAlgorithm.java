package org.fbox.common.algorithms;

import java.util.HashMap;

import org.fbox.common.IStructure;
import org.fbox.common.data.IAlgorithmContext;
import org.fbox.common.data.IDataElement;
import org.fbox.common.data.InputParameter;
import org.fbox.common.exception.AlgorithmExecutionException;
import org.fbox.common.exception.AlgorithmInitializationException;


public interface IAlgorithm extends IStructure {

	public IDataElement update(IAlgorithmContext state, IDataElement... measurement) throws AlgorithmExecutionException;
	public void initialize(IAlgorithmContext state, HashMap<String, InputParameter> iparams) throws AlgorithmInitializationException;
}
   