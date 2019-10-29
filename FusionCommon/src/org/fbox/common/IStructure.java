package org.fbox.common;

public interface IStructure {
	public String getType();
	public String[] getRequiredParameters();
	public String[] getOptionalParameters();
	public boolean allowsMultipleInputs();	
}
