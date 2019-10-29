package org.sos.ds.model;

public class DataStreamMetadata {
	private String procedureId;
	private String phenomenonId;
	private String latitude;
	private String longtitude;
	private boolean isActive;
	private boolean isMobile;
	
	// Necessary for Sensor Tool in COP 
	private String timeStamp;
	private String value;
	
	public DataStreamMetadata() {
		
	}
	
	public DataStreamMetadata(String procedureId, String phenomenonId,
			String latitude, String longtitude, boolean isActive,
			boolean isMobile) {
		this.procedureId = procedureId;
		this.phenomenonId = phenomenonId;
		this.latitude = latitude;
		this.longtitude = longtitude;
		this.isActive = isActive;
		this.isMobile = isMobile;
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
	
	public String getLatitude() {
		return latitude;
	}
	
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
	public String getLongtitude() {
		return longtitude;
	}
	
	public void setLongtitude(String longtitude) {
		this.longtitude = longtitude;
	}
	
	public boolean isActive() {
		return isActive;
	}
	
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	public boolean isMobile() {
		return isMobile;
	}
	
	public void setMobile(boolean isMobile) {
		this.isMobile = isMobile;
	}
	

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
	
	
	
	
	

}
