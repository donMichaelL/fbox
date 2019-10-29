package org.fbox.common.algorithms.invoker;

import org.fbox.common.data.IDataElement;

public interface IAlgorithmInvoker {

	public IDataElement update(String target, IDataElement data);
	
}
