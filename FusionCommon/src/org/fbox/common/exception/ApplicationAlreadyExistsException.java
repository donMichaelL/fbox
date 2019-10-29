package org.fbox.common.exception;

public class ApplicationAlreadyExistsException extends Exception {
	
	public ApplicationAlreadyExistsException(String msg) {
		super(msg);
	}

	public ApplicationAlreadyExistsException(String msg, Exception e) {
		super(msg, e);
	}

	public ApplicationAlreadyExistsException(Exception e) {
		super(e);
	}	
}
