package org.fbox.common.application.configuration;

public enum ApplicationStatus {

	STOPPED, STARTED, DEPLOYED, DEPLOYING, UNDEPLOYING, UNDEPLOYED, STARTING, STOPPING;
	
	public String toString() {
		String result=null;
		switch (this) {
		case STOPPED: result="STOPPED";
					  break;
		case STOPPING: result="STOPPING";
		  break;					  
		case STARTED: result="STARTED";
		  break;
		case STARTING: result="STARTING";
		  break;
		case DEPLOYED: result="DEPLOYED";
		  break;
		case DEPLOYING: result="DEPLOYING";
		  break;
		case UNDEPLOYING: result="UNDEPLOYING";
		  break;			  
		case UNDEPLOYED: result="UNDEPLOYED";
		  break;		  
		}
		
		return result;
	};
}
