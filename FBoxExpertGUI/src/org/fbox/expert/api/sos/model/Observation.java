package org.fbox.expert.api.sos.model;

public class Observation {
	
	private String timeStamp;
	private String value;
	
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "Observation [timeStamp=" + timeStamp + ", value=" + value + "]";
	}
	
	
	
	
}
