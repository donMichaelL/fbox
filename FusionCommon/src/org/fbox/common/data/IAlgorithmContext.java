package org.fbox.common.data;

import java.io.Serializable;


public interface IAlgorithmContext extends IContext, Serializable {
	public void setData(IDataElement data);
	public IDataElement getData();
}
