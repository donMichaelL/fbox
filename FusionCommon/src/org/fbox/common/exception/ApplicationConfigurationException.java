package org.fbox.common.exception;

public class ApplicationConfigurationException extends Exception {
	
	public ApplicationConfigurationException(String msg) {
		super(msg);
	}

	public ApplicationConfigurationException(String msg, Exception e) {
		super(msg, e);
	}

	public ApplicationConfigurationException(Exception e) {
		super(e);
	}	
}
