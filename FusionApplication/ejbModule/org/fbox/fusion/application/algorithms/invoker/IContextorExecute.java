package org.fbox.fusion.application.algorithms.invoker;

import org.fbox.common.data.IDataElement;
import org.fbox.common.exception.AlgorithmExecutionException;
import org.fbox.common.exception.FormatterException;
import org.fbox.common.exception.OutputAdapterException;
import org.fbox.common.registry.RegistryInsertionError;
import org.fbox.fusion.application.exception.ContextNotFoundException;


public interface IContextorExecute {

	public void update(String targetContextor) throws AlgorithmExecutionException, ContextNotFoundException, OutputAdapterException, FormatterException, RegistryInsertionError;

	public void update(String targetContextor, IDataElement... data) throws AlgorithmExecutionException, ContextNotFoundException, OutputAdapterException, FormatterException, RegistryInsertionError;
	
}
