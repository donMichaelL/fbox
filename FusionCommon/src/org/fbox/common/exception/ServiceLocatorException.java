package org.fbox.common.exception;

public class ServiceLocatorException extends Exception {

	public ServiceLocatorException(Exception ex) {
		super(ex);
	}

	public ServiceLocatorException(String str) {
		super(str);
	}
}
