package org.fbox.common.data;

import org.fbox.common.application.data.DataElement;

public class AlgorithmContext extends Context implements IAlgorithmContext {

	IDataElement data;
	String name;
	
	public AlgorithmContext(String contextorId, String algorithmName) {
		super();
		this.data=new DataElement(contextorId);
		this.name=algorithmName;
	}
	
	@Override
	public void setData(IDataElement data) {
		this.data=data;
	}

	@Override
	public IDataElement getData() {
		return this.data;
	}

	@Override
	public String toString() {
		String result;
		result="name: " + this.name +", data: " + this.data;
		return result;
	}
	
}
