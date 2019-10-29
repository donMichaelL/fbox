package org.fbox.common.algorithms;

public interface IStructure {
	public String getType();
	public String[] getRequiredParameters();
	public String[] getOptionalParameters();
	public boolean allowsMultipleInputs();	
}
