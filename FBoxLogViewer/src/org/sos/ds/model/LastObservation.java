package org.sos.ds.model;

public class LastObservation {
	private String timeStamp;
	private String procedureId;
	private String phenomenonId;
	// private String isMobile;
	// private String latitude;
	// private String longtitude;
	private String value;
	// private String spatialValue;
	
	
	public LastObservation() {
		
	}


	public String getTimeStamp() {
		return timeStamp;
	}


	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}


	public String getProcedureId() {
		return procedureId;
	}


	public void setProcedureId(String procedureId) {
		this.procedureId = procedureId;
	}


	public String getPhenomenonId() {
		return phenomenonId;
	}


	public void setPhenomenonId(String phenomenonId) {
		this.phenomenonId = phenomenonId;
	}


	public String getValue() {
		return value;
	}


	public void setValue(String numericValue) {
		this.value = numericValue;
	}
	
	
	@Override
	public String toString() {
		return "LastObservation [timeStamp=" + timeStamp + ", procedureId="
				+ procedureId + ", phenomenonId=" + phenomenonId
				+ ", numericValue=" + value + "]";
	}
	
	
	
	
	
	
}
