package org.sos.ds.model;

public class SOSObservationsStats {
	
	private String timestamp;
	private String observations;
	
	public SOSObservationsStats(String timestamp, String observations) {
		super();
		this.timestamp = timestamp;
		this.observations = observations;
	}

	public String getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	public String getObservations() {
		return observations;
	}
	
	public void setObservations(String observations) {
		this.observations = observations;
	}

}
