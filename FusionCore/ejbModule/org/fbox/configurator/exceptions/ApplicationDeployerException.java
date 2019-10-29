package org.fbox.configurator.exceptions;

public class ApplicationDeployerException extends Exception {

	public ApplicationDeployerException(String msg) {
		super(msg);
	}

	public ApplicationDeployerException(String message, Exception e) {
		super(message, e);
	}
}
