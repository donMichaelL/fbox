package org.fbox.configurator.exceptions;

@Deprecated
public class CLIManagerException extends Exception {

	public CLIManagerException(String msg) {
		super(msg);
	}
	
	public CLIManagerException(Exception e) {
		super(e);
	}
	
	public CLIManagerException(String msg, Exception e) {
		super(msg,e);
	}
}
