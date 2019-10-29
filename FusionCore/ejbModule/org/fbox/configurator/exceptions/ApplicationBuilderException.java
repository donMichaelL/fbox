package org.fbox.configurator.exceptions;

public class ApplicationBuilderException extends Exception {

	public ApplicationBuilderException(String msg) {
		super(msg);
	}
	
	public ApplicationBuilderException(Exception e) {
		super(e);
	}
}
