package org.fbox.expert.deploy.exceptions;

public class UploadServletException extends Exception {

	public UploadServletException(String msg) {
		super(msg);
	}
	
	public UploadServletException(Exception e) {
		super(e);
	}
}
