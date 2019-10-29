package org.fbox.expert.api.sos.model;

public class DataStreamMetadata {
	private String procedureId;
	private String phenomenonId;
	private String latitude;
	private String longtitude;
	private boolean isActive;
	private boolean isMobile;
	
	public void SensorStreamMetadata() {
		
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
	
	
	

}
